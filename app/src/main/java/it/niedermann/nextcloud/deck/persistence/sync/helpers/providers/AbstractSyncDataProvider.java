package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
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

    public void handleDeletes(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, List<T> entitiesFromServer) {
        // do nothing as a default.
    }

    /**
     * Searches each entry of <code>listB</code> in list <code>listA</code> and returns the missing ones
     *
     * @param listA List
     * @param listB List
     * @return all entries of <code>listB</code> missing in <code>listA</code>
     */
    public static <T extends IRemoteEntity> List<T> findDelta(List<T> listA, List<T> listB) {
        List<T> delta = new ArrayList<>();
        for (T b : listB) {
            if (b == null) {
                DeckLog.error("Entry in listB is null! skipping...");
                continue;
            }
            boolean found = false;
            for (T a : listA) {
                if (a == null) {
                    DeckLog.error("Entry in listA is null! skipping...");
                    continue;
                }
                if ((a.getLocalId() != null && b.getLocalId() != null ? (a.getLocalId().equals(b.getLocalId()))
                        : a.getId().equals(b.getId())) && b.getAccountId() == a.getAccountId()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                delta.add(b);
            }
        }
        return delta;
    }

    public void addChild(AbstractSyncDataProvider<?> child) {
        children.add(child);
    }

    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<T>> responder, Instant lastSync) {
        return;
    }

    public void getAllFromServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<List<T>> responder, Instant lastSync) {
        // Overridden, because we also need the DB-Adapter at some points here (see ACL data provider)
        getAllFromServer(serverAdapter, accountId, responder, lastSync);
    }

    public abstract T getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, T entity);

    public abstract long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, T b);

    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, T t) {
        updateInDB(dataBaseAdapter, accountId, t, true);
    }

    public abstract void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, T t, boolean setStatus);

    public abstract void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, T t);

    public void deletePhysicallyInDB(DataBaseAdapter dataBaseAdapter, long accountId, T t) {
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

    public abstract List<T> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Instant lastSync);

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
