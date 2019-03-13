package it.niedermann.nextcloud.deck.ui;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.ui.helper.dnd.CrossTabDragAndDrop;
import it.niedermann.nextcloud.deck.ui.login.LoginDialogFragment;
import it.niedermann.nextcloud.deck.ui.stack.StackAdapter;
import it.niedermann.nextcloud.deck.ui.stack.StackFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MENU_ID_ABOUT = -1;
    private static final int MENU_ID_ADD_ACCOUNT = -2;
    private static final int ACTIVITY_ABOUT = 1;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.navigationView)
    NavigationView navigationView;
    @BindView(R.id.stackLayout)
    TabLayout stackLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private StackAdapter stackAdapter;
    private LoginDialogFragment loginDialogFragment;
    private SyncManager syncManager;

    private List<Board> boardsList;
    private LiveData<List<Board>> boardsLiveData;
    private Observer<List<Board>> boardsLiveDataObserver;

    private List<Account> accountsList = new ArrayList<>();
    private Account account;
    private boolean accountChooserActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        navigationView.getHeaderView(0).findViewById(R.id.accountChooser).setOnClickListener(v -> {
            this.accountChooserActive = !this.accountChooserActive;
            if (accountChooserActive) {
                buildSidenavAccountChooser();
            } else {
                buildSidenavMenu();
            }
        });


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        syncManager = new SyncManager(getApplicationContext(), this);
        stackAdapter = new StackAdapter(getSupportFragmentManager());

        //TODO limit this call only to lower API levels like KitKat because they crash without
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

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


        this.syncManager.hasAccounts().observe(MainActivity.this, (Boolean hasAccounts) -> {
            if (hasAccounts != null && hasAccounts) {
                syncManager.readAccounts().observe(MainActivity.this, (List<Account> accounts) -> {
                    if (accounts != null) {
                        this.accountsList = accounts;
                        this.account = accounts.get(accounts.size() - 1);
                        String accountName = this.account.getName();
                        SingleAccountHelper.setCurrentAccount(getApplicationContext(), accountName);

                        // TODO show spinner
                        MainActivity.this.syncManager.synchronize(new IResponseCallback<Boolean>(this.account) {
                            @Override
                            public void onResponse(Boolean response) {
                                //nothing
                            }
                        });
                        boardsLiveData = syncManager.getBoards(this.account.getId());
                        boardsLiveDataObserver = (List<Board> boards) -> {
                            boardsList = boards;
                            buildSidenavMenu();
                        };
                        boardsLiveData.observe(MainActivity.this, boardsLiveDataObserver);
                    }
                });
            } else {
                loginDialogFragment = new LoginDialogFragment();
                loginDialogFragment.show(MainActivity.this.getSupportFragmentManager(), "NoticeDialogFragment");
            }
        });

        fab.setOnClickListener((View view) -> {
            BottomSheetCreateFragment bottomSheetFragment = new BottomSheetCreateFragment();
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        });
    }

    public void onAccountChoose(SingleSignOnAccount account) {
        getSupportFragmentManager().beginTransaction().remove(loginDialogFragment).commit();
        final WrappedLiveData<Account> accountLiveData = this.syncManager.createAccount(account.name);
        accountLiveData.observe(this, (Account ac) -> {
            if (accountLiveData.hasError()) {
                try {
                    accountLiveData.throwError();
                } catch (SQLiteConstraintException ex) {
                    Snackbar.make(coordinatorLayout, "Account bereits hinzugefügt", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(coordinatorLayout, "Account hinzugefügt", Snackbar.LENGTH_SHORT).show();
            }
        });

        SingleAccountHelper.setCurrentAccount(getApplicationContext(), account.name);
    }

    private void buildSidenavMenu() {
        navigationView.setItemIconTintList(null);
        Menu menu = navigationView.getMenu();
        menu.clear();
        SubMenu boardsMenu = menu.addSubMenu(getString(R.string.simple_boards));
        int index = 0;
        for (Board board : boardsList) {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_view_column_black_24dp);
            Drawable wrapped = DrawableCompat.wrap(drawable).mutate();
            int color = Color.parseColor("#" + board.getColor());
            DrawableCompat.setTint(wrapped, color);
            boardsMenu.add(Menu.NONE, index++, Menu.NONE, board.getTitle()).setIcon(wrapped);
        }
        menu.add(Menu.NONE, MENU_ID_ABOUT, Menu.NONE, getString(R.string.about)).setIcon(R.drawable.ic_info_outline_black_24dp);
        if (boardsList.size() > 0) {
            displayStacksForIndex(0, this.account);
        }
    }

    private void buildSidenavAccountChooser() {
        Menu menu = navigationView.getMenu();
        menu.clear();
        SubMenu accountMenu = menu.addSubMenu(getString(R.string.accounts));
        int index = 0;
        for (Account account : this.accountsList) {
            accountMenu.add(Menu.NONE, index++, Menu.NONE, account.getName()).setIcon(R.drawable.ic_person_grey600_24dp);
        }
        menu.add(Menu.NONE, MENU_ID_ADD_ACCOUNT, Menu.NONE, getString(R.string.add_account)).setIcon(R.drawable.ic_person_add_black_24dp);
    }

    /**
     * Displays the Stacks for the boardsList by index
     *
     * @param index of boardsList
     */
    private void displayStacksForIndex(int index, Account account) {
        if (boardsList.size() > index) {
            Board selectedBoard = boardsList.get(index);
            if (toolbar != null) {
                toolbar.setTitle(selectedBoard.getTitle());
            }
            syncManager.getStacksForBoard(account.getId(), selectedBoard.getLocalId()).observe(MainActivity.this, (List<FullStack> fullStacks) -> {
                if (fullStacks != null) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (accountChooserActive) {
            switch (item.getItemId()) {
                case MENU_ID_ADD_ACCOUNT:
                    loginDialogFragment = new LoginDialogFragment();
                    loginDialogFragment.show(MainActivity.this.getSupportFragmentManager(), "NoticeDialogFragment");
                    break;
                default:
                    boardsLiveData.removeObserver(boardsLiveDataObserver);
                    this.account = accountsList.get(item.getItemId());
                    SingleAccountHelper.setCurrentAccount(getApplicationContext(), this.account.getName());

                    boardsLiveData = syncManager.getBoards(this.account.getId());
                    boardsLiveDataObserver = (List<Board> boards) -> {
                        boardsList = boards;
                        accountChooserActive = false;
                        buildSidenavMenu();
                    };
                    boardsLiveData.observe(MainActivity.this, boardsLiveDataObserver);
                    displayStacksForIndex(0, this.account);
            }
        } else {
            switch (item.getItemId()) {
                case MENU_ID_ABOUT:
                    Intent aboutIntent = new Intent(getApplicationContext(), AboutActivity.class);
                    startActivityForResult(aboutIntent, ACTIVITY_ABOUT);
                    break;
                default:
                    displayStacksForIndex(item.getItemId(), account);
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
