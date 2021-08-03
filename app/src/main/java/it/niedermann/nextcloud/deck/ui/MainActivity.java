package it.niedermann.nextcloud.deck.ui;

import static androidx.lifecycle.Transformations.switchMap;
import static it.niedermann.nextcloud.deck.DeckApplication.NO_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.DeckApplication.NO_BOARD_ID;
import static it.niedermann.nextcloud.deck.DeckApplication.NO_STACK_ID;
import static it.niedermann.nextcloud.deck.DeckApplication.readCurrentAccountId;
import static it.niedermann.nextcloud.deck.DeckApplication.readCurrentBoardId;
import static it.niedermann.nextcloud.deck.DeckApplication.readCurrentStackId;
import static it.niedermann.nextcloud.deck.DeckApplication.saveCurrentAccount;
import static it.niedermann.nextcloud.deck.DeckApplication.saveCurrentBoardId;
import static it.niedermann.nextcloud.deck.DeckApplication.saveCurrentStackId;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.applyBrandToFAB;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.applyBrandToPrimaryTabLayout;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.clearBrandColors;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.getSecondaryForegroundColorDependingOnTheme;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.saveBrandColors;
import static it.niedermann.nextcloud.deck.util.DeckColorUtil.contrastRatioIsSufficient;
import static it.niedermann.nextcloud.deck.util.DeckColorUtil.contrastRatioIsSufficientBigAreas;
import static it.niedermann.nextcloud.deck.util.DrawerMenuUtil.MENU_ID_ABOUT;
import static it.niedermann.nextcloud.deck.util.DrawerMenuUtil.MENU_ID_ADD_BOARD;
import static it.niedermann.nextcloud.deck.util.DrawerMenuUtil.MENU_ID_ARCHIVED_BOARDS;
import static it.niedermann.nextcloud.deck.util.DrawerMenuUtil.MENU_ID_SETTINGS;
import static it.niedermann.nextcloud.deck.util.DrawerMenuUtil.MENU_ID_UPCOMING_CARDS;

import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;

import androidx.annotation.AnyThread;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.exceptions.AccountImportCancelledException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;
import com.nextcloud.android.sso.exceptions.UnknownErrorException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import it.niedermann.android.crosstabdnd.CrossTabDragAndDrop;
import it.niedermann.android.tablayouthelper.TabLayoutHelper;
import it.niedermann.android.tablayouthelper.TabTitleGenerator;
import it.niedermann.nextcloud.deck.DeckApplication;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.api.ResponseCallback;
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
import it.niedermann.nextcloud.deck.ui.about.AboutActivity;
import it.niedermann.nextcloud.deck.ui.accountswitcher.AccountSwitcherDialog;
import it.niedermann.nextcloud.deck.ui.archivedboards.ArchivedBoardsActvitiy;
import it.niedermann.nextcloud.deck.ui.board.ArchiveBoardListener;
import it.niedermann.nextcloud.deck.ui.board.DeleteBoardListener;
import it.niedermann.nextcloud.deck.ui.board.EditBoardDialogFragment;
import it.niedermann.nextcloud.deck.ui.board.EditBoardListener;
import it.niedermann.nextcloud.deck.ui.branding.BrandedSnackbar;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.card.NewCardDialog;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.ui.filter.FilterDialogFragment;
import it.niedermann.nextcloud.deck.ui.filter.FilterViewModel;
import it.niedermann.nextcloud.deck.ui.pickstack.PickStackViewModel;
import it.niedermann.nextcloud.deck.ui.settings.SettingsActivity;
import it.niedermann.nextcloud.deck.ui.stack.DeleteStackDialogFragment;
import it.niedermann.nextcloud.deck.ui.stack.DeleteStackListener;
import it.niedermann.nextcloud.deck.ui.stack.EditStackDialogFragment;
import it.niedermann.nextcloud.deck.ui.stack.EditStackListener;
import it.niedermann.nextcloud.deck.ui.stack.OnScrollListener;
import it.niedermann.nextcloud.deck.ui.stack.StackAdapter;
import it.niedermann.nextcloud.deck.ui.stack.StackFragment;
import it.niedermann.nextcloud.deck.ui.upcomingcards.UpcomingCardsActivity;
import it.niedermann.nextcloud.deck.util.CustomAppGlideModule;
import it.niedermann.nextcloud.deck.util.DrawerMenuUtil;

public class MainActivity extends AppCompatActivity implements DeleteStackListener, EditStackListener, DeleteBoardListener, EditBoardListener, ArchiveBoardListener, OnScrollListener, OnNavigationItemSelectedListener {

    protected ActivityMainBinding binding;
    protected NavHeaderMainBinding headerBinding;

    protected MainViewModel mainViewModel;
    private FilterViewModel filterViewModel;
    private PickStackViewModel pickStackViewModel;

    protected static final int ACTIVITY_SETTINGS = 2;

    @ColorInt
    private int colorAccent;
    protected SharedPreferences sharedPreferences;
    private StackAdapter stackAdapter;
    long lastBoardId;
    @NonNull
    private List<Board> boardsList = new ArrayList<>();
    private LiveData<List<Board>> boardsLiveData;
    private Observer<List<Board>> boardsLiveDataObserver;
    private Menu listMenu;

    private LiveData<List<Stack>> stacksLiveData;

    private LiveData<Boolean> hasArchivedBoardsLiveData;
    private Observer<Boolean> hasArchivedBoardsLiveDataObserver;

    private boolean currentBoardHasStacks = false;
    private int currentBoardStacksCount = 0;

    private boolean firstAccountAdded = false;
    private ConnectivityManager.NetworkCallback networkCallback;

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

        setTheme(R.style.AppTheme);

        final var typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        colorAccent = typedValue.data;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        headerBinding = NavHeaderMainBinding.bind(binding.navigationView.getHeaderView(0));
        setContentView(binding.getRoot());

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        filterViewModel = new ViewModelProvider(this).get(FilterViewModel.class);
        pickStackViewModel = new ViewModelProvider(this).get(PickStackViewModel.class);

        addList = getString(R.string.add_list);
        addBoard = getString(R.string.add_board);

        setSupportActionBar(binding.toolbar);

        final var toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navigationView.setNavigationItemSelectedListener(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        DeckApplication.readCurrentAccountColor().observe(this, this::applyAccountBranding);
        DeckApplication.readCurrentBoardColor().observe(this, this::applyBoardBranding);

        binding.filterText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterViewModel.setFilterText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mainViewModel.isDebugModeEnabled().observe(this, (enabled) -> headerBinding.copyDebugLogs.setVisibility(enabled ? View.VISIBLE : View.GONE));
        headerBinding.copyDebugLogs.setOnClickListener((v) -> {
            try {
                DeckLog.shareLogAsFile(this);
            } catch (Exception e) {
                ExceptionDialogFragment.newInstance(e, mainViewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
            }
        });
        switchMap(mainViewModel.hasAccounts(), hasAccounts -> {
            if (hasAccounts) {
                return mainViewModel.readAccounts();
            } else {
                startActivityForResult(ImportAccountActivity.createIntent(this), ImportAccountActivity.REQUEST_CODE_IMPORT_ACCOUNT);
                return null;
            }
        }).observe(this, accounts -> {
            if (accounts == null || accounts.size() == 0) {
                // Last account has been deleted. hasAccounts LiveData will handle this, but we make sure, that branding is reset.
                saveBrandColors(this, ContextCompat.getColor(this, R.color.defaultBrand));
                return;
            }

            final var lastAccountId = readCurrentAccountId(this);

            for (var account : accounts) {
                if (lastAccountId == account.getId() || lastAccountId == NO_ACCOUNT_ID) {
                    mainViewModel.setCurrentAccount(account);
                    if (!firstAccountAdded) {
                        DeckLog.info("Syncing the current account on app start");
                        registerAutoSyncOnNetworkAvailable();
                        firstAccountAdded = false;
                    }
                    break;
                }
            }

            mainViewModel.getCurrentAccountLiveData().removeObservers(this);
            mainViewModel.getCurrentAccountLiveData().observe(this, (currentAccount) -> {
                SingleAccountHelper.setCurrentAccount(getApplicationContext(), mainViewModel.getCurrentAccount().getName());
                mainViewModel.recreateSyncManager();

                saveCurrentAccount(this, mainViewModel.getCurrentAccount());
                if (mainViewModel.getCurrentAccount().isMaintenanceEnabled()) {
                    refreshCapabilities(mainViewModel.getCurrentAccount(), null);
                }

                lastBoardId = readCurrentBoardId(this, mainViewModel.getCurrentAccount().getId());

                if (boardsLiveData != null && boardsLiveDataObserver != null) {
                    boardsLiveData.removeObserver(boardsLiveDataObserver);
                }

                boardsLiveData = mainViewModel.getBoards(currentAccount.getId(), false);
                boardsLiveDataObserver = (boards) -> {
                    if (boards == null) {
                        throw new IllegalStateException("List<Board> boards must not be null.");
                    }

                    boardsList = boards;
                    Board currentBoard = null;

                    if (boardsList.size() > 0) {
                        boolean currentBoardIdWasInList = false;
                        for (int i = 0; i < boardsList.size(); i++) {
                            if (lastBoardId == boardsList.get(i).getLocalId() || lastBoardId == NO_BOARD_ID) {
                                currentBoard = boardsList.get(i);
                                setCurrentBoard(currentBoard);
                                currentBoardIdWasInList = true;
                                break;
                            }
                        }
                        if (!currentBoardIdWasInList) {
                            currentBoard = boardsList.get(0);
                            setCurrentBoard(currentBoard);
                        }

                        binding.filter.setOnClickListener((v) -> FilterDialogFragment.newInstance().show(getSupportFragmentManager(), EditStackDialogFragment.class.getCanonicalName()));
                    } else {
                        clearBrandColors(this);
                        clearCurrentBoard();

                        binding.filter.setOnClickListener(null);
                    }

                    final var finalCurrentBoard = currentBoard;
                    if (hasArchivedBoardsLiveData != null && hasArchivedBoardsLiveDataObserver != null) {
                        hasArchivedBoardsLiveData.removeObserver(hasArchivedBoardsLiveDataObserver);
                    }
                    hasArchivedBoardsLiveData = mainViewModel.hasArchivedBoards(currentAccount.getId());
                    hasArchivedBoardsLiveDataObserver = (hasArchivedBoards) -> {
                        mainViewModel.setCurrentAccountHasArchivedBoards(Boolean.TRUE.equals(hasArchivedBoards));
                        inflateBoardMenu(finalCurrentBoard);
                    };
                    hasArchivedBoardsLiveData.observe(this, hasArchivedBoardsLiveDataObserver);
                };
                boardsLiveData.observe(this, boardsLiveDataObserver);

                Glide
                        .with(binding.accountSwitcher.getContext())
                        .load(currentAccount.getAvatarUrl(binding.accountSwitcher.getWidth()))
                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                        .error(R.drawable.ic_baseline_account_circle_24)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.accountSwitcher);

                DeckLog.verbose("Displaying maintenance mode info for", mainViewModel.getCurrentAccount().getName() + ":", mainViewModel.getCurrentAccount().isMaintenanceEnabled());
                binding.infoBox.setVisibility(mainViewModel.getCurrentAccount().isMaintenanceEnabled() ? View.VISIBLE : View.GONE);
                if (mainViewModel.isCurrentAccountIsSupportedVersion()) {
                    binding.infoBoxVersionNotSupported.setVisibility(View.GONE);
                } else {
                    binding.infoBoxVersionNotSupported.setText(getString(R.string.info_box_version_not_supported, mainViewModel.getCurrentAccount().getServerDeckVersion(), Version.minimumSupported().getOriginalVersion()));
                    binding.infoBoxVersionNotSupported.setOnClickListener((v) -> startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mainViewModel.getCurrentAccount().getUrl() + getString(R.string.url_fragment_update_deck)))));
                    binding.infoBoxVersionNotSupported.setVisibility(View.VISIBLE);
                }
            });

            stackAdapter = new StackAdapter(this);
            binding.viewPager.setAdapter(stackAdapter);
            binding.viewPager.setOffscreenPageLimit(2);

            final var dragAndDrop = new CrossTabDragAndDrop<StackFragment, CardAdapter, FullCard>(getResources(), ViewCompat.getLayoutDirection(binding.getRoot()) == ViewCompat.LAYOUT_DIRECTION_LTR);
            dragAndDrop.register(binding.viewPager, binding.stackTitles, getSupportFragmentManager());
            dragAndDrop.addItemMovedByDragListener((movedCard, stackId, position) -> {
                mainViewModel.reorder(mainViewModel.getCurrentAccount().getId(), movedCard, stackId, position);
                DeckLog.info("Card", movedCard.getCard().getTitle(), "was moved to Stack", stackId, "on position", position);
            });

            final var listMenuPopup = new PopupMenu(this, binding.listMenuButton);
            listMenu = listMenuPopup.getMenu();
            getMenuInflater().inflate(R.menu.list_menu, listMenu);
            listMenuPopup.setOnMenuItemClickListener(this::onOptionsItemSelected);
            binding.listMenuButton.setOnClickListener((v) -> listMenuPopup.show());

            binding.fab.setOnClickListener((v) -> {
                if (this.boardsList.size() > 0) {
                    try {
                        NewCardDialog.newInstance(
                                mainViewModel.getCurrentAccount(),
                                mainViewModel.getCurrentBoardLocalId(),
                                stackAdapter.getItem(binding.viewPager.getCurrentItem()).getLocalId(),
                                mainViewModel.getCurrentBoardColor()
                        ).show(getSupportFragmentManager(), NewCardDialog.class.getSimpleName());
                    } catch (IndexOutOfBoundsException e) {
                        EditStackDialogFragment.newInstance().show(getSupportFragmentManager(), addList);
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
                            saveCurrentStackId(getApplicationContext(), mainViewModel.getCurrentAccount().getId(), mainViewModel.getCurrentBoardLocalId(), stackAdapter.getItem(position).getLocalId());
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
            filterViewModel.hasActiveFilter().observe(this, (hasActiveFilter) -> binding.filterIndicator.setVisibility(hasActiveFilter ? View.VISIBLE : View.GONE));
//            binding.archivedCards.setOnClickListener((v) -> startActivity(ArchivedCardsActivity.createIntent(this, mainViewModel.getCurrentAccount(), mainViewModel.getCurrentBoardLocalId(), mainViewModel.currentBoardHasEditPermission())));
            binding.enableSearch.setOnClickListener((v) -> showFilterTextToolbar());
            binding.toolbar.setOnClickListener((v) -> showFilterTextToolbar());


            binding.swipeRefreshLayout.setOnRefreshListener(() -> {
                DeckLog.info("Triggered manual refresh");

                CustomAppGlideModule.clearCache(this);

                DeckLog.verbose("Trigger refresh capabilities for", mainViewModel.getCurrentAccount().getName());
                refreshCapabilities(mainViewModel.getCurrentAccount(), () -> {
                    DeckLog.verbose("Trigger synchronization for", mainViewModel.getCurrentAccount().getName());
                    mainViewModel.synchronize(new ResponseCallback<>(mainViewModel.getCurrentAccount()) {
                        @Override
                        public void onResponse(Boolean response) {
                            DeckLog.info("End of synchronization for " + mainViewModel.getCurrentAccount().getName() + " → Stop spinner.");
                            runOnUiThread(() -> binding.swipeRefreshLayout.setRefreshing(false));
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            super.onError(throwable);
                            DeckLog.info("End of synchronization for " + mainViewModel.getCurrentAccount().getName() + " → Stop spinner.");
                            showSyncFailedSnackbar(throwable);
                            runOnUiThread(() -> binding.swipeRefreshLayout.setRefreshing(false));
                        }
                    });
                });
            });
        });
        binding.accountSwitcher.setOnClickListener((v) -> AccountSwitcherDialog.newInstance().show(getSupportFragmentManager(), AccountSwitcherDialog.class.getSimpleName()));
    }

    private void applyBoardBranding(@ColorInt int mainColor) {
        applyBrandToPrimaryTabLayout(mainColor, binding.stackTitles);
        applyBrandToFAB(mainColor, binding.fab);
        // TODO We assume, that the background of the spinner is always white
        binding.swipeRefreshLayout.setColorSchemeColors(contrastRatioIsSufficient(Color.WHITE, mainColor) ? mainColor : DeckApplication.isDarkTheme(this) ? Color.DKGRAY : colorAccent);
        DrawableCompat.setTint(binding.filterIndicator.getDrawable(), getSecondaryForegroundColorDependingOnTheme(this, mainColor));
    }

    private void applyAccountBranding(@ColorInt int accountColor) {
        headerBinding.headerView.setBackgroundColor(accountColor);
        @ColorInt final int headerTextColor = contrastRatioIsSufficientBigAreas(accountColor, Color.WHITE) ? Color.WHITE : Color.BLACK;
        headerBinding.appName.setTextColor(headerTextColor);
        DrawableCompat.setTint(headerBinding.logo.getDrawable(), headerTextColor);
        DrawableCompat.setTint(headerBinding.copyDebugLogs.getDrawable(), headerTextColor);
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
        DeckLog.info("Create Stack in account", mainViewModel.getCurrentAccount().getName(), "on board", mainViewModel.getCurrentBoardLocalId());
        mainViewModel.createStack(mainViewModel.getCurrentAccount().getId(), stackName, mainViewModel.getCurrentBoardLocalId(), new IResponseCallback<>() {
            @Override
            public void onResponse(FullStack response) {
                DeckApplication.saveCurrentStackId(MainActivity.this, mainViewModel.getCurrentAccount().getId(), mainViewModel.getCurrentBoardLocalId(), response.getLocalId());
                binding.viewPager.post(() -> {
                    try {
                        binding.viewPager.setCurrentItem(stackAdapter.getPosition(response.getLocalId()));
                    } catch (NoSuchElementException e) {
                        DeckLog.logError(e);
                    }
                });
            }

            @Override
            public void onError(Throwable error) {
                IResponseCallback.super.onError(error);
                runOnUiThread(() -> BrandedSnackbar.make(binding.coordinatorLayout, Objects.requireNonNull(error.getLocalizedMessage()), Snackbar.LENGTH_LONG)
                        .setAction(R.string.simple_more, v -> ExceptionDialogFragment.newInstance(error, mainViewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()))
                        .show());
            }
        });
    }

    @Override
    public void onUpdateStack(long localStackId, String stackName) {
        mainViewModel.updateStackTitle(localStackId, stackName, new IResponseCallback<>() {
            @Override
            public void onResponse(FullStack response) {
                DeckLog.info("Successfully updated", Stack.class.getSimpleName(), "to", stackName);
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, mainViewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
            }
        });
    }

    @Override
    public void onCreateBoard(String title, @ColorInt int color) {
        if (boardsLiveData == null || boardsLiveDataObserver == null) {
            throw new IllegalStateException("Cannot create board when no one observe boards yet. boardsLiveData or observer is null.");
        }
        boardsLiveData.removeObserver(boardsLiveDataObserver);
        final var boardToCreate = new Board(title, color);
        boardToCreate.setPermissionEdit(true);
        boardToCreate.setPermissionManage(true);

        mainViewModel.createBoard(mainViewModel.getCurrentAccount().getId(), boardToCreate, new IResponseCallback<>() {
            @Override
            public void onResponse(FullBoard response) {
                runOnUiThread(() -> {
                    if (response != null) {
                        boardsList.add(response.getBoard());
                        setCurrentBoard(response.getBoard());
                        inflateBoardMenu(response.getBoard());
                        EditStackDialogFragment.newInstance().show(getSupportFragmentManager(), addList);
                    }
                    boardsLiveData.observe(MainActivity.this, boardsLiveDataObserver);
                });
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                runOnUiThread(() -> BrandedSnackbar.make(binding.coordinatorLayout, R.string.synchronization_failed, Snackbar.LENGTH_LONG)
                        .setAction(R.string.simple_more, v -> ExceptionDialogFragment.newInstance(throwable, mainViewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()))
                        .show());
            }
        });
    }

    @Override
    public void onUpdateBoard(FullBoard fullBoard) {
        mainViewModel.updateBoard(fullBoard, new IResponseCallback<>() {
            @Override
            public void onResponse(FullBoard response) {
                DeckLog.info("Successfully updated board", fullBoard.getBoard().getTitle());
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, mainViewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
            }
        });
    }

    private void refreshCapabilities(final Account account, @Nullable Runnable runAfter) {
        DeckLog.verbose("Refreshing capabilities for", account.getName());
        mainViewModel.refreshCapabilities(new ResponseCallback<>(account) {
            @Override
            public void onResponse(Capabilities response) {
                DeckLog.verbose("Finished refreshing capabilities for", account.getName(), "successfully.");
                if (response.isMaintenanceEnabled()) {
                    DeckLog.verbose("Maintenance mode is enabled.");
                } else {
                    DeckLog.verbose("Maintenance mode is disabled.");
                    // If we notice after updating the capabilities, that the new version is not supported, but it was previously, recreate the activity to make sure all elements are disabled properly
                    if (mainViewModel.getCurrentAccount().getServerDeckVersionAsObject().isSupported() && !response.getDeckVersion().isSupported()) {
                        ActivityCompat.recreate(MainActivity.this);
                    }
                }

                if (runAfter != null) {
                    runAfter.run();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                DeckLog.warn("Error on refreshing capabilities for", account.getName(), "(" + throwable.getMessage() + ").");
                if (throwable.getClass() == OfflineException.class || throwable instanceof OfflineException) {
                    DeckLog.info("Cannot refresh capabilities because device is offline.");
                } else {
                    super.onError(throwable);
                    ExceptionDialogFragment.newInstance(throwable, account).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                }

                if (runAfter != null) {
                    runAfter.run();
                }
            }
        });
    }

    protected void clearCurrentBoard() {
        binding.toolbar.setTitle(R.string.app_name_short);
        binding.filterText.setHint(R.string.app_name_short);
        binding.swipeRefreshLayout.setVisibility(View.GONE);
        binding.listMenuButton.setVisibility(View.GONE);
        binding.emptyContentViewStacks.setVisibility(View.GONE);
        binding.emptyContentViewBoards.setVisibility(View.VISIBLE);
    }

    protected void setCurrentBoard(@NonNull Board board) {
        if (stacksLiveData != null) {
            stacksLiveData.removeObservers(this);
        }
        saveBrandColors(this, board.getColor());
        mainViewModel.setCurrentBoard(board);
        filterViewModel.clearFilterInformation(true);

        lastBoardId = board.getLocalId();
        saveCurrentBoardId(this, mainViewModel.getCurrentAccount().getId(), mainViewModel.getCurrentBoardLocalId());
        binding.navigationView.setCheckedItem(boardsList.indexOf(board));

        binding.toolbar.setTitle(board.getTitle());
        binding.filterText.setHint(getString(R.string.search_in, board.getTitle()));

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

        stacksLiveData = mainViewModel.getStacksForBoard(mainViewModel.getCurrentAccount().getId(), board.getLocalId());
        stacksLiveData.observe(this, (List<Stack> stacks) -> {
            if (stacks == null) {
                throw new IllegalStateException("Given List<FullStack> must not be null");
            }
            currentBoardStacksCount = stacks.size();

            if (currentBoardStacksCount == 0) {
                binding.emptyContentViewStacks.setVisibility(View.VISIBLE);
                currentBoardHasStacks = false;
            } else {
                binding.emptyContentViewStacks.setVisibility(View.GONE);
                currentBoardHasStacks = true;
            }
            listMenu.findItem(R.id.archive_cards).setVisible(currentBoardHasStacks);

            int stackPositionInAdapter = 0;
            stackAdapter.setStacks(stacks);

            final var currentStackId = readCurrentStackId(this, mainViewModel.getCurrentAccount().getId(), mainViewModel.getCurrentBoardLocalId());
            for (int i = 0; i < currentBoardStacksCount; i++) {
                if (stacks.get(i).getLocalId() == currentStackId || currentStackId == NO_STACK_ID) {
                    stackPositionInAdapter = i;
                    break;
                }
            }
            final int stackPositionInAdapterClone = stackPositionInAdapter;
            final TabTitleGenerator tabTitleGenerator = position -> {
                if (stacks.size() > position) {
                    return stacks.get(position).getTitle();
                } else {
                    DeckLog.warn("Could not generate tab title for position " + position + " because list size is only " + currentBoardStacksCount);
                    return "ERROR";
                }
            };
            final var newMediator = new TabLayoutMediator(binding.stackTitles, binding.viewPager, (tab, position) -> tab.setText(tabTitleGenerator.getTitle(position)));
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
    protected void inflateBoardMenu(@Nullable Board currentBoard) {
        binding.navigationView.setItemIconTintList(null);
        final var menu = binding.navigationView.getMenu();
        menu.clear();
        DrawerMenuUtil.inflateBoards(this, menu, this.boardsList, mainViewModel.currentAccountHasArchivedBoards(), mainViewModel.getCurrentAccount().getServerDeckVersionAsObject().isSupported());
        binding.navigationView.setCheckedItem(boardsList.indexOf(currentBoard));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ID_ABOUT:
                startActivity(AboutActivity.createIntent(this, mainViewModel.getCurrentAccount()));
                break;
            case MENU_ID_SETTINGS:
                startActivityForResult(SettingsActivity.createIntent(this), MainActivity.ACTIVITY_SETTINGS);
                break;
            case MENU_ID_ADD_BOARD:
                EditBoardDialogFragment.newInstance().show(getSupportFragmentManager(), addBoard);
                break;
            case MENU_ID_ARCHIVED_BOARDS:
                startActivity(ArchivedBoardsActvitiy.createIntent(MainActivity.this, mainViewModel.getCurrentAccount()));
                break;
            case MENU_ID_UPCOMING_CARDS:
                startActivity(UpcomingCardsActivity.createIntent(MainActivity.this));
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
        final int itemId = item.getItemId();
        if (itemId == R.id.archive_cards) {
            final var stack = stackAdapter.getItem(binding.viewPager.getCurrentItem());
            final var stackLocalId = stack.getLocalId();
            mainViewModel.countCardsInStack(mainViewModel.getCurrentAccount().getId(), stackLocalId, (numberOfCards) -> runOnUiThread(() ->
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.archive_cards)
                            .setMessage(getString(FilterInformation.hasActiveFilter(filterViewModel.getFilterInformation().getValue())
                                    ? R.string.do_you_want_to_archive_all_cards_of_the_filtered_list
                                    : R.string.do_you_want_to_archive_all_cards_of_the_list, stack.getTitle()))
                            .setPositiveButton(R.string.simple_archive, (dialog, whichButton) -> {
                                final FilterInformation filterInformation = filterViewModel.getFilterInformation().getValue();
                                mainViewModel.archiveCardsInStack(mainViewModel.getCurrentAccount().getId(), stackLocalId, filterInformation == null ? new FilterInformation() : filterInformation, new IResponseCallback<>() {
                                    @Override
                                    public void onResponse(Void response) {
                                        DeckLog.info("Successfully archived all cards in stack local id", stackLocalId);
                                    }

                                    @Override
                                    public void onError(Throwable throwable) {
                                        if (!SyncManager.ignoreExceptionOnVoidError(throwable)) {
                                            IResponseCallback.super.onError(throwable);
                                            runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, mainViewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
                                        }
                                    }
                                });
                            })
                            .setNeutralButton(android.R.string.cancel, null)
                            .create()
                            .show()
            ));
            return true;
        } else if (itemId == R.id.add_list) {
            EditStackDialogFragment.newInstance().show(getSupportFragmentManager(), addList);
            return true;
        } else if (itemId == R.id.rename_list) {
            final var stackId = stackAdapter.getItem(binding.viewPager.getCurrentItem()).getLocalId();
            observeOnce(mainViewModel.getStack(mainViewModel.getCurrentAccount().getId(), stackId), MainActivity.this, fullStack ->
                    EditStackDialogFragment.newInstance(fullStack.getLocalId(), fullStack.getStack().getTitle())
                            .show(getSupportFragmentManager(), EditStackDialogFragment.class.getCanonicalName()));
            return true;
        } else if (itemId == R.id.move_list_left || itemId == R.id.move_list_right) {
            final long stackId = stackAdapter.getItem(binding.viewPager.getCurrentItem()).getLocalId();
            // TODO error handling
            mainViewModel.reorderStack(mainViewModel.getCurrentAccount().getId(), mainViewModel.getCurrentBoardLocalId(), stackId, itemId == R.id.move_list_right);
            stackMoved = true;
            return true;
        } else if (itemId == R.id.delete_list) {
            final long stackLocalId = stackAdapter.getItem(binding.viewPager.getCurrentItem()).getLocalId();
            mainViewModel.countCardsInStack(mainViewModel.getCurrentAccount().getId(), stackLocalId, (numberOfCards) -> runOnUiThread(() -> {
                if (numberOfCards != null && numberOfCards > 0) {
                    DeleteStackDialogFragment.newInstance(stackLocalId, numberOfCards).show(getSupportFragmentManager(), DeleteStackDialogFragment.class.getCanonicalName());
                } else {
                    onStackDeleted(stackLocalId);
                }
            }));
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                    ActivityCompat.recreate(this);
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
                        final var accountToCreate = new Account(account.name, account.userId, account.url);
                        mainViewModel.createAccount(accountToCreate, new IResponseCallback<>() {
                            @Override
                            public void onResponse(Account createdAccount) {
                                final var importSyncManager = new SyncManager(MainActivity.this, account.name);
                                importSyncManager.refreshCapabilities(new ResponseCallback<>(createdAccount) {
                                    @SuppressLint("StringFormatInvalid")
                                    @Override
                                    public void onResponse(Capabilities response) {
                                        if (!response.isMaintenanceEnabled()) {
                                            if (response.getDeckVersion().isSupported()) {
                                                runOnUiThread(() -> {
                                                    final var importSnackbar = BrandedSnackbar.make(binding.coordinatorLayout, R.string.account_is_getting_imported, Snackbar.LENGTH_INDEFINITE);
                                                    importSnackbar.show();
                                                    importSyncManager.synchronize(new ResponseCallback<>(createdAccount) {
                                                        @Override
                                                        public void onResponse(Boolean syncSuccess) {
                                                            importSnackbar.dismiss();
                                                            runOnUiThread(() -> BrandedSnackbar.make(binding.coordinatorLayout, getString(R.string.account_imported), Snackbar.LENGTH_LONG)
                                                                    .setAction(R.string.simple_switch, (a) -> {
                                                                        createdAccount.setColor(response.getColor());
                                                                        mainViewModel.setSyncManager(importSyncManager);
                                                                        mainViewModel.setCurrentAccount(createdAccount);
                                                                    })
                                                                    .show());
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
                                                DeckLog.warn("Cannot import account because server version is too low (" + response.getDeckVersion() + "). Minimum server version is currently", Version.minimumSupported());
                                                runOnUiThread(() -> new AlertDialog.Builder(MainActivity.this)
                                                        .setTitle(R.string.update_deck)
                                                        .setMessage(getString(R.string.deck_outdated_please_update, response.getDeckVersion().getOriginalVersion()))
                                                        .setNegativeButton(R.string.simple_discard, null)
                                                        .setPositiveButton(R.string.simple_update, (dialog, whichButton) -> {
                                                            final var openURL = new Intent(Intent.ACTION_VIEW);
                                                            openURL.setData(Uri.parse(createdAccount.getUrl() + getString(R.string.url_fragment_update_deck)));
                                                            startActivity(openURL);
                                                            finish();
                                                        }).show());
                                                mainViewModel.deleteAccount(createdAccount.getId());
                                            }
                                        } else {
                                            DeckLog.warn("Cannot import account because server version is currently in maintenance mode.");
                                            runOnUiThread(() -> new AlertDialog.Builder(MainActivity.this)
                                                    .setTitle(R.string.maintenance_mode)
                                                    .setMessage(getString(R.string.maintenance_mode_explanation, createdAccount.getUrl()))
                                                    .setPositiveButton(R.string.simple_close, null)
                                                    .show());
                                            mainViewModel.deleteAccount(createdAccount.getId());
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable throwable) {
                                        super.onError(throwable);
                                        mainViewModel.deleteAccount(createdAccount.getId());
                                        if (throwable instanceof OfflineException) {
                                            DeckLog.warn("Cannot import account because device is currently offline.");
                                            runOnUiThread(() -> new AlertDialog.Builder(MainActivity.this)
                                                    .setTitle(R.string.you_are_currently_offline)
                                                    .setMessage(R.string.you_have_to_be_connected_to_the_internet_in_order_to_add_an_account)
                                                    .setPositiveButton(R.string.simple_close, null)
                                                    .show());
                                        } else {
                                            ExceptionDialogFragment.newInstance(throwable, createdAccount).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(Throwable error) {
                                IResponseCallback.super.onError(error);
                                if (error instanceof SQLiteConstraintException) {
                                    DeckLog.warn("Account already added");
                                    BrandedSnackbar.make(binding.coordinatorLayout, R.string.account_already_added, Snackbar.LENGTH_LONG).show();
                                } else {
                                    ExceptionDialogFragment.newInstance(error, accountToCreate).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
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
        } else if (binding.searchToolbar.getVisibility() == View.VISIBLE) {
            hideFilterTextToolbar();
        } else {
            super.onBackPressed();
        }
    }

    private void showFilterTextToolbar() {
        binding.toolbar.setVisibility(View.GONE);
        binding.searchToolbar.setVisibility(View.VISIBLE);
        binding.searchToolbar.setNavigationOnClickListener(v1 -> onBackPressed());
        binding.enableSearch.setVisibility(View.GONE);
        binding.filterText.requestFocus();
        final var imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.filterText, InputMethodManager.SHOW_IMPLICIT);
        binding.toolbarCard.setStateListAnimator(AnimatorInflater.loadStateListAnimator(this, R.animator.appbar_elevation_on));
    }

    private void hideFilterTextToolbar() {
        binding.filterText.setText(null);
        final var imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        binding.searchToolbar.setVisibility(View.GONE);
        binding.enableSearch.setVisibility(View.VISIBLE);
        binding.toolbar.setVisibility(View.VISIBLE);
        binding.toolbarCard.setStateListAnimator(AnimatorInflater.loadStateListAnimator(this, R.animator.appbar_elevation_off));
    }

    private void registerAutoSyncOnNetworkAvailable() {
        final var connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final var builder = new NetworkRequest.Builder();

        if (connectivityManager != null) {
            if (networkCallback == null) {
                networkCallback = new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(@NonNull Network network) {
                        DeckLog.log("Got Network connection");
                        mainViewModel.synchronize(new ResponseCallback<>(mainViewModel.getCurrentAccount()) {
                            @Override
                            public void onResponse(Boolean response) {
                                DeckLog.log("Auto-Sync after connection available successful");
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                super.onError(throwable);
                                if (throwable.getClass() == OfflineException.class || throwable instanceof OfflineException) {
                                    DeckLog.error("Do not show sync failed snackbar because it is an ", OfflineException.class.getSimpleName(), "- assuming the user has wi-fi disabled but \"Sync only on wi-fi\" enabled");
                                } else if (throwable.getClass() == UnknownErrorException.class || throwable instanceof UnknownErrorException) {
                                    DeckLog.error("Do not show sync failed snackbar because it is an ", UnknownErrorException.class.getSimpleName(), "- assuming a not reachable server or infrastructure issues");
                                } else {
                                    showSyncFailedSnackbar(throwable);
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
    public void onStackDeleted(long stackLocalId) {
        int nextStackPosition;
        try {
            nextStackPosition = stackAdapter.getNeighbourPosition(binding.viewPager.getCurrentItem());
        } catch (NoSuchElementException | IndexOutOfBoundsException e) {
            nextStackPosition = 0;
            DeckLog.logError(e);
        }
        binding.viewPager.setCurrentItem(nextStackPosition);
        mainViewModel.deleteStack(mainViewModel.getCurrentAccount().getId(), stackLocalId, mainViewModel.getCurrentBoardLocalId(), new IResponseCallback<>() {
            @Override
            public void onResponse(Void response) {
                DeckLog.info("Successfully deleted stack with local id", stackLocalId, "and remote id", stackLocalId);
            }

            @Override
            public void onError(Throwable throwable) {
                if (!SyncManager.ignoreExceptionOnVoidError(throwable)) {
                    IResponseCallback.super.onError(throwable);
                    runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, mainViewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
                }
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
                clearBrandColors(this);
                clearCurrentBoard();
                EditBoardDialogFragment.newInstance().show(getSupportFragmentManager(), addBoard);
            }
        }

        mainViewModel.deleteBoard(board, new IResponseCallback<>() {
            @Override
            public void onResponse(Void response) {
                DeckLog.info("Successfully deleted board", board.getTitle());
            }

            @Override
            public void onError(Throwable throwable) {
                if (!SyncManager.ignoreExceptionOnVoidError(throwable)) {
                    IResponseCallback.super.onError(throwable);
                    runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, mainViewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
                }
            }
        });

        binding.drawerLayout.closeDrawer(GravityCompat.START);
    }


    /**
     * Displays a {@link BrandedSnackbar} for an exception of a failed sync, but only if the cause wasn't maintenance mode (this should be handled by a TextView instead of a snackbar).
     *
     * @param throwable the cause of the failed sync
     */
    @AnyThread
    private void showSyncFailedSnackbar(@NonNull Throwable throwable) {
        if (!(throwable instanceof NextcloudHttpRequestFailedException) || ((NextcloudHttpRequestFailedException) throwable).getStatusCode() != HttpURLConnection.HTTP_UNAVAILABLE) {
            runOnUiThread(() -> BrandedSnackbar.make(binding.coordinatorLayout, R.string.synchronization_failed, Snackbar.LENGTH_LONG)
                    .setAction(R.string.simple_more, v -> ExceptionDialogFragment.newInstance(throwable, mainViewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()))
                    .show());
        }
    }

    @Override
    public void onArchive(@NonNull Board board) {
        mainViewModel.archiveBoard(board, new IResponseCallback<>() {
            @Override
            public void onResponse(FullBoard response) {
                DeckLog.info("Successfully archived board", board.getTitle());
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, mainViewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
            }
        });
    }

    @Override
    public void onClone(Board board) {
        final String[] animals = {getString(R.string.clone_cards)};
        final boolean[] checkedItems = {false};
        new AlertDialog.Builder(this)
                .setTitle(R.string.clone_board)
                .setMultiChoiceItems(animals, checkedItems, (dialog, which, isChecked) -> checkedItems[0] = isChecked)
                .setPositiveButton(R.string.simple_clone, (dialog, which) -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                    final var snackbar = BrandedSnackbar.make(binding.coordinatorLayout, getString(R.string.cloning_board, board.getTitle()), Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                    mainViewModel.cloneBoard(board.getAccountId(), board.getLocalId(), board.getAccountId(), board.getColor(), checkedItems[0], new IResponseCallback<>() {
                        @Override
                        public void onResponse(FullBoard response) {
                            runOnUiThread(() -> {
                                snackbar.dismiss();
                                setCurrentBoard(response.getBoard());
                                BrandedSnackbar.make(binding.coordinatorLayout, getString(R.string.successfully_cloned_board, response.getBoard().getTitle()), Snackbar.LENGTH_LONG)
                                        .setAction(R.string.edit, v -> EditBoardDialogFragment.newInstance(response.getLocalId()).show(getSupportFragmentManager(), EditBoardDialogFragment.class.getSimpleName()))
                                        .show();
                            });
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            IResponseCallback.super.onError(throwable);
                            runOnUiThread(() -> {
                                snackbar.dismiss();
                                ExceptionDialogFragment.newInstance(throwable, mainViewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                            });
                        }
                    });
                })
                .setNeutralButton(android.R.string.cancel, null)
                .show();
    }
}