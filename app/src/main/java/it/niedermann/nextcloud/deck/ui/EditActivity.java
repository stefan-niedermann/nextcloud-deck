package it.niedermann.nextcloud.deck.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.SupportUtil;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.CardTabAdapter;

import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;

public class EditActivity extends AppCompatActivity {

    FullCard card;
    SyncManager syncManager;

    @BindView(R.id.title)
    EditText title;

    @BindView(R.id.timestamps)
    TextView timestamps;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.pager)
    ViewPager pager;

    private Unbinder unbinder;

    private long accountId;
    private long localId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        unbinder = ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accountId = extras.getLong(BUNDLE_KEY_ACCOUNT_ID);
            localId = extras.getLong(BUNDLE_KEY_LOCAL_ID);
            syncManager = new SyncManager(getApplicationContext(), this);

            syncManager.getCardByLocalId(accountId, localId)
                    .observe(EditActivity.this, (FullCard card) -> {
                        this.card = card;
                        if (this.card != null) {
                            title.setText(this.card.getCard().getTitle());
                            if (this.card.getCard().getCreatedAt() != null
                                    && this.card.getCard().getLastModified() != null) {
                                timestamps.setText(
                                        getString(
                                                R.string.modified_created_time,
                                                SupportUtil.getRelativeDateTimeString(
                                                        this,
                                                        this.card.getCard().getLastModified().getTime()),
                                                SupportUtil.getRelativeDateTimeString(
                                                        this,
                                                        this.card.getCard().getCreatedAt().getTime())
                                        )
                                );
                            }
                        }
                    });
        } else {
            throw new IllegalArgumentException("No localId argument");
        }

        setupViewPager();
    }

    private void setupViewPager() {
        tabLayout.removeAllTabs();

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        CardTabAdapter adapter = new CardTabAdapter(getSupportFragmentManager(), accountId, localId);
        pager.setAdapter(adapter);

        tabLayout.setupWithViewPager(pager);
    }

    @Override
    protected void onPause() {
        syncManager.updateCard(this.card.card);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
