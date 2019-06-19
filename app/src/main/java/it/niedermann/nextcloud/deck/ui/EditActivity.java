package it.niedermann.nextcloud.deck.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Card;
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
                Card card = new Card("", "", stackId);
                fullCard.setCard(card);
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
    protected void onPause() {
        if (NO_LOCAL_ID.equals(localId)) {
            Toast.makeText(getApplicationContext(), "Creating cards is not yet supported.", Toast.LENGTH_LONG).show();
            syncManager.createCard(accountId, boardId, stackId, fullCard.card).observe(EditActivity.this, (createdCard) -> {
                syncManager.getCardByLocalId(accountId, createdCard.getLocalId()).observe(EditActivity.this, (next) -> fullCard = next);
            });
        } else {
            syncManager.updateCard(fullCard.card);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
