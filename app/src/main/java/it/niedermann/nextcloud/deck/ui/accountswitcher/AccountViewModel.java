package it.niedermann.nextcloud.deck.ui.accountswitcher;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.repository.AccountRepository;
import it.niedermann.nextcloud.deck.ui.viewmodel.BaseViewModel;

public class AccountViewModel extends BaseViewModel {

    private final AccountRepository accountRepository;

    public AccountViewModel(@NonNull Application application) {
        super(application);
        this.accountRepository = new AccountRepository(application);
    }

    public void saveCurrentAccount(@NonNull Account account) {
        baseRepository.saveCurrentAccount(account);
    }

    public LiveData<Account> getCurrentAccount() {
        return new ReactiveLiveData<>(baseRepository.getCurrentAccountId$())
                .flatMap(accountRepository::readAccount);
    }

    public LiveData<List<Account>> readAccounts() {
        return accountRepository.readAccounts();
    }
}
