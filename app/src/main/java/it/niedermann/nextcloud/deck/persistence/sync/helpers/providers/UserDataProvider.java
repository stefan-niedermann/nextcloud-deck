package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.time.Instant;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import it.niedermann.nextcloud.deck.api.ResponseCallback;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class UserDataProvider extends AbstractSyncDataProvider<User> {

    private final List<User> users;

    public UserDataProvider(AbstractSyncDataProvider<?> parent, List<User> users) {
        super(parent);
        this.users = users;
    }

    @Override
    public Disposable getAllFromServer(ServerAdapter serverAdapter, long accountId, ResponseCallback<List<User>> responder, Instant lastSync) {
        responder.onResponse(users);
        return new CompositeDisposable();
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
    public Disposable createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<User> responder, User entity) {
        //TODO: implement
        return new CompositeDisposable();
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, User user) {
        //TODO: implement
    }

    @Override
    public Disposable deleteOnServer(ServerAdapter serverAdapter, long accountId, ResponseCallback<Void> callback, User entity, DataBaseAdapter dataBaseAdapter) {
        //TODO: implement
        return new CompositeDisposable();
    }

    @Override
    public List<User> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Instant lastSync) {
        return null;
    }

    @Override
    public Disposable updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<User> callback, User entity) {
        //TODO: implement
        return new CompositeDisposable();
    }
}
