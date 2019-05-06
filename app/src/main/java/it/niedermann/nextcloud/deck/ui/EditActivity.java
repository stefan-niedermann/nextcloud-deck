package it.niedermann.nextcloud.deck.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityEditBinding;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.viewmodel.FullCardViewModel;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.CardTabAdapter;

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

    FullCardViewModel fullCardViewModel;
    private Unbinder unbinder;

    private long accountId;
    private long boardId;
    private long stackId;
    private long localId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullCardViewModel = ViewModelProviders.of(this)
                .get(FullCardViewModel.class);

        ActivityEditBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_edit);

        // Assign the component to a property in the binding class.
        binding.setLifecycleOwner(this);
        binding.setEditmodel(fullCardViewModel);

        //setContentView(R.layout.activity_edit);
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
            syncManager = new SyncManager(getApplicationContext(), this);

            if (NO_LOCAL_ID.equals(localId)) {
                Objects.requireNonNull(actionBar).setTitle(getString(R.string.create_card));
                // FIXME
                fullCardViewModel.fullCard = new LiveData<FullCard>() {
                    @Override
                    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super FullCard> observer) {
                        super.observe(owner, observer);
                    }
                };
            } else {
                fullCardViewModel.fullCard = syncManager.getCardByLocalId(accountId, localId);
            }
        } else {
            throw new IllegalArgumentException("No localId argument");
        }

        setupViewPager();
    }

    private void setupViewPager() {
        tabLayout.removeAllTabs();
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        CardTabAdapter adapter = new CardTabAdapter(getSupportFragmentManager(), accountId, localId, boardId);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
    }

    @Override
    protected void onPause() {
        if (fullCardViewModel.fullCard != null && fullCardViewModel.fullCard.getValue() != null) {
            if (NO_LOCAL_ID.equals(localId)) {
                Toast.makeText(getApplicationContext(), "Creating cards is not yet supported.", Toast.LENGTH_LONG).show();
                // TODO
//                syncManager.createCard(accountId, boardId, stackId, fullCardViewModel.fullCard.getValue().card).observe(EditActivity.this, (FullCard fullCard) -> {
//                    fullCardViewModel.fullCard = syncManager.getCardByLocalId(accountId, fullCard.getLocalId());
//                });
            } else {
                syncManager.updateCard(fullCardViewModel.fullCard.getValue().card);
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
