package it.niedermann.nextcloud.deck.ui.manageaccounts;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nextcloud.android.sso.helper.SingleAccountHelper;

import it.niedermann.nextcloud.deck.databinding.ActivityManageAccountsBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

import static it.niedermann.nextcloud.deck.DeckApplication.readCurrentAccountId;
import static it.niedermann.nextcloud.deck.DeckApplication.saveCurrentAccountId;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;

public class ManageAccountsActivity extends AppCompatActivity {

    private static final String TAG = ManageAccountsActivity.class.getSimpleName();

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

        adapter = new ManageAccountAdapter((account) -> {
            SingleAccountHelper.setCurrentAccount(getApplicationContext(), account.getName());
            syncManager = new SyncManager(this);
            saveCurrentAccountId(this, account.getId());
        }, (accountPair) -> {
            if (accountPair.first != null) {
                syncManager.deleteAccount(accountPair.first.getId());
            } else {
                throw new IllegalArgumentException("Could not delete account because given account was null.");
            }
            Account newAccount = accountPair.second;
            if (newAccount != null) {
                SingleAccountHelper.setCurrentAccount(getApplicationContext(), newAccount.getName());
                saveCurrentAccountId(this, newAccount.getId());
                syncManager = new SyncManager(this);
            } else {
                Log.i(TAG, "Got delete account request, but new account is null. Maybe last account has been deleted?");
            }
        });
        binding.accounts.setAdapter(adapter);

        observeOnce(syncManager.readAccount(readCurrentAccountId(this)), this, (account -> {
            adapter.setCurrentAccount(account);
            syncManager.readAccounts().observe(this, (localAccounts -> {
                if (localAccounts.size() == 0) {
                    Log.i(TAG, "No accounts, finishing " + ManageAccountsActivity.class.getSimpleName());
                    setResult(AppCompatActivity.RESULT_FIRST_USER);
                    finish();
                } else {
                    adapter.setAccounts(localAccounts);
                }
            }));
        }));
    }

    @Override
    public void onBackPressed() {
        onSupportNavigateUp();
    }
}
