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

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityAboutBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.theme.Themed;

public class AboutActivity extends AppCompatActivity implements Themed {

    private static final String KEY_ACCOUNT = "account";
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

        final var args = getIntent().getExtras();

        if (args == null || !args.containsKey(KEY_ACCOUNT)) {
            throw new IllegalArgumentException("Provide at least " + KEY_ACCOUNT);
        }

        final var account = (Account) args.getSerializable(KEY_ACCOUNT);

        binding = ActivityAboutBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        applyTheme(account.getColor());

        binding.viewPager.setAdapter(new TabsPagerAdapter(this, account));
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(tabTitles[position])).attach();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @Override
    public void applyTheme(int color) {
        final var utils = ThemeUtils.of(color, this);

        utils.platform.themeStatusBar(this);
        utils.material.themeToolbar(binding.toolbar);
        utils.material.themeTabLayoutOnSurface(binding.tabLayout);
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
            return switch (position) {
                case 0 -> AboutFragmentCreditsTab.newInstance(account);
                case 1 -> new AboutFragmentContributingTab();
                case 2 -> AboutFragmentLicenseTab.newInstance(account);
                default -> throw new IllegalArgumentException("position must be between 0 and 2");
            };
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
                .putExtra(KEY_ACCOUNT, account);
    }
}