package it.niedermann.nextcloud.deck.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.CardTabAdapter;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_STACK_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.NO_LOCAL_ID;

public class EditActivity extends AppCompatActivity {

    SyncManager syncManager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.titleTextInputLayout)
    TextInputLayout titleTextInputLayout;
    @BindView(R.id.title)
    EditText title;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.pager)
    ViewPager pager;

    @BindString(R.string.simple_add)
    String add;
    @BindString(R.string.edit)
    String edit;

    private Unbinder unbinder;
    private boolean modified = false;

    private FullCard fullCard;

    private long accountId;
    private long boardId;
    private long stackId;
    private long localId;

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
        if (extras != null) {
            accountId = extras.getLong(BUNDLE_KEY_ACCOUNT_ID);
            boardId = extras.getLong(BUNDLE_KEY_BOARD_ID);
            stackId = extras.getLong(BUNDLE_KEY_STACK_ID);
            localId = extras.getLong(BUNDLE_KEY_LOCAL_ID);
            syncManager = new SyncManager(this);

            createMode = NO_LOCAL_ID.equals(localId);
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
                        setupViewPager();
                        setupTitle(createMode);
                    });
                }
            }));
        } else {
            throw new IllegalArgumentException("No localId argument");
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
        switch (item.getItemId()) {
            case R.id.action_card_save:
                saveAndFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAndFinish() {
        //FIXME: nullpointer when no title entered! do not even save without title(?)
        if (fullCard.getCard().getTitle() != null && fullCard.getCard().getTitle().isEmpty()) {
            if (!fullCard.getCard().getDescription().isEmpty()) {
                fullCard.getCard().setTitle(fullCard.getCard().getDescription().split("\n")[0]);
            } else {
                fullCard.getCard().setTitle("");
            }
        }
        if (createMode) {
            observeOnce(syncManager.createFullCard(accountId, boardId, stackId, fullCard), EditActivity.this, (card) -> finish());
        } else {
            observeOnce(syncManager.updateCard(fullCard), EditActivity.this, (card) -> finish());
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
                    modified = true;
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

    public void setTitle(String title) {
        if (fullCard != null) {
            fullCard.getCard().setTitle(title);
        }
        modified = true;
    }

    public void setDescription(String description) {
        this.fullCard.getCard().setDescription(description);
        modified = true;
    }


    public void addUser(User user) {
        this.fullCard.getAssignedUsers().add(user);
    }

    public void removeUser(User user) {
        this.fullCard.getAssignedUsers().remove(user);
    }


    public void addLabel(Label label) {
        this.fullCard.getLabels().add(label);
    }


    public void removeLabel(Label label) {
        this.fullCard.getLabels().remove(label);
    }

    public void setDueDate(Date dueDate) {
        this.fullCard.getCard().setDueDate(dueDate);
        modified = true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return true;
    }

    @Override
    public void onBackPressed() {
        if (modified && canEdit) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.simple_save)
                    .setMessage(R.string.do_you_want_to_save_your_changes)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> saveAndFinish())
                    .setNegativeButton(R.string.simple_discard, (dialog, whichButton) -> super.onBackPressed()).show();
        } else {
            super.onBackPressed();
        }
    }
}
