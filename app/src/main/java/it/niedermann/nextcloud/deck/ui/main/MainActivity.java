package it.niedermann.nextcloud.deck.ui.main;

import static java.util.Collections.emptyList;
import static it.niedermann.nextcloud.deck.util.MimeTypeUtil.TEXT_PLAIN;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.AnyThread;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nextcloud.android.sso.api.EmptyResponse;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;
import com.nextcloud.android.sso.exceptions.UnknownErrorException;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import it.niedermann.android.crosstabdnd.CrossTabDragAndDrop;
import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.android.tablayouthelper.TabLayoutHelper;
import it.niedermann.android.tablayouthelper.TabTitleGenerator;
import it.niedermann.android.util.ColorUtil;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityMainBinding;
import it.niedermann.nextcloud.deck.databinding.NavHeaderMainBinding;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.Version;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import it.niedermann.nextcloud.deck.repository.SyncRepository;
import it.niedermann.nextcloud.deck.ui.ImportAccountActivity;
import it.niedermann.nextcloud.deck.ui.StackChangeCallback;
import it.niedermann.nextcloud.deck.ui.accountswitcher.AccountSwitcherDialog;
import it.niedermann.nextcloud.deck.ui.board.ArchiveBoardListener;
import it.niedermann.nextcloud.deck.ui.board.DeleteBoardListener;
import it.niedermann.nextcloud.deck.ui.board.edit.EditBoardDialogFragment;
import it.niedermann.nextcloud.deck.ui.board.edit.EditBoardListener;
import it.niedermann.nextcloud.deck.ui.card.CardActionListener;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.card.CreateCardListener;
import it.niedermann.nextcloud.deck.ui.card.NewCardDialog;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.ui.filter.FilterDialogFragment;
import it.niedermann.nextcloud.deck.ui.filter.FilterViewModel;
import it.niedermann.nextcloud.deck.ui.main.search.SearchAdapter;
import it.niedermann.nextcloud.deck.ui.main.search.SearchResults;
import it.niedermann.nextcloud.deck.ui.movecard.MoveCardDialogFragment;
import it.niedermann.nextcloud.deck.ui.movecard.MoveCardListener;
import it.niedermann.nextcloud.deck.ui.settings.PreferencesViewModel;
import it.niedermann.nextcloud.deck.ui.stack.DeleteStackDialogFragment;
import it.niedermann.nextcloud.deck.ui.stack.DeleteStackListener;
import it.niedermann.nextcloud.deck.ui.stack.EditStackDialogFragment;
import it.niedermann.nextcloud.deck.ui.stack.EditStackListener;
import it.niedermann.nextcloud.deck.ui.stack.OnScrollListener;
import it.niedermann.nextcloud.deck.ui.stack.StackAdapter;
import it.niedermann.nextcloud.deck.ui.stack.StackFragment;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.theme.ThemedSnackbar;
import it.niedermann.nextcloud.deck.util.CardUtil;
import it.niedermann.nextcloud.deck.util.CustomAppGlideModule;
import it.niedermann.nextcloud.deck.util.OnTextChangedWatcher;
import okhttp3.Headers;

public class MainActivity extends AppCompatActivity implements DeleteStackListener,
        EditStackListener,
        DeleteBoardListener,
        EditBoardListener,
        ArchiveBoardListener,
        OnScrollListener,
        CreateCardListener,
        CardActionListener,
        MoveCardListener {

    protected ActivityMainBinding binding;
    private NavHeaderMainBinding headerBinding;
    private PreferencesViewModel preferencesViewModel;
    protected MainViewModel mainViewModel;
    private FilterViewModel filterViewModel;
    private SearchAdapter searchAdapter;
    @Nullable
    private MutableLiveData<SearchResults> searchResults$ = null;
    private final Observer<SearchResults> searchResultsObserver = results -> {
        if (results.result.isEmpty()) {
            binding.emptyContentViewSearchNoResults.setVisibility(View.VISIBLE);
            binding.emptyContentViewSearchNoTerm.setVisibility(View.GONE);
            binding.searchResults.setVisibility(View.GONE);
        } else {
            binding.emptyContentViewSearchNoResults.setVisibility(View.GONE);
            binding.searchResults.setVisibility(View.VISIBLE);
        }
        this.searchAdapter.setItems(results);
    };
    private final ReactiveLiveData<String> searchTerm$ = new ReactiveLiveData<>();
    private StackAdapter stackAdapter;
    private DrawerMenuInflater<MainActivity> drawerMenuInflater;
    private Menu listMenu;
    private ConnectivityManager.NetworkCallback networkCallback;
    private MainActivityNavigationHandler navigationHandler;
    @Nullable
    private TabLayoutMediator mediator;
    @Nullable
    private TabLayoutHelper tabLayoutHelper;
    private final OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            } else if (binding.searchView.isShowing()) {
                binding.searchView.hide();
            } else {
                finish();
            }
        }
    };

    private final ActivityResultLauncher<Intent> importAccountLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() != RESULT_OK) {
            finish();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        headerBinding = NavHeaderMainBinding.bind(binding.navigationView.getHeaderView(0));

        setTheme(R.style.AppTheme);
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        final var toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        preferencesViewModel = new ViewModelProvider(this).get(PreferencesViewModel.class);
        filterViewModel = new ViewModelProvider(this).get(FilterViewModel.class);

        navigationHandler = new MainActivityNavigationHandler(this, binding.drawerLayout, mainViewModel::saveCurrentBoardId);
        binding.navigationView.setNavigationItemSelectedListener(navigationHandler);

        searchAdapter = new SearchAdapter(this);
        binding.searchResults.setAdapter(searchAdapter);
        binding.searchView.getEditText().addTextChangedListener(new OnTextChangedWatcher(value -> {
            if (TextUtils.isEmpty(value)) {
                binding.emptyContentViewSearchNoTerm.setVisibility(View.VISIBLE);
                binding.emptyContentViewSearchNoResults.setVisibility(View.GONE);
                binding.searchResults.setVisibility(View.GONE);
                searchAdapter.setItems(new SearchResults());
            } else {
                binding.emptyContentViewSearchNoTerm.setVisibility(View.GONE);
                binding.searchResults.setVisibility(View.VISIBLE);
                searchTerm$.setValue(value);
            }
        }));

        stackAdapter = new StackAdapter(this);
        binding.viewPager.setAdapter(stackAdapter);
        binding.viewPager.setOffscreenPageLimit(2);

        headerBinding.copyDebugLogs.setOnClickListener((v) -> {
            try {
                DeckLog.shareLogAsFile(this);
            } catch (Exception e) {
                showExceptionDialog(e, null);
            }
        });

        final var dragAndDrop = new CrossTabDragAndDrop<StackFragment, CardAdapter, FullCard>(getResources(), ViewCompat.getLayoutDirection(binding.getRoot()) == ViewCompat.LAYOUT_DIRECTION_LTR);
        dragAndDrop.register(binding.viewPager, binding.stackTitles, getSupportFragmentManager());
        dragAndDrop.addItemMovedByDragListener((movedCard, stackId, position) -> {
            mainViewModel.reorder(movedCard, stackId, position);
            DeckLog.info("Card", movedCard.getCard().getTitle(), "was moved to Stack", stackId, "on position", position);
        });

        final var listMenuPopup = new PopupMenu(this, binding.listMenuButton);
        listMenu = listMenuPopup.getMenu();
        getMenuInflater().inflate(R.menu.list_menu, listMenu);
        listMenuPopup.setOnMenuItemClickListener(this::onOptionsItemSelected);
        binding.listMenuButton.setOnClickListener((v) -> listMenuPopup.show());

        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        drawerMenuInflater = new DrawerMenuInflater<>(this, binding.navigationView.getMenu());

        preferencesViewModel.isDebugModeEnabled$().observe(this, enabled -> headerBinding.copyDebugLogs.setVisibility(enabled ? View.VISIBLE : View.GONE));
        filterViewModel.hasActiveFilter().observe(this, hasActiveFilter -> {
            final var menu = binding.toolbar.getMenu();
            menu.findItem(R.id.filter).setVisible(!hasActiveFilter);
            menu.findItem(R.id.filter_active).setVisible(hasActiveFilter);
        });

        // Flag to distinguish user initiated stack changes from stack changes derived by changing the board
        final var boardChanged = new AtomicBoolean(true);
        final var stackChangeCallback = new StackChangeCallback(stackAdapter,
                binding.viewPager,
                binding.fab,
                binding.swipeRefreshLayout,
                listMenu,
                stack -> mainViewModel.saveCurrentStackId(stack.getAccountId(), stack.getBoardId(), stack.getLocalId()));

        final var hasAccounts$ = new ReactiveLiveData<>(mainViewModel.hasAccounts());

        hasAccounts$
                .filter(hasAccounts -> !hasAccounts)
                .observe(this, () -> importAccountLauncher.launch(ImportAccountActivity.createIntent(this)));

        hasAccounts$
                .filter(hasAccounts -> hasAccounts)
                .tap(() -> binding.viewPager.unregisterOnPageChangeCallback(stackChangeCallback))
                .tap(() -> binding.viewPager.registerOnPageChangeCallback(stackChangeCallback))
                .flatMap(() -> mainViewModel.getCurrentAccount$())
                .flatMap(account -> {
                    try {
                        applyAccount(account);
                    } catch (NextcloudFilesAppAccountNotFoundException e) {
                        showExceptionDialog(e, account);
                        // There is not much we can do here. Hide everything because this Exception means that our SyncManager instance is invalid
                        applyBoards(account, false, emptyList());
                        applyStacks(null, null, null);
                        return new MutableLiveData<>();
                    }
                    applyAccountTheme(account.getColor());
                    return new ReactiveLiveData<>(mainViewModel.getBoards(account.getId()))
                            .map(boardsAndArchived -> applyBoards(account, boardsAndArchived.second, boardsAndArchived.first))
                            .flatMap(navigationMap -> new ReactiveLiveData<>(mainViewModel.getCurrentFullBoard(account.getId()))
                                    .combineWith(() -> new MutableLiveData<>(navigationMap)))
                            .flatMap(args -> {
                                        applyBoard(account, args.second, args.first);
                                        @Nullable final var currentBoard = args.first;
                                        if (currentBoard == null) {
                                            applyStacks(null, null, emptyList());
                                            return new MutableLiveData<>(null);
                                        } else {
                                            return new ReactiveLiveData<>(mainViewModel.getStacks(account.getId(), currentBoard.getLocalId()))
                                                    .flatMap(stacks -> {
                                                        binding.viewPager.unregisterOnPageChangeCallback(stackChangeCallback);
                                                        boardChanged.set(true);
                                                        applyStacks(account, currentBoard.getLocalId(), stacks);
                                                        return mainViewModel.getCurrentStackId$(account.getId(), currentBoard.getLocalId());
                                                    });

                                        }
                                    }
                            );
                })
                .observe(this, currentStackId -> {
                    stackChangeCallback.updateMoveItemVisibility();
                    if (boardChanged.getAndSet(false)) {
                        applyStack(currentStackId);
                        binding.viewPager.registerOnPageChangeCallback(stackChangeCallback);
                    }
                });
    }

    private void applyAccount(@NonNull Account account) throws NextcloudFilesAppAccountNotFoundException {
        DeckLog.verbose("= Apply Account", account);
        mainViewModel.recreateSyncManager(account);
        registerAutoSyncOnNetworkAvailable(account);
        navigationHandler.setCurrentAccount(account);

        if (account.isMaintenanceEnabled()) {
            refreshCapabilities(account, null);
        }

        Glide
                .with(binding.toolbar.getContext())
                .load(account.getAvatarUrl(binding.toolbar.getMenu().findItem(R.id.avatar).getIcon().getIntrinsicWidth()))
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_account_circle_24)
                .error(R.drawable.ic_account_circle_24)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        binding.toolbar.getMenu().findItem(R.id.avatar).setIcon(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        DeckLog.verbose("Displaying maintenance mode info for", account.getName() + ":", account.isMaintenanceEnabled());
        binding.infoBox.setVisibility(account.isMaintenanceEnabled() ? View.VISIBLE : View.GONE);
        if (account.getServerDeckVersionAsObject().isSupported()) {
            binding.infoBoxVersionNotSupported.setVisibility(View.GONE);
        } else {
            binding.infoBoxVersionNotSupported.setText(getString(R.string.info_box_version_not_supported, account.getServerDeckVersionAsObject(), Version.minimumSupported().getOriginalVersion()));
            binding.infoBoxVersionNotSupported.setOnClickListener((v) -> startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(account.getUrl() + getString(R.string.url_fragment_update_deck)))));
            binding.infoBoxVersionNotSupported.setVisibility(View.VISIBLE);
        }

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            DeckLog.info("Triggered manual refresh");
            CustomAppGlideModule.clearCache(this);

            DeckLog.verbose("Trigger refresh capabilities for", account);
            refreshCapabilities(account, () -> {
                DeckLog.verbose("Trigger synchronization for", account);
                mainViewModel.synchronize(account, new IResponseCallback<>() {
                    @Override
                    public void onResponse(Boolean response, Headers headers) {
                        DeckLog.info("End of synchronization for " + account + " → Stop spinner.");
                        runOnUiThread(() -> binding.swipeRefreshLayout.setRefreshing(false));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        IResponseCallback.super.onError(throwable);
                        DeckLog.info("End of synchronization for " + account + " → Stop spinner.");
                        showSyncFailedSnackbar(account, throwable);
                        runOnUiThread(() -> binding.swipeRefreshLayout.setRefreshing(false));
                    }
                });
            });
        });
    }

    private Map<Integer, Long> applyBoards(@NonNull Account account, boolean hasArchivedBoards, @Nullable List<FullBoard> fullBoards) {
        DeckLog.verbose("=== Apply Boards", fullBoards, "for", account);
        binding.navigationView.setItemIconTintList(null);

        final Map<Integer, Long> navigationMap;

        if (fullBoards == null || fullBoards.isEmpty()) {
            binding.emptyContentViewBoards.setVisibility(View.VISIBLE);
            navigationMap = drawerMenuInflater.inflateBoards(account, emptyList(), account.getColor(), hasArchivedBoards, account.getServerDeckVersionAsObject().isSupported());

        } else {
            binding.emptyContentViewBoards.setVisibility(View.GONE);
            navigationMap = drawerMenuInflater.inflateBoards(account, fullBoards, account.getColor(), hasArchivedBoards, account.getServerDeckVersionAsObject().isSupported());
        }

        navigationHandler.updateNavigationMap(navigationMap);
        return navigationMap;
    }

    protected void applyBoard(@NonNull Account account, @NonNull Map<Integer, Long> navigationMap, @Nullable FullBoard currentBoard) {
        DeckLog.verbose("===== Apply Board", currentBoard);
        filterViewModel.clearFilterInformation(true);
        binding.toolbar.getMenu().findItem(R.id.filter).setVisible(true);
        binding.toolbar.getMenu().findItem(R.id.filter_active).setVisible(false);
        binding.appBarLayout.setExpanded(true);

        observeSearchTerm(account, currentBoard);

        if (currentBoard == null) {
            applyBoardTheme(account.getColor());
            showEditButtonsIfPermissionsGranted(false, false);

            binding.toolbar.setHint(R.string.app_name_short);
            binding.fab.setText(R.string.add_board);
            binding.fab.setOnClickListener(v -> {
                binding.fab.hide();
                EditBoardDialogFragment.newInstance(account).show(getSupportFragmentManager(), EditBoardDialogFragment.class.getSimpleName());
            });
        } else {
            applyBoardTheme(currentBoard.getBoard().getColor());
            showEditButtonsIfPermissionsGranted(true, currentBoard.board.isPermissionEdit());

            binding.toolbar.setHint(getString(R.string.search_in, currentBoard.getBoard().getTitle()));
            binding.fab.setText(R.string.add_list);
            binding.fab.setOnClickListener(v -> {
                binding.fab.hide();
                EditStackDialogFragment.newInstance(currentBoard.getAccountId(), currentBoard.getLocalId()).show(getSupportFragmentManager(), EditStackDialogFragment.class.getSimpleName());
            });

            navigationMap
                    .entrySet()
                    .stream()
                    .filter(entry -> currentBoard.getLocalId().equals(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .ifPresent(menuItemId -> binding.navigationView.setCheckedItem(menuItemId));
        }

        binding.searchView.clearText();
    }

    private void observeSearchTerm(@NonNull Account account, @Nullable FullBoard currentBoard) {
        if (searchResults$ != null) {
            searchResults$.removeObserver(searchResultsObserver);
        }
        if (currentBoard != null) {
            searchResults$ = searchTerm$
                    .filter(() -> binding.searchView.isShowing())
                    .flatMap(term -> new ReactiveLiveData<>(mainViewModel.searchCards(account.getId(), currentBoard.getLocalId(), term, Integer.MAX_VALUE))
                            .combineWith(() -> new MutableLiveData<>(term)))
                    .map(result -> new SearchResults(account, currentBoard, result.first, result.second));
            searchResults$.observe(this, searchResultsObserver);
        }
    }

    private void applyStacks(@Nullable Account account, @Nullable Long boardId, @Nullable List<Stack> stacks) {
        DeckLog.verbose("======= Apply Stacks", stacks, "for Board", boardId);
        final boolean noStacksAvailable = stacks == null || stacks.isEmpty();

        listMenu.findItem(R.id.archive_cards).setVisible(!noStacksAvailable);
        listMenu.findItem(R.id.rename_list).setVisible(!noStacksAvailable);
        listMenu.findItem(R.id.delete_list).setVisible(!noStacksAvailable);

        if (account == null || noStacksAvailable) {
            binding.emptyContentViewStacks.setVisibility(View.VISIBLE);

            stackAdapter.setStacks(account, boardId, emptyList());
            setStackMediator(new TabLayoutMediator(binding.stackTitles, binding.viewPager, (tab, position) -> tab.setText("ERROR")));
        } else {
            binding.emptyContentViewStacks.setVisibility(View.GONE);

            binding.fab.setText(R.string.add_card);
            binding.fab.setOnClickListener(v -> {
                binding.fab.hide();
                final var stack = stackAdapter.getItem(binding.viewPager.getCurrentItem());
                NewCardDialog.newInstance(account, stack.getBoardId(), stack.getLocalId()).show(getSupportFragmentManager(), NewCardDialog.class.getSimpleName());
            });

            stackAdapter.setStacks(account, boardId, stacks);

            final TabTitleGenerator tabTitleGenerator = position -> {
                if (stacks.size() > position) {
                    return stacks.get(position).getTitle();
                } else {
                    DeckLog.warn("Could not generate tab title for position " + position + " because list size is only " + stacks.size());
                    return "ERROR";
                }
            };
            setStackMediator(new TabLayoutMediator(binding.stackTitles, binding.viewPager, (tab, position) -> tab.setText(tabTitleGenerator.getTitle(position))));
            updateTabLayoutHelper(tabTitleGenerator);
        }
    }

    private void applyStack(@Nullable Long stackId) {
        DeckLog.verbose("========= Apply Stack", stackId);
        if (stackId != null) {
            try {
                binding.viewPager.setCurrentItem(stackAdapter.getPosition(stackId), false);
            } catch (NoSuchElementException e) {
                DeckLog.warn(e);
            }
        }
    }

    private void applyBoardTheme(@ColorInt int color) {
        final var utils = ThemeUtils.of(color, this);

        utils.deck.themeSearchBar(binding.toolbar);
        utils.deck.themeSearchView(binding.searchView);
        utils.deck.themeTabLayoutOnTransparent(binding.stackTitles);
        utils.deck.themeEmptyContentView(binding.emptyContentViewStacks);
        utils.deck.themeEmptyContentView(binding.emptyContentViewSearchNoTerm);
        utils.deck.themeEmptyContentView(binding.emptyContentViewSearchNoResults);
        utils.material.themeExtendedFAB(binding.fab);
        utils.androidx.themeSwipeRefreshLayout(binding.swipeRefreshLayout);

    }

    private void applyAccountTheme(@ColorInt int accountColor) {
        final var utils = ThemeUtils.of(accountColor, this);

        utils.deck.themeEmptyContentView(binding.emptyContentViewBoards);
        utils.platform.colorNavigationView(binding.navigationView, false);

        @ColorInt final int headerTextColor = ColorUtil.getForegroundColorForBackgroundColor(accountColor);
        headerBinding.headerView.setBackgroundColor(accountColor);
        headerBinding.appName.setTextColor(headerTextColor);
//        DrawableCompat.setTint(headerBinding.logo.getDrawable(), headerTextColor);
        DrawableCompat.setTint(headerBinding.copyDebugLogs.getDrawable(), headerTextColor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
        this.headerBinding = null;
        if (tabLayoutHelper != null) {
            tabLayoutHelper.release();
        }
    }

    @Override
    public void onCreateStack(long accountId, long boardId, String stackName) {
        mainViewModel.createStack(accountId, boardId, stackName, new IResponseCallback<>() {
            @Override
            public void onResponse(FullStack response, Headers headers) {
                binding.viewPager.post(() -> {
                    try {
                        binding.viewPager.setCurrentItem(stackAdapter.getPosition(response.getLocalId()));
                        mainViewModel.saveCurrentStackId(response.getEntity().getAccountId(), response.getEntity().getBoardId(), response.getEntity().getLocalId());
                    } catch (NoSuchElementException e) {
                        DeckLog.logError(e);
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                mainViewModel.getCurrentBoardColor(accountId, boardId)
                        .thenAcceptAsync(color -> ThemedSnackbar.make(binding.coordinatorLayout, Objects.requireNonNull(throwable.getLocalizedMessage()), Snackbar.LENGTH_LONG, color)
                                .setAction(R.string.simple_more, v -> showExceptionDialog(throwable, accountId))
                                .setAnchorView(binding.fab)
                                .show(), ContextCompat.getMainExecutor(MainActivity.this));
            }
        });
    }

    @Override
    public void onUpdateStack(long localStackId, String stackName) {
        mainViewModel.updateStackTitle(localStackId, stackName, new IResponseCallback<>() {
            @Override
            public void onResponse(FullStack response, Headers headers) {
                DeckLog.info("Successfully updated", Stack.class.getSimpleName(), "to", stackName);
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                showExceptionDialog(throwable, null);
            }
        });
    }

    @Override
    public void onCreateBoard(@NonNull Account account, String title, @ColorInt int color) {
        final var boardToCreate = new Board(title, color);
        boardToCreate.setPermissionEdit(true);
        boardToCreate.setPermissionManage(true);

        mainViewModel.createBoard(account, boardToCreate, new IResponseCallback<>() {
            @Override
            public void onResponse(FullBoard response, Headers headers) {
                runOnUiThread(() -> {
                    if (response != null) {
                        mainViewModel.saveCurrentBoardId(response.getAccountId(), response.getLocalId());
                        EditStackDialogFragment.newInstance(response.getAccountId(), response.getLocalId()).show(getSupportFragmentManager(), EditStackDialogFragment.class.getSimpleName());
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                ThemedSnackbar.make(binding.coordinatorLayout, R.string.synchronization_failed, Snackbar.LENGTH_LONG, account.getColor())
                        .setAction(R.string.simple_more, v -> showExceptionDialog(throwable, account))
                        .setAnchorView(binding.fab)
                        .show();
            }
        });
    }

    @Override
    public void onUpdateBoard(FullBoard fullBoard) {
        mainViewModel.updateBoard(fullBoard, new IResponseCallback<>() {
            @Override
            public void onResponse(FullBoard response, Headers headers) {
                DeckLog.info("Successfully updated board", fullBoard.getBoard().getTitle());
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                showExceptionDialog(throwable, fullBoard.getAccountId());
            }
        });
    }

    private void refreshCapabilities(final Account account, @Nullable Runnable runAfter) {
        DeckLog.verbose("Refreshing capabilities for", account.getName());
        mainViewModel.refreshCapabilities(new ResponseCallback<>(account) {
            @Override
            public void onResponse(Capabilities response, Headers headers) {
                DeckLog.verbose("Finished refreshing capabilities for", account.getName(), "successfully.");
                if (response.isMaintenanceEnabled()) {
                    DeckLog.verbose("Maintenance mode is enabled.");
                } else {
                    DeckLog.verbose("Maintenance mode is disabled.");
                    // If we notice after updating the capabilities, that the new version is not supported, but it was previously, recreate the activity to make sure all elements are disabled properly
                    if (account.getServerDeckVersionAsObject().isSupported() && !response.getDeckVersion().isSupported()) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        if (itemId == R.id.filter || itemId == R.id.filter_active) {
            FilterDialogFragment.newInstance().show(getSupportFragmentManager(), FilterDialogFragment.class.getCanonicalName());
        } else if (itemId == R.id.avatar) {
            AccountSwitcherDialog.newInstance().show(getSupportFragmentManager(), AccountSwitcherDialog.class.getSimpleName());
        } else if (itemId == R.id.archive_cards) {
            final var stack = stackAdapter.getItem(binding.viewPager.getCurrentItem());
            final var stackLocalId = stack.getLocalId();
            mainViewModel.countCardsInStack(stack.getAccountId(), stackLocalId, (numberOfCards, headers) -> runOnUiThread(() ->
                    new MaterialAlertDialogBuilder(this)
                            .setTitle(R.string.archive_cards)
                            .setMessage(getString(FilterInformation.hasActiveFilter(filterViewModel.getFilterInformation().getValue())
                                    ? R.string.do_you_want_to_archive_all_cards_of_the_filtered_list
                                    : R.string.do_you_want_to_archive_all_cards_of_the_list, stack.getTitle()))
                            .setPositiveButton(R.string.simple_archive, (dialog, whichButton) -> {
                                final var filterInformation = Optional.ofNullable(filterViewModel.getFilterInformation().getValue()).orElse(new FilterInformation());
                                mainViewModel.archiveCardsInStack(stack.getAccountId(), stackLocalId, filterInformation, new IResponseCallback<>() {
                                    @Override
                                    public void onResponse(EmptyResponse response, Headers headers) {
                                        DeckLog.info("Successfully archived all cards in stack local id", stackLocalId);
                                    }

                                    @Override
                                    public void onError(Throwable throwable) {
                                        if (SyncRepository.isNoOnVoidError(throwable)) {
                                            IResponseCallback.super.onError(throwable);
                                            showExceptionDialog(throwable, stack.getAccountId());
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
            final Long accountId = stackAdapter.getAccount() == null ? null : stackAdapter.getAccount().getId();
            if (accountId == null) {
                DeckLog.warn("Can not launch stack dialog: accountId of stackAdapter is null.");
                return false;
            }
            final Long boardId = stackAdapter.getBoardId();
            if (boardId == null) {
                DeckLog.warn("Can not launch stack dialog: boardId of stackAdapter is null.");
                return false;
            }
            EditStackDialogFragment.newInstance(accountId, boardId).show(getSupportFragmentManager(), EditStackDialogFragment.class.getSimpleName());
            return true;
        } else if (itemId == R.id.rename_list) {
            final var stack = stackAdapter.getItem(binding.viewPager.getCurrentItem());
            new ReactiveLiveData<>(mainViewModel.getStack(stack.getAccountId(), stack.getLocalId()))
                    .observeOnce(MainActivity.this, fullStack -> EditStackDialogFragment
                            .newInstance(fullStack.getLocalId(), fullStack.getStack().getTitle())
                            .show(getSupportFragmentManager(), EditStackDialogFragment.class.getCanonicalName()));
            return true;
        } else if (itemId == R.id.move_list_left || itemId == R.id.move_list_right) {
            final var stack = stackAdapter.getItem(binding.viewPager.getCurrentItem());
            // TODO error handling
            mainViewModel.reorderStack(stack.getAccountId(), stack.getBoardId(), stack.getLocalId(), itemId == R.id.move_list_right);
            return true;
        } else if (itemId == R.id.delete_list) {
            final var stack = stackAdapter.getItem(binding.viewPager.getCurrentItem());
            mainViewModel.countCardsInStack(stack.getAccountId(), stack.getLocalId(), (numberOfCards, headers) -> runOnUiThread(() -> {
                if (numberOfCards != null && numberOfCards > 0) {
                    DeleteStackDialogFragment.newInstance(stack.getAccountId(), stack.getBoardId(), stack.getLocalId(), numberOfCards).show(getSupportFragmentManager(), DeleteStackDialogFragment.class.getCanonicalName());
                } else {
                    onDeleteStack(stack.getAccountId(), stack.getBoardId(), stack.getLocalId());
                }
            }));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEditButtonsIfPermissionsGranted(boolean currentBoardIsAvailable, boolean currentBoardHasEditPermission) {
        if (currentBoardIsAvailable) {
            if (!currentBoardHasEditPermission) {
                binding.fab.hide();
                binding.listMenuButton.setVisibility(View.GONE);
                binding.emptyContentViewStacks.hideDescription();
            } else {
                binding.fab.show();
                binding.listMenuButton.setVisibility(View.VISIBLE);
                binding.emptyContentViewStacks.showDescription();
            }
        } else {
            binding.fab.show();
            binding.listMenuButton.setVisibility(View.GONE);
            binding.emptyContentViewStacks.showDescription();
        }
    }

    @Override
    public void onScrollUp() {
        binding.fab.extend();
    }

    @Override
    public void onScrollDown() {
        binding.fab.shrink();
    }

    @Override
    public void onBottomReached() {
        binding.fab.extend();
    }

    private void registerAutoSyncOnNetworkAvailable(@NonNull Account account) {
        final var connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final var builder = new NetworkRequest.Builder();

        if (connectivityManager != null) {
            if (networkCallback != null) {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            }

            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    DeckLog.log("Got Network connection");
                    mainViewModel.synchronize(account, new IResponseCallback<>() {
                        @Override
                        public void onResponse(Boolean response, Headers headers) {
                            DeckLog.log("Auto-Sync after connection available successful");
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            IResponseCallback.super.onError(throwable);
                            if (throwable.getClass() == OfflineException.class || throwable instanceof OfflineException) {
                                DeckLog.error("Do not show sync failed snackbar because it is an ", OfflineException.class.getSimpleName(), "- assuming the user has wi-fi disabled but \"Sync only on wi-fi\" enabled");
                            } else if (throwable.getClass() == UnknownErrorException.class || throwable instanceof UnknownErrorException) {
                                DeckLog.error("Do not show sync failed snackbar because it is an ", UnknownErrorException.class.getSimpleName(), "- assuming a not reachable server or infrastructure issues");
                            } else {
                                showSyncFailedSnackbar(account, throwable);
                            }
                        }
                    });
                }

                @Override
                public void onLost(@NonNull Network network) {
                    DeckLog.log("Network lost");
                }
            };
            connectivityManager.registerNetworkCallback(builder.build(), networkCallback);
        }
    }

    /**
     * @param createdCard The new {@link Card}s data
     */
    @Override
    public void onCardCreated(@NonNull FullCard createdCard) {
        final var card = createdCard.getCard();
        DeckLog.log("Card Created! Title:" + card.getTitle() + " in stack ID: " + card.getStackId());

        // Scroll the given StackFragment to the bottom, so the new Card is in view.
        Optional.ofNullable((StackFragment) getSupportFragmentManager().findFragmentByTag("f" + card.getStackId()))
                .ifPresent(StackFragment::scrollToBottom);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        this.binding.fab.show();
    }

    @Override
    public void onDeleteStack(long accountId, long boardId, long stackId) {
        mainViewModel.deleteStack(accountId, boardId, stackId, new IResponseCallback<>() {
            @Override
            public void onResponse(EmptyResponse response, Headers headers) {
                DeckLog.info("Successfully deleted stack with local id", stackId, "and remote id", stackId);
            }

            @Override
            public void onError(Throwable throwable) {
                if (SyncRepository.isNoOnVoidError(throwable)) {
                    IResponseCallback.super.onError(throwable);
                    showExceptionDialog(throwable, accountId);
                }
            }
        });
    }

    @Override
    public void onBoardDeleted(Board board) {
        mainViewModel.deleteBoard(board, new IResponseCallback<>() {
            @Override
            public void onResponse(EmptyResponse response, Headers headers) {
                DeckLog.info("Successfully deleted board", board.getTitle());
            }

            @Override
            public void onError(Throwable throwable) {
                if (SyncRepository.isNoOnVoidError(throwable)) {
                    IResponseCallback.super.onError(throwable);
                    showExceptionDialog(throwable, board.getAccountId());
                }
            }
        });

        binding.drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onArchive(@NonNull Board board) {
        mainViewModel.archiveBoard(board, new IResponseCallback<>() {
            @Override
            public void onResponse(FullBoard response, Headers headers) {
                DeckLog.info("Successfully archived board", board.getTitle());
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                showExceptionDialog(throwable, board.getAccountId());
            }
        });
    }

    @Override
    public void onClone(@NonNull Account account, @NonNull Board board) {
        final String[] cloneOptions = {getString(R.string.clone_cards)};
        final boolean[] checkedItems = {false};
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.clone_board)
                .setMultiChoiceItems(cloneOptions, checkedItems, (dialog, which, isChecked) -> checkedItems[0] = isChecked)
                .setPositiveButton(R.string.simple_clone, (dialog, which) -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                    final var snackbar = ThemedSnackbar.make(binding.coordinatorLayout, getString(R.string.cloning_board, board.getTitle()), Snackbar.LENGTH_INDEFINITE, board.getColor())
                            .setAnchorView(binding.fab);
                    snackbar.show();
                    mainViewModel.cloneBoard(board.getAccountId(), board.getLocalId(), board.getAccountId(), board.getColor(), checkedItems[0], new IResponseCallback<>() {
                        @Override
                        public void onResponse(FullBoard response, Headers headers) {
                            runOnUiThread(() -> {
                                snackbar.dismiss();
                                mainViewModel.saveCurrentBoardId(response.getAccountId(), response.getLocalId());
                                ThemedSnackbar.make(binding.coordinatorLayout, getString(R.string.successfully_cloned_board, response.getBoard().getTitle()), Snackbar.LENGTH_LONG, response.getBoard().getColor())
                                        .setAction(R.string.edit, v -> EditBoardDialogFragment.newInstance(account, response.getLocalId()).show(getSupportFragmentManager(), EditBoardDialogFragment.class.getSimpleName()))
                                        .setAnchorView(binding.fab)
                                        .show();
                            });
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            IResponseCallback.super.onError(throwable);
                            runOnUiThread(() -> {
                                snackbar.dismiss();
                                showExceptionDialog(throwable, board.getAccountId());
                            });
                        }
                    });
                })
                .setNeutralButton(android.R.string.cancel, null)
                .show();
    }

    @Override
    public void onArchive(@NonNull FullCard fullCard) {
        mainViewModel.archiveCard(fullCard, new IResponseCallback<>() {
            @Override
            public void onResponse(FullCard response, Headers headers) {
                DeckLog.info("Successfully archived", Card.class.getSimpleName(), fullCard.getCard().getTitle());
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                showExceptionDialog(throwable, fullCard.getAccountId());
            }
        });
    }

    @Override
    public void onDelete(@NonNull FullCard fullCard) {
        mainViewModel.deleteCard(fullCard.getCard(), new IResponseCallback<>() {
            @Override
            public void onResponse(EmptyResponse response, Headers headers) {
                DeckLog.info("Successfully deleted card", fullCard.getCard().getTitle());
            }

            @Override
            public void onError(Throwable throwable) {
                if (SyncRepository.isNoOnVoidError(throwable)) {
                    IResponseCallback.super.onError(throwable);
                    showExceptionDialog(throwable, fullCard.getAccountId());
                }
            }
        });
    }

    @Override
    public void onAssignCurrentUser(@NonNull FullCard fullCard) {
        mainViewModel.assignUserToCard(fullCard);
    }

    @Override
    public void onUnassignCurrentUser(@NonNull FullCard fullCard) {
        mainViewModel.unassignUserFromCard(fullCard);
    }

    @Override
    public void onMove(@NonNull FullBoard fullBoard, @NonNull FullCard fullCard) {
        DeckLog.verbose("[Move card] Launch move dialog for " + Card.class.getSimpleName() + " \"" + fullCard.getCard().getTitle() + "\" (#" + fullCard.getLocalId() + ") from " + Stack.class.getSimpleName() + " #" + fullCard.getCard().getStackId());
        MoveCardDialogFragment
                .newInstance(fullCard.getAccountId(), fullBoard.getBoard().getLocalId(), fullCard.getCard().getTitle(), fullCard.getLocalId(), CardUtil.cardHasCommentsOrAttachments(fullCard))
                .show(getSupportFragmentManager(), MoveCardDialogFragment.class.getSimpleName());
    }

    @Override
    public void onShareLink(@NonNull FullBoard fullBoard, @NonNull FullCard fullCard) {
        mainViewModel.getAccountFuture(fullCard.getAccountId()).thenAcceptAsync(account -> {
            final int shareLinkRes = account.getServerDeckVersionAsObject().getShareLinkResource();
            final var shareIntent = new Intent()
                    .setAction(Intent.ACTION_SEND)
                    .setType(TEXT_PLAIN)
                    .putExtra(Intent.EXTRA_SUBJECT, fullCard.getCard().getTitle())
                    .putExtra(Intent.EXTRA_TITLE, fullCard.getCard().getTitle())
                    .putExtra(Intent.EXTRA_TEXT, account.getUrl() + getString(shareLinkRes, fullBoard.getBoard().getId(), fullCard.getCard().getId()));
            startActivity(Intent.createChooser(shareIntent, fullCard.getCard().getTitle()));
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    public void onShareContent(@NonNull FullCard fullCard) {
        final var shareIntent = new Intent()
                .setAction(Intent.ACTION_SEND)
                .setType(TEXT_PLAIN)
                .putExtra(Intent.EXTRA_SUBJECT, fullCard.getCard().getTitle())
                .putExtra(Intent.EXTRA_TITLE, fullCard.getCard().getTitle())
                .putExtra(Intent.EXTRA_TEXT, CardUtil.getCardContentAsString(this, fullCard));
        startActivity(Intent.createChooser(shareIntent, fullCard.getCard().getTitle()));
    }

    @Override
    public void move(long originAccountId, long originCardLocalId, long targetAccountId, long targetBoardLocalId, long targetStackLocalId) {
        mainViewModel.moveCard(originAccountId, originCardLocalId, targetAccountId, targetBoardLocalId, targetStackLocalId, new IResponseCallback<>() {
            @Override
            public void onResponse(EmptyResponse response, Headers headers) {
                DeckLog.log("Moved", Card.class.getSimpleName(), originCardLocalId, "to", Stack.class.getSimpleName(), targetStackLocalId);
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                if (SyncRepository.isNoOnVoidError(throwable)) {
                    ExceptionDialogFragment.newInstance(throwable, null).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                }
            }
        });
    }

    /**
     * Displays a {@link ThemedSnackbar} for an exception of a failed sync, but only if the cause wasn't maintenance mode (this should be handled by a TextView instead of a snackbar).
     *
     * @param throwable the cause of the failed sync
     */
    @AnyThread
    private void showSyncFailedSnackbar(@NonNull Account account, @NonNull Throwable throwable) {
        if (!(throwable instanceof NextcloudHttpRequestFailedException) || ((NextcloudHttpRequestFailedException) throwable).getStatusCode() != HttpURLConnection.HTTP_UNAVAILABLE) {
            runOnUiThread(() -> {
                if (binding != null) { // Can be null in case the activity has been destroyed before the synchronization process has been finished
                    ThemedSnackbar.make(binding.coordinatorLayout, R.string.synchronization_failed, Snackbar.LENGTH_LONG, account.getColor())
                            .setAction(R.string.simple_more, v -> showExceptionDialog(throwable, account))
                            .setAnchorView(binding.fab)
                            .show();
                }
            });
        }
    }

    @AnyThread
    protected void showExceptionDialog(@NonNull Throwable throwable, long accountId) {
        mainViewModel.getAccount(accountId).thenAccept(account -> showExceptionDialog(throwable, account));
    }

    @AnyThread
    protected void showExceptionDialog(@NonNull Throwable throwable, @Nullable Account account) {
        runOnUiThread(() -> ExceptionDialogFragment
                .newInstance(throwable, account)
                .show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
    }
}