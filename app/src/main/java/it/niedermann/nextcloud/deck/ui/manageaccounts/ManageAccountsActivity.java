package it.niedermann.nextcloud.deck.ui.manageaccounts;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.nextcloud.deck.databinding.ActivityManageAccountsBinding;
import it.niedermann.nextcloud.deck.model.Account;

import static it.niedermann.nextcloud.deck.DeckApplication.readCurrentAccountId;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;

public class ManageAccountsActivity extends AppCompatActivity {

    private static final String TAG = ManageAccountsActivity.class.getSimpleName();

    private ActivityManageAccountsBinding binding;
    private ManageAccountsViewModel viewModel;
    private ManageAccountAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityManageAccountsBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(ManageAccountsViewModel.class);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        adapter = new ManageAccountAdapter((account) -> viewModel.setNewAccount(account), (accountPair) -> {
            if (accountPair.first != null) {
                viewModel.deleteAccount(accountPair.first.getId());
            } else {
                throw new IllegalArgumentException("Could not delete account because given account was null.");
            }
            Account newAccount = accountPair.second;
            if (newAccount != null) {
                viewModel.setNewAccount(newAccount);
            } else {
                Log.i(TAG, "Got delete account request, but new account is null. Maybe last account has been deleted?");
            }
        });
        binding.accounts.setAdapter(adapter);

        observeOnce(viewModel.readAccount(readCurrentAccountId(this)), this, (account -> {
            adapter.setCurrentAccount(account);
            viewModel.readAccounts().observe(this, (localAccounts -> {
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
