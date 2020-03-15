package it.niedermann.nextcloud.deck.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.h6ah4i.android.tablayouthelper.TabLayoutHelper;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.databinding.ActivityMainBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.ui.board.AccessControlDialogFragment;
import it.niedermann.nextcloud.deck.ui.board.EditBoardDialogFragment;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.ui.helper.dnd.CrossTabDragAndDrop;
import it.niedermann.nextcloud.deck.ui.stack.EditStackDialogFragment;
import it.niedermann.nextcloud.deck.ui.stack.StackAdapter;
import it.niedermann.nextcloud.deck.ui.stack.StackFragment;
import it.niedermann.nextcloud.deck.util.DeleteDialogBuilder;
import it.niedermann.nextcloud.deck.util.ExceptionUtil;
import it.niedermann.nextcloud.deck.util.ViewUtil;

import static it.niedermann.nextcloud.deck.DeckLog.Severity.INFO;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_STACK_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.NO_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.stack.EditStackDialogFragment.NO_STACK_ID;

public class MainActivity extends DrawerActivity implements
        EditStackDialogFragment.EditStackListener,
        EditBoardDialogFragment.EditBoardListener,
        StackFragment.OnScrollListener {

    private static final String TAG = MainActivity.class.getCanonicalName();

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
    private @Nullable
    List<Board> boardsList;
    private LiveData<List<Board>> boardsLiveData;
    private Observer<List<Board>> boardsLiveDataObserver;

    private long currentBoardId = 0;
    private boolean currentBoardHasEditPermission = false;
    private boolean currentBoardHasStacks = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        stackAdapter = new StackAdapter(getSupportFragmentManager());

        //TODO limit this call only to lower API levels like KitKat because they crash without
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

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

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                binding.viewPager.post(() -> {
                    // Remember last stack for this board
                    if (stackAdapter.getCount() >= position) {
                        long currentStackId = stackAdapter.getItem(position).getStackId();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        DeckLog.log("--- Write: shared_preference_last_stack_for_account_and_board_" + account.getId() + "_" + currentBoardId + " | " + currentStackId);
                        editor.putLong(sharedPreferencesLastStackForAccountAndBoard_ + account.getId() + "_" + currentBoardId, currentStackId);
                        editor.apply();
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
    }

    @Override
    public void onCreateStack(String stackName) {
        observeOnce(syncManager.getStacksForBoard(account.getId(), currentBoardId), MainActivity.this, fullStacks -> {
            Stack s = new Stack();
            s.setTitle(stackName);
            s.setBoardId(currentBoardId);
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

                // Remember last board for this account
                SharedPreferences.Editor editor = sharedPreferences.edit();
                DeckLog.log("--- Write: shared_preference_last_board_for_account_" + account.getId() + " | " + currentBoardId);
                editor.putLong(sharedPreferencesLastBoardForAccount_ + this.account.getId(), currentBoardId);
                editor.apply();
            }
        });
    }

    @Override
    public void onUpdateBoard(FullBoard fullBoard) {
        syncManager.updateBoard(fullBoard);
    }

    @Override
    protected void accountSet(@Nullable Account account) {
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

    @Override
    protected void boardSelected(int itemId, Account account) {
        if (boardsList != null) {
            Board selectedBoard = boardsList.get(itemId);
            currentBoardId = selectedBoard.getLocalId();
            displayStacksForBoard(selectedBoard, account);

            // Remember last board for this account
            SharedPreferences.Editor editor = sharedPreferences.edit();
            DeckLog.log("--- Write: shared_preference_last_board_for_account_" + account.getId() + " | " + currentBoardId);
            editor.putLong(sharedPreferencesLastBoardForAccount_ + this.account.getId(), currentBoardId);
            editor.apply();
        } else {
            DeckLog.logError(new IllegalStateException("boardsList is null but it shouldn't be."));
        }
    }

    @Override
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
}