package it.niedermann.nextcloud.deck.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SpinnerAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityEditBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.board.BoardAdapter;
import it.niedermann.nextcloud.deck.ui.card.CardAttachmentsFragment.NewCardAttachmentHandler;
import it.niedermann.nextcloud.deck.ui.card.CardDetailsFragment.CardDetailsListener;
import it.niedermann.nextcloud.deck.ui.card.CardTabAdapter;
import it.niedermann.nextcloud.deck.ui.card.CommentDialogFragment.AddCommentListener;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.util.CardUtil;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_STACK_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.NO_LOCAL_ID;

public class EditActivity extends AppCompatActivity implements CardDetailsListener, AddCommentListener, NewCardAttachmentHandler, OnItemSelectedListener {

    private ActivityEditBinding binding;
    private SyncManager syncManager;

    private static final int[] tabTitles = new int[]{
            R.string.card_edit_details,
            R.string.card_edit_attachments,
            R.string.card_edit_activity
    };

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

        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

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
                    binding.selectBoardWrapper.setVisibility(View.VISIBLE);
                    syncManager.getBoards(account.getId()).observe(this, (List<Board> boardsList) -> {
                        for (Board board : boardsList) {
                            if (!board.isPermissionEdit()) {
                                boardsList.remove(board);
                            }
                        }
                        Board[] boardsArray = new Board[boardsList.size()];
                        boardsArray = boardsList.toArray(boardsArray);
                        SpinnerAdapter adapter = new BoardAdapter(this, boardsArray);
                        binding.boardSelector.setAdapter(adapter);

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                        long lastBoardId = sharedPreferences.getLong(getString(R.string.shared_preference_last_board_for_account_) + accountId, 0L);
                        DeckLog.log("--- Read: shared_preference_last_board_for_account_" + account.getId() + " | " + lastBoardId);
                        if (lastBoardId != 0L) {
                            for (int i = 0; i < boardsArray.length; i++) {
                                if (boardsArray[i].getLocalId() == lastBoardId) {
                                    binding.boardSelector.setSelection(i);
                                }
                            }
                        }
                        binding.boardSelector.setOnItemSelectedListener(this);
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
                    originalCard = new FullCard();
                    fullCard.setLabels(new ArrayList<>());
                    fullCard.setAssignedUsers(new ArrayList<>());
                    fullCard.setAttachments(new ArrayList<>());
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
            if (fullCard.getCard().getTitle() == null || fullCard.getCard().getTitle().isEmpty()) {
                fullCard.getCard().setTitle(CardUtil.generateTitleFromDescription(fullCard.getCard().getDescription()));
            }
            if (fullCard.getCard().getTitle().isEmpty()) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_is_mandatory)
                        .setMessage(R.string.provide_at_least_a_title_or_description)
                        .setPositiveButton(android.R.string.ok, null)
                        .setOnDismissListener(dialog -> {
                            pendingCreation = false;
                        })
                        .show();
            } else {
                if (createMode) {
                    observeOnce(syncManager.createFullCard(accountId, boardId, stackId, fullCard), EditActivity.this, (card) -> super.finish());
                } else {
                    observeOnce(syncManager.updateCard(fullCard), EditActivity.this, (card) -> super.finish());
                }
            }
        }
    }

    private void setupViewPager() {
        binding.tabLayout.removeAllTabs();
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        CardTabAdapter adapter = new CardTabAdapter(getSupportFragmentManager(), getLifecycle(), accountId, localId, boardId, canEdit);
        binding.pager.setOffscreenPageLimit(2);
        binding.pager.setAdapter(adapter);
        new TabLayoutMediator(binding.tabLayout, binding.pager, (tab, position) -> tab.setText(tabTitles[position])).attach();
    }

    private void setupTitle(boolean createMode) {
        binding.title.setText(fullCard.getCard().getTitle());
        if (canEdit) {
            if (createMode) {
                binding.title.requestFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                if (fullCard.getCard().getTitle() != null) {
                    binding.title.setSelection(fullCard.getCard().getTitle().length());
                }
            }
            binding.titleTextInputLayout.setHint(getString(createMode ? R.string.simple_add : R.string.edit));
            binding.title.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    fullCard.getCard().setTitle(binding.title.getText().toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        } else {
            binding.titleTextInputLayout.setHintEnabled(false);
            binding.title.setEnabled(false);
        }
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
        boardId = ((Board) binding.boardSelector.getItemAtPosition(position)).getLocalId();
        observeOnce(syncManager.getFullBoardById(accountId, boardId), EditActivity.this, (fullBoard -> {
            canEdit = fullBoard.getBoard().isPermissionEdit();

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            long savedStackId = sharedPreferences.getLong(getString(R.string.shared_preference_last_stack_for_account_and_board_) + accountId + "_" + boardId, 0L);
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

    @Override
    public void attachmentAdded(Attachment attachment) {
        fullCard.getAttachments().add(attachment);
    }

    @Override
    public void attachmentRemoved(Attachment attachment) {
        fullCard.getAttachments().remove(attachment);
    }
}
