package it.niedermann.nextcloud.deck.ui.about;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayoutMediator;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityAboutBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

public class AboutActivity extends BrandedActivity {
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

        setSupportActionBar(binding.toolbar);
        binding.viewPager.setAdapter(new TabsPagerAdapter(getSupportFragmentManager(), getLifecycle(), (Account) getIntent().getSerializableExtra(BUNDLE_KEY_ACCOUNT)));
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(tabTitles[position])).attach();

        setResult(RESULT_OK);
    }

    private static class TabsPagerAdapter extends FragmentStateAdapter {

        @Nullable
        private final Account account;

        TabsPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, @Nullable Account account) {
            super(fragmentManager, lifecycle);
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

    @Override
    public void applyBrand(int mainColor, int textColor) {
        applyBrandToPrimaryToolbar(mainColor, textColor, binding.toolbar);
        applyBrandToPrimaryTabLayout(mainColor, textColor, binding.tabLayout);
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context, @NonNull Account account) {
        return new Intent(context, AboutActivity.class)
                .putExtra(BUNDLE_KEY_ACCOUNT, account);
    }
}