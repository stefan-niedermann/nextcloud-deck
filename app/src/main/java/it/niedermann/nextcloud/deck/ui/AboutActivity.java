package it.niedermann.nextcloud.deck.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityAboutBinding;
import it.niedermann.nextcloud.deck.ui.about.AboutFragmentContributingTab;
import it.niedermann.nextcloud.deck.ui.about.AboutFragmentCreditsTab;
import it.niedermann.nextcloud.deck.ui.about.AboutFragmentLicenseTab;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Application.getAppTheme(this) ? R.style.DarkAppTheme : R.style.AppTheme);
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        ActivityAboutBinding binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.viewPager.setAdapter(new TabsPagerAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }

    private class TabsPagerAdapter extends FragmentPagerAdapter {

        @SuppressWarnings("WeakerAccess")
        public TabsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getCount() {
            return 3;
        }

        /**
         * return the right fragment for the given position
         */
        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new AboutFragmentCreditsTab();
                case 1:
                    return new AboutFragmentContributingTab();
                case 2:
                    return new AboutFragmentLicenseTab();
                default:
                    throw new IllegalArgumentException("position must be between 0 and 2");
            }
        }

        /**
         * generate title based on given position
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.about_credits_tab_title);
                case 1:
                    return getString(R.string.about_contribution_tab_title);
                case 2:
                    return getString(R.string.about_license_tab_title);
                default:
                    throw new IllegalArgumentException("position must be between 0 and 2");
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return true;
    }
}