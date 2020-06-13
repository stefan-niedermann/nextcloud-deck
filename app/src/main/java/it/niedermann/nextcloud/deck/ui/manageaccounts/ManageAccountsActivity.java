package it.niedermann.nextcloud.deck.ui.manageaccounts;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import it.niedermann.nextcloud.deck.databinding.ActivityManageAccountsBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

public class ManageAccountsActivity extends AppCompatActivity {

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

            adapter = new ManageAccountAdapter((localAccount) -> {
                SingleAccountHelper.setCurrentAccount(getApplicationContext(), localAccount.getName());
            }, (localAccount) -> {
                syncManager.deleteAccount(localAccount.getId());
                for (Account temp : localAccounts) {
                    if (temp.getId() == localAccount.getId()) {
                        localAccounts.remove(temp);
                        break;
                    }
                }
                if (localAccounts.size() > 0) {
                    SingleAccountHelper.setCurrentAccount(getApplicationContext(), localAccounts.get(0).getName());
                    adapter.setCurrentAccount(localAccounts.get(0));
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
}
