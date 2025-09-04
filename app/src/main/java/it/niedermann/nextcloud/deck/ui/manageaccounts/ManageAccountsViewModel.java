package it.niedermann.nextcloud.deck.ui.manageaccounts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.repository.AccountRepository;
import it.niedermann.nextcloud.deck.ui.viewmodel.BaseViewModel;

@SuppressWarnings("WeakerAccess")
public class ManageAccountsViewModel extends BaseViewModel {

    private final AccountRepository accountRepository;

    public ManageAccountsViewModel(@NonNull Application application) {
        super(application);
        this.accountRepository = new AccountRepository(application);
    }

    public LiveData<Integer> getCurrentAccountColor() {
        return new ReactiveLiveData<>(baseRepository.getCurrentAccountId$())
                .flatMap(baseRepository::getAccountColor);
    }

    public LiveData<Account> readAccount(long id) {
        return accountRepository.readAccount(id);
    }

    public LiveData<List<Account>> readAccounts() {
        return accountRepository.readAccounts();
    }

    public CompletableFuture<Long> getCurrentAccountId() {
        return baseRepository.getCurrentAccountId();
    }

    public void saveCurrentAccount(@NonNull Account account) {
        baseRepository.saveCurrentAccount(account);
    }

    public CompletableFuture<Void> deleteAccount(long id) {
        return accountRepository.deleteAccount(id);
    }
}
