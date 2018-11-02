package it.niedermann.nextcloud.deck.ui.board;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.login.LoginDialogFragment;
import it.niedermann.nextcloud.deck.ui.stack.StackAdapter;
import it.niedermann.nextcloud.deck.ui.stack.StackFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.stackLayout)
    TabLayout stackLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    private StackAdapter stackAdapter;

    private LoginDialogFragment loginDialogFragment;
    private BoardAdapter adapter = null;
    private SyncManager syncManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Creating new Boards is not yet supported", Snackbar.LENGTH_LONG).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        initRecyclerView();
        syncManager = new SyncManager(getApplicationContext(), this);
        if(this.syncManager.hasAccounts()) {
            String accountName = syncManager.readAccounts().get(0).getName();
            SingleAccountHelper.setCurrentAccount(getApplicationContext(), accountName);

            syncManager.getBoards(new IResponseCallback<List<Board>>() {
                @Override
                public void onResponse(List<Board> boards) {
                    adapter.setBoardList(boards);
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

        stackAdapter = new StackAdapter(getSupportFragmentManager());
        stackAdapter.addFragment(new StackFragment(), "Stack 1");
        stackAdapter.addFragment(new StackFragment(), "Stack 2");
        stackAdapter.addFragment(new StackFragment(), "Stack 3");
        stackAdapter.addFragment(new StackFragment(), "Stack 4");
        viewPager.setAdapter(stackAdapter);
        stackLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onAccountChoose(SingleSignOnAccount account) {
        getSupportFragmentManager().beginTransaction().remove(loginDialogFragment).commit();
        this.syncManager.createAccount(account.name);
    }

    public void initRecyclerView() {
        adapter = new BoardAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
