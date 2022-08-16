package it.niedermann.nextcloud.deck.ui;

import static com.nextcloud.android.sso.AccountImporter.REQUEST_AUTH_TOKEN_SSO;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.exceptions.AccountImportCancelledException;
import com.nextcloud.android.sso.exceptions.AndroidGetAccountsPermissionNotGranted;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotInstalledException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;
import com.nextcloud.android.sso.ui.UiExceptionManager;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.api.ResponseCallback;
import it.niedermann.nextcloud.deck.databinding.ActivityImportAccountBinding;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.SyncWorker;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

public class ImportAccountActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private String prefKeyWifiOnly;
    private boolean originalWifiOnlyValue = false;
    private String sharedPreferenceLastAccount;
    private String urlFragmentUpdateDeck;

    private ActivityImportAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        binding = ActivityImportAccountBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        prefKeyWifiOnly = getString(R.string.pref_key_wifi_only);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferenceLastAccount = getString(R.string.shared_preference_last_account);
        urlFragmentUpdateDeck = getString(R.string.url_fragment_update_deck);

        originalWifiOnlyValue = sharedPreferences.getBoolean(prefKeyWifiOnly, false);

        binding.welcomeText.setText(getString(R.string.welcome_text, getString(R.string.app_name)));
        binding.addButton.setOnClickListener((v) -> {
            binding.status.setText("");
            binding.addButton.setEnabled(false);
            binding.updateDeckButton.setVisibility(View.GONE);
            disableWifiPref();
            try {
                AccountImporter.pickNewAccount(this);
            } catch (NextcloudFilesAppNotInstalledException e) {
                UiExceptionManager.showDialogForException(this, e);
                DeckLog.warn("=============================================================");
                DeckLog.warn("Nextcloud app is not installed. Cannot choose account");
                DeckLog.logError(e);
                binding.addButton.setEnabled(true);
            } catch (AndroidGetAccountsPermissionNotGranted e) {
                binding.addButton.setEnabled(true);
                AccountImporter.requestAndroidAccountPermissionsAndPickAccount(this);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onSupportNavigateUp();
        setResult(RESULT_CANCELED);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_AUTH_TOKEN_SSO && resultCode == RESULT_CANCELED) {
            binding.addButton.setEnabled(true);
        } else {
            try {
                AccountImporter.onActivityResult(requestCode, resultCode, data, ImportAccountActivity.this, new AccountImporter.IAccountAccessGranted() {
                    @SuppressLint("ApplySharedPref")
                    @Override
                    public void accountAccessGranted(SingleSignOnAccount account) {
                        runOnUiThread(() -> {
                            binding.status.setText(null);
                            binding.status.setVisibility(View.GONE);
                            binding.progressCircular.setVisibility(View.VISIBLE);
                            binding.progressText.setVisibility(View.VISIBLE);
                            binding.progressCircular.setIndeterminate(true);
                            binding.progressText.setText(R.string.progress_import_indeterminate);
                        });

                        SingleAccountHelper.setCurrentAccount(getApplicationContext(), account.name);
                        final var syncManager = new SyncManager(ImportAccountActivity.this);
                        final var accountToCreate = new Account(account.name, account.userId, account.url);
                        syncManager.createAccount(accountToCreate, new IResponseCallback<>() {
                            @Override
                            public void onResponse(Account createdAccount) {
                                // Remember last account - THIS HAS TO BE DONE SYNCHRONOUSLY
                                DeckLog.log("--- Write: shared_preference_last_account | ", createdAccount.getId());
                                sharedPreferences
                                        .edit()
                                        .putLong(sharedPreferenceLastAccount, createdAccount.getId())
                                        .commit();

                                syncManager.refreshCapabilities(new ResponseCallback<>(createdAccount) {
                                    @Override
                                    public void onResponse(Capabilities response) {
                                        if (!response.isMaintenanceEnabled()) {
                                            if (response.getDeckVersion().isSupported()) {
                                                var progress$ = syncManager.synchronize(new ResponseCallback<>(account) {
                                                    @Override
                                                    public void onResponse(Boolean response) {
                                                        restoreWifiPref();
                                                        SyncWorker.update(getApplicationContext());
                                                        setResult(RESULT_OK);
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onError(Throwable throwable) {
                                                        super.onError(throwable);
                                                        setStatusText(throwable.getMessage());
                                                        runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, createdAccount).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
                                                        rollbackAccountCreation(syncManager, createdAccount.getId());
                                                    }
                                                });
                                                runOnUiThread(() -> progress$.observe(ImportAccountActivity.this, (progress) -> {
                                                    DeckLog.log("New progress value", progress.first, progress.second);
                                                    if(progress.first > 0) {
                                                        binding.progressCircular.setIndeterminate(false);
                                                    }
                                                    binding.progressText.setText(getString(R.string.progress_import, progress.first + 1, progress.second));
                                                    binding.progressCircular.setProgress(progress.first);
                                                    binding.progressCircular.setMax(progress.second);
                                                }));
                                            } else {
                                                setStatusText(getString(R.string.deck_outdated_please_update, response.getDeckVersion().getOriginalVersion()));
                                                runOnUiThread(() -> {
                                                    binding.updateDeckButton.setOnClickListener((v) -> startActivity(new Intent(Intent.ACTION_VIEW)
                                                            .setData(Uri.parse(createdAccount.getUrl() + urlFragmentUpdateDeck))));
                                                    binding.updateDeckButton.setVisibility(View.VISIBLE);
                                                });
                                                rollbackAccountCreation(syncManager, createdAccount.getId());
                                            }
                                        } else {
                                            setStatusText(R.string.maintenance_mode);
                                            rollbackAccountCreation(syncManager, createdAccount.getId());
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable throwable) {
                                        super.onError(throwable);
                                        if (throwable instanceof OfflineException) {
                                            setStatusText(R.string.you_have_to_be_connected_to_the_internet_in_order_to_add_an_account);
                                        } else {
                                            setStatusText(throwable.getMessage());
                                            runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, createdAccount).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
                                        }
                                        rollbackAccountCreation(syncManager, createdAccount.getId());
                                    }
                                });
                            }

                            @Override
                            public void onError(Throwable error) {
                                IResponseCallback.super.onError(error);
                                if (error instanceof SQLiteConstraintException) {
                                    DeckLog.error("Account has already been added, this should not be the case");
                                }
                                setStatusText(error.getMessage());
                                runOnUiThread(() -> ExceptionDialogFragment.newInstance(error, accountToCreate).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName()));
                                restoreWifiPref();
                            }
                        });
                    }
                });
            } catch (AccountImportCancelledException e) {
                runOnUiThread(() -> binding.addButton.setEnabled(true));
                restoreWifiPref();
                DeckLog.info("Account import has been canceled.");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AccountImporter.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @SuppressLint("ApplySharedPref")
    private void rollbackAccountCreation(@NonNull SyncManager syncManager, final long accountId) {
        DeckLog.log("Rolling back account creation for " + accountId);
        syncManager.deleteAccount(accountId);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        DeckLog.log("--- Remove: shared_preference_last_account |", accountId);
        editor.remove(sharedPreferenceLastAccount);
        editor.commit(); // Has to be done synchronously
        runOnUiThread(() -> binding.addButton.setEnabled(true));
        restoreWifiPref();
    }

    private void setStatusText(@StringRes int statusText) {
        setStatusText(getString(statusText));
    }

    private void setStatusText(String statusText) {
        runOnUiThread(() -> {
            binding.updateDeckButton.setVisibility(View.GONE);
            binding.progressCircular.setVisibility(View.GONE);
            binding.progressText.setVisibility(View.GONE);
            binding.status.setVisibility(View.VISIBLE);
            binding.status.setText(statusText);
        });
    }

    @SuppressLint("ApplySharedPref")
    private void disableWifiPref() {
        DeckLog.info("--- Temporarily disable sync on wifi only setting");
        sharedPreferences
                .edit()
                .putBoolean(prefKeyWifiOnly, false)
                .commit();
    }

    private void restoreWifiPref() {
        DeckLog.info("--- Restoring sync on wifi only setting");
        sharedPreferences
                .edit()
                .putBoolean(prefKeyWifiOnly, originalWifiOnlyValue)
                .apply();
    }

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, ImportAccountActivity.class);
    }
}