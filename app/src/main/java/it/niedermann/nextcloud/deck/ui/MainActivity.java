package it.niedermann.nextcloud.deck.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.snackbar.Snackbar;
import com.h6ah4i.android.tablayouthelper.TabLayoutHelper;
import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.exceptions.AccountImportCancelledException;
import com.nextcloud.android.sso.exceptions.AndroidGetAccountsPermissionNotGranted;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotInstalledException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;
import com.nextcloud.android.sso.ui.UiExceptionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.databinding.ActivityMainBinding;
import it.niedermann.nextcloud.deck.databinding.NavHeaderMainBinding;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.Version;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.SyncWorker;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.ui.board.AccessControlDialogFragment;
import it.niedermann.nextcloud.deck.ui.board.EditBoardDialogFragment;
import it.niedermann.nextcloud.deck.ui.board.EditBoardDialogFragment.EditBoardListener;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.ui.helper.dnd.CrossTabDragAndDrop;
import it.niedermann.nextcloud.deck.ui.stack.EditStackDialogFragment;
import it.niedermann.nextcloud.deck.ui.stack.EditStackDialogFragment.EditStackListener;
import it.niedermann.nextcloud.deck.ui.stack.StackAdapter;
import it.niedermann.nextcloud.deck.ui.stack.StackFragment;
import it.niedermann.nextcloud.deck.ui.stack.StackFragment.OnScrollListener;
import it.niedermann.nextcloud.deck.util.DeleteDialogBuilder;
import it.niedermann.nextcloud.deck.util.ExceptionUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;

import static androidx.lifecycle.Transformations.switchMap;
import static it.niedermann.nextcloud.deck.DeckLog.Severity.INFO;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_STACK_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.NO_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.stack.EditStackDialogFragment.NO_STACK_ID;

public class MainActivity extends AppCompatActivity implements EditStackListener, EditBoardListener, OnScrollListener, OnNavigationItemSelectedListener {

    protected ActivityMainBinding binding;

    protected static final int MENU_ID_ABOUT = -1;
    protected static final int MENU_ID_ADD_BOARD = -2;
    protected static final int MENU_ID_SETTINGS = -3;
    protected static final int MENU_ID_ADD_ACCOUNT = -2;
    protected static final int ACTIVITY_ABOUT = 1;
    protected static final int ACTIVITY_SETTINGS = 2;
    protected static final long NO_ACCOUNTS = -1;
    protected static final long NO_BOARDS = -1;
    protected static final long NO_STACKS = -1;
    @NonNull
    protected List<Account> accountsList = new ArrayList<>();
    protected Account account;
    protected boolean accountChooserActive = false;
    protected SyncManager syncManager;
    protected SharedPreferences sharedPreferences;

    private String sharedPreferencesLastBoardForAccount_;
    private String sharedPreferencesLastStackForAccountAndBoard_;
    private String simpleSettings;
    private String simpleBoards;
    private String about;
    private String shareBoard;
    private String editBoard;
    private String addColumn;
    private String addBoard;

    private StackAdapter stackAdapter;
    @Nullable
    private List<Board> boardsList;
    private LiveData<List<Board>> boardsLiveData;
    private Observer<List<Board>> boardsLiveDataObserver;

    private long currentBoardId = 0;
    private long currentStackId = 0;
    private boolean currentBoardHasEditPermission = false;
    private boolean currentBoardHasStacks = false;
    private String accountAlreadyAdded;
    private String sharedPreferenceLastAccount;
    private String urlFragmentUpdateDeck;
    private String noAccount;
    private String addAccount;
    private int minimumServerAppMajor;
    private int minimumServerAppMinor;
    private int minimumServerAppPatch;
    private Snackbar deckVersionTooLowSnackbar = null;
    private Snackbar accountIsGettingImportedSnackbar;
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setTheme(Application.getAppTheme(this) ? R.style.DarkAppTheme : R.style.AppTheme);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferencesLastBoardForAccount_ = getString(R.string.shared_preference_last_board_for_account_);
        sharedPreferencesLastStackForAccountAndBoard_ = getString(R.string.shared_preference_last_stack_for_account_and_board_);
        simpleSettings = getString(R.string.simple_settings);
        simpleBoards = getString(R.string.simple_boards);
        about = getString(R.string.about);
        shareBoard = getString(R.string.share_board);
        editBoard = getString(R.string.edit_board);
        addColumn = getString(R.string.add_column);
        addBoard = getString(R.string.add_board);
        accountAlreadyAdded = getString(R.string.account_already_added);
        sharedPreferenceLastAccount = getString(R.string.shared_preference_last_account);
        urlFragmentUpdateDeck = getString(R.string.url_fragment_update_deck);
        noAccount = getString(R.string.no_account);
        addAccount = getString(R.string.add_account);
        minimumServerAppMajor = getResources().getInteger(R.integer.minimum_server_app_major);
        minimumServerAppMinor = getResources().getInteger(R.integer.minimum_server_app_minor);
        minimumServerAppPatch = getResources().getInteger(R.integer.minimum_server_app_patch);

        setSupportActionBar(binding.toolbar);
        accountIsGettingImportedSnackbar = Snackbar.make(binding.coordinatorLayout, R.string.account_is_getting_imported, Snackbar.LENGTH_INDEFINITE);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navigationView.setNavigationItemSelectedListener(this);
        syncManager = new SyncManager(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        NavHeaderMainBinding headerBinding = NavHeaderMainBinding.bind(binding.navigationView.getHeaderView(0));

        switchMap(syncManager.hasAccounts(), hasAccounts -> {
            if (hasAccounts) {
                return syncManager.readAccounts();
            } else {
                setCurrentAccount(null);
                startActivityForResult(new Intent(this, ImportAccountActivity.class), ImportAccountActivity.REQUEST_CODE_IMPORT_ACCOUNT);
                return null;
            }
        }).observe(this, (List<Account> accounts) -> {
            if (accounts == null) {
                throw new IllegalStateException("hasAccounts() returns true, but readAccounts() returns null");
            }

            accountsList = accounts;

            long lastAccountId = sharedPreferences.getLong(sharedPreferenceLastAccount, NO_ACCOUNTS);
            DeckLog.log("--- Read: shared_preference_last_account" + " | " + lastAccountId);

            for (Account account : accountsList) {
                if (lastAccountId == account.getId() || lastAccountId == NO_ACCOUNTS) {
                    this.account = account;
                    SingleAccountHelper.setCurrentAccount(getApplicationContext(), this.account.getName());
                    syncManager = new SyncManager(this);
                    setHeaderView();
                    ViewUtil.addAvatar(this, headerBinding.drawerCurrentAccount, this.account.getUrl(), this.account.getUserName(), R.mipmap.ic_launcher_round);
                    // TODO show spinner
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        registerAutoSyncOnNetworkAvailable();
                    } else {
                        syncManager.synchronize(new IResponseCallback<Boolean>(MainActivity.this.account) {
                            @Override
                            public void onResponse(Boolean response) {
                                MainActivity.this.accountIsGettingImportedSnackbar.dismiss();
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                super.onError(throwable);
                                if (throwable instanceof NextcloudHttpRequestFailedException) {
                                    ExceptionUtil.handleHttpRequestFailedException((NextcloudHttpRequestFailedException) throwable, MainActivity.this.binding.coordinatorLayout, MainActivity.this);
                                }
                            }
                        });
                    }
                    setCurrentAccount(this.account);
                    break;
                }
            }

            binding.navigationView.getHeaderView(0).findViewById(R.id.drawer_header_view).setOnClickListener(v1 -> {
                this.accountChooserActive = !this.accountChooserActive;
                if (accountChooserActive) {
                    buildSidenavAccountChooser();
                } else {
                    buildSidenavMenu();
                }
            });

            stackAdapter = new StackAdapter(getSupportFragmentManager());

            CrossTabDragAndDrop dragAndDrop = new CrossTabDragAndDrop(this);
            dragAndDrop.register(binding.viewPager, binding.stackLayout);
            dragAndDrop.addCardMovedByDragListener((movedCard, stackId, position) -> {
                syncManager.reorder(account.getId(), movedCard, stackId, position);
                DeckLog.log("Card \"" + movedCard.getCard().getTitle() + "\" was moved to Stack " + stackId + " on position " + position);
            });


            binding.fab.setOnClickListener((View view) -> {
                if (this.boardsList == null) {
                    DeckLog.log("FAB has been clicked, but boardsList is null... Asking to add an account", INFO);
                    Snackbar
                            .make(binding.coordinatorLayout, R.string.please_add_an_account_first, Snackbar.LENGTH_LONG)
                            .setAction(R.string.simple_add, (v) -> showAccountPicker())
                            .show();
                } else if (this.boardsList.size() > 0) {
                    Intent intent = new Intent(this, EditActivity.class);
                    intent.putExtra(BUNDLE_KEY_ACCOUNT_ID, account.getId());
                    intent.putExtra(BUNDLE_KEY_LOCAL_ID, NO_LOCAL_ID);
                    intent.putExtra(BUNDLE_KEY_BOARD_ID, currentBoardId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        intent.putExtra(BUNDLE_KEY_STACK_ID, stackAdapter.getItem(binding.viewPager.getCurrentItem()).getStackId());
                        startActivity(intent);
                    } catch (IndexOutOfBoundsException e) {
                        EditStackDialogFragment.newInstance(NO_STACK_ID)
                                .show(getSupportFragmentManager(), addColumn);
                    }
                } else {
                    EditBoardDialogFragment.newInstance().show(getSupportFragmentManager(), addBoard);
                }
            });

            binding.addStackButton.setOnClickListener((v) -> {
                if (this.boardsList == null) {
                    DeckLog.log("Add stack has been added but boardsList is null, displaying account picker", INFO);
                    Snackbar
                            .make(binding.coordinatorLayout, R.string.please_add_an_account_first, Snackbar.LENGTH_LONG)
                            .setAction(R.string.simple_add, (view) -> showAccountPicker())
                            .show();
                } else {
                    if (this.boardsList.size() == 0) {
                        EditBoardDialogFragment.newInstance().show(getSupportFragmentManager(), addBoard);
                    } else {
                        EditStackDialogFragment.newInstance(NO_STACK_ID).show(getSupportFragmentManager(), addColumn);
                    }
                }
            });

            binding.viewPager.addOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    binding.viewPager.post(() -> {
                        // Remember last stack for this board
                        if (stackAdapter.getCount() >= position) {
                            currentStackId = stackAdapter.getItem(position).getStackId();
                        }
                    });
                    showFabIfEditPermissionGranted();
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    binding.swipeRefreshLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
                }
            });

            binding.swipeRefreshLayout.setOnRefreshListener(() -> {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm != null) {
                    NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        DeckLog.info("Clearing Glide memory cache");
                        Glide.get(this).clearMemory();
                        new Thread(() -> {
                            DeckLog.info("Clearing Glide disk cache");
                            Glide.get(getApplicationContext()).clearDiskCache();
                        }).start();
                    } else {
                        DeckLog.info("Do not clear Glide caches, because the user currently does not have a working internet connection");
                    }
                } else DeckLog.warn("ConnectivityManager is null");
                syncManager.synchronize(new IResponseCallback<Boolean>(account) {
                    @Override
                    public void onResponse(Boolean response) {
                        runOnUiThread(() -> binding.swipeRefreshLayout.setRefreshing(false));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        runOnUiThread(() -> binding.swipeRefreshLayout.setRefreshing(false));
                        if (throwable instanceof NextcloudHttpRequestFailedException) {
                            ExceptionUtil.handleHttpRequestFailedException((NextcloudHttpRequestFailedException) throwable, binding.coordinatorLayout, MainActivity.this);
                        }
                        DeckLog.logError(throwable);
                    }
                });
            });
        });

        //TODO limit this call only to lower API levels like KitKat because they crash without
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        DeckLog.log("onSaveInstanceState");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (this.account == null) {
            DeckLog.log("--- Remove: shared_preference_last_account");
            editor.remove(sharedPreferenceLastAccount);
        } else {
            DeckLog.log("--- Write: shared_preference_last_account" + " | " + this.account.getId());
            editor.putLong(sharedPreferenceLastAccount, this.account.getId());

            // Remember last board for this account
            DeckLog.log("--- Write: shared_preference_last_board_for_account_" + account.getId() + " | " + currentBoardId);
            editor.putLong(sharedPreferencesLastBoardForAccount_ + this.account.getId(), currentBoardId);

            DeckLog.log("--- Write: shared_preference_last_stack_for_account_and_board_" + account.getId() + "_" + currentBoardId + " | " + currentStackId);
            editor.putLong(sharedPreferencesLastStackForAccountAndBoard_ + account.getId() + "_" + currentBoardId, currentStackId);
        }
        editor.apply();
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onCreateStack(String stackName) {
        observeOnce(syncManager.getStacksForBoard(account.getId(), currentBoardId), MainActivity.this, fullStacks -> {
            Stack s = new Stack(stackName, currentBoardId);
            int heighestOrder = 0;
            for (FullStack fullStack : fullStacks) {
                int currentStackOrder = fullStack.stack.getOrder();
                if (currentStackOrder >= heighestOrder) {
                    heighestOrder = currentStackOrder + 1;
                }
            }
            s.setOrder(heighestOrder);
            //TODO: returns liveData of the created stack (once!) as desired
            // original to do: should return ID of the created stack, so one can immediately switch to the new board after creation
            DeckLog.log("Create Stack with account id = " + account.getId());
            syncManager.createStack(account.getId(), s).observe(MainActivity.this, (stack) -> {
                binding.viewPager.setCurrentItem(stackAdapter.getCount());
            });
        });
    }

    @Override
    public void onUpdateStack(long localStackId, String stackName) {
        observeOnce(syncManager.getStack(account.getId(), localStackId), MainActivity.this, fullStack -> {
            fullStack.getStack().setTitle(stackName);
            syncManager.updateStack(fullStack);
        });
    }

    @Override
    public void onCreateBoard(String title, String color) {
        Board b = new Board();
        b.setTitle(title);
        String colorToSet = color.startsWith("#") ? color.substring(1) : color;
        b.setColor(colorToSet);
        observeOnce(syncManager.createBoard(account.getId(), b), this, board -> {
            if (board == null) {
                Snackbar.make(binding.coordinatorLayout, "Open Deck in web interface first!", Snackbar.LENGTH_LONG);
            } else {
                if (boardsList == null) {
                    boardsList = new ArrayList<>();
                }
                boardsList.add(board.getBoard());
                currentBoardId = board.getLocalId();
                buildSidenavMenu();

                EditStackDialogFragment.newInstance(NO_STACK_ID).show(getSupportFragmentManager(), addColumn);
            }
        });
    }

    @Override
    public void onUpdateBoard(FullBoard fullBoard) {
        syncManager.updateBoard(fullBoard);
    }

    protected void setCurrentAccount(@Nullable Account account) {
        if (account != null) {
            currentBoardId = sharedPreferences.getLong(sharedPreferencesLastBoardForAccount_ + this.account.getId(), NO_BOARDS);
            DeckLog.log("--- Read: shared_preference_last_board_for_account_" + account.getId() + " | " + currentBoardId);

            if (boardsLiveData != null && boardsLiveDataObserver != null) {
                boardsLiveData.removeObserver(boardsLiveDataObserver);
            }

            boardsLiveData = syncManager.getBoards(account.getId());
            boardsLiveDataObserver = (List<Board> boards) -> {
                boardsList = boards;
                buildSidenavMenu();
            };
            boardsLiveData.observe(this, boardsLiveDataObserver);
        } else {
            boardsList = null;
            buildSidenavMenu();
        }
    }

    protected void boardSelected(int itemId, Account account) {
        if (boardsList != null) {
            Board selectedBoard = boardsList.get(itemId);
            currentBoardId = selectedBoard.getLocalId();
            displayStacksForBoard(selectedBoard, account);
        } else {
            DeckLog.logError(new IllegalStateException("boardsList is null but it shouldn't be."));
        }
    }

    protected void buildSidenavMenu() {
        binding.navigationView.setItemIconTintList(null);
        Menu menu = binding.navigationView.getMenu();
        menu.clear();
        SubMenu boardsMenu = menu.addSubMenu(simpleBoards);
        if (boardsList != null) {
            int index = 0;
            for (Board board : boardsList) {
                final int currentIndex = index;
                MenuItem m = boardsMenu.add(Menu.NONE, index++, Menu.NONE, board.getTitle()).setIcon(ViewUtil.getTintedImageView(this, R.drawable.circle_grey600_36dp, "#" + board.getColor()));
                if (board.isPermissionManage()) {
                    AppCompatImageButton contextMenu = new AppCompatImageButton(this);
                    contextMenu.setBackgroundDrawable(null);
                    contextMenu.setImageDrawable(ViewUtil.getTintedImageView(this, R.drawable.ic_menu, R.color.grey600));
                    contextMenu.setOnClickListener((v) -> {
                        PopupMenu popup = new PopupMenu(MainActivity.this, contextMenu);
                        popup.getMenuInflater()
                                .inflate(R.menu.navigation_context_menu, popup.getMenu());
                        final int SHARE_BOARD_ID = -1;
                        if (board.isPermissionShare()) {
                            MenuItem shareItem = popup.getMenu().add(Menu.NONE, SHARE_BOARD_ID, 5, R.string.share_board);
                        }
                        popup.setOnMenuItemClickListener((MenuItem item) -> {
                            switch (item.getItemId()) {
                                case SHARE_BOARD_ID:
                                    AccessControlDialogFragment.newInstance(account.getId(), board.getLocalId()).show(getSupportFragmentManager(), shareBoard);
                                    break;
                                case R.id.edit_board:
                                    EditBoardDialogFragment.newInstance(account.getId(), board.getLocalId()).show(getSupportFragmentManager(), editBoard);
                                    break;
                                case R.id.archive_board:
                                    // TODO implement
                                    Snackbar.make(binding.drawerLayout, "Archiving boards is not yet supported.", Snackbar.LENGTH_LONG).show();
                                    break;
                                case R.id.delete_board:
                                    new DeleteDialogBuilder(this)
                                            .setTitle(getString(R.string.delete_something, board.getTitle()))
                                            .setMessage(R.string.delete_board_message)
                                            .setPositiveButton(R.string.simple_delete, (dialog, which) -> {
                                                if (board.getLocalId() == currentBoardId) {
                                                    if (currentIndex > 0) { // Select first board after deletion
                                                        boardSelected(0, account);
                                                    } else if (boardsList.size() > 1) { // Select second board after deletion
                                                        boardSelected(1, account);
                                                    } else { // No other board is available, open create dialog
                                                        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name_short);
                                                        EditBoardDialogFragment.newInstance().show(getSupportFragmentManager(), addBoard);
                                                    }
                                                }
                                                syncManager.deleteBoard(board);
                                                binding.drawerLayout.closeDrawer(GravityCompat.START);
                                            })
                                            .setNegativeButton(android.R.string.cancel, null)
                                            .show();
                                    break;
                            }
                            return true;
                        });
                        popup.show();
                    });
                    m.setActionView(contextMenu);
                } else if (board.isPermissionShare()) {
                    AppCompatImageButton contextMenu = new AppCompatImageButton(this);
                    contextMenu.setBackgroundDrawable(null);
                    contextMenu.setImageDrawable(ViewUtil.getTintedImageView(this, R.drawable.ic_share_grey600_18dp, R.color.grey600));
                    contextMenu.setOnClickListener((v) -> {
                        AccessControlDialogFragment.newInstance(account.getId(), board.getLocalId()).show(getSupportFragmentManager(), shareBoard);
                    });
                    m.setActionView(contextMenu);
                }
            }
            boardsMenu.add(Menu.NONE, MENU_ID_ADD_BOARD, Menu.NONE, addBoard).setIcon(R.drawable.ic_add_grey_24dp);

            if (currentBoardId == NO_BOARDS && boardsList.size() > 0) {
                Board currentBoard = boardsList.get(0);
                currentBoardId = currentBoard.getLocalId();
                displayStacksForBoard(currentBoard, this.account);
            } else {
                for (Board board : boardsList) {
                    if (currentBoardId == board.getLocalId()) {
                        displayStacksForBoard(board, this.account);
                        break;
                    }
                }
            }
        } else {
            displayStacksForBoard(null, null);
        }
        menu.add(Menu.NONE, MENU_ID_SETTINGS, Menu.NONE, simpleSettings).setIcon(R.drawable.ic_settings_grey600_24dp);
        menu.add(Menu.NONE, MENU_ID_ABOUT, Menu.NONE, about).setIcon(R.drawable.ic_info_outline_grey_24dp);
    }

    int stackPositionInAdapter = 0;

    /**
     * Displays the Stacks for the boardsList by index
     *
     * @param board Board
     */
    protected void displayStacksForBoard(@Nullable Board board, @Nullable Account account) {
        binding.toolbar.setTitle(board == null ? getString(R.string.app_name) : board.getTitle());

        currentBoardHasEditPermission = board != null && board.isPermissionEdit();
        if (currentBoardHasEditPermission) {
            binding.fab.show();
            binding.addStackButton.setVisibility(View.VISIBLE);
        } else {
            binding.fab.hide();
            binding.addStackButton.setVisibility(View.GONE);
            binding.emptyContentView.hideDescription();
        }

        if (board != null && account != null) {
            binding.stackLayout.setVisibility(View.VISIBLE);
            binding.swipeRefreshLayout.setVisibility(View.VISIBLE);
            syncManager.getStacksForBoard(account.getId(), board.getLocalId()).observe(MainActivity.this, (List<FullStack> fullStacks) -> {
                if (fullStacks == null) {
                    binding.emptyContentView.setVisibility(View.VISIBLE);
                    currentBoardHasStacks = false;
                } else {
                    binding.emptyContentView.setVisibility(View.GONE);
                    currentBoardHasStacks = true;

                    long savedStackId = sharedPreferences.getLong(sharedPreferencesLastStackForAccountAndBoard_ + account.getId() + "_" + this.currentBoardId, NO_STACKS);
                    DeckLog.log("--- Read: shared_preference_last_stack_for_account_and_board" + account.getId() + "_" + this.currentBoardId + " | " + savedStackId);
                    if (fullStacks.size() == 0) {
                        binding.emptyContentView.setVisibility(View.VISIBLE);
                        currentBoardHasStacks = false;
                    } else {
                        binding.emptyContentView.setVisibility(View.GONE);
                        currentBoardHasStacks = true;
                    }

                    stackAdapter = new StackAdapter(getSupportFragmentManager());
                    for (int i = 0; i < fullStacks.size(); i++) {
                        FullStack stack = fullStacks.get(i);
                        stackAdapter.addFragment(StackFragment.newInstance(board.getLocalId(), stack.getStack().getLocalId(), account, currentBoardHasEditPermission), stack.getStack().getTitle());
                        if (stack.getLocalId() == savedStackId) {
                            stackPositionInAdapter = i;
                        }
                    }
                    binding.viewPager.setAdapter(stackAdapter);
                    runOnUiThread(() -> {
                        new TabLayoutHelper(binding.stackLayout, binding.viewPager).setAutoAdjustTabModeEnabled(true);
                        binding.viewPager.setCurrentItem(stackPositionInAdapter);
                        binding.stackLayout.setupWithViewPager(binding.viewPager);
                    });
                }
                invalidateOptionsMenu();
            });
        } else {
            binding.stackLayout.setVisibility(View.GONE);
            binding.swipeRefreshLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (currentBoardHasEditPermission) {
            inflater.inflate(R.menu.card_list_menu, menu);
            menu.findItem(R.id.action_card_list_rename_column).setVisible(currentBoardHasStacks);
            menu.findItem(R.id.action_card_list_delete_column).setVisible(currentBoardHasStacks);
        } else {
            menu.clear();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_card_list_delete_column:
                new DeleteDialogBuilder(this)
                        .setTitle(R.string.action_card_list_delete_column)
                        .setMessage(R.string.do_you_want_to_delete_the_current_column)
                        .setPositiveButton(R.string.simple_delete, (dialog, whichButton) -> {
                            long stackId = stackAdapter.getItem(binding.viewPager.getCurrentItem()).getStackId();
                            observeOnce(syncManager.getStack(account.getId(), stackId), MainActivity.this, fullStack -> {
                                DeckLog.log("Delete stack #" + fullStack.getLocalId() + ": " + fullStack.getStack().getTitle());
                                syncManager.deleteStack(fullStack.getStack());
                            });
                        })
                        .setNegativeButton(android.R.string.cancel, null).show();
                break;
            case R.id.action_card_list_rename_column:
                long stackId = stackAdapter.getItem(binding.viewPager.getCurrentItem()).getStackId();
                observeOnce(syncManager.getStack(account.getId(), stackId), MainActivity.this, fullStack -> {
                    EditStackDialogFragment.newInstance(fullStack.getLocalId(), fullStack.getStack().getTitle())
                            .show(getSupportFragmentManager(), getString(R.string.action_card_list_rename_column));
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void showFabIfEditPermissionGranted() {
        if (currentBoardHasEditPermission) {
            binding.fab.show();
        }
    }

    @Override
    public void onScrollUp() {
        showFabIfEditPermissionGranted();
    }

    @Override
    public void onScrollDown() {
        binding.fab.hide();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AccountImporter.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainActivity.ACTIVITY_SETTINGS) {
            if (resultCode == RESULT_OK) {
                recreate();
            }
        } else if (requestCode == ImportAccountActivity.REQUEST_CODE_IMPORT_ACCOUNT) {
            if (resultCode != RESULT_OK) {
                finish();
            } else {
                accountChooserActive = false;
            }
        } else {
            try {
                AccountImporter.onActivityResult(requestCode, resultCode, data, this, this::importNewAccount);
            } catch (AccountImportCancelledException e) {
                DeckLog.info("Account import has been canceled.");
            }
        }
    }

    private void importNewAccount(SingleSignOnAccount account) {

        final WrappedLiveData<Account> accountLiveData = this.syncManager.createAccount(new Account(account.name, account.userId, account.url));
        accountLiveData.observe(this, (Account createdAccount) -> {
            if (accountLiveData.hasError()) {
                try {
                    accountLiveData.throwError();
                } catch (SQLiteConstraintException ex) {
                    Snackbar.make(binding.coordinatorLayout, accountAlreadyAdded, Snackbar.LENGTH_LONG).show();
                }
            } else {
                Account oldAccount = this.account;
                this.account = createdAccount;

                try {
                    syncManager.getServerVersion(new IResponseCallback<Capabilities>(createdAccount) {
                        @Override
                        public void onResponse(Capabilities response) {
                            if (response.getDeckVersion().compareTo(new Version(minimumServerAppMajor, minimumServerAppMinor, minimumServerAppPatch)) < 0) {
                                deckVersionTooLowSnackbar = Snackbar.make(binding.coordinatorLayout, R.string.your_deck_version_is_too_old, Snackbar.LENGTH_INDEFINITE).setAction("Learn more", v -> {
                                    new AlertDialog.Builder(MainActivity.this, Application.getAppTheme(getApplicationContext()) ? R.style.DialogDarkTheme : R.style.ThemeOverlay_AppCompat_Dialog_Alert)
                                            .setTitle(R.string.update_deck)
                                            .setMessage(R.string.deck_outdated_please_update)
                                            .setPositiveButton(R.string.simple_update, (dialog, whichButton) -> {
                                                Intent openURL = new Intent(Intent.ACTION_VIEW);
                                                openURL.setData(Uri.parse(createdAccount.getUrl() + urlFragmentUpdateDeck));
                                                startActivity(openURL);
                                            })
                                            .setNegativeButton(R.string.simple_discard, null).show();
                                });
                                deckVersionTooLowSnackbar.show();
                                syncManager.deleteAccount(createdAccount.getId());
                                this.account = oldAccount;
                            } else {
                                SyncWorker.update(getApplicationContext());
                                accountIsGettingImportedSnackbar.show();
                            }
                        }
                    });
                } catch (OfflineException e) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage(R.string.you_have_to_be_connected_to_the_internet_in_order_to_add_an_account)
                            .setPositiveButton(R.string.simple_close, null)
                            .show();
                    syncManager.deleteAccount(createdAccount.getId());
                    this.account = oldAccount;
                }
            }
        });

        SingleAccountHelper.setCurrentAccount(getApplicationContext(), account.name);
    }

    protected void showAccountPicker() {
        if (deckVersionTooLowSnackbar != null) {
            deckVersionTooLowSnackbar.dismiss();
        }

        try {
            AccountImporter.pickNewAccount(this);
        } catch (NextcloudFilesAppNotInstalledException e) {
            UiExceptionManager.showDialogForException(this, e);
            Log.w("Deck", "=============================================================");
            Log.w("Deck", "Nextcloud app is not installed. Cannot choose account");
            e.printStackTrace();
        } catch (AndroidGetAccountsPermissionNotGranted e) {
            AccountImporter.requestAndroidAccountPermissionsAndPickAccount(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (accountChooserActive) {
            //noinspection SwitchStatementWithTooFewBranches
            switch (item.getItemId()) {
                case MainActivity.MENU_ID_ADD_ACCOUNT:
                    showAccountPicker();
                    break;
                default:
                    this.account = accountsList.get(item.getItemId());
                    SingleAccountHelper.setCurrentAccount(getApplicationContext(), this.account.getName());
                    syncManager = new SyncManager(this);
                    setHeaderView();
                    accountChooserActive = false;
                    setCurrentAccount(this.account);
            }
        } else {
            switch (item.getItemId()) {
                case MainActivity.MENU_ID_ABOUT:
                    Intent aboutIntent = new Intent(getApplicationContext(), AboutActivity.class);
                    startActivityForResult(aboutIntent, MainActivity.ACTIVITY_ABOUT);
                    break;
                case MainActivity.MENU_ID_SETTINGS:
                    Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivityForResult(settingsIntent, MainActivity.ACTIVITY_SETTINGS);
                    break;
                case MainActivity.MENU_ID_ADD_BOARD:
                    EditBoardDialogFragment.newInstance().show(getSupportFragmentManager(), addBoard);
                    break;
                default:
                    boardSelected(item.getItemId(), account);
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void setHeaderView() {
        ViewUtil.addAvatar(this, binding.navigationView.getHeaderView(0).findViewById(R.id.drawer_current_account), account.getUrl(), account.getUserName(), R.mipmap.ic_launcher_round);
        ((TextView) binding.navigationView.getHeaderView(0).findViewById(R.id.drawer_username_full)).setText(account.getName());
    }

    private void buildSidenavAccountChooser() {
        Menu menu = binding.navigationView.getMenu();
        menu.clear();
        int index = 0;
        for (Account account : this.accountsList) {
            final int currentIndex = index;
            MenuItem m = menu.add(Menu.NONE, index++, Menu.NONE, account.getName()).setIcon(R.drawable.ic_person_grey600_24dp);
            AppCompatImageButton contextMenu = new AppCompatImageButton(this);
            contextMenu.setBackgroundDrawable(null);

            String uri = account.getUrl() + "/index.php/avatar/" + Uri.encode(account.getUserName()) + "/56";
            Glide.with(this)
                    .load(uri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            m.setIcon(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });

            contextMenu.setImageDrawable(ViewUtil.getTintedImageView(this, R.drawable.ic_delete_black_24dp, R.color.grey600));
            contextMenu.setOnClickListener((v) -> {
                if (currentIndex != 0) { // Select first account after deletion
                    this.account = accountsList.get(0);
                    SingleAccountHelper.setCurrentAccount(getApplicationContext(), this.account.getName());
                    syncManager = new SyncManager(this);
                    setCurrentAccount(this.account);
                    setHeaderView();
                    accountChooserActive = false;
                } else if (accountsList.size() > 1) { // Select second account after deletion
                    this.account = accountsList.get(1);
                    SingleAccountHelper.setCurrentAccount(getApplicationContext(), this.account.getName());
                    syncManager = new SyncManager(this);
                    setCurrentAccount(this.account);
                    setHeaderView();
                    accountChooserActive = false;
                } else {
                    setCurrentAccount(null);
                }

                syncManager.deleteAccount(account.getId());
                buildSidenavAccountChooser();
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            });
            m.setActionView(contextMenu);
        }
        menu.add(Menu.NONE, MainActivity.MENU_ID_ADD_ACCOUNT, Menu.NONE, addAccount).setIcon(R.drawable.ic_person_add_black_24dp);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void registerAutoSyncOnNetworkAvailable() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        if (connectivityManager != null) {
            if (networkCallback == null) {
                networkCallback = new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(@NonNull Network network) {
                        DeckLog.log("Got Network connection");
                        syncManager.synchronize(new IResponseCallback<Boolean>(account) {
                            @Override
                            public void onResponse(Boolean response) {
                                accountIsGettingImportedSnackbar.dismiss();
                                DeckLog.log("Auto-Sync after connection available successful");
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                super.onError(throwable);
                                if (throwable instanceof NextcloudHttpRequestFailedException) {
                                    ExceptionUtil.handleHttpRequestFailedException((NextcloudHttpRequestFailedException) throwable, binding.coordinatorLayout, MainActivity.this);
                                }
                            }
                        });
                    }

                    @Override
                    public void onLost(@NonNull Network network) {
                        DeckLog.log("Network lost");
                    }
                };
            }
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (IllegalArgumentException ignored) {
            }
            connectivityManager.registerNetworkCallback(builder.build(), networkCallback);
        }
    }
}