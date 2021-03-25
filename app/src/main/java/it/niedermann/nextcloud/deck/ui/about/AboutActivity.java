package it.niedermann.nextcloud.deck.ui.about;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayoutMediator;

import it.niedermann.nextcloud.deck.DeckApplication;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityAboutBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.applyBrandToPrimaryTabLayout;

public class AboutActivity extends AppCompatActivity {
    private static final String BUNDLE_KEY_ACCOUNT = "account";

    private ActivityAboutBinding binding;
    private final static int[] tabTitles = new int[]{
            R.string.about_credits_tab_title,
            R.string.about_contribution_tab_title,
            R.string.about_license_tab_title
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DeckApplication.readCurrentAccountColor().observe(this, (mainColor) -> applyBrandToPrimaryTabLayout(mainColor, binding.tabLayout));

        setSupportActionBar(binding.toolbar);
        binding.viewPager.setAdapter(new TabsPagerAdapter(this, (Account) getIntent().getSerializableExtra(BUNDLE_KEY_ACCOUNT)));
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(tabTitles[position])).attach();
    }

    private static class TabsPagerAdapter extends FragmentStateAdapter {

        @Nullable
        private final Account account;

        TabsPagerAdapter(final FragmentActivity fa, @Nullable Account account) {
            super(fa);
            this.account = account;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return AboutFragmentCreditsTab.newInstance(account);
                case 1:
                    return new AboutFragmentContributingTab();
                case 2:
                    return new AboutFragmentLicenseTab();
                default:
                    throw new IllegalArgumentException("position must be between 0 and 2");
            }
        }

        @Override
        public int getItemCount() {
            return tabTitles.length;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return true;
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context, @NonNull Account account) {
        return new Intent(context, AboutActivity.class)
                .putExtra(BUNDLE_KEY_ACCOUNT, account);
    }
}