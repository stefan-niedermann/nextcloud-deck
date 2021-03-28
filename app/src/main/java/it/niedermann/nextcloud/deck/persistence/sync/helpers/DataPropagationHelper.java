package it.niedermann.nextcloud.deck.persistence.sync.helpers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.BiConsumer;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.AbstractSyncDataProvider;

public class DataPropagationHelper {
    @NonNull
    private final ServerAdapter serverAdapter;
    @NonNull
    private final DataBaseAdapter dataBaseAdapter;

    public DataPropagationHelper(@NonNull ServerAdapter serverAdapter, @NonNull DataBaseAdapter dataBaseAdapter) {
        this.serverAdapter = serverAdapter;
        this.dataBaseAdapter = dataBaseAdapter;
    }

    public <T extends IRemoteEntity> void createEntity(@NonNull final AbstractSyncDataProvider<T> provider, @NonNull T entity, IResponseCallback<T> callback){
        createEntity(provider, entity, callback, null);
    }

    public <T extends IRemoteEntity> void createEntity(@NonNull final AbstractSyncDataProvider<T> provider, @NonNull T entity, IResponseCallback<T> callback, @Nullable BiConsumer<T, T> actionOnResponse){
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
        if (serverAdapter.hasInternetConnection()) {
            try {
                provider.createOnServer(serverAdapter, dataBaseAdapter, accountId, new IResponseCallback<T>(callback.getAccount()) {
                    @Override
                    public void onResponse(T response) {
                        new Thread(() -> {
                            response.setAccountId(accountId);
                            response.setLocalId(newID);
                            if (actionOnResponse != null) {
                                actionOnResponse.accept(entity, response);
                            }
                            response.setStatus(DBStatus.UP_TO_DATE.getId());
                            provider.updateInDB(dataBaseAdapter, accountId, response, false);
                            callback.onResponse(response);
                        }).start();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        new Thread(() -> callback.onError(throwable)).start();
                    }
                }, entity);
            } catch (Throwable t) {
                callback.onError(t);
            }
        } else {
            callback.onResponse(entity);
        }
    }

    public <T extends IRemoteEntity> void updateEntity(@NonNull final AbstractSyncDataProvider<T> provider, @NonNull T entity, @NonNull IResponseCallback<T> callback){
        final long accountId = callback.getAccount().getId();
        entity.setStatus(DBStatus.LOCAL_EDITED.getId());
        try {
            provider.updateInDB(dataBaseAdapter, accountId, entity);
        } catch (Throwable t) {
            callback.onError(t);
            return;
        }
        if (entity.getId() != null && serverAdapter.hasInternetConnection()) {
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
                        new Thread(() -> callback.onError(throwable)).start();
                    }
                }, entity);
            } catch (Throwable t) {
                callback.onError(t);
            }
        } else {
            callback.onResponse(entity);
        }
    }

    public <T extends IRemoteEntity> void deleteEntity(@NonNull final AbstractSyncDataProvider<T> provider, @NonNull T entity, @NonNull IResponseCallback<Void> callback){
        final long accountId = callback.getAccount().getId();
        provider.deleteInDB(dataBaseAdapter, accountId, entity);
        if (entity.getId() != null && serverAdapter.hasInternetConnection()) {
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
                        new Thread(() -> callback.onError(throwable)).start();
                    }
                }, entity, dataBaseAdapter);
            } catch (Throwable t) {
                callback.onError(t);
            }
        } else {
            callback.onResponse(null);
        }
    }
}
