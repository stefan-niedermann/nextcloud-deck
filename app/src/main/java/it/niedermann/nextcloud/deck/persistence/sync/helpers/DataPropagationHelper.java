package it.niedermann.nextcloud.deck.persistence.sync.helpers;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
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
        final long accountId = callback.getAccount().getId();
        long newID = provider.createInDB(dataBaseAdapter, accountId, entity);
        entity.setLocalId(newID);
        boolean connected = serverAdapter.hasInternetConnection();
        if (connected) {
            provider.createOnServer(serverAdapter, dataBaseAdapter, accountId, new IResponseCallback<T>(new Account(accountId)) {
                @Override
                public void onResponse(T response) {
                    new Thread(() -> {
                        applyUpdatesFromRemote(entity, response, accountId);
                        entity.setId(response.getId());
                        provider.updateInDB(dataBaseAdapter, accountId, entity);
                        callback.onResponse(entity);
                    }).start();
                }

                @Override
                public void onError(Throwable throwable) {
                    super.onError(throwable);
                    new Thread(() -> {
                        callback.onError(throwable);
                    }).start();
                }
            }, entity);
        } else {
            callback.onResponse(entity);
        }
    }

    public <T extends IRemoteEntity> void updateEntity(final AbstractSyncDataProvider<T> provider, T entity, IResponseCallback<T> callback){
        final long accountId = callback.getAccount().getId();
        provider.updateInDB(dataBaseAdapter, accountId, entity);
        boolean connected = serverAdapter.hasInternetConnection();
        if (connected) {
            provider.updateOnServer(serverAdapter, dataBaseAdapter, accountId, new IResponseCallback<T>(new Account(accountId)) {
                @Override
                public void onResponse(T response) {
                    new Thread(() -> {
                        callback.onResponse(entity);
                    }).start();
                }

                @Override
                public void onError(Throwable throwable) {
                    super.onError(throwable);
                    new Thread(() -> {
                        callback.onError(throwable);
                    }).start();
                }
            }, entity);
        } else {
            callback.onResponse(entity);
        }
    }
    public <T extends IRemoteEntity> void deleteEntity(final AbstractSyncDataProvider<T> provider, T entity, IResponseCallback<T> callback){
        final long accountId = callback.getAccount().getId();
        provider.deleteInDB(dataBaseAdapter, accountId, entity);
        boolean connected = serverAdapter.hasInternetConnection();
        if (connected) {
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
        } else {
            callback.onResponse(null);
        }
    }

    private <T extends IRemoteEntity> T applyUpdatesFromRemote(T localEntity, T remoteEntity, Long accountId) {
        if (!accountId.equals(localEntity.getAccountId())) {
            throw new IllegalArgumentException("IDs of Accounts are not matching! WTF are you doin?!");
        }
        remoteEntity.setLastModifiedLocal(remoteEntity.getLastModified()); // not an error! local-modification = remote-mod
        remoteEntity.setLocalId(localEntity.getLocalId());
        return remoteEntity;
    }
}
