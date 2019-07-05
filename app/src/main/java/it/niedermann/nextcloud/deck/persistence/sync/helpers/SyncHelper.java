package it.niedermann.nextcloud.deck.persistence.sync.helpers;

import java.util.Date;
import java.util.List;

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
    public <T extends IRemoteEntity> void doSyncFor(final AbstractSyncDataProvider<T> provider){
        provider.registerChildInParent(provider);
        provider.getAllFromServer(serverAdapter, accountId, new IResponseCallback<List<T>>(account) {
            @Override
            public void onResponse(List<T> response) {
                if (response != null && !response.isEmpty()) {
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
                                provider.updateInDB(dataBaseAdapter, accountId, applyUpdatesFromRemote(existingEntity, entityFromServer, accountId));
                            }
                        }
                        existingEntity = provider.getSingleFromDB(dataBaseAdapter, accountId, entityFromServer);
                        provider.goDeeper(SyncHelper.this, existingEntity, entityFromServer, responseCallback);
                    }
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
        List<T> allFromDB = provider.getAllChangedFromDB(dataBaseAdapter, accountId, lastSync);
        if (allFromDB != null && !allFromDB.isEmpty()) {
            for (T entity : allFromDB) {
                if (entity.getId()!=null) {
                    if (entity.getStatusEnum() == DBStatus.LOCAL_DELETED) {
                        provider.deleteOnServer(serverAdapter, accountId, getDeleteCallback(provider, entity), entity, dataBaseAdapter);
                    } else {
                        provider.updateOnServer(serverAdapter, dataBaseAdapter, accountId, getUpdateCallback(provider, entity), entity);
                    }
                } else {
                    provider.createOnServer(serverAdapter, dataBaseAdapter, accountId, getUpdateCallback(provider, entity), entity);
                }
            }
        }
        provider.goDeeperForUpSync(this, serverAdapter, dataBaseAdapter, responseCallback);
    }

    private <T extends IRemoteEntity> IResponseCallback<Void> getDeleteCallback(AbstractSyncDataProvider<T> provider, T entity) {
        return new IResponseCallback<Void>(account) {
            @Override
            public void onResponse(Void response) {
                provider.deleteInDB(dataBaseAdapter, accountId, entity);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                responseCallback.onError(throwable);
            }
        };
    }

    private <T extends IRemoteEntity> IResponseCallback<T> getUpdateCallback(AbstractSyncDataProvider<T> provider, T entity) {
        return new IResponseCallback<T>(account) {
            @Override
            public void onResponse(T response) {
                provider.updateInDB(dataBaseAdapter, accountId, applyUpdatesFromRemote(entity, response, accountId));
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                responseCallback.onError(throwable);
            }
        };
    }

    public void fixRelations(IRelationshipProvider relationshipProvider) {
        // this is OK, since the delete only affects records with status UP_TO_DATE
        relationshipProvider.deleteAllExisting(dataBaseAdapter, accountId);
        relationshipProvider.insertAllNecessary(dataBaseAdapter, accountId);
    }

    private <T extends IRemoteEntity> T applyUpdatesFromRemote(T localEntity, T remoteEntity, Long accountId) {
        if (!accountId.equals(localEntity.getAccountId())) {
            throw new IllegalArgumentException("IDs of Accounts are not matching! WTF are you doin?!");
        }
        remoteEntity.setLocalId(localEntity.getLocalId());
        return remoteEntity;
    }

    public void setResponseCallback(IResponseCallback<Boolean> callback) {
        this.responseCallback = callback;
        this.account = responseCallback.getAccount();
        accountId = account.getId();
    }
}
