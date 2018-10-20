package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Account;

public interface IDataBasePersistenceAdapter extends IPersistenceAdapter {
    boolean hasAccounts();

    Account createAccount(String accoutName);

    void deleteAccount(long id);

    void updateAccount(Account account);

    Account readAccount(long id);

    List<Account> readAccounts();
}
