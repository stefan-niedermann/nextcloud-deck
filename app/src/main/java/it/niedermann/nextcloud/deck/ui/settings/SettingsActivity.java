package it.niedermann.nextcloud.deck.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import it.niedermann.nextcloud.deck.databinding.ActivitySettingsBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.theme.Themed;

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

        applyTheme(account.getColor());
        setSupportActionBar(binding.toolbar);
        setContentView(binding.getRoot());

        // TODO We should only set this if a preference has changed that influences the MainActivity
        setResult(RESULT_OK);
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

//        utils.platform.themeStatusBar(this);
//        utils.material.themeToolbar(binding.toolbar);
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context, @NonNull Account account) {
        return new Intent(context, SettingsActivity.class)
                .putExtra(KEY_ACCOUNT, account);
    }
}
