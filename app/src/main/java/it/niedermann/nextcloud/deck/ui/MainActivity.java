package it.niedermann.nextcloud.deck.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.login.LoginDialogFragment;
import it.niedermann.nextcloud.deck.ui.stack.StackAdapter;
import it.niedermann.nextcloud.deck.ui.stack.StackFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.navigationView) NavigationView navigationView;
    @BindView(R.id.stackLayout) TabLayout stackLayout;
    @BindView(R.id.viewPager) ViewPager viewPager;

    private StackAdapter stackAdapter;
    private LoginDialogFragment loginDialogFragment;
    private SyncManager syncManager;
    private List<Board> boardsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        fab.setOnClickListener((View view) -> {
            Snackbar.make(view, "Creating new Cards is not yet supported", Snackbar.LENGTH_LONG).show();
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        syncManager = new SyncManager(getApplicationContext(), this);
        stackAdapter = new StackAdapter(getSupportFragmentManager());

        if(this.syncManager.hasAccounts()) {
            Account account = syncManager.readAccounts().get(0);
            String accountName = account.getName();
            SingleAccountHelper.setCurrentAccount(getApplicationContext(), accountName);

            syncManager.getBoards(account.getId(), new IResponseCallback<List<Board>>() {
                @Override
                public void onResponse(List<Board> boards) {
                    Menu menu = navigationView.getMenu();
                    boardsList = boards;
                    int index = 0;
                    for(Board board: boardsList) {
                        menu.add(Menu.NONE, index++, Menu.NONE, board.getTitle());
                    }
                    displayStacksForIndex(0);
                }

                @Override
                public void onError(Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        } else {
            loginDialogFragment = new LoginDialogFragment();
            loginDialogFragment.show(this.getSupportFragmentManager(), "NoticeDialogFragment");
        }
    }

    public void onAccountChoose(SingleSignOnAccount account) {
        getSupportFragmentManager().beginTransaction().remove(loginDialogFragment).commit();
        this.syncManager.createAccount(account.name);
    }

    /**
     * Displays the Stacks for the boardsList by index
     * @param index of boardsList
     */
    private void displayStacksForIndex(int index) {
        Board selectedBoard = boardsList.get(index);
        if(toolbar != null) {
            toolbar.setTitle(selectedBoard.getTitle());
        }
        syncManager.getStacks(0, selectedBoard.getId(), new IResponseCallback<List<Stack>>() {
            @Override
            public void onResponse(List<Stack> response) {
                stackAdapter.clear();
                for(Stack stack: response) {
                    stackAdapter.addFragment(StackFragment.newInstance(stack.getId()), stack.getTitle());
                }
                viewPager.setAdapter(stackAdapter);
                stackLayout.setupWithViewPager(viewPager);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("Deck", throwable.getMessage());
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displayStacksForIndex(item.getItemId());
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
