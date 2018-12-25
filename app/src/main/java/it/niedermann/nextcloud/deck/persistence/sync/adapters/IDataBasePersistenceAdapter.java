package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import android.arch.lifecycle.LiveData;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;

public interface IDataBasePersistenceAdapter extends IPersistenceAdapter {
    void hasAccounts(IResponseCallback<Boolean> responseCallback);

    LiveData<Account> createAccount(String accoutName);

    void deleteAccount(long id);

    void updateAccount(Account account);

    LiveData<Account> readAccount(long id);

    LiveData<List<Account>> readAccounts();
}
