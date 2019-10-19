package it.niedermann.nextcloud.deck.ui.settings;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import androidx.annotation.Nullable;

import butterknife.BindString;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;

public class SettingsFragment extends PreferenceFragment {

    @BindString(R.string.pref_key_wifi_only)
    String prefKeyWifiOnly;
    @BindString(R.string.pref_key_dark_theme)
    String prefKeyDarkTheme;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this, getActivity());

        addPreferencesFromResource(R.xml.settings);

        final SwitchPreference wifiOnlyPref = (SwitchPreference) findPreference(prefKeyWifiOnly);
        wifiOnlyPref.setOnPreferenceChangeListener((Preference preference, Object newValue) -> {
            Boolean syncOnWifiOnly = (Boolean) newValue;
            DeckLog.log("syncOnWifiOnly: " + syncOnWifiOnly);
            return true;
        });

        final SwitchPreference themePref = (SwitchPreference) findPreference(prefKeyDarkTheme);
        themePref.setOnPreferenceChangeListener((Preference preference, Object newValue) -> {
            Boolean darkTheme = (Boolean) newValue;
            DeckLog.log("darkTheme: " + darkTheme);
            Application.setAppTheme(darkTheme);
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().recreate();
            return true;
        });
    }
}
