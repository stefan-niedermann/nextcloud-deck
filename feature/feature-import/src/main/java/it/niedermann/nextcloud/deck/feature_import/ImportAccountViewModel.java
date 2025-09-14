package it.niedermann.nextcloud.deck.feature_import;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.nextcloud.android.sso.model.SingleSignOnAccount;

import it.niedermann.nextcloud.deck.repository.AccountRepository;
import it.niedermann.nextcloud.deck.shared.model.Account;

public class ImportAccountViewModel extends ViewModel {

    private final AccountRepository accountRepository;

    public ImportAccountViewModel(@NonNull Application application) {
        this.accountRepository = new AccountRepository(application.getApplicationContext());
    }

    public LiveData<Account> importAccount(@NonNull SingleSignOnAccount account) {
        return this.accountRepository.importAccount(account.name, account.url, account.userId, account.token);
    }
}
