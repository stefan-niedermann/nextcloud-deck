package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public interface IDataProvider <T extends IRemoteEntity> {
    void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<T>> responder, Date lastSync);

    T getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, T entity);

    long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, T b);

    void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, T t);

    void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, T t);

    void goDeeper(SyncHelper syncHelper, T existingEntity, T entityFromServer);

    void createOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<T> responder, T entity);

    void updateOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<T> callback, T entity);

    void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<T> callback, T entity);

    default void doneAll(IResponseCallback<Boolean> responseCallback, boolean syncChangedSomething){
        // do nothing! Only BoardDataProvider should overwrite this!
    }

    List<T> getAllFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Date lastSync);

    void goDeeperForUpSync(SyncHelper syncHelper, T entity, T response);
}
