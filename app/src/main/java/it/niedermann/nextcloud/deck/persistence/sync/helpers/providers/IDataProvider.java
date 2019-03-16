package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public abstract class IDataProvider <T extends IRemoteEntity> {

    protected IDataProvider<?> parent;
    private List<IDataProvider<?>> children = new ArrayList<>();
    private boolean stillGoingDeeper = false;

    public IDataProvider(IDataProvider<?> parent){
        this.parent = parent;
    }

    public void registerChildInParent(IDataProvider<?> child){
        if (parent != null) {
            parent.addChild(child);
        }
    }

    public void addChild(IDataProvider<?> child){
        children.add(child);
    }

    public abstract void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<T>> responder, Date lastSync);

    public abstract T getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, T entity);

    public abstract long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, T b);

    public abstract void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, T t);

    public abstract void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, T t);

    public void goDeeper(SyncHelper syncHelper, T existingEntity, T entityFromServer, IResponseCallback<Boolean> callback) {
        childDone(this, callback, true);
    }

    public abstract void createOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<T> responder, T entity);

    public abstract void updateOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<T> callback, T entity);

    public abstract void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<T> callback, T entity);

//    public void doneAll(IResponseCallback<Boolean> responseCallback, boolean hadSomethingToSync, boolean syncChangedSomething){
//        requestStallCount--;
////        if (!hadSomethingToSync){
////            DeckLog.log("responded by "+this.getClass().getSimpleName());
////            String stacktrace = DeckLog.getCurrentStacktrace();
////            DeckLog.log("responded by "+stacktrace);
////        }
//        if (requestStallCount < 1 && parent != null) {
//            DeckLog.log("responded by "+this.getClass().getSimpleName());
//            parent.childDone(responseCallback, syncChangedSomething);
//        }
//    }

    public void childDone(IDataProvider<?> child, IResponseCallback<Boolean> responseCallback, boolean syncChangedSomething) {
        children.remove(child);
        if (!stillGoingDeeper && children.isEmpty()) {
            if (parent!=null){
                parent.childDone(this, responseCallback, syncChangedSomething);
            } else {
                responseCallback.onResponse(syncChangedSomething);
            }
        }
    }

    public void doneGoingDeeper(IResponseCallback<Boolean> responseCallback, boolean syncChangedSomething){
        stillGoingDeeper = false;
        childDone(this, responseCallback, syncChangedSomething);
    }

    public void goingDeeper(){
        stillGoingDeeper = true;
    }

    public abstract List<T> getAllFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Date lastSync);

    public abstract void goDeeperForUpSync(SyncHelper syncHelper, T entity, T response);
}
