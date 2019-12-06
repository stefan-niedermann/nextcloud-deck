package it.niedermann.nextcloud.deck.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SpinnerAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.board.BoardAdapter;
import it.niedermann.nextcloud.deck.ui.card.CardDetailsFragment;
import it.niedermann.nextcloud.deck.ui.card.CardTabAdapter;
import it.niedermann.nextcloud.deck.ui.card.CommentDialogFragment;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_STACK_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.NO_LOCAL_ID;

public class EditActivity extends AppCompatActivity implements
        CardDetailsFragment.CardDetailsListener,
        CommentDialogFragment.AddCommentListener,
        AdapterView.OnItemSelectedListener {

    SyncManager syncManager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.titleTextInputLayout)
    TextInputLayout titleTextInputLayout;
    @BindView(R.id.title)
    EditText title;
    @BindView(R.id.selectBoardWrapper)
    View selectBoardWrapper;
    @BindView(R.id.boardSelector)
    AppCompatSpinner boardSelector;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.pager)
    ViewPager pager;

    @BindString(R.string.shared_preference_last_board_for_account_)
    String sharedPreferencesLastBoardForAccount_;
    @BindString(R.string.shared_preference_last_stack_for_account_and_board_)
    String sharedPreferencesLastStackForAccountAndBoard_;
    @BindString(R.string.simple_add)
    String add;
    @BindString(R.string.edit)
    String edit;

    private Unbinder unbinder;

    private FullCard originalCard;
    private FullCard fullCard;

    private long accountId;
    private long boardId;
    private long stackId;
    private long localId;

    private boolean pendingCreation = false;
    private boolean canEdit;
    private boolean createMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(Application.getAppTheme(this) ? R.style.DarkAppTheme : R.style.AppTheme);
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.activity_edit);
        unbinder = ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            throw new IllegalArgumentException("Provide localId");
        }
        accountId = extras.getLong(BUNDLE_KEY_ACCOUNT_ID);
        boardId = extras.getLong(BUNDLE_KEY_BOARD_ID);
        stackId = extras.getLong(BUNDLE_KEY_STACK_ID);
        localId = extras.getLong(BUNDLE_KEY_LOCAL_ID);
        syncManager = new SyncManager(this);

        createMode = NO_LOCAL_ID.equals(localId);
        if (boardId == 0L) {
            try {
                createMode = true;
                SingleSignOnAccount ssoa = SingleAccountHelper.getCurrentSingleSignOnAccount(this);
                syncManager.readAccount(ssoa.name).observe(this, (Account account) -> {
                    accountId = account.getId();
                    selectBoardWrapper.setVisibility(View.VISIBLE);
                    syncManager.getBoards(account.getId()).observe(this, (List<Board> boardsList) -> {
                        for (Board board : boardsList) {
                            if (!board.isPermissionEdit()) {
                                boardsList.remove(board);
                            }
                        }
                        Board[] boardsArray = new Board[boardsList.size()];
                        boardsArray = boardsList.toArray(boardsArray);
                        SpinnerAdapter adapter = new BoardAdapter(this, boardsArray);
                        boardSelector.setAdapter(adapter);

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                        long lastBoardId = sharedPreferences.getLong(sharedPreferencesLastBoardForAccount_ + accountId, 0L);
                        DeckLog.log("--- Read: shared_preference_last_board_for_account_" + account.getId() + " | " + lastBoardId);
                        if (lastBoardId != 0L) {
                            for (int i = 0; i < boardsArray.length; i++) {
                                if (boardsArray[i].getLocalId() == lastBoardId) {
                                    boardSelector.setSelection(i);
                                }
                            }
                        }
                        boardSelector.setOnItemSelectedListener(this);
                    });
                });
            } catch (NextcloudFilesAppAccountNotFoundException | NoCurrentAccountSelectedException e) {
                e.printStackTrace();
            }
        } else {
            if (accountId == 0L) {
                throw new IllegalArgumentException("No accountId given");
            }
            observeOnce(syncManager.getFullBoardById(accountId, boardId), EditActivity.this, (fullBoard -> {
                canEdit = fullBoard.getBoard().isPermissionEdit();
                invalidateOptionsMenu();
                if (createMode) {
                    fullCard = new FullCard();
                    fullCard.setLabels(new ArrayList<>());
                    fullCard.setAssignedUsers(new ArrayList<>());
                    Card card = new Card();
                    card.setStackId(stackId);
                    fullCard.setCard(card);
                    setupViewPager();
                    setupTitle(createMode);
                } else {
                    observeOnce(syncManager.getCardByLocalId(accountId, localId), EditActivity.this, (next) -> {
                        fullCard = next;
                        originalCard = new FullCard(fullCard);
                        setupViewPager();
                        setupTitle(createMode);
                    });
                }
            }));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (canEdit) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.card_edit_menu, menu);
        } else {
            menu.clear();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_card_save) {
            saveAndFinish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAndFinish() {
        if (!pendingCreation) {
            pendingCreation = true;
            //FIXME: nullpointer when no title entered! do not even save without title(?)
            if (fullCard.getCard().getTitle() != null && fullCard.getCard().getTitle().isEmpty()) {
                if (!fullCard.getCard().getDescription().isEmpty()) {
                    fullCard.getCard().setTitle(fullCard.getCard().getDescription().split("\n")[0]);
                } else {
                    fullCard.getCard().setTitle("");
                }
            }
            if (createMode) {
                observeOnce(syncManager.createFullCard(accountId, boardId, stackId, fullCard), EditActivity.this, (card) -> super.finish());
            } else {
                observeOnce(syncManager.updateCard(fullCard), EditActivity.this, (card) -> super.finish());
            }
        }
    }

    private void setupViewPager() {
        tabLayout.removeAllTabs();
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        CardTabAdapter adapter = new CardTabAdapter(getSupportFragmentManager(), this, accountId, localId, boardId, canEdit);
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
    }

    private void setupTitle(boolean createMode) {
        title.setText(fullCard.getCard().getTitle());
        if (canEdit) {
            if (createMode) {
                title.requestFocus();
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                if (fullCard.getCard().getTitle() != null) {
                    title.setSelection(fullCard.getCard().getTitle().length());
                }
            }
            titleTextInputLayout.setHint(createMode ? add : edit);
            title.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    fullCard.getCard().setTitle(title.getText().toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        } else {
            titleTextInputLayout.setHintEnabled(false);
            title.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onDescriptionChanged(String description) {
        this.fullCard.getCard().setDescription(description);
    }


    @Override
    public void onUserAdded(User user) {
        this.fullCard.getAssignedUsers().add(user);
    }

    @Override
    public void onUserRemoved(User user) {
        this.fullCard.getAssignedUsers().remove(user);
    }

    @Override
    public void onLabelAdded(Label label) {
        this.fullCard.getLabels().add(label);
    }

    @Override
    public void onLabelRemoved(Label label) {
        this.fullCard.getLabels().remove(label);
    }

    @Override
    public void onDueDateChanged(Date dueDate) {
        this.fullCard.getCard().setDueDate(dueDate);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        if (!fullCard.equals(originalCard) && canEdit) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.simple_save)
                    .setMessage(R.string.do_you_want_to_save_your_changes)
                    .setPositiveButton(R.string.simple_save, (dialog, whichButton) -> saveAndFinish())
                    .setNegativeButton(R.string.simple_discard, (dialog, whichButton) -> super.finish()).show();
        } else {
            super.finish();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        boardId = ((Board) boardSelector.getItemAtPosition(position)).getLocalId();
        observeOnce(syncManager.getFullBoardById(accountId, boardId), EditActivity.this, (fullBoard -> {
            canEdit = fullBoard.getBoard().isPermissionEdit();

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            long savedStackId = sharedPreferences.getLong(sharedPreferencesLastStackForAccountAndBoard_ + accountId + "_" + boardId, 0L);
            DeckLog.log("--- Read: shared_preference_last_stack_for_account_and_board" + accountId + "_" + boardId + " | " + savedStackId);
            if (savedStackId == 0L) {
                observeOnce(syncManager.getStacksForBoard(accountId, boardId), EditActivity.this, (stacks -> {
                    if (stacks != null && stacks.size() > 0) {
                        stackId = stacks.get(0).getLocalId();
                    }
                }));
            } else {
                stackId = savedStackId;
            }
            if (fullCard == null) {
                invalidateOptionsMenu();
                fullCard = new FullCard();
                fullCard.setLabels(new ArrayList<>());
                fullCard.setAssignedUsers(new ArrayList<>());
                Card card = new Card();
                card.setStackId(stackId);
                fullCard.setCard(card);
                setupViewPager();
                setupTitle(createMode);
            }
        }));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCommentAdded(String comment) {
        syncManager.addCommentToCard(accountId, boardId, localId, comment);
    }
}
