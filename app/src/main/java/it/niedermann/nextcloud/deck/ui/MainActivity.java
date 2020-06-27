package it.niedermann.nextcloud.deck.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Color;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.exceptions.AccountImportCancelledException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;

import java.net.HttpURLConnection;
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
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.Version;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.ui.about.AboutActivity;
import it.niedermann.nextcloud.deck.ui.accountswitcher.AccountSwitcherDialog;
import it.niedermann.nextcloud.deck.ui.archivedboards.ArchivedBoardsActvitiy;
import it.niedermann.nextcloud.deck.ui.archivedcards.ArchivedCardsActvitiy;
import it.niedermann.nextcloud.deck.ui.board.ArchiveBoardListener;
import it.niedermann.nextcloud.deck.ui.board.DeleteBoardListener;
import it.niedermann.nextcloud.deck.ui.board.EditBoardDialogFragment;
import it.niedermann.nextcloud.deck.ui.board.EditBoardListener;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;
import it.niedermann.nextcloud.deck.ui.branding.BrandedAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.branding.BrandedSnackbar;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.ui.filter.FilterDialogFragment;
import it.niedermann.nextcloud.deck.ui.filter.FilterViewModel;
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

import static android.graphics.Color.parseColor;
import static androidx.lifecycle.Transformations.switchMap;
import static it.niedermann.nextcloud.deck.Application.NO_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.Application.NO_BOARD_ID;
import static it.niedermann.nextcloud.deck.Application.NO_STACK_ID;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.util.DrawerMenuUtil.MENU_ID_ABOUT;
import static it.niedermann.nextcloud.deck.util.DrawerMenuUtil.MENU_ID_ADD_BOARD;
import static it.niedermann.nextcloud.deck.util.DrawerMenuUtil.MENU_ID_ARCHIVED_BOARDS;
import static it.niedermann.nextcloud.deck.util.DrawerMenuUtil.MENU_ID_SETTINGS;

public class MainActivity extends BrandedActivity implements DeleteStackListener, EditStackListener, DeleteBoardListener, EditBoardListener, ArchiveBoardListener, OnScrollListener, OnNavigationItemSelectedListener {

    protected ActivityMainBinding binding;
    protected NavHeaderMainBinding headerBinding;

    protected MainViewModel mainViewModel;
    private FilterViewModel filterViewModel;

    protected static final int ACTIVITY_ABOUT = 1;
    protected static final int ACTIVITY_SETTINGS = 2;
    public static final int ACTIVITY_MANAGE_ACCOUNTS = 4;

    @NonNull
    protected List<Account> accountsList = new ArrayList<>();
    protected SyncManager syncManager;
    protected SharedPreferences sharedPreferences;
    private StackAdapter stackAdapter;
    long lastBoardId;
    @NonNull
    private List<Board> boardsList = new ArrayList<>();
    private LiveData<List<Board>> boardsLiveData;
    private Observer<List<Board>> boardsLiveDataObserver;
    private Menu listMenu;

    private LiveData<List<FullStack>> stacksLiveData;

    private LiveData<Boolean> hasArchivedBoardsLiveData;
    private Observer<Boolean> hasArchivedBoardsLiveDataObserver;

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

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        filterViewModel = new ViewModelProvider(this).get(FilterViewModel.class);

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
                Application.saveBrandColors(this, getResources().getColor(R.color.primary));
                return;
            }

            accountsList = accounts;

            long lastAccountId = Application.readCurrentAccountId(this);

            for (Account account : accountsList) {
                if (lastAccountId == account.getId() || lastAccountId == NO_ACCOUNT_ID) {
                    mainViewModel.setCurrentAccount(account, account.getServerDeckVersionAsObject().isSupported(this));
                    if (!firstAccountAdded) {
                        DeckLog.info("Syncing the current account on app start");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            registerAutoSyncOnNetworkAvailable();
                        } else {
                            syncManager.synchronize(new IResponseCallback<Boolean>(mainViewModel.getCurrentAccount()) {
                                @Override
                                public void onResponse(Boolean response) {
                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    super.onError(throwable);
                                    showSyncFailedSnackbar(throwable);
                                }
                            });
                        }
                        firstAccountAdded = false;
                    }
                    break;
                }
            }

            mainViewModel.getCurrentAccountLiveData().observe(this, (currentAccount) -> {
                SingleAccountHelper.setCurrentAccount(getApplicationContext(), mainViewModel.getCurrentAccount().getName());
                syncManager = new SyncManager(this);

                Application.saveBrandColors(this, parseColor(mainViewModel.getCurrentAccount().getColor()));
                Application.saveCurrentAccountId(this, mainViewModel.getCurrentAccount().getId());
                if (mainViewModel.getCurrentAccount().isMaintenanceEnabled()) {
                    refreshCapabilities(mainViewModel.getCurrentAccount());
                }

                lastBoardId = Application.readCurrentBoardId(this, mainViewModel.getCurrentAccount().getId());

                if (boardsLiveData != null && boardsLiveDataObserver != null) {
                    boardsLiveData.removeObserver(boardsLiveDataObserver);
                }

                boardsLiveData = syncManager.getBoards(currentAccount.getId(), false);
                boardsLiveDataObserver = (boards) -> {
                    if (boards == null) {
                        throw new IllegalStateException("List<Board> boards must not be null.");
                    }

                    boardsList = boards;

                    if (boardsList.size() > 0) {
                        boolean currentBoardIdWasInList = false;
                        for (int i = 0; i < boardsList.size(); i++) {
                            if (lastBoardId == boardsList.get(i).getLocalId() || lastBoardId == NO_BOARD_ID) {
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

                    if (hasArchivedBoardsLiveData != null && hasArchivedBoardsLiveDataObserver != null) {
                        hasArchivedBoardsLiveData.removeObserver(hasArchivedBoardsLiveDataObserver);
                    }
                    hasArchivedBoardsLiveData = syncManager.hasArchivedBoards(currentAccount.getId());
                    hasArchivedBoardsLiveDataObserver = (hasArchivedBoards) -> {
                        mainViewModel.setCurrentAccountHasArchivedBoards(Boolean.TRUE.equals(hasArchivedBoards));
                        inflateBoardMenu();
                    };
                    hasArchivedBoardsLiveData.observe(this, hasArchivedBoardsLiveDataObserver);
                };
                boardsLiveData.observe(this, boardsLiveDataObserver);

                final Drawable placeholderAvatar = getResources().getDrawable(R.drawable.ic_baseline_account_circle_24);
                DrawableCompat.setTint(placeholderAvatar, parseColor(currentAccount.getTextColor()));
                Glide
                        .with(binding.accountSwitcher.getContext())
                        .load(currentAccount.getAvatarUrl(64))
                        .placeholder(placeholderAvatar)
                        .error(placeholderAvatar)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.accountSwitcher);

                DeckLog.verbose("Displaying maintenance mode info for " + mainViewModel.getCurrentAccount().getName() + ": " + mainViewModel.getCurrentAccount().isMaintenanceEnabled());
                binding.infoBox.setVisibility(mainViewModel.getCurrentAccount().isMaintenanceEnabled() ? View.VISIBLE : View.GONE);
                if (mainViewModel.isCurrentAccountIsSupportedVersion()) {
                    binding.infoBoxVersionNotSupported.setVisibility(View.GONE);
                } else {
                    binding.infoBoxVersionNotSupportedText.setText(getString(R.string.info_box_version_not_supported, mainViewModel.getCurrentAccount().getServerDeckVersion(), Version.minimumSupported(this).getOriginalVersion()));
                    binding.infoBoxVersionNotSupportedText.setOnClickListener((v) -> {
                        Intent openURL = new Intent(Intent.ACTION_VIEW);
                        openURL.setData(Uri.parse(mainViewModel.getCurrentAccount().getUrl() + urlFragmentUpdateDeck));
                        startActivity(openURL);
                    });
                    binding.infoBoxVersionNotSupported.setVisibility(View.VISIBLE);
                }
            });

            stackAdapter = new StackAdapter(this);
            binding.viewPager.setAdapter(stackAdapter);
            binding.viewPager.setOffscreenPageLimit(2);

            CrossTabDragAndDrop<StackFragment, CardAdapter, FullCard> dragAndDrop = new CrossTabDragAndDrop<>(getResources(), ViewCompat.getLayoutDirection(binding.getRoot()) == ViewCompat.LAYOUT_DIRECTION_LTR);
            dragAndDrop.register(binding.viewPager, binding.stackTitles, getSupportFragmentManager());
            dragAndDrop.addItemMovedByDragListener((movedCard, stackId, position) -> {
                syncManager.reorder(mainViewModel.getCurrentAccount().getId(), movedCard, stackId, position);
                DeckLog.info("Card \"" + movedCard.getCard().getTitle() + "\" was moved to Stack " + stackId + " on position " + position);
            });


            final PopupMenu listMenuPopup = new PopupMenu(this, binding.listMenuButton);
            listMenu = listMenuPopup.getMenu();
            getMenuInflater().inflate(R.menu.list_menu, listMenu);
            listMenuPopup.setOnMenuItemClickListener(this::onOptionsItemSelected);
            binding.listMenuButton.setOnClickListener((v) -> listMenuPopup.show());

            binding.fab.setOnClickListener((v) -> {
                if (this.boardsList.size() > 0) {
                    try {
                        Long stackId = stackAdapter.getItem(binding.viewPager.getCurrentItem()).getLocalId();
                        startActivity(EditActivity.createNewCardIntent(this, mainViewModel.getCurrentAccount(), mainViewModel.getCurrentBoardLocalId(), stackId));
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
                    final int currentViewPagerItem = binding.viewPager.getCurrentItem();
                    listMenu.findItem(R.id.move_list_left).setVisible(currentBoardHasStacks && currentViewPagerItem > 0);
                    listMenu.findItem(R.id.move_list_right).setVisible(currentBoardHasStacks && currentViewPagerItem < currentBoardStacksCount - 1);
                    binding.viewPager.post(() -> {
                        // stackAdapter size might differ from position when an account has been deleted
                        if (stackAdapter.getItemCount() > position) {
                            Application.saveCurrentStackId(getApplicationContext(), mainViewModel.getCurrentAccount().getId(), mainViewModel.getCurrentBoardLocalId(), stackAdapter.getItem(position).getLocalId());
                        } else {
                            DeckLog.logError(new IllegalStateException("Tried to save current Stack which cannot be available (stackAdapter doesn't have this position)"));
                        }
                    });

                    showFabIfEditPermissionGranted();
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (!binding.swipeRefreshLayout.isRefreshing()) {
                        binding.swipeRefreshLayout.setEnabled(state == ViewPager2.SCROLL_STATE_IDLE);
                    }
                }
            });
            filterViewModel.getFilterInformation().observe(this, (info) ->
                    binding.filter.setImageDrawable(getResources().getDrawable(
                            filterViewModel.getFilterInformation().getValue() == null
                                    ? R.drawable.ic_filter_list_white_24dp
                                    : R.drawable.ic_filter_list_active_white_24dp)
                    ));

            binding.filter.setOnClickListener((v) -> FilterDialogFragment.newInstance().show(getSupportFragmentManager(), EditStackDialogFragment.class.getCanonicalName()));
            binding.archivedCards.setOnClickListener((v) -> startActivity(ArchivedCardsActvitiy.createIntent(this, mainViewModel.getCurrentAccount(), mainViewModel.getCurrentBoardLocalId(), mainViewModel.currentBoardHasEditPermission())));


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
                refreshCapabilities(mainViewModel.getCurrentAccount());
                syncManager.synchronize(new IResponseCallback<Boolean>(mainViewModel.getCurrentAccount()) {
                    @Override
                    public void onResponse(Boolean response) {
                        runOnUiThread(() -> binding.swipeRefreshLayout.setRefreshing(false));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        runOnUiThread(() -> {
                            binding.swipeRefreshLayout.setRefreshing(false);
                            showSyncFailedSnackbar(throwable);
                        });
                    }
                });
            });
        });
        binding.accountSwitcher.setOnClickListener((v) -> AccountSwitcherDialog.newInstance()
                .show(getSupportFragmentManager(), AccountSwitcherDialog.class.getSimpleName()));
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void applyBrand(@ColorInt int mainColor) {
        applyBrandToPrimaryTabLayout(mainColor, binding.stackTitles);
        applyBrandToFAB(mainColor, binding.fab);


        // Is null as soon as the avatar has been set
//        @Nullable
//        Drawable accountSwitcherDrawable = binding.accountSwitcher.getDrawable();
//        if (accountSwitcherDrawable != null) {
//            DrawableCompat.setTint(accountSwitcherDrawable, textColor);
//        }
        DrawableCompat.setTint(headerBinding.logo.getDrawable(), ColorUtil.contrastRatioIsSufficient(mainColor, Color.WHITE) ? Color.WHITE : Color.BLACK);
        headerBinding.headerView.setBackgroundColor(mainColor);
        headerBinding.appName.setTextColor(ColorUtil.contrastRatioIsSufficient(mainColor, Color.WHITE) ? Color.WHITE : Color.BLACK);
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
        // TODO this outer call is only necessary to get the highest order. Move logic to SyncManager.
        observeOnce(syncManager.getStacksForBoard(mainViewModel.getCurrentAccount().getId(), mainViewModel.getCurrentBoardLocalId()), MainActivity.this, fullStacks -> {
            final Stack s = new Stack(stackName, mainViewModel.getCurrentBoardLocalId());
            int heighestOrder = 0;
            for (FullStack fullStack : fullStacks) {
                int currentStackOrder = fullStack.stack.getOrder();
                if (currentStackOrder >= heighestOrder) {
                    heighestOrder = currentStackOrder + 1;
                }
            }
            s.setOrder(heighestOrder);
            DeckLog.info("Create Stack in account " + mainViewModel.getCurrentAccount().getName() + " on board " + mainViewModel.getCurrentBoardLocalId());
            WrappedLiveData<FullStack> createLiveData = syncManager.createStack(mainViewModel.getCurrentAccount().getId(), s);
            observeOnce(createLiveData, this, (fullStack) -> {
                if (createLiveData.hasError()) {
                    final Throwable error = createLiveData.getError();
                    assert error != null;
                    BrandedSnackbar.make(binding.coordinatorLayout, Objects.requireNonNull(error.getLocalizedMessage()), Snackbar.LENGTH_LONG)
                            .setAction(R.string.simple_more, v -> ExceptionDialogFragment.newInstance(error, mainViewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()))
                            .show();
                } else {
                    binding.viewPager.setCurrentItem(stackAdapter.getItemCount());
                }
            });
        });
    }

    @Override
    public void onUpdateStack(long localStackId, String stackName) {
        observeOnce(syncManager.getStack(mainViewModel.getCurrentAccount().getId(), localStackId), MainActivity.this, fullStack -> {
            fullStack.getStack().setTitle(stackName);
            final WrappedLiveData<FullStack> archiveLiveData = syncManager.updateStack(fullStack);
            observeOnce(archiveLiveData, this, (v) -> {
                if (archiveLiveData.hasError()) {
                    ExceptionDialogFragment.newInstance(archiveLiveData.getError(), mainViewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                }
            });
        });
    }

    @Override
    public void onCreateBoard(String title, String color) {
        if (boardsLiveData == null || boardsLiveDataObserver == null) {
            throw new IllegalStateException("Cannot create board when noone observe boards yet. boardsLiveData or observer is null.");
        }
        boardsLiveData.removeObserver(boardsLiveDataObserver);
        final Board boardToCreate = new Board(title, color.startsWith("#") ? color.substring(1) : color);
        boardToCreate.setPermissionEdit(true);
        boardToCreate.setPermissionManage(true);
        observeOnce(syncManager.createBoard(mainViewModel.getCurrentAccount().getId(), boardToCreate), this, createdBoard -> {
            if (createdBoard == null) {
                BrandedSnackbar.make(binding.coordinatorLayout, "Open Deck in web interface first!", Snackbar.LENGTH_LONG)
                        // TODO implement action!
                        // .setAction(R.string.simple_open, v -> ExceptionDialogFragment.newInstance(throwable).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()))
                        .show();
            } else {
                boardsList.add(createdBoard.getBoard());
                setCurrentBoard(createdBoard.getBoard());

                inflateBoardMenu();
                EditStackDialogFragment.newInstance(NO_STACK_ID).show(getSupportFragmentManager(), addList);
            }
            boardsLiveData.observe(this, boardsLiveDataObserver);
        });
    }

    @Override
    public void onUpdateBoard(FullBoard fullBoard) {
        syncManager.updateBoard(fullBoard);
    }

    private void refreshCapabilities(final Account account) {
        syncManager.refreshCapabilities(new IResponseCallback<Capabilities>(account) {
            @Override
            public void onResponse(Capabilities response) {
                if (response.isMaintenanceEnabled()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    // If we notice after updating the capabilities, that the new version is not supported, but it was previously, recreate the activity to make sure all elements are disabled properly
                    if (mainViewModel.getCurrentAccount().getServerDeckVersionAsObject().isSupported(MainActivity.this) && !response.getDeckVersion().isSupported(MainActivity.this)) {
                        recreate();
                    }
                    @ColorInt final int mainColor = parseColor(response.getColor());
                    runOnUiThread(() -> Application.saveBrandColors(MainActivity.this, mainColor));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                if (throwable instanceof OfflineException) {
                    DeckLog.info("Cannot refresh capabilities because device is offline.");
                } else {
                    super.onError(throwable);
                }
            }
        });
    }

    protected void clearCurrentBoard() {
        binding.toolbar.setTitle(R.string.app_name_short);
        binding.swipeRefreshLayout.setVisibility(View.GONE);
        binding.listMenuButton.setVisibility(View.GONE);
        binding.emptyContentViewStacks.setVisibility(View.GONE);
        binding.emptyContentViewBoards.setVisibility(View.VISIBLE);
    }

    protected void setCurrentBoard(@NonNull Board board) {
        if (stacksLiveData != null) {
            stacksLiveData.removeObservers(this);
        }
        mainViewModel.setCurrentBoard(board);
        filterViewModel.clearFilterInformation();

        lastBoardId = board.getLocalId();
        Application.saveCurrentBoardId(this, mainViewModel.getCurrentAccount().getId(), mainViewModel.getCurrentBoardLocalId());

        binding.toolbar.setTitle(board.getTitle());

        if (mainViewModel.currentBoardHasEditPermission()) {
            binding.fab.show();
            binding.listMenuButton.setVisibility(View.VISIBLE);
        } else {
            binding.fab.hide();
            binding.listMenuButton.setVisibility(View.GONE);
            binding.emptyContentViewStacks.hideDescription();
        }

        binding.emptyContentViewBoards.setVisibility(View.GONE);
        binding.swipeRefreshLayout.setVisibility(View.VISIBLE);

        stacksLiveData = syncManager.getStacksForBoard(mainViewModel.getCurrentAccount().getId(), board.getLocalId());
        stacksLiveData.observe(this, (List<FullStack> fullStacks) -> {
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
            listMenu.findItem(R.id.archive_cards).setVisible(currentBoardHasStacks);

            int stackPositionInAdapter = 0;
            stackAdapter.setStacks(fullStacks);

            long currentStackId = Application.readCurrentStackId(this, mainViewModel.getCurrentAccount().getId(), mainViewModel.getCurrentBoardLocalId());
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

            listMenu.findItem(R.id.rename_list).setVisible(currentBoardHasStacks);
            listMenu.findItem(R.id.delete_list).setVisible(currentBoardHasStacks);
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
    protected void inflateBoardMenu() {
        binding.navigationView.setItemIconTintList(null);
        Menu menu = binding.navigationView.getMenu();
        menu.clear();
        DrawerMenuUtil.inflateBoards(this, menu, this.boardsList, mainViewModel.currentAccountHasArchivedBoards(), mainViewModel.getCurrentAccount().getServerDeckVersionAsObject().isSupported(this));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ID_ABOUT:
                startActivityForResult(AboutActivity.createIntent(this, mainViewModel.getCurrentAccount()), MainActivity.ACTIVITY_ABOUT);
                break;
            case MENU_ID_SETTINGS:
                startActivityForResult(new Intent(this, SettingsActivity.class), MainActivity.ACTIVITY_SETTINGS);
                break;
            case MENU_ID_ADD_BOARD:
                EditBoardDialogFragment.newInstance().show(getSupportFragmentManager(), addBoard);
                break;
            case MENU_ID_ARCHIVED_BOARDS:
                startActivity(ArchivedBoardsActvitiy.createIntent(MainActivity.this, mainViewModel.getCurrentAccount()));
                break;
            default:
                setCurrentBoard(boardsList.get(item.getItemId()));
                break;
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.archive_cards: {
                final FullStack fullStack = stackAdapter.getItem(binding.viewPager.getCurrentItem());
                final long stackLocalId = fullStack.getLocalId();
                observeOnce(syncManager.countCardsInStack(mainViewModel.getCurrentAccount().getId(), stackLocalId), MainActivity.this, (numberOfCards) -> {
                    new BrandedAlertDialogBuilder(this)
                            .setTitle(R.string.archive_cards)
                            .setMessage(getString(R.string.do_you_want_to_archive_all_cards_of_the_list, fullStack.getStack().getTitle()))
                            .setPositiveButton(R.string.simple_archive, (dialog, whichButton) -> {
                                final WrappedLiveData<Void> archiveStackLiveData = syncManager.archiveCardsInStack(mainViewModel.getCurrentAccount().getId(), stackLocalId);
                                observeOnce(archiveStackLiveData, this, (result) -> {
                                    if (archiveStackLiveData.hasError()) {
                                        ExceptionDialogFragment.newInstance(archiveStackLiveData.getError(), mainViewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                                    }
                                });
                            })
                            .setNeutralButton(android.R.string.cancel, null)
                            .create()
                            .show();
                });
                return true;
            }
            case R.id.add_list: {
                EditStackDialogFragment.newInstance(NO_STACK_ID).show(getSupportFragmentManager(), addList);
                return true;
            }
            case R.id.rename_list: {
                final long stackId = stackAdapter.getItem(binding.viewPager.getCurrentItem()).getLocalId();
                observeOnce(syncManager.getStack(mainViewModel.getCurrentAccount().getId(), stackId), MainActivity.this, fullStack ->
                        EditStackDialogFragment.newInstance(fullStack.getLocalId(), fullStack.getStack().getTitle())
                                .show(getSupportFragmentManager(), EditStackDialogFragment.class.getCanonicalName()));
                return true;
            }
            case R.id.move_list_left: {
                final long stackId = stackAdapter.getItem(binding.viewPager.getCurrentItem()).getLocalId();
                // TODO error handling
                final int stackLeftPosition = binding.viewPager.getCurrentItem() - 1;
                final long stackLeftId = stackAdapter.getItem(stackLeftPosition).getLocalId();
                syncManager.swapStackOrder(mainViewModel.getCurrentAccount().getId(), mainViewModel.getCurrentBoardLocalId(), new Pair<>(stackId, stackLeftId));
                stackMoved = true;
                return true;
            }
            case R.id.move_list_right: {
                final long stackId = stackAdapter.getItem(binding.viewPager.getCurrentItem()).getLocalId();
                // TODO error handling
                final int stackRightPosition = binding.viewPager.getCurrentItem() + 1;
                final long stackRightId = stackAdapter.getItem(stackRightPosition).getLocalId();
                syncManager.swapStackOrder(mainViewModel.getCurrentAccount().getId(), mainViewModel.getCurrentBoardLocalId(), new Pair<>(stackId, stackRightId));
                stackMoved = true;
                return true;
            }
            case R.id.delete_list: {
                final long stackId = stackAdapter.getItem(binding.viewPager.getCurrentItem()).getLocalId();
                observeOnce(syncManager.countCardsInStack(mainViewModel.getCurrentAccount().getId(), stackId), MainActivity.this, (numberOfCards) -> {
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
        if (mainViewModel.currentBoardHasEditPermission()) {
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
                                                    mainViewModel.setCurrentAccount(account, account.getServerDeckVersionAsObject().isSupported(MainActivity.this));

                                                    final Snackbar importSnackbar = BrandedSnackbar.make(binding.coordinatorLayout, R.string.account_is_getting_imported, Snackbar.LENGTH_INDEFINITE);
                                                    importSnackbar.show();
                                                    importSyncManager.synchronize(new IResponseCallback<Boolean>(mainViewModel.getCurrentAccount()) {
                                                        @Override
                                                        public void onResponse(Boolean response) {
                                                            importSnackbar.dismiss();
                                                        }

                                                        @Override
                                                        public void onError(Throwable throwable) {
                                                            super.onError(throwable);
                                                            runOnUiThread(() -> {
                                                                importSnackbar.dismiss();
                                                                runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, createdAccount).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
                                                            });
                                                        }
                                                    });
                                                });
                                            } else {
                                                DeckLog.warn("Cannot import account because server version is too low (" + response.getDeckVersion() + "). Minimum server version is currently " + Version.minimumSupported(getApplicationContext()));
                                                runOnUiThread(() -> new BrandedAlertDialogBuilder(MainActivity.this)
                                                        .setTitle(R.string.update_deck)
                                                        .setMessage(getString(R.string.deck_outdated_please_update, response.getDeckVersion().getOriginalVersion()))
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
                                        super.onError(throwable);
                                        syncManager.deleteAccount(createdAccount.getId());
                                        if (throwable instanceof OfflineException) {
                                            DeckLog.warn("Cannot import account because device is currently offline.");
                                            runOnUiThread(() -> new BrandedAlertDialogBuilder(MainActivity.this)
                                                    .setTitle(R.string.you_are_currently_offline)
                                                    .setMessage(R.string.you_have_to_be_connected_to_the_internet_in_order_to_add_an_account)
                                                    .setPositiveButton(R.string.simple_close, null)
                                                    .show());
                                        } else {
                                            ExceptionDialogFragment.newInstance(throwable, createdAccount).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                                        }
                                    }
                                });
                            } else {
                                final Throwable error = accountLiveData.getError();
                                if (error instanceof SQLiteConstraintException) {
                                    DeckLog.warn("Account already added");
                                    BrandedSnackbar.make(binding.coordinatorLayout, accountAlreadyAdded, Snackbar.LENGTH_LONG).show();
                                } else {
                                    ExceptionDialogFragment.newInstance(error, createdAccount).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
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
                        syncManager.synchronize(new IResponseCallback<Boolean>(mainViewModel.getCurrentAccount()) {
                            @Override
                            public void onResponse(Boolean response) {
                                DeckLog.log("Auto-Sync after connection available successful");
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                super.onError(throwable);
                                showSyncFailedSnackbar(throwable);
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
    public void onStackDeleted(Long stackLocalId) {
        long stackId = stackAdapter.getItem(binding.viewPager.getCurrentItem()).getLocalId();
        final WrappedLiveData<Void> deleteStackLiveData = syncManager.deleteStack(mainViewModel.getCurrentAccount().getId(), stackId, mainViewModel.getCurrentBoardLocalId());
        observeOnce(deleteStackLiveData, this, (v) -> {
            if (deleteStackLiveData.hasError()) {
                ExceptionDialogFragment.newInstance(deleteStackLiveData.getError(), mainViewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
            }
        });
    }

    @Override
    public void onBoardDeleted(Board board) {
        final int index = this.boardsList.indexOf(board);
        if (board.getLocalId().equals(mainViewModel.getCurrentBoardLocalId())) {
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


    /**
     * Displays a snackbar for an exception of a failed sync, but only if the cause wasn't maintenance mode (this should be handled by a TextView instead of a snackbar).
     *
     * @param throwable the cause of the failed sync
     */
    private void showSyncFailedSnackbar(@NonNull Throwable throwable) {
        if (!(throwable instanceof NextcloudHttpRequestFailedException) || ((NextcloudHttpRequestFailedException) throwable).getStatusCode() != HttpURLConnection.HTTP_UNAVAILABLE) {
            runOnUiThread(() -> BrandedSnackbar.make(binding.coordinatorLayout, R.string.synchronization_failed, Snackbar.LENGTH_LONG)
                    .setAction(R.string.simple_more, v -> ExceptionDialogFragment.newInstance(throwable, mainViewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()))
                    .show());
        }
    }

    @Override
    public void onArchive(@NonNull Board board) {
        syncManager.archiveBoard(board);
    }
}