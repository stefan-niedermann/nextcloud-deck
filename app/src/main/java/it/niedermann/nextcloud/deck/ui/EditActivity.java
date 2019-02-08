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
import it.niedermann.nextcloud.deck.ui.card.CardTabAdapter;

import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;

public class EditActivity extends AppCompatActivity {

    @BindView(R.id.title)
    EditText title;

    @BindView(R.id.timestamps)
    TextView timestamps;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.pager)
    ViewPager pager;

    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        unbinder = ButterKnife.bind(this);

        long localId;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            localId = extras.getLong(BUNDLE_KEY_LOCAL_ID);
        }

        setupViewPager();
    }

    private void setupViewPager() {
        tabLayout.removeAllTabs();

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        CardTabAdapter adapter = new CardTabAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        tabLayout.setupWithViewPager(pager);
    }
}
