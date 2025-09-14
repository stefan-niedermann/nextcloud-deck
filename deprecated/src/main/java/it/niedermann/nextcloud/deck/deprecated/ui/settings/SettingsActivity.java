package it.niedermann.nextcloud.deck.deprecated.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import it.niedermann.nextcloud.deck.databinding.ActivitySettingsBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.Themed;

public class SettingsActivity extends AppCompatActivity implements Themed {

    private static final String KEY_ACCOUNT = "account";
    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        if (!getIntent().hasExtra(KEY_ACCOUNT)) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " must be provided");
        }

        final var account = (Account) getIntent().getSerializableExtra(KEY_ACCOUNT);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        applyTheme(account.getColor());
        setResult(RESULT_OK);

        getSupportFragmentManager()
                .beginTransaction()
                .add(binding.settingsFragment.getId(), SettingsFragment.newInstance(account))
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @Override
    public void applyTheme(int color) {
        final var utils = ThemeUtils.of(color, this);

        utils.material.themeToolbar(binding.toolbar);
        utils.deck.themeStatusBar(this, binding.appBarLayout);
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context, @NonNull Account account) {
        return new Intent(context, SettingsActivity.class)
                .putExtra(KEY_ACCOUNT, account);
    }
}
