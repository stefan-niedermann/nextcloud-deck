package it.niedermann.nextcloud.deck.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class ImportAccountActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_IMPORT_ACCOUNT = 1;
    private SharedPreferences sharedPreferences;
    private String sharedPreferenceLastAccount;
    private String urlFragmentUpdateDeck;
    private int minimumServerAppMajor;
    private int minimumServerAppMinor;
    private int minimumServerAppPatch;


    private ActivityImportAccountBinding binding;
    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                binding.addButton.setEnabled(Objects.requireNonNull(conn).isDefaultNetworkActive());
//                binding.networkHint.setVisibility(conn.isDefaultNetworkActive() ? View.GONE : View.INVISIBLE);
//            } else {
//                NetworkInfo networkInfo = Objects.requireNonNull(conn).getActiveNetworkInfo();
//                if (networkInfo != null) {
//                    binding.addButton.setEnabled(networkInfo.isConnected());
//                    binding.networkHint.setVisibility(networkInfo.isConnected() ? View.GONE : View.INVISIBLE);
//                }
//            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        binding = ActivityImportAccountBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferenceLastAccount = getString(R.string.shared_preference_last_account);
        urlFragmentUpdateDeck = getString(R.string.url_fragment_update_deck);
        minimumServerAppMajor = getResources().getInteger(R.integer.minimum_server_app_major);
        minimumServerAppMinor = getResources().getInteger(R.integer.minimum_server_app_minor);
        minimumServerAppPatch = getResources().getInteger(R.integer.minimum_server_app_patch);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(networkReceiver, filter);

        binding.welcomeText.setText(getString(R.string.welcome_text, getString(R.string.app_name)));
        binding.error.setText("");
        binding.addButton.setOnClickListener((v) -> {
            try {
                AccountImporter.pickNewAccount(this);
            } catch (NextcloudFilesAppNotInstalledException e) {
                UiExceptionManager.showDialogForException(this, e);
                DeckLog.warn("=============================================================");
                DeckLog.warn("Nextcloud app is not installed. Cannot choose account");
                e.printStackTrace();
            } catch (AndroidGetAccountsPermissionNotGranted e) {
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
    protected void onDestroy() {
        super.onDestroy();
        // Unregisters BroadcastReceiver when app is destroyed.
        this.unregisterReceiver(networkReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            AccountImporter.onActivityResult(requestCode, resultCode, data, ImportAccountActivity.this, new AccountImporter.IAccountAccessGranted() {
                @Override
                public void accountAccessGranted(SingleSignOnAccount account) {
                    ImportAccountActivity.this.runOnUiThread(() -> binding.error.setText("Importing..."));
                    SyncManager syncManager = new SyncManager(ImportAccountActivity.this);
                    final WrappedLiveData<Account> accountLiveData = syncManager.createAccount(new Account(account.name, account.userId, account.url));
                    accountLiveData.observe(ImportAccountActivity.this, (Account createdAccount) -> {
                        if (accountLiveData.hasError()) {
                            try {
                                accountLiveData.throwError();
                            } catch (SQLiteConstraintException ex) {
                                DeckLog.logError(ex);
                            }
                        } else {
                            // Remember last account - THIS HAS TO BE DONE SYNCHRONOUSLY
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            DeckLog.log("--- Write: shared_preference_last_account" + " | " + createdAccount.getId());
                            editor.putLong(sharedPreferenceLastAccount, createdAccount.getId());
                            editor.commit();

                            try {
                                ImportAccountActivity.this.runOnUiThread(() -> binding.error.setText("Checking server app version"));
                                syncManager.getServerVersion(new IResponseCallback<Capabilities>(createdAccount) {
                                    @Override
                                    public void onResponse(Capabilities response) {
                                        DeckLog.info("onResponse");
                                        if (response.getDeckVersion().compareTo(new Version(minimumServerAppMajor, minimumServerAppMinor, minimumServerAppPatch)) < 0) {
                                            ImportAccountActivity.this.runOnUiThread(() -> binding.error.setText("Version too low"));
//                                deckVersionTooLowSnackbar = Snackbar.make(binding.coordinatorLayout, R.string.your_deck_version_is_too_old, Snackbar.LENGTH_INDEFINITE).setAction("Learn more", v -> {
//                                    new AlertDialog.Builder(ImportAccountActivity.this, Application.getAppTheme(getApplicationContext()) ? R.style.DialogDarkTheme : R.style.ThemeOverlay_AppCompat_Dialog_Alert)
//                                            .setTitle(R.string.update_deck)
//                                            .setMessage(R.string.deck_outdated_please_update)
//                                            .setPositiveButton(R.string.simple_update, (dialog, whichButton) -> {
//                                                Intent openURL = new Intent(Intent.ACTION_VIEW);
//                                                openURL.setData(Uri.parse(createdAccount.getUrl() + urlFragmentUpdateDeck));
//                                                startActivity(openURL);
//                                            })
//                                            .setNegativeButton(R.string.simple_discard, null).show();
//                                });
//                                deckVersionTooLowSnackbar.show();
                                            rollbackAccountCreation(syncManager, createdAccount.getId());
                                        } else {
                                            ImportAccountActivity.this.runOnUiThread(() -> binding.error.setText("Importing..."));
                                            SyncWorker.update(getApplicationContext());
                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable throwable) {
                                        DeckLog.error("onError");
                                        throwable.printStackTrace();
                                        DeckLog.logError(throwable);
                                        ImportAccountActivity.this.runOnUiThread(() -> binding.error.setText(throwable.getMessage()));
                                        rollbackAccountCreation(syncManager, createdAccount.getId());
                                        super.onError(throwable);
                                    }
                                });
                            } catch (OfflineException e) {
                                DeckLog.info("You are offline");
                                new AlertDialog.Builder(ImportAccountActivity.this)
                                        .setMessage(R.string.you_have_to_be_connected_to_the_internet_in_order_to_add_an_account)
                                        .setPositiveButton(R.string.simple_close, null)
                                        .show();
                                rollbackAccountCreation(syncManager, createdAccount.getId());
                            }
                        }
                    });

                    SingleAccountHelper.setCurrentAccount(getApplicationContext(), account.name);
                }
            });
        } catch (AccountImportCancelledException e) {
            DeckLog.info("Account import has been canceled.");
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
    }
}