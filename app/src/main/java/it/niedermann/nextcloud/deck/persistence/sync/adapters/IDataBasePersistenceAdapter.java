package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import android.arch.lifecycle.LiveData;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;

public interface IDataBasePersistenceAdapter extends IPersistenceAdapter {
    boolean hasAccounts();

    LiveData<Account> createAccount(String accoutName);

    void deleteAccount(long id);

    void updateAccount(Account account);

    LiveData<Account> readAccount(long id);

    LiveData<List<Account>> readAccounts();


    void getStacks(long accountId, long boardId, IResponseCallback<LiveData<List<Stack>>> responseCallback);

    void getStack(long accountId, long boardId, long stackId, IResponseCallback<LiveData<Stack>> responseCallback);

    void getBoards(long accountId, IResponseCallback<LiveData<List<Board>>> responseCallback);

    void getCard(long accountId, long boardId, long stackId, long cardId, IResponseCallback<LiveData<FullCard>> responseCallback);

}
