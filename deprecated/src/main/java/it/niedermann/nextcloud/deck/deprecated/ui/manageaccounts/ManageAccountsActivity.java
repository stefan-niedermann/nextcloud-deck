package it.niedermann.nextcloud.deck.deprecated.ui.manageaccounts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.databinding.ActivityManageAccountsBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.Themed;

public class ManageAccountsActivity extends AppCompatActivity implements Themed {

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

        adapter = new ManageAccountAdapter(account -> viewModel.saveCurrentAccount(account), accountPair -> {
            if (accountPair.first != null) {
                viewModel.deleteAccount(accountPair.first.getId());
            } else {
                throw new IllegalArgumentException("Could not delete account because given account was null.");
            }
            Account newAccount = accountPair.second;
            if (newAccount != null) {
                viewModel.saveCurrentAccount(newAccount);
            } else {
                Log.i(TAG, "Got delete account request, but new account is null. Maybe last account has been deleted?");
            }
        });
        binding.accounts.setAdapter(adapter);

        viewModel.getCurrentAccountColor().observe(this, this::applyTheme);

        viewModel.getCurrentAccountId().thenAcceptAsync(accountId -> new ReactiveLiveData<>(viewModel.readAccount(accountId))
                .observeOnce(this, account -> {
                    adapter.setCurrentAccount(account);
                    viewModel.readAccounts().observe(this, (localAccounts -> {
                        if (localAccounts.size() == 0) {
                            Log.i(TAG, "No accounts, finishing " + ManageAccountsActivity.class.getSimpleName());
                            finish();
                        } else {
                            adapter.setAccounts(localAccounts);
                        }
                    }));
                }), ContextCompat.getMainExecutor(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }

    @Override
    public void applyTheme(int color) {
        final var utils = ThemeUtils.of(color, this);

        utils.material.themeToolbar(binding.toolbar);
        utils.deck.themeStatusBar(this, binding.appBarLayout);
    }

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, ManageAccountsActivity.class);
    }
}
