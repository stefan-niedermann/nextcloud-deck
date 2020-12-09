package it.niedermann.nextcloud.deck.persistence.sync.helpers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.net.HttpURLConnection;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.AbstractSyncDataProvider;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.IRelationshipProvider;

public class SyncHelper {
    private final ServerAdapter serverAdapter;
    private final DataBaseAdapter dataBaseAdapter;
    private Account account;
    private long accountId;
    private IResponseCallback<Boolean> responseCallback;
    private final Instant lastSync;

    public SyncHelper(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, Instant lastSync) {
        this.serverAdapter = serverAdapter;
        this.dataBaseAdapter = dataBaseAdapter;
        this.lastSync = lastSync;
    }

    // Sync Server -> App
    public <T extends IRemoteEntity> void doSyncFor(@NonNull final AbstractSyncDataProvider<T> provider) {
        provider.registerChildInParent(provider);
        provider.getAllFromServer(serverAdapter, dataBaseAdapter, accountId, new IResponseCallback<List<T>>(account) {
            @Override
            public void onResponse(List<T> response) {
                if (response != null) {
                    provider.goingDeeper();
                    for (T entityFromServer : response) {
                        if (entityFromServer == null) {
                            // see https://github.com/stefan-niedermann/nextcloud-deck/issues/574
                            DeckLog.error("Skipped null value from server for DataProvider: " + provider.getClass().getSimpleName());
                            continue;
                        }
                        entityFromServer.setAccountId(accountId);
                        T existingEntity = provider.getSingleFromDB(dataBaseAdapter, accountId, entityFromServer);

                        if (existingEntity == null) {
                            provider.createInDB(dataBaseAdapter, accountId, entityFromServer);
                        } else {
                            //TODO: how to handle deletes? what about archived?
                            if (existingEntity.getStatus() != DBStatus.UP_TO_DATE.getId()) {
                                DeckLog.warn("Conflicting changes on entity: " + existingEntity);
                                // TODO: what to do?
                            } else {
                                if (entityFromServer.getEtag() != null && entityFromServer.getEtag().equals(existingEntity.getEtag())) {
                                    DeckLog.log("[" + provider.getClass().getSimpleName() + "] ETags do match! skipping " + existingEntity.getClass().getSimpleName() + " with localId: " + existingEntity.getLocalId());
                                    continue;
                                }
                                provider.updateInDB(dataBaseAdapter, accountId, applyUpdatesFromRemote(provider, existingEntity, entityFromServer, accountId), false);
                            }
                        }
                        existingEntity = provider.getSingleFromDB(dataBaseAdapter, accountId, entityFromServer);
                        provider.goDeeper(SyncHelper.this, existingEntity, entityFromServer, responseCallback);
                    }

                    provider.handleDeletes(serverAdapter, dataBaseAdapter, accountId, response);

                    provider.doneGoingDeeper(responseCallback, true);
                } else {
                    provider.childDone(provider, responseCallback, false);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (throwable.getClass() == NextcloudHttpRequestFailedException.class) {
                    NextcloudHttpRequestFailedException requestFailedException = (NextcloudHttpRequestFailedException) throwable;
                    if (HttpURLConnection.HTTP_NOT_MODIFIED == requestFailedException.getStatusCode()){
                        DeckLog.log("[" + provider.getClass().getSimpleName() + "] ETags do match! skipping this one.");
                        // well, etags say we're fine here. no need to go deeper.
                        provider.childDone(provider, responseCallback, false);
                        return;
                    }
                }
                provider.onError(throwable, responseCallback);
                responseCallback.onError(throwable);
            }
        }, lastSync);
    }

    // Sync App -> Server
    public <T extends IRemoteEntity> void doUpSyncFor(@NonNull AbstractSyncDataProvider<T> provider) {
        doUpSyncFor(provider, null);
    }

    public <T extends IRemoteEntity> void doUpSyncFor(@NonNull AbstractSyncDataProvider<T> provider, @Nullable CountDownLatch countDownLatch) {
        final List<T> allFromDB = provider.getAllChangedFromDB(dataBaseAdapter, accountId, lastSync);
        if (allFromDB != null && !allFromDB.isEmpty()) {
            for (T entity : allFromDB) {
                if (entity.getId() != null) {
                    if (entity.getStatusEnum() == DBStatus.LOCAL_DELETED) {
                        provider.deleteOnServer(serverAdapter, accountId, getDeleteCallback(provider, entity), entity, dataBaseAdapter);
                        if (countDownLatch != null) {
                            countDownLatch.countDown();
                        }
                    } else {
                        provider.updateOnServer(serverAdapter, dataBaseAdapter, accountId, getUpdateCallback(provider, entity, countDownLatch), entity);
                    }
                } else {
                    provider.createOnServer(serverAdapter, dataBaseAdapter, accountId, getUpdateCallback(provider, entity, countDownLatch), entity);
                }
            }
        } else {
            provider.goDeeperForUpSync(this, serverAdapter, dataBaseAdapter, responseCallback);
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
        }
    }

    private <T extends IRemoteEntity> IResponseCallback<Void> getDeleteCallback(@NonNull AbstractSyncDataProvider<T> provider, T entity) {
        return new IResponseCallback<Void>(account) {
            @Override
            public void onResponse(Void response) {
                provider.deletePhysicallyInDB(dataBaseAdapter, accountId, entity);
                provider.goDeeperForUpSync(SyncHelper.this, serverAdapter, dataBaseAdapter, responseCallback);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                responseCallback.onError(throwable);
            }
        };
    }

    private <T extends IRemoteEntity> IResponseCallback<T> getUpdateCallback(@NonNull AbstractSyncDataProvider<T> provider, @NonNull T entity, @Nullable CountDownLatch countDownLatch) {
        return new IResponseCallback<T>(account) {
            @Override
            public void onResponse(T response) {
                response.setAccountId(this.account.getId());
                T update = applyUpdatesFromRemote(provider, entity, response, accountId);
                update.setId(response.getId());
                update.setStatus(DBStatus.UP_TO_DATE.getId());
                provider.updateInDB(dataBaseAdapter, accountId, update, false);
                provider.goDeeperForUpSync(SyncHelper.this, serverAdapter, dataBaseAdapter, responseCallback);
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                responseCallback.onError(throwable);
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
            }
        };
    }

    public void fixRelations(@NonNull IRelationshipProvider relationshipProvider) {
        // this is OK, since the delete only affects records with status UP_TO_DATE
        relationshipProvider.deleteAllExisting(dataBaseAdapter, accountId);
        relationshipProvider.insertAllNecessary(dataBaseAdapter, accountId);
    }

    private <T extends IRemoteEntity> T applyUpdatesFromRemote(@NonNull AbstractSyncDataProvider<T> provider, @NonNull T localEntity, @NonNull T remoteEntity, @NonNull Long accountId) {
        if (!accountId.equals(localEntity.getAccountId())) {
            throw new IllegalArgumentException("IDs of Accounts are not matching! WTF are you doin?!");
        }
        remoteEntity.setLocalId(localEntity.getLocalId());
        remoteEntity = provider.applyUpdatesFromRemote(localEntity, remoteEntity, accountId);
        return remoteEntity;
    }

    public SyncHelper setResponseCallback(@NonNull IResponseCallback<Boolean> callback) {
        this.responseCallback = callback;
        this.account = responseCallback.getAccount();
        accountId = account.getId();
        return this;
    }
}
