package it.niedermann.nextcloud.deck.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityEditBinding;
import it.niedermann.nextcloud.deck.model.viewmodel.FullCardViewModel;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.CardTabAdapter;

import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;

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
                if (actionBar != null) {
                    actionBar.setTitle(getString(R.string.edit) + " " + title.getText());
                }
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
            localId = extras.getLong(BUNDLE_KEY_LOCAL_ID);
            syncManager = new SyncManager(getApplicationContext(), this);

            fullCardViewModel.fullCard = syncManager.getCardByLocalId(accountId, localId);
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
        // TODO ????
        if (fullCardViewModel.fullCard.getValue() != null) {
            syncManager.updateCard(fullCardViewModel.fullCard.getValue().card);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
