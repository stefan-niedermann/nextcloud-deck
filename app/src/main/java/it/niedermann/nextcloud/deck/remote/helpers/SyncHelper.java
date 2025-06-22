package it.niedermann.nextcloud.deck.remote.helpers;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteConstraintException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.nextcloud.android.sso.api.EmptyResponse;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.net.HttpURLConnection;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.database.DataBaseAdapter;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;
import it.niedermann.nextcloud.deck.remote.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import it.niedermann.nextcloud.deck.remote.helpers.providers.AbstractSyncDataProvider;
import it.niedermann.nextcloud.deck.remote.helpers.providers.IRelationshipProvider;
import okhttp3.Headers;

public class SyncHelper {
    // entity-class -> id if entity
    private static final HashMap<Class<? extends IRemoteEntity>, ConcurrentSkipListSet<Long>> CURRENTLY_IN_UPSYNC = new HashMap<>();

    @NonNull
    private final ServerAdapter serverAdapter;
    @NonNull
    private final DataBaseAdapter dataBaseAdapter;
    @Nullable
    private final Instant lastSync;
    private final boolean etagsEnabled;

    private Account account;
    private long accountId;
    private ResponseCallback<Boolean> responseCallback;

    public SyncHelper(@NonNull ServerAdapter serverAdapter, @NonNull DataBaseAdapter dataBaseAdapter, @Nullable Instant lastSync) {
        this.serverAdapter = serverAdapter;
        this.dataBaseAdapter = dataBaseAdapter;
        this.lastSync = lastSync;
        // check only once per sync
        this.etagsEnabled = serverAdapter.isEtagsEnabled();
    }

    // Sync Server -> App
    public <T extends IRemoteEntity> void doSyncFor(@NonNull final AbstractSyncDataProvider<T> provider) {
        doSyncFor(provider, true);
    }
    public <T extends IRemoteEntity> void doSyncFor(@NonNull final AbstractSyncDataProvider<T> provider, boolean parallel) {
        provider.registerChildInParent(provider);
        provider.getAllFromServer(serverAdapter, dataBaseAdapter, accountId, new ResponseCallback<>(account) {
            @Override
            public void onResponse(List<T> response, Headers headers) {
                if (response != null) {
                    provider.goingDeeper();

                    for (T entityFromServer : response) {
                        if (entityFromServer == null) {
                            // see https://github.com/stefan-niedermann/nextcloud-deck/issues/574
                            DeckLog.error("Skipped null value from server for DataProvider:", provider.getClass().getSimpleName());
                            continue;
                        }
                        entityFromServer.setAccountId(accountId);

                        T existingEntity = provider.getSingleFromDB(dataBaseAdapter, accountId, entityFromServer);

                        if (existingEntity == null) {
                            try {
                                provider.createInDB(dataBaseAdapter, accountId, entityFromServer);
                            } catch (SQLiteConstraintException e) {
                                provider.onInsertFailed(dataBaseAdapter, e, account, accountId, response, entityFromServer);
                                throw new RuntimeException("ConstraintViolation! Entity: " + provider.getClass().getSimpleName()+"\n"
                                        +entityFromServer.getClass().getSimpleName()+": "+ new Gson().toJson(entityFromServer),
                                        e);
                            }
                        } else {
                            //TODO: how to handle deletes? what about archived?
                            if (existingEntity.getStatus() != DBStatus.UP_TO_DATE.getId()) {
                                DeckLog.warn("Conflicting changes on entity:", existingEntity);
                                // TODO: what to do?
                            } else {
                                if (etagsEnabled && entityFromServer.getEtag() != null && entityFromServer.getEtag().equals(existingEntity.getEtag())) {
                                    DeckLog.log("[" + provider.getClass().getSimpleName() + "] ETags do match! skipping " + existingEntity.getClass().getSimpleName() + " with localId: " + existingEntity.getLocalId());
                                    continue;
                                }
                                provider.updateInDB(dataBaseAdapter, accountId, applyUpdatesFromRemote(provider, existingEntity, entityFromServer, accountId), false);
                            }
                        }

                        existingEntity = provider.getSingleFromDB(dataBaseAdapter, accountId, entityFromServer);
                        final T tmp = existingEntity;
                        if (parallel) {
                            provider.goDeeper(SyncHelper.this, existingEntity, entityFromServer, responseCallback);
                        } else {
                            DeckLog.verbose("### SYNC Sequencial!"+tmp.getId());
                            CountDownLatch latch = new CountDownLatch(1);
                            provider.goDeeper(SyncHelper.this, existingEntity, entityFromServer, new ResponseCallback<>(responseCallback.getAccount()) {
                                @Override
                                public void onResponse(Boolean response, Headers headers) {
                                    DeckLog.verbose("### SYNC board "+tmp.getId()+" done! Changes: "+response);
                                    latch.countDown();
                                }

                                @SuppressLint("MissingSuperCall")
                                @Override
                                public void onError(Throwable throwable) {
                                    DeckLog.verbose("### SYNC board done (error)! ");
                                    responseCallback.onError(throwable);
                                    latch.countDown();
                                }
                            });
                            try {
                                latch.await();
                            } catch (InterruptedException e) {
                                onError(e);
                            }
                        }
                    }

                    provider.handleDeletes(serverAdapter, dataBaseAdapter, accountId, response);

                    provider.doneGoingDeeper(responseCallback, true);
                } else {
                    provider.childDone(provider, responseCallback, false);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                if (throwable.getClass() == NextcloudHttpRequestFailedException.class) {
                    final NextcloudHttpRequestFailedException requestFailedException = (NextcloudHttpRequestFailedException) throwable;
                    if (HttpURLConnection.HTTP_NOT_MODIFIED == requestFailedException.getStatusCode()) {
                        DeckLog.log("[" + provider.getClass().getSimpleName() + "] ETags do match! skipping this one.");
                        // well, etags say we're fine here. no need to go deeper.
                        provider.childDone(provider, responseCallback, false);
                        return;
                    }
                }
                super.onError(throwable);
                provider.onError(responseCallback);
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
            Class<? extends IRemoteEntity> classOfEntity = allFromDB.get(0).getClass();
            ConcurrentSkipListSet<Long> idsInSync = CURRENTLY_IN_UPSYNC.get(classOfEntity);
            if (idsInSync == null) {
                idsInSync = new ConcurrentSkipListSet<>();
                CURRENTLY_IN_UPSYNC.put(classOfEntity, idsInSync);
            }
            for (T entity : allFromDB) {
                if (idsInSync.contains(entity.getLocalId())){
                    continue;
                }
                idsInSync.add(entity.getLocalId());
                if (entity.getId() != null) {
                    if (entity.getStatusEnum() == DBStatus.LOCAL_DELETED) {
                        provider.deleteOnServer(serverAdapter, accountId, getDeleteCallback(provider, entity, countDownLatch), entity, dataBaseAdapter);
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

    private <T extends IRemoteEntity> ResponseCallback<EmptyResponse> getDeleteCallback(@NonNull AbstractSyncDataProvider<T> provider, T entity, @Nullable CountDownLatch countDownLatch) {
        return new ResponseCallback<>(account) {
            @Override
            public void onResponse(EmptyResponse response, Headers headers) {
                provider.deletePhysicallyInDB(dataBaseAdapter, accountId, entity);
                provider.goDeeperForUpSync(SyncHelper.this, serverAdapter, dataBaseAdapter, responseCallback);;
                doneWithUpsync(countDownLatch, entity);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                responseCallback.onError(throwable);;
                doneWithUpsync(countDownLatch, entity);
            }
        };
    }

    private <T extends IRemoteEntity> ResponseCallback<T> getUpdateCallback(@NonNull AbstractSyncDataProvider<T> provider, @NonNull T entity, @Nullable CountDownLatch countDownLatch) {
        return new ResponseCallback<>(account) {
            @Override
            public void onResponse(T response, Headers headers) {
                response.setAccountId(this.account.getId());
                T update = applyUpdatesFromRemote(provider, entity, response, accountId);
                update.setId(response.getId());
                update.setStatus(DBStatus.UP_TO_DATE.getId());
                provider.updateInDB(dataBaseAdapter, accountId, update, false);
                provider.goDeeperForUpSync(SyncHelper.this, serverAdapter, dataBaseAdapter, responseCallback);

                doneWithUpsync(countDownLatch, entity);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                responseCallback.onError(throwable);
                doneWithUpsync(countDownLatch, entity);
            }
        };
    }

    private <T extends IRemoteEntity> void doneWithUpsync(CountDownLatch countDownLatch, T entity) {
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
        ConcurrentSkipListSet<Long> idsInSync = CURRENTLY_IN_UPSYNC.get(entity.getClass());
        if (idsInSync != null) {
            idsInSync.remove(entity.getLocalId());
        }
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

    public SyncHelper setResponseCallback(@NonNull ResponseCallback<Boolean> callback) {
        this.responseCallback = callback;
        this.account = responseCallback.getAccount();
        accountId = account.getId();
        return this;
    }

    public interface Factory {
        SyncHelper create(@NonNull ServerAdapter serverAdapter, @NonNull DataBaseAdapter dataBaseAdapter, @Nullable Instant lastSync);
    }
}
