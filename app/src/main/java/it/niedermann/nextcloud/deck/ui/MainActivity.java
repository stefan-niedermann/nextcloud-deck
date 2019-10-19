package it.niedermann.nextcloud.deck.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.h6ah4i.android.tablayouthelper.TabLayoutHelper;

import java.util.List;
import java.util.Objects;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
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
import it.niedermann.nextcloud.deck.util.ViewUtil;

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


    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.navigationView)
    NavigationView navigationView;
    @BindView(R.id.stackLayout)
    TabLayout stackLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.no_stacks)
    RelativeLayout noStacks;

    @BindString(R.string.shared_preference_last_board_for_account_)
    String sharedPreferencesLastBoardForAccount_;
    @BindString(R.string.shared_preference_last_stack_for_account_and_board_)
    String sharedPreferencesLastStackForAccountAndBoard_;
    @BindString(R.string.simple_settings)
    String simpleSettings;
    @BindString(R.string.simple_boards)
    String simpleBoards;
    @BindString(R.string.about)
    String about;
    @BindString(R.string.share_board)
    String shareBoard;
    @BindString(R.string.edit_board)
    String editBoard;
    @BindString(R.string.add_column)
    String addColumn;
    @BindString(R.string.action_card_list_rename_column)
    String actionCardListRenameColumn;

    private StackAdapter stackAdapter;
    private List<Board> boardsList;
    private LiveData<List<Board>> boardsLiveData;
    private Observer<List<Board>> boardsLiveDataObserver;

    private long currentBoardId = 0;
    private boolean currentBoardHasEditPermission = false;
    private boolean currentBoardHasStacks = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Application.getAppTheme(this) ? R.style.DarkAppTheme : R.style.AppTheme);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        stackAdapter = new StackAdapter(getSupportFragmentManager());

        //TODO limit this call only to lower API levels like KitKat because they crash without
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        CrossTabDragAndDrop dragAndDrop = new CrossTabDragAndDrop(this);
        dragAndDrop.register(viewPager);
        dragAndDrop.addCardMovedByDragListener((movedCard, stackId, position) -> {
            //FIXME: implement me por favour!
            syncManager.reorder(account.getId(), movedCard, stackId, position);
            DeckLog.log("Card \"" + movedCard.getCard().getTitle() + "\" was moved to Stack " + stackId + " on position " + position);
        });

        fab.setOnClickListener((View view) -> {
            if (this.boardsList == null) {
                Snackbar.make(coordinatorLayout, "Please add an account first", Snackbar.LENGTH_LONG);
            } else if (this.boardsList.size() > 0) {
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra(BUNDLE_KEY_ACCOUNT_ID, account.getId());
                intent.putExtra(BUNDLE_KEY_LOCAL_ID, NO_LOCAL_ID);
                intent.putExtra(BUNDLE_KEY_BOARD_ID, currentBoardId);
                try {
                    intent.putExtra(BUNDLE_KEY_STACK_ID, stackAdapter.getItem(viewPager.getCurrentItem()).getStackId());
                    startActivity(intent);
                } catch (IndexOutOfBoundsException e) {
                    EditStackDialogFragment.newInstance(NO_STACK_ID)
                            .show(getSupportFragmentManager(), addColumn);
                }
            } else {
                EditBoardDialogFragment.newInstance().show(getSupportFragmentManager(), addBoard);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.post(() -> {
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

            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> syncManager.synchronize(new IResponseCallback<Boolean>(account) {
            @Override
            public void onResponse(Boolean response) {
                runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));
            }

            @Override
            public void onError(Throwable throwable) {
                runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));
                DeckLog.logError(throwable);
            }
        }));
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
                viewPager.setCurrentItem(stackAdapter.getCount());
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
                Snackbar.make(coordinatorLayout, "Open Deck in web interface first!", Snackbar.LENGTH_LONG);
            } else {
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
    protected void accountSet(Account account) {
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
    }

    @Override
    protected void boardSelected(int itemId, Account account) {
        Board selectedBoard = boardsList.get(itemId);
        currentBoardId = selectedBoard.getLocalId();
        displayStacksForBoard(selectedBoard, account);

        // Remember last board for this account
        SharedPreferences.Editor editor = sharedPreferences.edit();
        DeckLog.log("--- Write: shared_preference_last_board_for_account_" + account.getId() + " | " + currentBoardId);
        editor.putLong(sharedPreferencesLastBoardForAccount_ + this.account.getId(), currentBoardId);
        editor.apply();
    }

    @Override
    protected void buildSidenavMenu() {
        navigationView.setItemIconTintList(null);
        Menu menu = navigationView.getMenu();
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
                                    Snackbar.make(drawer, "Archiving boards is not yet supported.", Snackbar.LENGTH_LONG).show();
                                    break;
                                case R.id.delete_board:
                                    new DeleteDialogBuilder(this)
                                            .setTitle(R.string.delete_board)
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
                                                drawer.closeDrawer(GravityCompat.START);
                                            })
                                            .setNegativeButton(R.string.simple_cancel, (dialog, which) -> {
                                            })
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
        }
        boardsMenu.add(Menu.NONE, MENU_ID_ADD_BOARD, Menu.NONE, addBoard).setIcon(R.drawable.ic_add_grey_24dp);
        menu.add(Menu.NONE, MENU_ID_SETTINGS, Menu.NONE, simpleSettings).setIcon(R.drawable.ic_settings_grey600_24dp);
        menu.add(Menu.NONE, MENU_ID_ABOUT, Menu.NONE, about).setIcon(R.drawable.ic_info_outline_grey_24dp);
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
    }

    int stackPositionInAdapter = 0;

    /**
     * Displays the Stacks for the boardsList by index
     *
     * @param board Board
     */
    private void displayStacksForBoard(Board board, Account account) {
        toolbar.setTitle(board.getTitle());

        currentBoardHasEditPermission = board.isPermissionEdit();
        if (currentBoardHasEditPermission) {
            fab.show();
        } else {
            fab.hide();
        }

        syncManager.getStacksForBoard(account.getId(), board.getLocalId()).observe(MainActivity.this, (List<FullStack> fullStacks) -> {
            if (fullStacks == null || fullStacks.size() == 0) {
                noStacks.setVisibility(View.VISIBLE);
                currentBoardHasStacks = false;
            } else {
                noStacks.setVisibility(View.GONE);
                currentBoardHasStacks = true;

                long savedStackId = sharedPreferences.getLong(sharedPreferencesLastStackForAccountAndBoard_ + account.getId() + "_" + this.currentBoardId, NO_STACKS);
                DeckLog.log("--- Read: shared_preference_last_stack_for_account_and_board" + account.getId() + "_" + this.currentBoardId + " | " + savedStackId);

                StackAdapter newStackAdapter = new StackAdapter(getSupportFragmentManager());
                for (int i = 0; i < fullStacks.size(); i++) {
                    FullStack stack = fullStacks.get(i);
                    newStackAdapter.addFragment(StackFragment.newInstance(board.getLocalId(), stack.getStack().getLocalId(), account, currentBoardHasEditPermission), stack.getStack().getTitle());
                    if (stack.getLocalId() == savedStackId) {
                        stackPositionInAdapter = i;
                    }
                }
                stackAdapter = newStackAdapter;
                runOnUiThread(() -> {
                    viewPager.setAdapter(newStackAdapter);

                    new TabLayoutHelper(stackLayout, viewPager).setAutoAdjustTabModeEnabled(true);

                    viewPager.setCurrentItem(stackPositionInAdapter);
                    stackLayout.setupWithViewPager(viewPager);
                });
            }
            invalidateOptionsMenu();
        });
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
                            long stackId = stackAdapter.getItem(viewPager.getCurrentItem()).getStackId();
                            observeOnce(syncManager.getStack(account.getId(), stackId), MainActivity.this, fullStack -> {
                                DeckLog.log("Delete stack #" + fullStack.getLocalId() + ": " + fullStack.getStack().getTitle());
                                syncManager.deleteStack(fullStack.getStack());
                            });
                        })
                        .setNegativeButton(android.R.string.cancel, null).show();
                break;
            case R.id.action_card_list_add_column:
                EditStackDialogFragment.newInstance(NO_STACK_ID)
                        .show(getSupportFragmentManager(), addColumn);
                break;
            case R.id.action_card_list_rename_column:
                // TODO call newInstance with old stack name
                EditStackDialogFragment.newInstance(stackAdapter.getItem(viewPager.getCurrentItem()).getStackId())
                        .show(getSupportFragmentManager(), actionCardListRenameColumn);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFabIfEditPermissionGranted() {
        if (currentBoardHasEditPermission) {
            this.fab.show();
        }
    }

    @Override
    public void onScrollUp() {
        showFabIfEditPermissionGranted();
    }

    @Override
    public void onScrollDown() {
        this.fab.hide();
    }
}