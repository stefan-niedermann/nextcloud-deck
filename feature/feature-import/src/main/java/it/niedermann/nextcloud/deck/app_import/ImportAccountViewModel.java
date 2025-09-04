package it.niedermann.nextcloud.deck.feature_import;

import android.accounts.Account;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.nextcloud.android.sso.model.SingleSignOnAccount;

import it.niedermann.nextcloud.deck.repository.AccountRepository;

public class ImportAccountViewModel extends ViewModel {

    private final AccountRepository accountRepository;

    public ImportAccountViewModel(@NonNull Application application) {
        this.accountRepository = new AccountRepository(application);
    }

    public LiveData<Account> importAccount(@NonNull SingleSignOnAccount account) {
        return this.accountRepository.importAccount(account.name, account.url, account.userId);
    }
}
