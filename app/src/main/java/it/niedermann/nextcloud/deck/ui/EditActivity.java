package it.niedermann.nextcloud.deck.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.CardTabAdapter;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_BOARD_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_STACK_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.NO_LOCAL_ID;

public class EditActivity extends AppCompatActivity {

    SyncManager syncManager;

    @BindView(R.id.title)
    EditText title;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.pager)
    ViewPager pager;

    private Unbinder unbinder;

    private FullCard fullCard;

    private long accountId;
    private long boardId;
    private long stackId;
    private long localId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.activity_edit);
        unbinder = ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (fullCard != null) {
                    fullCard.getCard().setTitle(title.getText().toString());
                }
                String prefix = NO_LOCAL_ID.equals(localId) ? getString(R.string.create_card) : getString(R.string.edit);
                Objects.requireNonNull(actionBar).setTitle(prefix + " " + title.getText());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accountId = extras.getLong(BUNDLE_KEY_ACCOUNT_ID);
            boardId = extras.getLong(BUNDLE_KEY_BOARD_ID);
            stackId = extras.getLong(BUNDLE_KEY_STACK_ID);
            localId = extras.getLong(BUNDLE_KEY_LOCAL_ID);
            syncManager = new SyncManager(this);

            if (NO_LOCAL_ID.equals(localId)) {
                Objects.requireNonNull(actionBar).setTitle(getString(R.string.create_card));
                fullCard = new FullCard();
                Card pristineCard = new Card("", "", stackId);
                pristineCard.setAccountId(accountId);
                fullCard.setCard(pristineCard);

                try { // FIXME this might happen delayed so the user might not be available onStop()
                    LiveData<User> userLiveData = syncManager.getUserByUid(accountId, SingleAccountHelper.getCurrentSingleSignOnAccount(getApplicationContext()).userId);
                    Observer<User> userObserver = new Observer<User>() {
                        @Override
                        public void onChanged(User user) {
                            userLiveData.removeObserver(this);
                            fullCard.card.setUserId(user.getLocalId());
                        }
                    };
                    userLiveData.observe(this, userObserver);
                } catch (NextcloudFilesAppAccountNotFoundException | NoCurrentAccountSelectedException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "An error appeared while creating the card.", Toast.LENGTH_LONG).show();
                }
            } else {
                syncManager.getCardByLocalId(accountId, localId)
                        .observe(EditActivity.this, (next) -> {
                            fullCard = next;
                            title.setText(fullCard.getCard().getTitle());
                        });
            }
        } else {
            throw new IllegalArgumentException("No localId argument");
        }

        setupViewPager();
    }

    private void setupViewPager() {
        tabLayout.removeAllTabs();
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        CardTabAdapter adapter = new CardTabAdapter(getSupportFragmentManager(), getResources(), accountId, localId, boardId);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
    }

    @Override
    protected void onStop() {
        if (NO_LOCAL_ID.equals(localId)) {
            if (fullCard.getCard().getTitle().isEmpty()) {
                if (!fullCard.getCard().getDescription().isEmpty()) {
                    fullCard.getCard().setTitle(fullCard.getCard().getDescription().split("\n")[0]);
                } else {
                    Toast.makeText(getApplicationContext(), "Provide at least a title or a description.", Toast.LENGTH_LONG).show();
                    super.onStop();
                    return;
                }
            }
            syncManager.createCard(accountId, boardId, stackId, fullCard.card);
        } else {
            syncManager.updateCard(fullCard.card);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public void setDescription(String description) {
        this.fullCard.card.setDescription(description);
    }

    public void setDueDate(Date dueDate) {
        this.fullCard.card.setDueDate(dueDate);
    }
}
