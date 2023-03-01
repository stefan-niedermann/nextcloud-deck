package it.niedermann.nextcloud.deck.ui.manageaccounts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.ui.viewmodel.BaseViewModel;

@SuppressWarnings("WeakerAccess")
public class ManageAccountsViewModel extends BaseViewModel {

    public ManageAccountsViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Account> readAccount(long id) {
        return baseRepository.readAccount(id);
    }

    public LiveData<List<Account>> readAccounts() {
        return baseRepository.readAccounts();
    }

    public CompletableFuture<Long> getCurrentAccountId() {
        return baseRepository.getCurrentAccountId();
    }

    public void saveCurrentAccount(@NonNull Account account) {
        baseRepository.saveCurrentAccount(account);
    }

    public void deleteAccount(long id) {
        baseRepository.deleteAccount(id);
    }
}
