package it.niedermann.nextcloud.deck.ui.settings;

import android.os.Bundle;

import androidx.annotation.Nullable;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivitySettingsBinding;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

public class SettingsActivity extends BrandedActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
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

    @Override
    public void applyBrand(int mainColor, int textColor) {
        applyBrandToPrimaryToolbar(mainColor, textColor, binding.toolbar);
    }
}
