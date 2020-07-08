package it.niedermann.nextcloud.deck.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.persistence.sync.SyncWorker;
import it.niedermann.nextcloud.deck.ui.branding.Branded;
import it.niedermann.nextcloud.deck.ui.branding.BrandedSwitchPreference;

import static it.niedermann.nextcloud.deck.DeckApplication.setAppTheme;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.readBrandMainColor;

public class SettingsFragment extends PreferenceFragmentCompat implements Branded {

    private BrandedSwitchPreference wifiOnlyPref;
    private BrandedSwitchPreference themePref;
    private BrandedSwitchPreference brandingPref;
    private BrandedSwitchPreference compactPref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        wifiOnlyPref = findPreference(getString(R.string.pref_key_wifi_only));

        if (wifiOnlyPref != null) {
            wifiOnlyPref.setOnPreferenceChangeListener((Preference preference, Object newValue) -> {
                final Boolean syncOnWifiOnly = (Boolean) newValue;
                DeckLog.log("syncOnWifiOnly: " + syncOnWifiOnly);
                return true;
            });
        } else {
            DeckLog.error("Could not find preference with key: \"" + getString(R.string.pref_key_wifi_only) + "\"");
        }

        themePref = findPreference(getString(R.string.pref_key_dark_theme));
        if (themePref != null) {
            themePref.setOnPreferenceChangeListener((Preference preference, Object newValue) -> {
                final Boolean darkTheme = (Boolean) newValue;
                DeckLog.log("darkTheme: " + darkTheme);
                setAppTheme(darkTheme);
                requireActivity().setResult(Activity.RESULT_OK);
                requireActivity().recreate();
                return true;
            });
        } else {
            DeckLog.error("Could not find preference with key: \"" + getString(R.string.pref_key_dark_theme) + "\"");
        }

        brandingPref = findPreference(getString(R.string.pref_key_branding));
        if (brandingPref != null) {
            brandingPref.setOnPreferenceChangeListener((Preference preference, Object newValue) -> {
                final Boolean branding = (Boolean) newValue;
                DeckLog.log("branding: " + branding);
                requireActivity().setResult(Activity.RESULT_OK);
                requireActivity().recreate();
                return true;
            });
        } else {
            DeckLog.error("Could not find preference with key: \"" + getString(R.string.pref_key_dark_theme) + "\"");
        }

        compactPref = findPreference(getString(R.string.pref_key_compact));

        final ListPreference backgroundSyncPref = findPreference(getString(R.string.pref_key_background_sync));
        if (backgroundSyncPref != null) {
            backgroundSyncPref.setOnPreferenceChangeListener((Preference preference, Object newValue) -> {
                SyncWorker.update(requireContext().getApplicationContext(), (String) newValue);
                return true;
            });
        } else {
            DeckLog.error("Could not find preference with key: \"" + getString(R.string.pref_key_background_sync) + "\"");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        @Nullable Context context = getContext();
        if (context != null) {
            applyBrand(readBrandMainColor(context));
        }
    }

    @Override
    public void applyBrand(int mainColor) {
        wifiOnlyPref.applyBrand(mainColor);
        themePref.applyBrand(mainColor);
        brandingPref.applyBrand(mainColor);
        compactPref.applyBrand(mainColor);
    }
}
