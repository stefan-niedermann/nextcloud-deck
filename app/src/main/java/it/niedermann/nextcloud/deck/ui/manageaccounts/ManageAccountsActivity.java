package it.niedermann.nextcloud.deck.ui.manageaccounts;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.databinding.ActivityManageAccountsBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;

public class ManageAccountsActivity extends BrandedActivity {

    private ActivityManageAccountsBinding binding;
    private ManageAccountAdapter adapter;
    private SyncManager syncManager = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityManageAccountsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        syncManager = new SyncManager(this);

        syncManager.readAccounts().observe(this, (localAccounts -> {

            adapter = new ManageAccountAdapter((account) -> {
                SingleAccountHelper.setCurrentAccount(getApplicationContext(), account.getName());
                syncManager = new SyncManager(this);
                Application.saveBrandColors(this, Color.parseColor(account.getColor()), Color.parseColor(account.getTextColor()));
                Application.saveCurrentAccountId(this, account.getId());
            }, (deletedAccount) -> {
                syncManager.deleteAccount(deletedAccount.getId());
                localAccounts.remove(deletedAccount);
                if (localAccounts.size() > 0) {
                    Account newAccount = localAccounts.get(0);
                    syncManager = new SyncManager(this);
                    adapter.setCurrentAccount(newAccount);
                    SingleAccountHelper.setCurrentAccount(getApplicationContext(), newAccount.getName());
                    Application.saveBrandColors(this, Color.parseColor(newAccount.getColor()), Color.parseColor(newAccount.getTextColor()));
                    Application.saveCurrentAccountId(this, newAccount.getId());
                } else {
                    setResult(AppCompatActivity.RESULT_FIRST_USER);
                    finish();
                }
            });
            adapter.setAccounts(localAccounts);
            try {
                SingleSignOnAccount ssoAccount = SingleAccountHelper.getCurrentSingleSignOnAccount(this);
                if (ssoAccount != null) {
                    syncManager.readAccount(ssoAccount.name).observe(this, (account -> adapter.setCurrentAccount(account)));
                }
            } catch (NextcloudFilesAppAccountNotFoundException | NoCurrentAccountSelectedException e) {
                e.printStackTrace();
            }
            binding.accounts.setAdapter(adapter);
        }));
    }

    @Override
    public void onBackPressed() {
        onSupportNavigateUp();
    }

    @Override
    public void applyBrand(int mainColor, int textColor) {
        applyBrandToPrimaryToolbar(mainColor, textColor, binding.toolbar);
    }
}
