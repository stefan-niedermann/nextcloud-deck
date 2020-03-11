package it.niedermann.nextcloud.deck.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.exceptions.AccountImportCancelledException;
import com.nextcloud.android.sso.exceptions.AndroidGetAccountsPermissionNotGranted;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotInstalledException;
import com.nextcloud.android.sso.ui.UiExceptionManager;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ActivityImportAccountBinding;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionHandler;

public class ImportAccountActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_IMPORT_ACCOUNT = 1;
    public static final String EXTRA_IMPORTED_ACCOUNT = "importedAccount";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AccountImporter.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));

        ActivityImportAccountBinding binding = ActivityImportAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.welcomeText.setText(getString(R.string.welcome_text, getString(R.string.app_name)));

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (cm != null) {
                cm.addDefaultNetworkActiveListener(() -> {
                    binding.addButton.setEnabled(cm.isDefaultNetworkActive());
                    binding.networkHint.setVisibility(cm.isDefaultNetworkActive() ? View.GONE : View.INVISIBLE);
                });
                binding.addButton.setEnabled(cm.isDefaultNetworkActive());
                binding.networkHint.setVisibility(cm.isDefaultNetworkActive() ? View.GONE : View.INVISIBLE);
            } else {
                binding.networkHint.setVisibility(View.VISIBLE);
            }
        } else {
            binding.networkHint.setVisibility(View.VISIBLE);
        }

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            AccountImporter.onActivityResult(requestCode, resultCode, data, this, (account) -> {
                if (resultCode == RESULT_OK) {
                    data.putExtra(EXTRA_IMPORTED_ACCOUNT, account);
                    setResult(RESULT_OK, data);
                    finish();
                }
            });
        } catch (AccountImportCancelledException e) {
            DeckLog.info("Account import has been canceled.");
        }
    }
}