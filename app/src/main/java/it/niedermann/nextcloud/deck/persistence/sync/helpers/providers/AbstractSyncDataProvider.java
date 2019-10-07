package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public abstract class AbstractSyncDataProvider<T extends IRemoteEntity> {

    protected AbstractSyncDataProvider<?> parent;
    private List<AbstractSyncDataProvider<?>> children = new ArrayList<>();
    private boolean stillGoingDeeper = false;

    public AbstractSyncDataProvider(AbstractSyncDataProvider<?> parent) {
        this.parent = parent;
    }

    public void registerChildInParent(AbstractSyncDataProvider<?> child) {
        if (parent != null) {
            parent.addChild(child);
        }
    }

    public void handleDeletes(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, List<T> entitiesFromServer){
        // do nothing as a default.
    }

    protected static <T extends IRemoteEntity> List<T> findDelta(List<T> entitiesFromServer, List<T> localEntities){
        List<T> delta = new ArrayList<>();
        for (T localEntity : localEntities) {
            boolean found = false;
            for (T remoteEntity : entitiesFromServer) {
                if (remoteEntity.getId().equals(localEntity.getId()) && localEntity.getAccountId() == remoteEntity.getAccountId()) {
                    found = true;
                    break;
                }
            }
            if (!found){
                delta.add(localEntity);
            }
        }
        return delta;
    }

    public void addChild(AbstractSyncDataProvider<?> child) {
        children.add(child);
    }

    public abstract void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<T>> responder, Date lastSync);

    public abstract T getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, T entity);

    public abstract long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, T b);

    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, T t) {
        updateInDB(dataBaseAdapter, accountId, t, true);
    }

    public abstract void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, T t, boolean setStatus);

    public abstract void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, T t);

    public void deletePhysicallyInDB(DataBaseAdapter dataBaseAdapter, long accountId, T t){
        deleteInDB(dataBaseAdapter, accountId, t);
    }

    public void goDeeper(SyncHelper syncHelper, T existingEntity, T entityFromServer, IResponseCallback<Boolean> callback) {
        childDone(this, callback, true);
    }

    public abstract void createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<T> responder, T entity);

    public abstract void updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<T> callback, T entity);

    public abstract void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Void> callback, T entity, DataBaseAdapter dataBaseAdapter);

    public void childDone(AbstractSyncDataProvider<?> child, IResponseCallback<Boolean> responseCallback, boolean syncChangedSomething) {
        children.remove(child);
        if (!stillGoingDeeper && children.isEmpty()) {
            if (parent != null) {
                parent.childDone(this, responseCallback, syncChangedSomething);
            } else {
                responseCallback.onResponse(syncChangedSomething);
            }
        }
    }

    public void doneGoingDeeper(IResponseCallback<Boolean> responseCallback, boolean syncChangedSomething) {
        stillGoingDeeper = false;
        childDone(this, responseCallback, syncChangedSomething);
    }

    public void goingDeeper() {
        stillGoingDeeper = true;
    }

    public abstract List<T> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Date lastSync);

    public void goDeeperForUpSync(SyncHelper syncHelper, ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, IResponseCallback<Boolean> callback) {
        //do nothing
    }

    public void onError(Throwable error, IResponseCallback<Boolean> responseCallback) {
        if (parent != null) {
            parent.childDone(this, responseCallback, false);
        }
        //TODO: what to do? what side effect would the following have:
//        responseCallback.onError(error);
    }

    public T applyUpdatesFromRemote(T localEntity, T remoteEntity, Long accountId) {
        return remoteEntity;
    }
}
