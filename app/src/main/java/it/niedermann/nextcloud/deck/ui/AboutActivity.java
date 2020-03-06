package it.niedermann.nextcloud.deck.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayoutMediator;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityAboutBinding;
import it.niedermann.nextcloud.deck.ui.about.AboutFragmentContributingTab;
import it.niedermann.nextcloud.deck.ui.about.AboutFragmentCreditsTab;
import it.niedermann.nextcloud.deck.ui.about.AboutFragmentLicenseTab;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

public class AboutActivity extends AppCompatActivity {

    private final static int[] tabTitles = new int[]{
            R.string.about_credits_tab_title,
            R.string.about_contribution_tab_title,
            R.string.about_license_tab_title
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Application.getAppTheme(this) ? R.style.DarkAppTheme : R.style.AppTheme);
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        ActivityAboutBinding binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.viewPager.setAdapter(new TabsPagerAdapter(getSupportFragmentManager(), getLifecycle()));
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(tabTitles[position])).attach();
    }

    private static class TabsPagerAdapter extends FragmentStateAdapter {

        TabsPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
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
}