package it.niedermann.nextcloud.deck.deprecated.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.stream.Stream;

import it.niedermann.nextcloud.deck.deprecated.util.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.repository.sync.SyncWorker;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.ThemedSwitchPreference;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String KEY_ACCOUNT = "account";
    private Account account;
    private PreferencesViewModel preferencesViewModel;
    private ThemedSwitchPreference wifiOnlyPref;
    private Preference pushNotificationPref;
    private ThemedSwitchPreference compactPref;
    private ThemedSwitchPreference coverImagesPref;
    private ThemedSwitchPreference compressImageAttachmentsPref;
    private ThemedSwitchPreference debuggingPref;
    private ThemedSwitchPreference eTagPref;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        final var args = getArguments();
        if (args == null || !args.containsKey(KEY_ACCOUNT)) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " must be provided");
        }

        account = (Account) args.getSerializable(KEY_ACCOUNT);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        preferencesViewModel = new ViewModelProvider(requireActivity()).get(PreferencesViewModel.class);

        wifiOnlyPref = findPreference(getString(R.string.pref_key_wifi_only));
        pushNotificationPref = findPreference(getString(R.string.pref_key_push_notifications));
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

        final var pushNotificationPref = findPreference(getString(R.string.pref_key_push_notifications));
        if (pushNotificationPref != null) {
            pushNotificationPref.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_faq_push_notifications))));
                return true;
            });
        } else {
            DeckLog.error("Could not find preference with key", getString(R.string.pref_key_push_notifications));
        }

        final var themePref = findPreference(getString(R.string.pref_key_dark_theme));
        if (themePref != null) {
            themePref.setOnPreferenceChangeListener((Preference preference, Object newValue) -> {
                preferencesViewModel.setAppTheme(Integer.parseInt((String) newValue));
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

        Stream.of(wifiOnlyPref, compactPref, coverImagesPref, compressImageAttachmentsPref, debuggingPref, eTagPref)
                .forEach(pref -> pref.applyTheme(account.getColor()));
    }

    @NonNull
    public static Fragment newInstance(@NonNull Account account) {
        final var fragment = new SettingsFragment();

        final var args = new Bundle();
        args.putSerializable(KEY_ACCOUNT, account);
        fragment.setArguments(args);

        return fragment;
    }
}
