package it.niedermann.nextcloud.deck.ui.settings;

import android.app.Activity;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.persistence.sync.SyncWorker;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        final SwitchPreference wifiOnlyPref = findPreference(getString(R.string.pref_key_wifi_only));
        if (wifiOnlyPref != null) {
            wifiOnlyPref.setOnPreferenceChangeListener((Preference preference, Object newValue) -> {
                Boolean syncOnWifiOnly = (Boolean) newValue;
                DeckLog.log("syncOnWifiOnly: " + syncOnWifiOnly);
                return true;
            });
        } else {
            DeckLog.error("Could not find preference with key: \"" + getString(R.string.pref_key_wifi_only) + "\"");
        }

        final SwitchPreference themePref = findPreference(getString(R.string.pref_key_dark_theme));
        if (themePref != null) {
            themePref.setOnPreferenceChangeListener((Preference preference, Object newValue) -> {
                Boolean darkTheme = (Boolean) newValue;
                DeckLog.log("darkTheme: " + darkTheme);
                Application.setAppTheme(darkTheme);
                requireActivity().setResult(Activity.RESULT_OK);
                requireActivity().recreate();
                return true;
            });
        } else {
            DeckLog.error("Could not find preference with key: \"" + getString(R.string.pref_key_dark_theme) + "\"");
        }

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
}
