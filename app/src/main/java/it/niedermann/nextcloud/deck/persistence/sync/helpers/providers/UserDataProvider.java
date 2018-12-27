package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public class UserDataProvider implements IDataProvider<User> {

    private Board board;
    private FullStack stack;
    private FullCard card;
    private List<User> users;

    public UserDataProvider(Board board, FullStack stack, FullCard card, List<User> users) {
        this.board = board;
        this.stack = stack;
        this.card = card;
        this.users = users;
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<User>> responder) {
        responder.onResponse(users);
    }

    @Override
    public User getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, long remoteId) {
        return dataBaseAdapter.getUserByRemoteIdDirectly(accountId, remoteId);
    }

    @Override
    public void createInDB(DataBaseAdapter dataBaseAdapter, long accountId, User entity) {
        dataBaseAdapter.createUser(accountId, entity);
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, User entity) {
        dataBaseAdapter.updateUser(accountId, entity);
    }

    @Override
    public void goDeeper(SyncHelper syncHelper, User entityFromServer) {
        // ain't goin' deeper <3
        return;
    }
}
