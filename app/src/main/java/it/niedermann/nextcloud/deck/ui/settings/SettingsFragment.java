package it.niedermann.nextcloud.deck.ui.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.stream.Stream;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.remote.SyncWorker;
import it.niedermann.nextcloud.deck.ui.theme.ThemedSwitchPreference;

public class SettingsFragment extends PreferenceFragmentCompat {

    private PreferencesViewModel preferencesViewModel;
    private ThemedSwitchPreference wifiOnlyPref;
    private ThemedSwitchPreference compactPref;
    private ThemedSwitchPreference coverImagesPref;
    private ThemedSwitchPreference compressImageAttachmentsPref;
    private ThemedSwitchPreference debuggingPref;
    private ThemedSwitchPreference eTagPref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        preferencesViewModel = new ViewModelProvider(requireActivity()).get(PreferencesViewModel.class);

        wifiOnlyPref = findPreference(getString(R.string.pref_key_wifi_only));
        coverImagesPref = findPreference(getString(R.string.pref_key_cover_images));
        compactPref = findPreference(getString(R.string.pref_key_compact));
        compressImageAttachmentsPref = findPreference(getString(R.string.pref_key_compress_image_attachments));
        eTagPref = findPreference(getString(R.string.pref_key_etags));

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

        final var backgroundSyncPref = findPreference(getString(R.string.pref_key_background_sync));
        if (backgroundSyncPref != null) {
            backgroundSyncPref.setOnPreferenceChangeListener((Preference preference, Object newValue) -> {
                SyncWorker.update(requireContext().getApplicationContext(), (String) newValue);
                return true;
            });
        } else {
            DeckLog.error("Could not find preference with key", getString(R.string.pref_key_background_sync));
        }

        final var themePref = findPreference(getString(R.string.pref_key_dark_theme));
        if (themePref != null) {
            themePref.setOnPreferenceChangeListener((Preference preference, Object newValue) -> {
                preferencesViewModel.setAppTheme(Integer.parseInt((String) newValue));
                requireActivity().setResult(Activity.RESULT_OK);
                ActivityCompat.recreate(requireActivity());
                return true;
            });
        } else {
            DeckLog.error("Could not find preference with key:", getString(R.string.pref_key_dark_theme));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new ReactiveLiveData<>(preferencesViewModel.getCurrentAccountId$())
                .flatMap(preferencesViewModel::getAccountColor)
                .observe(getViewLifecycleOwner(), color -> Stream.of(
                                wifiOnlyPref,
                                compactPref,
                                coverImagesPref,
                                compressImageAttachmentsPref,
                                debuggingPref,
                                eTagPref)
                        .forEach(pref -> pref.applyTheme(color)));
    }
}
