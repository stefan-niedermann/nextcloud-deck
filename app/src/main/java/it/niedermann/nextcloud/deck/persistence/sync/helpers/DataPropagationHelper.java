package it.niedermann.nextcloud.deck.persistence.sync.helpers;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.AbstractSyncDataProvider;

public class DataPropagationHelper {
    private ServerAdapter serverAdapter;
    private DataBaseAdapter dataBaseAdapter;

    public DataPropagationHelper(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter) {
        this.serverAdapter = serverAdapter;
        this.dataBaseAdapter = dataBaseAdapter;
    }


    public <T extends IRemoteEntity> void createEntity(final AbstractSyncDataProvider<T> provider, T entity, IResponseCallback<T> callback){
        createEntity(provider, entity, callback, null);
    }
    public <T extends IRemoteEntity> void createEntity(final AbstractSyncDataProvider<T> provider, T entity, IResponseCallback<T> callback, OnResponseAction<T> actionOnResponse){
        final long accountId = callback.getAccount().getId();
        entity.setStatus(DBStatus.LOCAL_EDITED.getId());
        long newID;
        try {
            newID = provider.createInDB(dataBaseAdapter, accountId, entity);
        } catch (Throwable t) {
            callback.onError(t);
            return;
        }
        entity.setLocalId(newID);
        boolean connected = serverAdapter.hasInternetConnection();
        if (connected) {
            try {
                provider.createOnServer(serverAdapter, dataBaseAdapter, accountId, new IResponseCallback<T>(new Account(accountId)) {
                    @Override
                    public void onResponse(T response) {
                        new Thread(() -> {
                            response.setAccountId(accountId);
                            response.setLocalId(newID);
                            if (actionOnResponse != null) {
                                actionOnResponse.onResponse(entity, response);
                            }
                            response.setStatus(DBStatus.UP_TO_DATE.getId());
                            provider.updateInDB(dataBaseAdapter, accountId, response, false);
                            callback.onResponse(response);
                        }).start();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        new Thread(() -> callback.onError(throwable, entity)).start();
                    }
                }, entity);
            } catch (Throwable t) {
                callback.onError(t, entity);
            }
        } else {
            callback.onResponse(entity);
        }
    }

    public <T extends IRemoteEntity> void updateEntity(final AbstractSyncDataProvider<T> provider, T entity, IResponseCallback<T> callback){
        final long accountId = callback.getAccount().getId();
        entity.setStatus(DBStatus.LOCAL_EDITED.getId());
        try {
            provider.updateInDB(dataBaseAdapter, accountId, entity);
        } catch (Throwable t) {
            callback.onError(t);
            return;
        }
        boolean connected = serverAdapter.hasInternetConnection();
        if (entity.getId() != null && connected) {
            try {
                provider.updateOnServer(serverAdapter, dataBaseAdapter, accountId, new IResponseCallback<T>(new Account(accountId)) {
                    @Override
                    public void onResponse(T response) {
                        new Thread(() -> {
                            entity.setStatus(DBStatus.UP_TO_DATE.getId());
                            provider.updateInDB(dataBaseAdapter, accountId, entity, false);
                            callback.onResponse(entity);
                        }).start();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        new Thread(() -> {
                            callback.onError(throwable, entity);
                        }).start();
                    }
                }, entity);
            } catch (Throwable t) {
                callback.onError(t, entity);
            }
        } else {
            callback.onResponse(entity);
        }
    }
    public <T extends IRemoteEntity> void deleteEntity(final AbstractSyncDataProvider<T> provider, T entity, IResponseCallback<Void> callback){
        final long accountId = callback.getAccount().getId();
        provider.deleteInDB(dataBaseAdapter, accountId, entity);
        boolean connected = serverAdapter.hasInternetConnection();
        if (entity.getId() != null && connected) {
            try {
                provider.deleteOnServer(serverAdapter, accountId, new IResponseCallback<Void>(new Account(accountId)) {
                    @Override
                    public void onResponse(Void response) {
                        new Thread(() -> {
                            provider.deletePhysicallyInDB(dataBaseAdapter, accountId, entity);
                            callback.onResponse(null);
                        }).start();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        new Thread(() -> {
                            callback.onError(throwable);
                        }).start();
                    }
                }, entity, dataBaseAdapter);
            } catch (Throwable t) {
                callback.onError(t);
            }

        } else {
            callback.onResponse(null);
        }
    }

    public interface OnResponseAction <T> {
        void onResponse(T entity, T response);
    }
}
