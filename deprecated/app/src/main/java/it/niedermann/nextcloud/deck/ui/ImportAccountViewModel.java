package it.niedermann.nextcloud.deck.ui;

import android.app.Application;

import androidx.annotation.NonNull;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.ui.viewmodel.BaseViewModel;

public class ImportAccountViewModel extends BaseViewModel {

    public ImportAccountViewModel(@NonNull Application application) {
        super(application);
    }

    public ReactiveLiveData<Boolean> hasAccounts() {
        return new ReactiveLiveData<>(baseRepository.hasAccounts());
    }

    public void saveCurrentAccount(@NonNull Account account) {
        this.baseRepository.saveCurrentAccount(account);
    }

    public void createAccount(@NonNull Account account, @NonNull IResponseCallback<Account> callback) {
        this.baseRepository.createAccount(account, callback);
    }

    public void deleteAccount(long accountId) {
        this.baseRepository.deleteAccount(accountId);
    }
}
