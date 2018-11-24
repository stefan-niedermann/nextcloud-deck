package it.niedermann.nextcloud.deck.api;

import java.util.Collection;
import java.util.List;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.interfaces.RemoteEntity;


public abstract class IResponseCallback<T> {
    private Account account;

    public IResponseCallback(Account account) {
        this.account = account;
    }

    public abstract void onResponse(T response);

    public abstract void onError(Throwable throwable);

    public void fillAccountIDs(T response){
        if (response != null){
            if (isListOfRemoteEntity(response)){
                fillAccountIDs((Collection<RemoteEntity>)response);
            } else if (isRemoteEntity(response)){
                fillAccountIDs((RemoteEntity)response);
            }
        }
    }
    private void fillAccountIDs(RemoteEntity response){
        response.setAccount(this.account);
    }
    private void fillAccountIDs(Collection<RemoteEntity> response){
        for (RemoteEntity entity: response) {
            entity.setAccount(this.account);
        }
    }

    private boolean isRemoteEntity(T response){
        return response instanceof RemoteEntity;
    }

    private boolean isListOfRemoteEntity(T response){
        if (response instanceof List){
            List<?> collection = (List)response;
            return collection.size() > 0 && collection.get(0) instanceof RemoteEntity;
        }
        return false;
    }
}