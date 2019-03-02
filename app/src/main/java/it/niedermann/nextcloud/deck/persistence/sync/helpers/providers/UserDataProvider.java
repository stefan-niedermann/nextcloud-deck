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
    public User getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, User entity) {
        return dataBaseAdapter.getUserByUidDirectly(accountId, entity.getUid());
    }

    @Override
    public long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, User entity) {
        return dataBaseAdapter.createUser(accountId, entity);
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, User entity) {
        dataBaseAdapter.updateUser(accountId, entity);
    }

    @Override
    public void goDeeper(SyncHelper syncHelper, User existingEntity, User entityFromServer) {
        // ain't goin' deeper <3
        return;
    }

    @Override
    public void createOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<User> responder, User entity) {

    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, User user) {

    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<User> callback, User entity) {

    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<User> callback, User entity) {

    }
}
