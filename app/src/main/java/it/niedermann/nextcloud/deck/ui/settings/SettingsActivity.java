package it.niedermann.nextcloud.deck.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivitySettingsBinding;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        final var binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        setResult(RESULT_OK);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.settings_layout, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return true;
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, SettingsActivity.class);
    }
}
