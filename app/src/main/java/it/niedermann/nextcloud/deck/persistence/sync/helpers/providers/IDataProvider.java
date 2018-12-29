package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public interface IDataProvider <T extends IRemoteEntity> {
    void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<T>> responder);


    T getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, long remoteId);

    void createInDB(DataBaseAdapter dataBaseAdapter, long accountId, T b);

    void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, T t);

    void goDeeper(SyncHelper syncHelper, T entityFromServer);

    default void doneAll(IResponseCallback<Boolean> responseCallback, boolean syncChangedSomething){
        // do nothing! Only BoardDataProvider should overwrite this!
    }
}
