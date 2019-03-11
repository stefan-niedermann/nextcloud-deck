package it.niedermann.nextcloud.deck.persistence.sync.helpers;

import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
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
    private boolean syncChangedSomething = false;
    private Date lastSync;

    public SyncHelper(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, Date lastSync, IResponseCallback<Boolean> responseCallback) {
        this.serverAdapter = serverAdapter;
        this.dataBaseAdapter = dataBaseAdapter;
        this.lastSync = lastSync;
        this.responseCallback = responseCallback;
        this.account = responseCallback.getAccount();
        accountId = account.getId();
    }

    public <T extends IRemoteEntity> void doSyncFor(final IDataProvider<T> provider){
        provider.getAllFromServer(serverAdapter, accountId, new IResponseCallback<List<T>>(account) {
            @Override
            public void onResponse(List<T> response) {
                // Sync Server -> App
                if (response != null && !response.isEmpty()) {
                    for (T entityFromServer : response) {
                        entityFromServer.setAccountId(accountId);
                        T existingEntity = provider.getSingleFromDB(dataBaseAdapter, accountId, entityFromServer);
                        if (existingEntity == null) {
                            provider.createInDB(dataBaseAdapter, accountId, entityFromServer);
                            syncChangedSomething = true;
                        } else {
                            provider.updateInDB(dataBaseAdapter, accountId, applyUpdatesFromRemote(existingEntity, entityFromServer, accountId));
                            syncChangedSomething = true; //TODO: only if no diff!
                        }
                        existingEntity = provider.getSingleFromDB(dataBaseAdapter, accountId, entityFromServer);
                        provider.goDeeper(SyncHelper.this, existingEntity, entityFromServer);
                    }
                }

                // Sync App -> Server
                List<T> allFromDB = provider.getAllFromDB(dataBaseAdapter, accountId, lastSync);
                if (allFromDB != null && !allFromDB.isEmpty()) {
                    for (T entity : allFromDB) {
                        IResponseCallback<T> updateCallback = new IResponseCallback<T>(account) {
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
                        if (entity.getId()!=null) {
                            provider.updateOnServer(serverAdapter, accountId, updateCallback, entity);
                        } else {
                            provider.createOnServer(serverAdapter, accountId, updateCallback, entity);
                        }
                    }
                }

                provider.doneAll(responseCallback, syncChangedSomething);
            }

            @Override
            public void onError(Throwable throwable) {
                DeckLog.logError(throwable);
                responseCallback.onError(throwable);
            }
        });
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
}
