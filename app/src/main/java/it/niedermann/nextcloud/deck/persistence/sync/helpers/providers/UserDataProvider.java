package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.time.Instant;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class UserDataProvider extends AbstractSyncDataProvider<User> {

    private Board board;
    private FullStack stack;
    private FullCard card;
    private List<User> users;

    public UserDataProvider(AbstractSyncDataProvider<?> parent, Board board, FullStack stack, FullCard card, List<User> users) {
        super(parent);
        this.board = board;
        this.stack = stack;
        this.card = card;
        this.users = users;
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<User>> responder, Instant lastSync) {
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
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, User entity, boolean setStatus) {
        dataBaseAdapter.updateUser(accountId, entity, setStatus);
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, User entity) {
        updateInDB(dataBaseAdapter, accountId, entity, false);
    }

    @Override
    public void createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<User> responder, User entity) {
        //TODO: implement
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, User user) {
        //TODO: implement
    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Void> callback, User entity, DataBaseAdapter dataBaseAdapter) {
        //TODO: implement
    }

    @Override
    public List<User> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Instant lastSync) {
        return null;
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<User> callback, User entity) {
        //TODO: implement
    }
}
