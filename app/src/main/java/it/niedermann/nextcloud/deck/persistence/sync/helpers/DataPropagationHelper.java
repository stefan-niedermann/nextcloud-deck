package it.niedermann.nextcloud.deck.persistence.sync.helpers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

import it.niedermann.nextcloud.deck.api.ResponseCallback;
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
    @NonNull
    private final ExecutorService executor;

    public DataPropagationHelper(@NonNull ServerAdapter serverAdapter, @NonNull DataBaseAdapter dataBaseAdapter, @NonNull ExecutorService executor) {
        this.serverAdapter = serverAdapter;
        this.dataBaseAdapter = dataBaseAdapter;
        this.executor = executor;
    }

    public <T extends IRemoteEntity> void createEntity(@NonNull final AbstractSyncDataProvider<T> provider, @NonNull T entity, ResponseCallback<T> callback){
        createEntity(provider, entity, callback, null);
    }

    public <T extends IRemoteEntity> void createEntity(@NonNull final AbstractSyncDataProvider<T> provider, @NonNull T entity, ResponseCallback<T> callback, @Nullable BiConsumer<T, T> actionOnResponse){
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
                provider.createOnServer(serverAdapter, dataBaseAdapter, accountId, new ResponseCallback<>(callback.getAccount()) {
                    @Override
                    public void onResponse(T response) {
                        executor.submit(() -> {
                            response.setAccountId(accountId);
                            response.setLocalId(newID);
                            if (actionOnResponse != null) {
                                actionOnResponse.accept(entity, response);
                            }
                            response.setStatus(DBStatus.UP_TO_DATE.getId());
                            provider.updateInDB(dataBaseAdapter, accountId, response, false);
                            callback.onResponse(response);
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        executor.submit(() -> callback.onError(throwable));
                    }
                }, entity);
            } catch (Throwable t) {
                callback.onError(t);
            }
        } else {
            callback.onResponse(entity);
        }
    }

    public <T extends IRemoteEntity> void updateEntity(@NonNull final AbstractSyncDataProvider<T> provider, @NonNull T entity, @NonNull ResponseCallback<T> callback){
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
                provider.updateOnServer(serverAdapter, dataBaseAdapter, accountId, new ResponseCallback<>(new Account(accountId)) {
                    @Override
                    public void onResponse(T response) {
                        executor.submit(() -> {
                            entity.setStatus(DBStatus.UP_TO_DATE.getId());
                            provider.updateInDB(dataBaseAdapter, accountId, entity, false);
                            callback.onResponse(entity);
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        executor.submit(() -> callback.onError(throwable));
                    }
                }, entity);
            } catch (Throwable t) {
                callback.onError(t);
            }
        } else {
            callback.onResponse(entity);
        }
    }

    public <T extends IRemoteEntity> void deleteEntity(@NonNull final AbstractSyncDataProvider<T> provider, @NonNull T entity, @NonNull ResponseCallback<Void> callback){
        final long accountId = callback.getAccount().getId();
        provider.deleteInDB(dataBaseAdapter, accountId, entity);
        if (entity.getId() != null && serverAdapter.hasInternetConnection()) {
            try {
                provider.deleteOnServer(serverAdapter, accountId, new ResponseCallback<>(new Account(accountId)) {
                    @Override
                    public void onResponse(Void response) {
                        executor.submit(() -> {
                            provider.deletePhysicallyInDB(dataBaseAdapter, accountId, entity);
                            callback.onResponse(null);
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        executor.submit(() -> callback.onError(throwable));
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
