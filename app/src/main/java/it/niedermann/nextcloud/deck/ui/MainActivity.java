package it.niedermann.nextcloud.deck.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.exceptions.AccountImportCancelledException;
import com.nextcloud.android.sso.exceptions.AndroidGetAccountsPermissionNotGranted;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotInstalledException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.niedermann.android.crosstabdnd.CrossTabDragAndDrop;
import it.niedermann.android.tablayouthelper.TabLayoutHelper;
import it.niedermann.android.tablayouthelper.TabTitleGenerator;
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
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.Version;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.ui.about.AboutActivity;
import it.niedermann.nextcloud.deck.ui.archivedcards.ArchivedCardsActvitiy;
import it.niedermann.nextcloud.deck.ui.board.DeleteBoardListener;
import it.niedermann.nextcloud.deck.ui.board.EditBoardDialogFragment;
import it.niedermann.nextcloud.deck.ui.board.EditBoardListener;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;
import it.niedermann.nextcloud.deck.ui.branding.BrandedAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.ui.filter.FilterDialogFragment;
import it.niedermann.nextcloud.deck.ui.settings.SettingsActivity;
import it.niedermann.nextcloud.deck.ui.stack.DeleteStackDialogFragment;
import it.niedermann.nextcloud.deck.ui.stack.DeleteStackListener;
import it.niedermann.nextcloud.deck.ui.stack.EditStackDialogFragment;
import it.niedermann.nextcloud.deck.ui.stack.EditStackListener;
import it.niedermann.nextcloud.deck.ui.stack.OnScrollListener;
import it.niedermann.nextcloud.deck.ui.stack.StackAdapter;
import it.niedermann.nextcloud.deck.ui.stack.StackFragment;
import it.niedermann.nextcloud.deck.util.ColorUtil;
import it.niedermann.nextcloud.deck.util.DrawerMenuUtil;
import it.niedermann.nextcloud.deck.util.DrawerMenuUtil.DrawerAccountListener;
import it.niedermann.nextcloud.deck.util.ExceptionUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;

import static android.graphics.Color.parseColor;
import static androidx.lifecycle.Transformations.switchMap;
import static it.niedermann.nextcloud.deck.Application.NO_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.Application.NO_BOARD_ID;
import static it.niedermann.nextcloud.deck.Application.NO_STACK_ID;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.util.ClipboardUtil.copyToClipboard;
import static it.niedermann.nextcloud.deck.util.DrawerMenuUtil.MENU_ID_ABOUT;
import static it.niedermann.nextcloud.deck.util.DrawerMenuUtil.MENU_ID_ADD_ACCOUNT;
import static it.niedermann.nextcloud.deck.util.DrawerMenuUtil.MENU_ID_ADD_BOARD;
import static it.niedermann.nextcloud.deck.util.DrawerMenuUtil.MENU_ID_SETTINGS;
import static it.niedermann.nextcloud.deck.util.ExceptionUtil.handleHttpRequestFailedException;

public class MainActivity extends BrandedActivity implements DeleteStackListener, EditStackListener, DeleteBoardListener, EditBoardListener, OnScrollListener, OnNavigationItemSelectedListener, DrawerAccountListener {

    protected ActivityMainBinding binding;
    protected NavHeaderMainBinding headerBinding;

    private MainViewModel viewModel;

    protected static final int ACTIVITY_ABOUT = 1;
    protected static final int ACTIVITY_SETTINGS = 2;

    @NonNull
    protected List<Account> accountsList = new ArrayList<>();
    protected boolean accountChooserActive = false;
    protected SyncManager syncManager;
    protected SharedPreferences sharedPreferences;
    private StackAdapter stackAdapter;
    @NonNull
    private List<Board> boardsList = new ArrayList<>();
    private LiveData<List<Board>> boardsLiveData;
    private Observer<List<Board>> boardsLiveDataObserver;

    private boolean currentBoardHasStacks = false;
    private int currentBoardStacksCount = 0;

    private boolean firstAccountAdded = false;
    private ConnectivityManager.NetworkCallback networkCallback;

    private String accountAlreadyAdded;
    private String urlFragmentUpdateDeck;
    private String addList;
    private String addBoard;
    @Nullable
    private TabLayoutMediator mediator;
    @Nullable
    private TabLayoutHelper tabLayoutHelper;
    private boolean stackMoved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        headerBinding = NavHeaderMainBinding.bind(binding.navigationView.getHeaderView(0));
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getFilterInformation().observe(this, (info) -> invalidateOptionsMenu());

        addList = getString(R.string.add_list);
        addBoard = getString(R.string.add_board);
        accountAlreadyAdded = getString(R.string.account_already_added);
        urlFragmentUpdateDeck = getString(R.string.url_fragment_update_deck);

        setSupportActionBar(binding.toolbar);

        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navigationView.setNavigationItemSelectedListener(this);
        syncManager = new SyncManager(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        switchMap(syncManager.hasAccounts(), hasAccounts -> {
            if (hasAccounts) {
                return syncManager.readAccounts();
            } else {
                startActivityForResult(new Intent(this, ImportAccountActivity.class), ImportAccountActivity.REQUEST_CODE_IMPORT_ACCOUNT);
                return null;
            }
        }).observe(this, (List<Account> accounts) -> {
            if (accounts == null || accounts.size() == 0) {
                // Last account has been deleted.  hasAccounts LiveData will handle this, but we make sure, that branding is reset.
                Application.saveBrandColors(this, getResources().getColor(R.color.primary), Color.WHITE);
                return;
            }

            accountsList = accounts;

            long lastAccountId = Application.readCurrentAccountId(this);

            for (Account account : accountsList) {
                if (lastAccountId == account.getId() || lastAccountId == NO_ACCOUNT_ID) {
                    setCurrentAccount(account);
                    if (!firstAccountAdded) {
                        DeckLog.info("Syncing the current account on app start");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            registerAutoSyncOnNetworkAvailable();
                        } else {
                            syncManager.synchronize(new IResponseCallback<Boolean>(viewModel.getCurrentAccount()) {
                                @Override
                                public void onResponse(Boolean response) {
                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    super.onError(throwable);
                                    if (throwable instanceof NextcloudHttpRequestFailedException) {
                                        runOnUiThread(() -> handleHttpRequestFailedException((NextcloudHttpRequestFailedException) throwable, MainActivity.this.binding.coordinatorLayout, MainActivity.this));
                                    }
                                }
                            });
                        }
                        firstAccountAdded = false;
                    }
                    break;
                }
            }

            headerBinding.drawerHeaderView.setOnClickListener(v -> {
                this.accountChooserActive = !this.accountChooserActive;
                if (accountChooserActive) {
                    inflateAccountMenu();
                } else {
                    inflateBoardMenu();
                }
            });

            stackAdapter = new StackAdapter(this);
            binding.viewPager.setAdapter(stackAdapter);
            binding.viewPager.setOffscreenPageLimit(2);

            CrossTabDragAndDrop<StackFragment, CardAdapter, FullCard> dragAndDrop = new CrossTabDragAndDrop<>(getResources());
            dragAndDrop.register(binding.viewPager, binding.stackTitles, getSupportFragmentManager());
            dragAndDrop.addItemMovedByDragListener((movedCard, stackId, position) -> {
                syncManager.reorder(viewModel.getCurrentAccount().getId(), movedCard, stackId, position);
                DeckLog.info("Card \"" + movedCard.getCard().getTitle() + "\" was moved to Stack " + stackId + " on position " + position);
            });

            binding.addStackButton.setOnClickListener((v) -> {
                if (this.boardsList.size() == 0) {
                    EditBoardDialogFragment.newInstance().show(getSupportFragmentManager(), addBoard);
                } else {
                    EditStackDialogFragment.newInstance(NO_STACK_ID).show(getSupportFragmentManager(), addList);
                }
            });

            binding.fab.setOnClickListener((v) -> {
                if (this.boardsList.size() > 0) {
                    try {
                        Long stackId = stackAdapter.getItem(binding.viewPager.getCurrentItem()).getLocalId();
                        startActivity(EditActivity.createNewCardIntent(this, viewModel.getCurrentAccount(), viewModel.getCurrentBoardLocalId(), stackId));
                    } catch (IndexOutOfBoundsException e) {
                        EditStackDialogFragment.newInstance(NO_STACK_ID).show(getSupportFragmentManager(), addList);
                    }
                } else {
                    EditBoardDialogFragment.newInstance().show(getSupportFragmentManager(), addBoard);
                }
            });

            binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { /* Silence is gold */ }

                @Override
                public void onPageSelected(int position) {
                    invalidateOptionsMenu();
                    binding.viewPager.post(() -> {
                        // stackAdapter size might differ from position when an account has been deleted
                        if (stackAdapter.getItemCount() > position) {
                            Application.saveCurrentStackId(getApplicationContext(), viewModel.getCurrentAccount().getId(), viewModel.getCurrentBoardLocalId(), stackAdapter.getItem(position).getLocalId());
                        } else {
                            DeckLog.logError(new IllegalStateException("Tried to save current Stack which cannot be available (stackAdapter doesn't have this position)"));
                        }
                    });

                    showFabIfEditPermissionGranted();
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    binding.swipeRefreshLayout.setEnabled(state == ViewPager2.SCROLL_STATE_IDLE);
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
                refreshCapabilities(viewModel.getCurrentAccount());
                syncManager.synchronize(new IResponseCallback<Boolean>(viewModel.getCurrentAccount()) {
                    @Override
                    public void onResponse(Boolean response) {
                        runOnUiThread(() -> binding.swipeRefreshLayout.setRefreshing(false));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        runOnUiThread(() -> binding.swipeRefreshLayout.setRefreshing(false));
                        if (throwable instanceof NextcloudHttpRequestFailedException) {
                            runOnUiThread(() -> handleHttpRequestFailedException((NextcloudHttpRequestFailedException) throwable, binding.coordinatorLayout, MainActivity.this));
                        }
                    }
                });
            });
        });

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void applyBrand(@ColorInt int mainColor, @ColorInt int textColor) {
        applyBrandToPrimaryToolbar(mainColor, textColor, binding.toolbar);
        applyBrandToPrimaryTabLayout(mainColor, textColor, binding.stackTitles);
        applyBrandToFAB(mainColor, textColor, binding.fab);

        binding.addStackButton.setBackgroundColor(mainColor);
        binding.addStackButton.setColorFilter(textColor);

        headerBinding.drawerHeaderView.setBackgroundColor(mainColor);
        headerBinding.drawerAppTitle.setTextColor(textColor);
        headerBinding.drawerUsernameFull.setTextColor(textColor);

        final boolean isDarkTextColor = ColorUtil.isColorDark(textColor);
        if (isDarkTextColor) {
            headerBinding.drawerAppTitle.setShadowLayer(2, 0.5f, 0, Color.WHITE);
            headerBinding.drawerUsernameFull.setShadowLayer(2, 0.5f, 0, Color.WHITE);
        } else {
            headerBinding.drawerAppTitle.setShadowLayer(2, 0.5f, 0, Color.BLACK);
            headerBinding.drawerUsernameFull.setShadowLayer(2, 0.5f, 0, Color.BLACK);
        }

        final Drawable overflowDrawable = headerBinding.drawerAccountChooserToggle.getDrawable();
        if (overflowDrawable != null) {
            overflowDrawable.setColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
            headerBinding.drawerAccountChooserToggle.setImageDrawable(overflowDrawable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tabLayoutHelper != null) {
            tabLayoutHelper.release();
        }
    }

    @Override
    public void onCreateStack(String stackName) {
        observeOnce(syncManager.getStacksForBoard(viewModel.getCurrentAccount().getId(), viewModel.getCurrentBoardLocalId()), MainActivity.this, fullStacks -> {
            final Stack s = new Stack(stackName, viewModel.getCurrentBoardLocalId());
            int heighestOrder = 0;
            for (FullStack fullStack : fullStacks) {
                int currentStackOrder = fullStack.stack.getOrder();
                if (currentStackOrder >= heighestOrder) {
                    heighestOrder = currentStackOrder + 1;
                }
            }
            s.setOrder(heighestOrder);
            DeckLog.info("Create Stack with account id = " + viewModel.getCurrentAccount().getId());
            syncManager.createStack(viewModel.getCurrentAccount().getId(), s).observe(MainActivity.this, (stack) -> binding.viewPager.setCurrentItem(stackAdapter.getItemCount()));
        });
    }

    @Override
    public void onUpdateStack(long localStackId, String stackName) {
        observeOnce(syncManager.getStack(viewModel.getCurrentAccount().getId(), localStackId), MainActivity.this, fullStack -> {
            fullStack.getStack().setTitle(stackName);
            // TODO error handling
            syncManager.updateStack(fullStack);
        });
    }

    @Override
    public void onCreateBoard(String title, String color) {
        Board b = new Board(title, color.startsWith("#") ? color.substring(1) : color);
        observeOnce(syncManager.createBoard(viewModel.getCurrentAccount().getId(), b), this, board -> {
            if (board == null) {
                Snackbar.make(binding.coordinatorLayout, "Open Deck in web interface first!", Snackbar.LENGTH_LONG).show();
            } else {
                boardsList.add(board.getBoard());
                viewModel.setCurrentBoard(board.getBoard());
                inflateBoardMenu();

                EditStackDialogFragment.newInstance(NO_STACK_ID).show(getSupportFragmentManager(), addList);
            }
        });
    }

    @Override
    public void onUpdateBoard(FullBoard fullBoard) {
        syncManager.updateBoard(fullBoard);
    }

    @UiThread
    protected void setCurrentAccount(@NonNull Account account) {
        viewModel.setCurrentAccount(account);
        SingleAccountHelper.setCurrentAccount(getApplicationContext(), viewModel.getCurrentAccount().getName());
        syncManager = new SyncManager(this);

        Application.saveBrandColors(this, Color.parseColor(viewModel.getCurrentAccount().getColor()), Color.parseColor(viewModel.getCurrentAccount().getTextColor()));
        Application.saveCurrentAccountId(this, viewModel.getCurrentAccount().getId());
        if (viewModel.getCurrentAccount().isMaintenanceEnabled()) {
            refreshCapabilities(viewModel.getCurrentAccount());
        }

        final long currentBoardId = Application.readCurrentBoardId(this, viewModel.getCurrentAccount().getId());

        if (boardsLiveData != null && boardsLiveDataObserver != null) {
            boardsLiveData.removeObserver(boardsLiveDataObserver);
        }

        boardsLiveData = syncManager.getBoards(account.getId());
        boardsLiveDataObserver = (List<Board> boards) -> {
            if (boards == null) {
                throw new IllegalStateException("List<Board> boards must not be null.");
            }

            boardsList = boards;

            if (boardsList.size() > 0) {
                boolean currentBoardIdWasInList = false;
                for (int i = 0; i < boardsList.size(); i++) {
                    if (currentBoardId == boardsList.get(i).getLocalId() || currentBoardId == NO_BOARD_ID) {
                        setCurrentBoard(boardsList.get(i));
                        currentBoardIdWasInList = true;
                        break;
                    }
                }
                if (!currentBoardIdWasInList) {
                    setCurrentBoard(boardsList.get(0));
                }
            } else {
                clearCurrentBoard();
            }
            inflateBoardMenu();
        };
        boardsLiveData.observe(this, boardsLiveDataObserver);

        ViewUtil.addAvatar(headerBinding.drawerCurrentAccount, viewModel.getCurrentAccount().getUrl(), viewModel.getCurrentAccount().getUserName(), R.mipmap.ic_launcher_round);
        headerBinding.drawerUsernameFull.setText(viewModel.getCurrentAccount().getName());
        accountChooserActive = false;
        inflateAccountMenu();
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        DeckLog.verbose("Displaying maintenance mode info for " + viewModel.getCurrentAccount().getName() + ": " + viewModel.getCurrentAccount().isMaintenanceEnabled());
        binding.infoBox.setVisibility(viewModel.getCurrentAccount().isMaintenanceEnabled() ? View.VISIBLE : View.GONE);
    }

    private void refreshCapabilities(final Account account) {
        syncManager.refreshCapabilities(new IResponseCallback<Capabilities>(account) {
            @Override
            public void onResponse(Capabilities response) {
                if (!response.isMaintenanceEnabled()) {
                    @ColorInt final int mainColor = parseColor(response.getColor());
                    @ColorInt final int textColor = parseColor(response.getTextColor());
                    runOnUiThread(() -> Application.saveBrandColors(MainActivity.this, mainColor, textColor));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                DeckLog.info("Cannot refresh capabilities because device is offline.");
            }
        });
    }

    protected void clearCurrentBoard() {
        binding.toolbar.setTitle(R.string.app_name_short);
        binding.swipeRefreshLayout.setVisibility(View.GONE);
        binding.addStackButton.setVisibility(View.GONE);
        binding.emptyContentViewStacks.setVisibility(View.GONE);
        binding.emptyContentViewBoards.setVisibility(View.VISIBLE);
    }

    protected void setCurrentBoard(@NonNull Board board) {
        viewModel.setCurrentBoard(board);
        viewModel.postFilterInformation(null);

        Application.saveCurrentBoardId(this, viewModel.getCurrentAccount().getId(), viewModel.getCurrentBoardLocalId());

        binding.toolbar.setTitle(board.getTitle());

        if (viewModel.currentBoardHasEditPermission()) {
            binding.fab.show();
            binding.addStackButton.setVisibility(View.VISIBLE);
        } else {
            binding.fab.hide();
            binding.addStackButton.setVisibility(View.GONE);
            binding.emptyContentViewStacks.hideDescription();
        }

        binding.emptyContentViewBoards.setVisibility(View.GONE);
        binding.swipeRefreshLayout.setVisibility(View.VISIBLE);


        syncManager.getStacksForBoard(viewModel.getCurrentAccount().getId(), board.getLocalId()).observe(MainActivity.this, (List<FullStack> fullStacks) -> {
            if (fullStacks == null) {
                throw new IllegalStateException("Given List<FullStack> must not be null");
            }
            currentBoardStacksCount = fullStacks.size();

            if (currentBoardStacksCount == 0) {
                binding.emptyContentViewStacks.setVisibility(View.VISIBLE);
                currentBoardHasStacks = false;
            } else {
                binding.emptyContentViewStacks.setVisibility(View.GONE);
                currentBoardHasStacks = true;
            }

            int stackPositionInAdapter = 0;
            stackAdapter.setStacks(fullStacks);

            long currentStackId = Application.readCurrentStackId(this, viewModel.getCurrentAccount().getId(), viewModel.getCurrentBoardLocalId());
            for (int i = 0; i < currentBoardStacksCount; i++) {
                if (fullStacks.get(i).getLocalId() == currentStackId || currentStackId == NO_STACK_ID) {
                    stackPositionInAdapter = i;
                    break;
                }
            }
            final int stackPositionInAdapterClone = stackPositionInAdapter;
            final TabTitleGenerator tabTitleGenerator = position -> {
                if (fullStacks.size() > position) {
                    return fullStacks.get(position).getStack().getTitle();
                } else {
                    DeckLog.logError(new IllegalStateException("Could not generate tab title for position " + position + " because list size is only " + currentBoardStacksCount));
                    return "ERROR";
                }
            };
            final TabLayoutMediator newMediator = new TabLayoutMediator(binding.stackTitles, binding.viewPager, (tab, position) -> tab.setText(tabTitleGenerator.getTitle(position)));
            runOnUiThread(() -> {
                setStackMediator(newMediator);
                binding.viewPager.setCurrentItem(stackPositionInAdapterClone, false);
                if (stackMoved) { // Required to make sure that the correct tab will be selected after moving stacks
                    binding.viewPager.post(() -> binding.viewPager.setCurrentItem(stackPositionInAdapterClone, false));
                    stackMoved = false;
                }
                updateTabLayoutHelper(tabTitleGenerator);
            });
            invalidateOptionsMenu();
        });
    }

    @UiThread
    private void updateTabLayoutHelper(@NonNull TabTitleGenerator tabTitleGenerator) {
        if (this.tabLayoutHelper == null) {
            this.tabLayoutHelper = new TabLayoutHelper(binding.stackTitles, binding.viewPager, tabTitleGenerator);
        } else {
            tabLayoutHelper.setTabTitleGenerator(tabTitleGenerator);
        }
    }

    @UiThread
    private void setStackMediator(@NonNull final TabLayoutMediator newMediator) {
        if (mediator != null) {
            mediator.detach();
        }
        newMediator.attach();
        this.mediator = newMediator;
    }

    @UiThread
    private void inflateAccountMenu() {
        headerBinding.drawerAccountChooserToggle.setRotation(180);
        Menu menu = binding.navigationView.getMenu();
        menu.clear();
        DrawerMenuUtil.inflateAccounts(this, menu, this.accountsList);
    }

    @UiThread
    protected void inflateBoardMenu() {
        headerBinding.drawerAccountChooserToggle.setRotation(0);
        binding.navigationView.setItemIconTintList(null);
        Menu menu = binding.navigationView.getMenu();
        menu.clear();
        DrawerMenuUtil.inflateBoards(this, menu, viewModel.getCurrentAccount().getId(), this.boardsList);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (accountChooserActive) {
            //noinspection SwitchStatementWithTooFewBranches
            switch (item.getItemId()) {
                case MENU_ID_ADD_ACCOUNT:
                    try {
                        AccountImporter.pickNewAccount(this);
                    } catch (NextcloudFilesAppNotInstalledException e) {
                        ExceptionUtil.handleNextcloudFilesAppNotInstalledException(this, e);
                    } catch (AndroidGetAccountsPermissionNotGranted e) {
                        AccountImporter.requestAndroidAccountPermissionsAndPickAccount(this);
                    }
                    break;
                default:
                    setCurrentAccount(accountsList.get(item.getItemId()));
                    break;
            }
        } else {
            switch (item.getItemId()) {
                case MENU_ID_ABOUT:
                    startActivityForResult(AboutActivity.createIntent(this, viewModel.getCurrentAccount()), MainActivity.ACTIVITY_ABOUT);
                    break;
                case MENU_ID_SETTINGS:
                    startActivityForResult(new Intent(this, SettingsActivity.class), MainActivity.ACTIVITY_SETTINGS);
                    break;
                case MENU_ID_ADD_BOARD:
                    EditBoardDialogFragment.newInstance().show(getSupportFragmentManager(), addBoard);
                    break;
                default:
                    setCurrentBoard(boardsList.get(item.getItemId()));
                    break;
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        if (viewModel.currentBoardHasEditPermission()) {
            final int currentViewPagerItem = binding.viewPager.getCurrentItem();
            inflater.inflate(R.menu.list_menu, menu);
            menu.findItem(R.id.rename_list).setVisible(currentBoardHasStacks);
            menu.findItem(R.id.move_list_left).setVisible(currentBoardHasStacks && currentViewPagerItem > 0);
            menu.findItem(R.id.move_list_right).setVisible(currentBoardHasStacks && currentViewPagerItem < currentBoardStacksCount - 1);
            menu.findItem(R.id.delete_list).setVisible(currentBoardHasStacks);
        } else {
            menu.clear();
        }
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.archived_cards).setVisible(false);
        final FilterInformation filterInformation = viewModel.getFilterInformation().getValue();
        menu.findItem(R.id.filter).setIcon(filterInformation == null
                ? R.drawable.ic_filter_list_white_24dp
                : R.drawable.ic_filter_list_active_white_24dp);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter: {
                FilterDialogFragment.newInstance().show(getSupportFragmentManager(), EditStackDialogFragment.class.getCanonicalName());
                return true;
            }
            case R.id.archived_cards: {
                startActivity(ArchivedCardsActvitiy.createIntent(this, viewModel.getCurrentAccount(), viewModel.getCurrentBoardLocalId(), viewModel.currentBoardHasEditPermission()));
                return true;
            }
            case R.id.rename_list: {
                final long stackId = stackAdapter.getItem(binding.viewPager.getCurrentItem()).getLocalId();
                observeOnce(syncManager.getStack(viewModel.getCurrentAccount().getId(), stackId), MainActivity.this, fullStack ->
                        EditStackDialogFragment.newInstance(fullStack.getLocalId(), fullStack.getStack().getTitle())
                                .show(getSupportFragmentManager(), EditStackDialogFragment.class.getCanonicalName()));
                return true;
            }
            case R.id.move_list_left: {
                final long stackId = stackAdapter.getItem(binding.viewPager.getCurrentItem()).getLocalId();
                // TODO error handling
                final int stackLeftPosition = binding.viewPager.getCurrentItem() - 1;
                final long stackLeftId = stackAdapter.getItem(stackLeftPosition).getLocalId();
                syncManager.swapStackOrder(viewModel.getCurrentAccount().getId(), viewModel.getCurrentBoardLocalId(), new Pair<>(stackId, stackLeftId));
                stackMoved = true;
                return true;
            }
            case R.id.move_list_right: {
                final long stackId = stackAdapter.getItem(binding.viewPager.getCurrentItem()).getLocalId();
                // TODO error handling
                final int stackRightPosition = binding.viewPager.getCurrentItem() + 1;
                final long stackRightId = stackAdapter.getItem(stackRightPosition).getLocalId();
                syncManager.swapStackOrder(viewModel.getCurrentAccount().getId(), viewModel.getCurrentBoardLocalId(), new Pair<>(stackId, stackRightId));
                stackMoved = true;
                return true;
            }
            case R.id.delete_list: {
                final long stackId = stackAdapter.getItem(binding.viewPager.getCurrentItem()).getLocalId();
                observeOnce(syncManager.countCardsInStack(viewModel.getCurrentAccount().getId(), stackId), MainActivity.this, (numberOfCards) -> {
                    if (numberOfCards != null && numberOfCards > 0) {
                        DeleteStackDialogFragment.newInstance(stackId, numberOfCards).show(getSupportFragmentManager(), DeleteStackDialogFragment.class.getCanonicalName());
                    } else {
                        onStackDeleted(stackId);
                    }
                });
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void showFabIfEditPermissionGranted() {
        if (viewModel.currentBoardHasEditPermission()) {
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

        switch (requestCode) {
            case MainActivity.ACTIVITY_SETTINGS:
                if (resultCode == RESULT_OK) {
                    recreate();
                }
                break;
            case ImportAccountActivity.REQUEST_CODE_IMPORT_ACCOUNT:
                if (resultCode == RESULT_OK) {
                    firstAccountAdded = true;
                    accountChooserActive = false;
                } else {
                    finish();
                }
                break;
            default:
                try {
                    AccountImporter.onActivityResult(requestCode, resultCode, data, this, (account) -> {
                        final WrappedLiveData<Account> accountLiveData = this.syncManager.createAccount(new Account(account.name, account.userId, account.url));
                        accountLiveData.observe(this, (createdAccount) -> {
                            if (!accountLiveData.hasError()) {
                                if (createdAccount == null) {
                                    throw new IllegalStateException("Created account must not be null");
                                }

                                final SyncManager importSyncManager = new SyncManager(this, account.name);
                                importSyncManager.refreshCapabilities(new IResponseCallback<Capabilities>(createdAccount) {
                                    @Override
                                    public void onResponse(Capabilities response) {
                                        if (!response.isMaintenanceEnabled()) {
                                            if (response.getDeckVersion().isSupported(getApplicationContext())) {
                                                runOnUiThread(() -> {
                                                    syncManager = importSyncManager;
                                                    setCurrentAccount(account);

                                                    final Snackbar importSnackbar = Snackbar.make(binding.coordinatorLayout, R.string.account_is_getting_imported, Snackbar.LENGTH_INDEFINITE);
                                                    importSnackbar.show();
                                                    importSyncManager.synchronize(new IResponseCallback<Boolean>(viewModel.getCurrentAccount()) {
                                                        @Override
                                                        public void onResponse(Boolean response) {
                                                            importSnackbar.dismiss();
                                                        }

                                                        @Override
                                                        public void onError(Throwable throwable) {
                                                            super.onError(throwable);
                                                            runOnUiThread(importSnackbar::dismiss);
                                                            if (throwable instanceof NextcloudHttpRequestFailedException) {
                                                                runOnUiThread(() -> handleHttpRequestFailedException((NextcloudHttpRequestFailedException) throwable, binding.coordinatorLayout, MainActivity.this));
                                                            }
                                                        }
                                                    });
                                                });
                                            } else {
                                                DeckLog.warn("Cannot import account because server version is too low (" + response.getDeckVersion() + "). Minimum server version is currently " + Version.minimumSupported(getApplicationContext()));
                                                runOnUiThread(() -> new BrandedAlertDialogBuilder(MainActivity.this)
                                                        .setTitle(R.string.update_deck)
                                                        .setMessage(R.string.deck_outdated_please_update)
                                                        .setNegativeButton(R.string.simple_discard, null)
                                                        .setPositiveButton(R.string.simple_update, (dialog, whichButton) -> {
                                                            final Intent openURL = new Intent(Intent.ACTION_VIEW);
                                                            openURL.setData(Uri.parse(createdAccount.getUrl() + urlFragmentUpdateDeck));
                                                            startActivity(openURL);
                                                            finish();
                                                        }).show());
                                                syncManager.deleteAccount(createdAccount.getId());
                                            }
                                        } else {
                                            DeckLog.warn("Cannot import account because server version is currently in maintenance mode.");
                                            runOnUiThread(() -> new BrandedAlertDialogBuilder(MainActivity.this)
                                                    .setTitle(R.string.maintenance_mode)
                                                    .setMessage(getString(R.string.maintenance_mode_explanation, createdAccount.getUrl()))
                                                    .setPositiveButton(R.string.simple_close, null)
                                                    .show());
                                            syncManager.deleteAccount(createdAccount.getId());
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable throwable) {
                                        syncManager.deleteAccount(createdAccount.getId());
                                        Context context = MainActivity.this;
                                        if (throwable instanceof OfflineException) {
                                            DeckLog.warn("Cannot import account because device is currently offline.");
                                            runOnUiThread(() -> new BrandedAlertDialogBuilder(context)
                                                    .setTitle(R.string.you_are_currently_offline)
                                                    .setMessage(R.string.you_have_to_be_connected_to_the_internet_in_order_to_add_an_account)
                                                    .setPositiveButton(R.string.simple_close, null)
                                                    .show());
                                        } else {
                                            throwable.printStackTrace();
                                            final String debugInfos = ExceptionUtil.getDebugInfos(context, throwable);
                                            AlertDialog dialog = new BrandedAlertDialogBuilder(context)
                                                    .setTitle(R.string.server_misconfigured)
                                                    .setMessage(context.getString(R.string.server_misconfigured_explanation) + "\n\n\n" + debugInfos)
                                                    .setPositiveButton(android.R.string.copy, (a, b) -> {
                                                        copyToClipboard(context, context.getString(R.string.simple_exception), "```\n" + debugInfos + "\n```");
                                                        a.dismiss();
                                                    })
                                                    .setNeutralButton(R.string.simple_close, null)
                                                    .create();
                                            dialog.show();
                                            ((TextView) Objects.requireNonNull(dialog.findViewById(android.R.id.message))).setTypeface(Typeface.MONOSPACE);
                                        }
                                    }
                                });
                            } else {
                                try {
                                    accountLiveData.throwError();
                                } catch (SQLiteConstraintException ex) {
                                    Snackbar.make(binding.coordinatorLayout, accountAlreadyAdded, Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
                    });
                } catch (AccountImportCancelledException e) {
                    DeckLog.info("Account import has been canceled.");
                }
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
                        syncManager.synchronize(new IResponseCallback<Boolean>(viewModel.getCurrentAccount()) {
                            @Override
                            public void onResponse(Boolean response) {
                                DeckLog.log("Auto-Sync after connection available successful");
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                super.onError(throwable);
                                if (throwable instanceof NextcloudHttpRequestFailedException) {
                                    runOnUiThread(() -> handleHttpRequestFailedException((NextcloudHttpRequestFailedException) throwable, binding.coordinatorLayout, MainActivity.this));
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

    @Override
    public void onAccountChosen(@NonNull Account account) {
        setCurrentAccount(account);
    }

    @Override
    public void onAccountDeleted(@NonNull Long accountId) {
        syncManager.deleteAccount(accountId);
    }

    @Override
    public void onStackDeleted(Long stackLocalId) {
        long stackId = stackAdapter.getItem(binding.viewPager.getCurrentItem()).getLocalId();
        observeOnce(syncManager.getStack(viewModel.getCurrentAccount().getId(), stackId), MainActivity.this, fullStack -> {
            DeckLog.log("Delete stack #" + fullStack.getLocalId() + ": " + fullStack.getStack().getTitle());
            // TODO error handling
            syncManager.deleteStack(fullStack.getStack());
        });
    }

    @Override
    public void onBoardDeleted(Board board) {
        final int index = this.boardsList.indexOf(board);
        if (board.getLocalId().equals(viewModel.getCurrentBoardLocalId())) {
            if (index > 0) { // Select first board after deletion
                setCurrentBoard(this.boardsList.get(0));
            } else if (this.boardsList.size() > 1) { // Select second board after deletion
                setCurrentBoard(this.boardsList.get(1));
            } else { // No other board is available, open create dialog
                clearCurrentBoard();
                EditBoardDialogFragment.newInstance().show(getSupportFragmentManager(), addBoard);
            }
        }
        syncManager.deleteBoard(board);
        binding.drawerLayout.closeDrawer(GravityCompat.START);
    }
}