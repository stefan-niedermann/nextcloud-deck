package it.niedermann.nextcloud.deck.ui.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import it.niedermann.nextcloud.deck.DeckApplication;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.persistence.sync.SyncWorker;
import it.niedermann.nextcloud.deck.ui.branding.BrandedSwitchPreference;

import static it.niedermann.nextcloud.deck.DeckApplication.setAppTheme;

public class SettingsFragment extends PreferenceFragmentCompat {

    private BrandedSwitchPreference wifiOnlyPref;
    private BrandedSwitchPreference compactPref;
    private BrandedSwitchPreference debuggingPref;
    private BrandedSwitchPreference eTagPref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        wifiOnlyPref = findPreference(getString(R.string.pref_key_wifi_only));

        if (wifiOnlyPref != null) {
            wifiOnlyPref.setOnPreferenceChangeListener((Preference preference, Object newValue) -> {
                final Boolean syncOnWifiOnly = (Boolean) newValue;
                DeckLog.log("syncOnWifiOnly:", syncOnWifiOnly);
                return true;
            });
        } else {
            DeckLog.error("Could not find preference with key: ", getString(R.string.pref_key_wifi_only));
        }

        Preference themePref = findPreference(getString(R.string.pref_key_dark_theme));
        if (themePref != null) {
            themePref.setOnPreferenceChangeListener((Preference preference, Object newValue) -> {
                setAppTheme(Integer.parseInt((String) newValue));
                requireActivity().setResult(Activity.RESULT_OK);
                ActivityCompat.recreate(requireActivity());
                return true;
            });
        } else {
            DeckLog.error("Could not find preference with key:", getString(R.string.pref_key_dark_theme));
        }

        compactPref = findPreference(getString(R.string.pref_key_compact));

        final ListPreference backgroundSyncPref = findPreference(getString(R.string.pref_key_background_sync));
        if (backgroundSyncPref != null) {
            backgroundSyncPref.setOnPreferenceChangeListener((Preference preference, Object newValue) -> {
                SyncWorker.update(requireContext().getApplicationContext(), (String) newValue);
                return true;
            });
        } else {
            DeckLog.error("Could not find preference with key", getString(R.string.pref_key_background_sync));
        }

        debuggingPref = findPreference(getString(R.string.pref_key_debugging));
        if (debuggingPref != null) {
            debuggingPref.setOnPreferenceChangeListener((Preference preference, Object newValue) -> {
                DeckLog.enablePersistentLogs((Boolean) newValue);
                DeckLog.log("persistet debug logs:", newValue);
                return true;
            });
        } else {
            DeckLog.error("Could not find preference with key:", getString(R.string.pref_key_debugging));
        }

        eTagPref = findPreference(getString(R.string.pref_key_etags));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DeckApplication.readCurrentAccountColor().observe(getViewLifecycleOwner(), (mainColor) -> {
            wifiOnlyPref.applyBrand(mainColor);
            compactPref.applyBrand(mainColor);
            debuggingPref.applyBrand(mainColor);
            eTagPref.applyBrand(mainColor);
        });
    }
}
