package it.niedermann.nextcloud.deck.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.database.DeckDatabase;
import it.niedermann.nextcloud.deck.databinding.ActivityRecoveryBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.ui.theme.Themed;
import it.niedermann.nextcloud.deck.util.ExecutorServiceProvider;

public class RecoveryActivity extends AppCompatActivity implements Themed {

    private static final String KEY_ACCOUNT = "account";
    private ActivityRecoveryBinding binding;
    private PreferencesViewModel preferencesViewModel;
    private Account account;

    private final ActivityResultLauncher<String> exportDatabaseLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("application/octet-stream"), (Uri uri) -> {
        if (uri != null) {
            ExecutorServiceProvider.getLinkedBlockingQueueExecutor().execute(() -> {
                try {
                    File dbFile = getDatabasePath(DeckDatabase.DECK_DB_NAME);
                    try (InputStream in = new FileInputStream(dbFile);
                         OutputStream out = getContentResolver().openOutputStream(uri)) {
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                    }
                    runOnUiThread(() -> Toast.makeText(this, R.string.export_database_success, Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    DeckLog.logError(e);
                    runOnUiThread(() -> Toast.makeText(this, R.string.export_database_failed, Toast.LENGTH_LONG).show());
                }
            });
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        if (!getIntent().hasExtra(KEY_ACCOUNT)) {
            throw new IllegalArgumentException(KEY_ACCOUNT + " must be provided");
        }

        account = (Account) getIntent().getSerializableExtra(KEY_ACCOUNT);
        preferencesViewModel = new ViewModelProvider(this).get(PreferencesViewModel.class);

        binding = ActivityRecoveryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        applyTheme(account.getColor());

        binding.btnRestoreServer.setOnClickListener(v -> restoreServer());
        binding.btnCreateBackup.setOnClickListener(v -> createBackup());
        binding.btnRestoreLocalBackup.setOnClickListener(v -> restoreLocalBackup());
        binding.btnExportDatabase.setOnClickListener(v -> exportDatabase());

        updateRestoreLocalBackupButton();
    }

    private void updateRestoreLocalBackupButton() {
        binding.btnRestoreLocalBackup.setEnabled(preferencesViewModel.hasBackup());
    }

    private void restoreServer() {
        ExecutorServiceProvider.getLinkedBlockingQueueExecutor().execute(() -> {
            try {
                final List<User> users = preferencesViewModel.getUsersForAccountDirectly(account.getId());
                final String userUids = users.stream()
                        .filter(u -> u.getType() == User.TYPE_USER && u.getUid() != null)
                        .map(User::getUid)
                        .distinct()
                        .sorted()
                        .collect(Collectors.joining("\n"));

                runOnUiThread(() -> new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.restore_warning_title)
                        .setMessage(getString(R.string.restore_warning_message, userUids))
                        .setPositiveButton(R.string.simple_restore, (dialog, which) -> ExecutorServiceProvider.getLinkedBlockingQueueExecutor().execute(() -> {
                            try {
                                if (preferencesViewModel.backupDatabase()) {
                                    preferencesViewModel.setRestoreAccount(account.getId());
                                    // Restart app
                                    Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                                    if (intent != null) {
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        runOnUiThread(() -> {
                                            startActivity(intent);
                                            Runtime.getRuntime().exit(0);
                                        });
                                    }
                                } else {
                                    runOnUiThread(() -> Toast.makeText(this, R.string.restore_backup_failed, Toast.LENGTH_LONG).show());
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
    }

    private void createBackup() {
        ExecutorServiceProvider.getLinkedBlockingQueueExecutor().execute(() -> {
            if (preferencesViewModel.backupDatabase()) {
                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.create_local_backup_success, Toast.LENGTH_SHORT).show();
                    updateRestoreLocalBackupButton();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, R.string.create_local_backup_failed, Toast.LENGTH_LONG).show());
            }
        });
    }

    private void restoreLocalBackup() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.restore_local_backup_warning_title)
                .setMessage(R.string.restore_local_backup_warning_message)
                .setPositiveButton(R.string.simple_restore, (dialog, which) -> ExecutorServiceProvider.getLinkedBlockingQueueExecutor().execute(() -> {
                    if (preferencesViewModel.restoreDatabase()) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, R.string.restore_local_backup_success, Toast.LENGTH_LONG).show();
                            // Restart app
                            Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                            if (intent != null) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Runtime.getRuntime().exit(0);
                            }
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, R.string.restore_local_backup_failed, Toast.LENGTH_LONG).show());
                    }
                }))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void exportDatabase() {
        exportDatabaseLauncher.launch(DeckDatabase.DECK_DB_NAME);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void applyTheme(int color) {
        final var utils = ThemeUtils.of(color, this);

        utils.material.themeToolbar(binding.toolbar);
        utils.deck.themeStatusBar(this, binding.appBarLayout);

        utils.platform.colorTextView(binding.recoverDeckServerTitle);
        utils.platform.colorTextView(binding.recoverDeckAndroidTitle);
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context, @NonNull Account account) {
        return new Intent(context, RecoveryActivity.class)
                .putExtra(KEY_ACCOUNT, account);
    }
}
