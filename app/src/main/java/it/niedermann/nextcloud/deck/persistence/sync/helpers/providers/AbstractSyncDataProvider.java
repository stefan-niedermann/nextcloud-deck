package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import androidx.annotation.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.ResponseCallback;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public abstract class AbstractSyncDataProvider<T extends IRemoteEntity> {

    @Nullable
    protected AbstractSyncDataProvider<?> parent;
    protected final List<AbstractSyncDataProvider<?>> children = new ArrayList<>();
    protected boolean stillGoingDeeper = false;

    public AbstractSyncDataProvider(@Nullable AbstractSyncDataProvider<?> parent) {
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

    public Disposable getAllFromServer(ServerAdapter serverAdapter, long accountId, ResponseCallback<List<T>> responder, Instant lastSync) {
        return new CompositeDisposable();
    }

    public Disposable getAllFromServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<List<T>> responder, Instant lastSync) {
        // Overridden, because we also need the DB-Adapter at some points here (see ACL data provider)
        return getAllFromServer(serverAdapter, accountId, responder, lastSync);
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

    public void goDeeper(SyncHelper syncHelper, T existingEntity, T entityFromServer, ResponseCallback<Boolean> callback) {
        childDone(this, callback, true);
    }

    public abstract Disposable createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<T> responder, T entity);

    public abstract Disposable updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<T> callback, T entity);

    public abstract Disposable deleteOnServer(ServerAdapter serverAdapter, long accountId, ResponseCallback<Void> callback, T entity, DataBaseAdapter dataBaseAdapter);

    public void childDone(AbstractSyncDataProvider<?> child, ResponseCallback<Boolean> responseCallback, boolean syncChangedSomething) {
        removeChild(child);
        if (!stillGoingDeeper && children.isEmpty()) {
            if (parent != null) {
                parent.childDone(this, responseCallback, syncChangedSomething);
            } else {
                responseCallback.onResponse(syncChangedSomething);
            }
        }
    }

    protected boolean removeChild(AbstractSyncDataProvider<?> child) {
        return children.remove(child);
    }

    public void doneGoingDeeper(ResponseCallback<Boolean> responseCallback, boolean syncChangedSomething) {
        stillGoingDeeper = false;
        childDone(this, responseCallback, syncChangedSomething);
    }

    public void goingDeeper() {
        stillGoingDeeper = true;
    }

    public abstract List<T> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Instant lastSync);

    public Disposable goDeeperForUpSync(SyncHelper syncHelper, ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, ResponseCallback<Boolean> callback) {
        return new CompositeDisposable();
    }

    public void onError(ResponseCallback<Boolean> responseCallback) {
        if (parent != null) {
            parent.childDone(this, responseCallback, false);
        }
    }

    public T applyUpdatesFromRemote(T localEntity, T remoteEntity, Long accountId) {
        return remoteEntity;
    }
}
