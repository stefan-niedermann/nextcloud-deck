package it.niedermann.nextcloud.deck.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
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
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.helper.dnd.CrossTabDragAndDrop;
import it.niedermann.nextcloud.deck.ui.login.LoginDialogFragment;
import it.niedermann.nextcloud.deck.ui.stack.StackAdapter;
import it.niedermann.nextcloud.deck.ui.stack.StackFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MENU_ID_ABOUT = -1;
    private static final int ACTIVITY_ABOUT = 1;

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
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        syncManager = new SyncManager(getApplicationContext(), this);
        stackAdapter = new StackAdapter(getSupportFragmentManager());

        //TODO replace nulls
        new CrossTabDragAndDrop().register(this, viewPager, null, null);

//        viewPager.setOnDragListener((View v, DragEvent dragEvent) -> {
//            Log.d("Deck", "Drag: "+ dragEvent.getAction());
//            if(dragEvent.getAction() == 4)
//                Log.d("Deck", dragEvent.getAction() + "");
//
//            View view = (View) dragEvent.getLocalState();
//            RecyclerView owner = (RecyclerView) view.getParent();
//            CardAdapter cardAdapter = (CardAdapter) owner.getAdapter();
//
//            switch(dragEvent.getAction()) {
//                case DragEvent.ACTION_DRAG_LOCATION:
//                    Point size = new Point();
//                    getWindowManager().getDefaultDisplay().getSize(size);
//                    if(dragEvent.getX() <= 20) {
//                        Log.d("Deck", dragEvent.getAction() + " moved left");
//                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
//                    } else if(dragEvent.getX() >= size.x - 20) {
//                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
//                        Log.d("Deck", dragEvent.getAction() + " moved right");
//                    }
//                    int viewUnderPosition = owner.getChildAdapterPosition(owner.findChildViewUnder(dragEvent.getX(), dragEvent.getY()));
//                    if(viewUnderPosition != -1) {
//                        Log.d("Deck", dragEvent.getAction() + " moved something...");
//                        cardAdapter.moveItem(owner.getChildLayoutPosition(view), viewUnderPosition);
//                    }
//                    break;
//                case DragEvent.ACTION_DROP:
//                    view.setVisibility(View.VISIBLE);
//                    break;
//            }
//            return true;
//        });

        handleAccounts();
    }

    public void onAccountChoose(SingleSignOnAccount account) {
        getSupportFragmentManager().beginTransaction().remove(loginDialogFragment).commit();

        this.syncManager.createAccount(account.name);

        // TODO Fetch data directly after login
        // TODO combine with onCreate

        SingleAccountHelper.setCurrentAccount(getApplicationContext(), account.name);
        handleAccounts();
    }

    private void handleAccounts() {
        this.syncManager.hasAccounts().observe(MainActivity.this, (Boolean hasAccounts) -> {
            if(hasAccounts != null && hasAccounts) {
                syncManager.readAccounts().observe(MainActivity.this, (List<Account> accounts) -> {
                    if(accounts != null) {
                        this.account = accounts.get(0);
                        String accountName = this.account.getName();
                        SingleAccountHelper.setCurrentAccount(getApplicationContext(), accountName);

                        fab.setOnClickListener((View view) -> {
                            new Thread(() -> {
                                Board b = new Board();
                                b.setTitle("Test - " + System.currentTimeMillis());
                                b.setOwnerId(boardsList.get(0).getOwnerId());
                                b.setColor("F1DB50");
                                syncManager.createBoard(this.account.getId(), b);
                                Snackbar.make(view, "Creating new Cards is not yet supported", Snackbar.LENGTH_LONG).show();
                            }).run();
                        });

                        // TODO show spinner
                        MainActivity.this.syncManager.synchronize(new IResponseCallback<Boolean>(this.account) {
                            @Override
                            public void onResponse(Boolean response) {
                                runOnUiThread(() -> {
                                    syncManager.getBoards(this.account.getId()).observe(MainActivity.this, MainActivity.this::buildSidenavMenu);
                                });
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                //DeckLog.log(throwable.getMessage());
                                throwable.printStackTrace();
                            }
                        });
                    }
                });
            } else {
                loginDialogFragment = new LoginDialogFragment();
                loginDialogFragment.show(MainActivity.this.getSupportFragmentManager(), "NoticeDialogFragment");
            }
        });
    }

    private void buildSidenavMenu(List<Board> boards) {
        navigationView.setItemIconTintList(null);
        Menu menu = navigationView.getMenu();
        menu.clear();
        SubMenu boardsMenu = menu.addSubMenu(getString(R.string.simple_boards));
        boardsList = boards;
        int index = 0;
        for(Board board: boardsList) {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_view_column_black_24dp);
            Drawable wrapped = DrawableCompat.wrap(drawable).mutate();
            int color = Color.parseColor("#" + board.getColor());
            DrawableCompat.setTint(wrapped, color);
            boardsMenu.add(Menu.NONE, index++, Menu.NONE, board.getTitle()).setIcon(wrapped);
        }
        menu.add(Menu.NONE, MENU_ID_ABOUT, Menu.NONE, getString(R.string.about)).setIcon(R.drawable.ic_info_outline_black_24dp);
        if (boardsList.size()>0){
            displayStacksForIndex(0, this.account);
        }
    }

    /**
     * Displays the Stacks for the boardsList by index
     * @param index of boardsList
     */
    private void displayStacksForIndex(int index, Account account) {
        Board selectedBoard = boardsList.get(index);
        if(toolbar != null) {
            toolbar.setTitle(selectedBoard.getTitle());
        }
        syncManager.getStacksForBoard(account.getId(), selectedBoard.getLocalId()).observe(MainActivity.this, (List<FullStack> fullStacks) -> {
            if(fullStacks != null) {
                stackAdapter.clear();
                for (FullStack stack : fullStacks) {
                    stackAdapter.addFragment(StackFragment.newInstance(selectedBoard.getLocalId(), stack.getStack().getLocalId(), account), stack.getStack().getTitle());
                }
                runOnUiThread(() -> {
                    viewPager.setAdapter(stackAdapter);
                    stackLayout.setupWithViewPager(viewPager);
                });
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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

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
        switch(item.getItemId()) {
            case MENU_ID_ABOUT:
                Intent aboutIntent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivityForResult(aboutIntent, ACTIVITY_ABOUT);
                break;
            default:
                displayStacksForIndex(item.getItemId(), account);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
