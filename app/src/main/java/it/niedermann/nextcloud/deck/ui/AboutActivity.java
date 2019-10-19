package it.niedermann.nextcloud.deck.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.ui.about.AboutFragmentContributingTab;
import it.niedermann.nextcloud.deck.ui.about.AboutFragmentCreditsTab;
import it.niedermann.nextcloud.deck.ui.about.AboutFragmentLicenseTab;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.pager)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    @BindString(R.string.about_credits_tab_title)
    String creditsTitle;
    @BindString(R.string.about_license_tab_title)
    String licenseTitle;
    @BindString(R.string.about_contribution_tab_title)
    String contributionTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Application.getAppTheme(this) ? R.style.DarkAppTheme : R.style.AppTheme);
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        mViewPager.setAdapter(new TabsPagerAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private class TabsPagerAdapter extends FragmentPagerAdapter {
        private final int PAGE_COUNT = 3;

        public TabsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
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
                    return creditsTitle;

                case 1:
                    return contributionTitle;

                case 2:
                    return licenseTitle;

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