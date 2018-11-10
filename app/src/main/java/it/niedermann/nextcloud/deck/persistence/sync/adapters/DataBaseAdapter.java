package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import android.content.Context;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.persistence.DeckDataBase;

public class DataBaseAdapter implements IDataBasePersistenceAdapter {

    private DeckDataBase deckDataBase;
    private Context applicationContext;

    public DataBaseAdapter(Context applicationContext) {
        this.applicationContext = applicationContext;
        this.deckDataBase = DeckDataBase.getInstance(applicationContext);
    }

    @Override
    public boolean hasAccounts() {
        return deckDataBase.hasAccounts();
    }

    @Override
    public Account createAccount(String accoutName) {
        return deckDataBase.createAccount(accoutName);
    }

    @Override
    public void deleteAccount(long id) {
        deckDataBase.deleteAccount(id);
    }

    @Override
    public void updateAccount(Account account) {
        deckDataBase.updateAccount(account);
    }

    @Override
    public Account readAccount(long id) {
        return deckDataBase.readAccount(id);
    }

    @Override
    public List<Account> readAccounts() {
        return deckDataBase.readAccounts();
    }

    @Override
    public void getBoards(long accountId, IResponseCallback<List<Board>> responseCallback) {
        // TODO implement
    }

    @Override
    public void getStacks(long accountId, long boardId, IResponseCallback<List<Stack>> responseCallback) {

    }

    @Override
    public void getStack(long accountId, long boardId, long stackId, IResponseCallback<Stack> responseCallback) {

    }

    @Override
    public void getCard(long accountId, long boardId, long stackId, long cardId, IResponseCallback<Card> responseCallback) {

    }
}
