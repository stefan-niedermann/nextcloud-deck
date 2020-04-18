package it.niedermann.nextcloud.deck.persistence.sync.helpers;

import java.util.Date;
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
    private ServerAdapter serverAdapter;
    private DataBaseAdapter dataBaseAdapter;
    private Account account;
    private long accountId;
    private IResponseCallback<Boolean> responseCallback;
    private Date lastSync;

    public SyncHelper(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, Date lastSync) {
        this.serverAdapter = serverAdapter;
        this.dataBaseAdapter = dataBaseAdapter;
        this.lastSync = lastSync;
    }

    // Sync Server -> App
    public <T extends IRemoteEntity> void  doSyncFor(final AbstractSyncDataProvider<T> provider){
        provider.registerChildInParent(provider);
        provider.getAllFromServer(serverAdapter, accountId, new IResponseCallback<List<T>>(account) {
            @Override
            public void onResponse(List<T> response) {
                if (response != null) {
                    provider.goingDeeper();
                    for (T entityFromServer : response) {
                        entityFromServer.setAccountId(accountId);
                        T existingEntity = provider.getSingleFromDB(dataBaseAdapter, accountId, entityFromServer);

                        if (existingEntity == null) {
                            provider.createInDB(dataBaseAdapter, accountId, entityFromServer);
                        } else {
                            //TODO: how to handle deletes? what about archived?
                            if (existingEntity.getStatus() != DBStatus.UP_TO_DATE.getId()){
                                DeckLog.log("Conflicting changes on entity: "+existingEntity);
                                // TODO: what to do?
                            } else {
//                                if (existingEntity.getLastModified().getTime() == entityFromServer.getLastModified().getTime()) {
//                                    continue; // TODO: is this is ok for sure? -> isn`t! NPE
//                                }
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
                provider.onError(throwable, responseCallback);
                DeckLog.logError(throwable);
                responseCallback.onError(throwable);
            }
        }, lastSync);
    }

    // Sync App -> Server
    public <T extends IRemoteEntity> void doUpSyncFor(AbstractSyncDataProvider<T> provider){
        doUpSyncFor(provider, null);
    }
    public <T extends IRemoteEntity> void doUpSyncFor(AbstractSyncDataProvider<T> provider, CountDownLatch countDownLatch){
        List<T> allFromDB = provider.getAllChangedFromDB(dataBaseAdapter, accountId, lastSync);
        if (allFromDB != null && !allFromDB.isEmpty()) {
            for (T entity : allFromDB) {
                if (entity.getId()!=null) {
                    if (entity.getStatusEnum() == DBStatus.LOCAL_DELETED) {
                        provider.deleteOnServer(serverAdapter, accountId, getDeleteCallback(provider, entity), entity, dataBaseAdapter);
                        if (countDownLatch != null){
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
            if (countDownLatch != null){
                countDownLatch.countDown();
            }
        }
    }

    private <T extends IRemoteEntity> IResponseCallback<Void> getDeleteCallback(AbstractSyncDataProvider<T> provider, T entity) {
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

    private <T extends IRemoteEntity> IResponseCallback<T> getUpdateCallback(AbstractSyncDataProvider<T> provider, T entity, CountDownLatch countDownLatch) {
        return new IResponseCallback<T>(account) {
            @Override
            public void onResponse(T response) {
                response.setAccountId(this.account.getId());
                T update = applyUpdatesFromRemote(provider, entity, response, accountId);
                update.setStatus(DBStatus.UP_TO_DATE.getId());
                provider.updateInDB(dataBaseAdapter, accountId, update, false);
                provider.goDeeperForUpSync(SyncHelper.this, serverAdapter, dataBaseAdapter, responseCallback);
                if (countDownLatch != null){
                    countDownLatch.countDown();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                responseCallback.onError(throwable);
                if (countDownLatch != null){
                    countDownLatch.countDown();
                }
            }
        };
    }

    public void fixRelations(IRelationshipProvider relationshipProvider) {
        // this is OK, since the delete only affects records with status UP_TO_DATE
        relationshipProvider.deleteAllExisting(dataBaseAdapter, accountId);
        relationshipProvider.insertAllNecessary(dataBaseAdapter, accountId);
    }

    private <T extends IRemoteEntity> T applyUpdatesFromRemote(AbstractSyncDataProvider<T> provider, T localEntity, T remoteEntity, Long accountId) {
        if (!accountId.equals(localEntity.getAccountId())) {
            throw new IllegalArgumentException("IDs of Accounts are not matching! WTF are you doin?!");
        }
        remoteEntity.setLocalId(localEntity.getLocalId());
        remoteEntity = provider.applyUpdatesFromRemote(localEntity, remoteEntity, accountId);
        return remoteEntity;
    }

    public SyncHelper setResponseCallback(IResponseCallback<Boolean> callback) {
        this.responseCallback = callback;
        this.account = responseCallback.getAccount();
        accountId = account.getId();
        return this;
    }
}
