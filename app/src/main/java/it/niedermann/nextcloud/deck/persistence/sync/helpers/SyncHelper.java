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
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.IDataProvider;
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
    public <T extends IRemoteEntity> void doSyncFor(final IDataProvider<T> provider){
        provider.registerChildInParent(provider); // todo unregister? where to handl
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
                DeckLog.logError(throwable);
                responseCallback.onError(throwable);
            }
        }, lastSync);
    }

    // Sync App -> Server
    public <T extends IRemoteEntity> void doUpSyncFor(IDataProvider<T> provider){
        List<T> allFromDB = provider.getAllFromDB(dataBaseAdapter, accountId, lastSync);
        boolean hadSomethingToSync = false;
        if (allFromDB != null && !allFromDB.isEmpty()) {
            hadSomethingToSync = true;
            for (T entity : allFromDB) {
                IResponseCallback<T> updateCallback = new IResponseCallback<T>(account) {
                    @Override
                    public void onResponse(T response) {
                        provider.updateInDB(dataBaseAdapter, accountId, applyUpdatesFromRemote(entity, response, accountId));
                        provider.goDeeperForUpSync(SyncHelper.this, entity, response);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        responseCallback.onError(throwable);
                    }
                };
                if (entity.getId()!=null) {
                    if (entity.getStatusEnum() == DBStatus.LOCAL_DELETED) {
                        provider.deleteOnServer(serverAdapter, accountId, new IResponseCallback<T>(account) {
                            @Override
                            public void onResponse(T response) {
                                provider.deleteInDB(dataBaseAdapter, accountId, response);
                            }
                        }, entity);
                    } else {
                        provider.updateOnServer(serverAdapter, accountId, updateCallback, entity);
                    }
                } else {
                    provider.createOnServer(serverAdapter, accountId, updateCallback, entity);
                }
            }
        }
        provider.childDone(provider, responseCallback, hadSomethingToSync);
    }

    public void fixRelations(IRelationshipProvider relationshipProvider) {
        relationshipProvider.deleteAllExisting(dataBaseAdapter, accountId);
        relationshipProvider.insertAllNecessary(dataBaseAdapter, accountId);
    }

    private <T extends IRemoteEntity> T applyUpdatesFromRemote(T localEntity, T remoteEntity, Long accountId) {
        if (!accountId.equals(localEntity.getAccountId())) {
            throw new IllegalArgumentException("IDs of Accounts are not matching! WTF are you doin?!");
        }
        remoteEntity.setLastModifiedLocal(remoteEntity.getLastModified()); // not an error! local-modification = remote-mod
        remoteEntity.setLocalId(localEntity.getLocalId());
        return remoteEntity;
    }

    public void setResponseCallback(IResponseCallback<Boolean> callback) {
        this.responseCallback = callback;
        this.account = responseCallback.getAccount();
        accountId = account.getId();
    }
}
