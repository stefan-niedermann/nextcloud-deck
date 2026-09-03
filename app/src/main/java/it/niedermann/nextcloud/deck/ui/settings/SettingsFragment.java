package it.niedermann.nextcloud.deck.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.database.DeckDatabase;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.remote.SyncWorker;
import it.niedermann.nextcloud.deck.ui.theme.ThemedSwitchPreference;
import it.niedermann.nextcloud.deck.util.ExecutorServiceProvider;

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

    private final ActivityResultLauncher<String> exportDatabaseLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("application/octet-stream"), uri -> {
        if (uri != null) {
            ExecutorServiceProvider.getLinkedBlockingQueueExecutor().execute(() -> {
                try {
                    File dbFile = requireContext().getDatabasePath(DeckDatabase.DECK_DB_NAME);
                    try (InputStream in = new FileInputStream(dbFile);
                         OutputStream out = requireContext().getContentResolver().openOutputStream(uri)) {
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                    }
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), R.string.export_database_success, Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    DeckLog.logError(e);
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), R.string.export_database_failed, Toast.LENGTH_LONG).show());
                }
            });
        }
    });

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

        final var restoreServerPref = findPreference(getString(R.string.pref_key_restore_server));
        if (restoreServerPref != null) {
            restoreServerPref.setOnPreferenceClickListener(preference -> {
                ExecutorServiceProvider.getLinkedBlockingQueueExecutor().execute(() -> {
                    try {
                        final List<User> users = preferencesViewModel.getUsersForAccountDirectly(account.getId());
                        final String userUids = users.stream()
                                .filter(u -> u.getType() == User.TYPE_USER && u.getUid() != null)
                                .map(User::getUid)
                                .distinct()
                                .sorted()
                                .collect(Collectors.joining("\n"));

                        requireActivity().runOnUiThread(() -> new MaterialAlertDialogBuilder(requireContext())
                                .setTitle(R.string.restore_warning_title)
                                .setMessage(getString(R.string.restore_warning_message, userUids))
                                .setPositiveButton(R.string.simple_restore, (dialog, which) -> ExecutorServiceProvider.getLinkedBlockingQueueExecutor().execute(() -> {
                                    try {
                                        if (preferencesViewModel.backupDatabase()) {
                                            preferencesViewModel.setRestoreAccount(account.getId());
                                            // Restart app
                                            Intent intent = requireContext().getPackageManager().getLaunchIntentForPackage(requireContext().getPackageName());
                                            if (intent != null) {
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                requireActivity().runOnUiThread(() -> {
                                                    startActivity(intent);
                                                    Runtime.getRuntime().exit(0);
                                                });
                                            }
                                        } else {
                                            requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), R.string.restore_backup_failed, Toast.LENGTH_LONG).show());
                                        }
                                    } catch (Exception e) {
                                        DeckLog.logError(e);
                                    }
                                }))
                                .setNegativeButton(android.R.string.cancel, null)
                                .show());
                    } catch (Exception e) {
                        DeckLog.logError(e);
                    }
                });
                return true;
            });
        }

        final var restoreLocalBackupPref = findPreference(getString(R.string.pref_key_restore_local_backup));
        if (restoreLocalBackupPref != null) {
            restoreLocalBackupPref.setEnabled(preferencesViewModel.hasBackup());
            restoreLocalBackupPref.setOnPreferenceClickListener(preference -> {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.restore_local_backup_warning_title)
                        .setMessage(R.string.restore_local_backup_warning_message)
                        .setPositiveButton(R.string.simple_restore, (dialog, which) -> ExecutorServiceProvider.getLinkedBlockingQueueExecutor().execute(() -> {
                            if (preferencesViewModel.restoreDatabase()) {
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(requireContext(), R.string.restore_local_backup_success, Toast.LENGTH_LONG).show();
                                    // Restart app
                                    Intent intent = requireContext().getPackageManager().getLaunchIntentForPackage(requireContext().getPackageName());
                                    if (intent != null) {
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        Runtime.getRuntime().exit(0);
                                    }
                                });
                            } else {
                                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), R.string.restore_local_backup_failed, Toast.LENGTH_LONG).show());
                            }
                        }))
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                return true;
            });
        }

        final var createLocalBackupPref = findPreference(getString(R.string.pref_key_create_local_backup));
        if (createLocalBackupPref != null) {
            createLocalBackupPref.setOnPreferenceClickListener(preference -> {
                ExecutorServiceProvider.getLinkedBlockingQueueExecutor().execute(() -> {
                    if (preferencesViewModel.backupDatabase()) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), R.string.create_local_backup_success, Toast.LENGTH_SHORT).show();
                            if (restoreLocalBackupPref != null) {
                                restoreLocalBackupPref.setEnabled(true);
                            }
                        });
                    } else {
                        requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), R.string.create_local_backup_failed, Toast.LENGTH_LONG).show());
                    }
                });
                return true;
            });
        }

        final var exportDatabasePref = findPreference(getString(R.string.pref_key_export_database));
        if (exportDatabasePref != null) {
            exportDatabasePref.setOnPreferenceClickListener(preference -> {
                exportDatabaseLauncher.launch(DeckDatabase.DECK_DB_NAME);
                return true;
            });
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
