package it.niedermann.nextcloud.deck.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

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
import it.niedermann.nextcloud.deck.databinding.ActivityImportAccountBinding;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.Version;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.SyncWorker;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

import static com.nextcloud.android.sso.AccountImporter.REQUEST_AUTH_TOKEN_SSO;

public class ImportAccountActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_IMPORT_ACCOUNT = 1;

    private SharedPreferences sharedPreferences;

    private String prefKeyWifiOnly;
    private boolean originalWifiOnlyValue = false;
    private String sharedPreferenceLastAccount;
    private String urlFragmentUpdateDeck;
    private int minimumServerAppMajor;
    private int minimumServerAppMinor;
    private int minimumServerAppPatch;


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
        minimumServerAppMajor = getResources().getInteger(R.integer.minimum_server_app_major);
        minimumServerAppMinor = getResources().getInteger(R.integer.minimum_server_app_minor);
        minimumServerAppPatch = getResources().getInteger(R.integer.minimum_server_app_patch);

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
                binding.addButton.setEnabled(true);
                DeckLog.warn("=============================================================");
                DeckLog.warn("Nextcloud app is not installed. Cannot choose account");
                e.printStackTrace();
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
                        });

                        SingleAccountHelper.setCurrentAccount(getApplicationContext(), account.name);
                        SyncManager syncManager = new SyncManager(ImportAccountActivity.this);
                        final WrappedLiveData<Account> accountLiveData = syncManager.createAccount(new Account(account.name, account.userId, account.url));
                        accountLiveData.observe(ImportAccountActivity.this, (Account createdAccount) -> {
                            if (accountLiveData.hasError()) {
                                try {
                                    accountLiveData.throwError();
                                } catch (SQLiteConstraintException ex) {
                                    // Account has already been added - should never be the case
                                    DeckLog.error("Account has already been added, this should not be the case");
                                    DeckLog.logError(ex);
                                    setStatusText(ex.getMessage());
                                    restoreWifiPref();
                                }
                            } else {
                                // Remember last account - THIS HAS TO BE DONE SYNCHRONOUSLY
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                DeckLog.log("--- Write: shared_preference_last_account" + " | " + createdAccount.getId());
                                editor.putLong(sharedPreferenceLastAccount, createdAccount.getId());
                                editor.commit();

                                try {
                                    syncManager.getServerVersion(new IResponseCallback<Capabilities>(createdAccount) {
                                        @Override
                                        public void onResponse(Capabilities response) {
                                            if (response.getDeckVersion().compareTo(new Version(minimumServerAppMajor, minimumServerAppMinor, minimumServerAppPatch)) < 0) {
                                                setStatusText(R.string.deck_outdated_please_update);
                                                runOnUiThread(() -> {
                                                    binding.updateDeckButton.setOnClickListener((v) -> {
                                                        Intent openURL = new Intent(Intent.ACTION_VIEW);
                                                        openURL.setData(Uri.parse(createdAccount.getUrl() + urlFragmentUpdateDeck));
                                                        startActivity(openURL);
                                                    });
                                                    binding.updateDeckButton.setVisibility(View.VISIBLE);
                                                });
                                                rollbackAccountCreation(syncManager, createdAccount.getId());
                                            } else {
                                                restoreWifiPref();
                                                SyncWorker.update(getApplicationContext());
                                                setResult(RESULT_OK);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable throwable) {
                                            super.onError(throwable);
                                            setStatusText(throwable.getMessage());
                                            rollbackAccountCreation(syncManager, createdAccount.getId());
                                        }
                                    });
                                } catch (OfflineException e) {
                                    setStatusText(R.string.you_have_to_be_connected_to_the_internet_in_order_to_add_an_account);
                                    rollbackAccountCreation(syncManager, createdAccount.getId());
                                }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AccountImporter.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @SuppressLint("ApplySharedPref")
    private void rollbackAccountCreation(@NonNull SyncManager syncManager, final long accountId) {
        DeckLog.log("Rolling back account creation for " + accountId);
        syncManager.deleteAccount(accountId);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        DeckLog.log("--- Remove: shared_preference_last_account" + " | " + accountId);
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
            binding.status.setVisibility(View.VISIBLE);
            binding.status.setText(statusText);
        });
    }

    @SuppressLint("ApplySharedPref")
    private void disableWifiPref() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        DeckLog.info("--- Temporarily disable sync on wifi only setting");
        editor.putBoolean(prefKeyWifiOnly, false);
        editor.commit();

    }

    private void restoreWifiPref() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        DeckLog.info("--- Restoring sync on wifi only setting");
        editor.putBoolean(prefKeyWifiOnly, originalWifiOnlyValue);
        editor.apply();
    }
}