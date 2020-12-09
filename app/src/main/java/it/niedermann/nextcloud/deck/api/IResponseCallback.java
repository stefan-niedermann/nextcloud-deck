package it.niedermann.nextcloud.deck.api;

import androidx.annotation.CallSuper;

import java.util.Collection;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;


public abstract class IResponseCallback<T> {
    protected Account account;

    public IResponseCallback(Account account) {
        this.account = account;
    }

    public abstract void onResponse(T response);

    @CallSuper
    public void onError(Throwable throwable) {
        DeckLog.logError(throwable);
    }

    @CallSuper
    public void onError(Throwable throwable, T locallyCreatedEntity) {
        onError(throwable);
    }

    public static <T> IResponseCallback<T> getDefaultResponseCallback(Account account) {
        return new IResponseCallback<T>(account) {
            @Override
            public void onResponse(T response) {
                // Do Nothing
            }

        };
    }

    public void fillAccountIDs(T response) {
        if (response != null) {
            if (isListOfRemoteEntity(response)) {
                fillAccountIDs((Collection<AbstractRemoteEntity>) response);
            } else if (isRemoteEntity(response)) {
                fillAccountIDs((AbstractRemoteEntity) response);
            }
        }
    }

    private void fillAccountIDs(AbstractRemoteEntity response) {
        response.setAccountId(this.account.getId());
    }

    private void fillAccountIDs(Collection<AbstractRemoteEntity> response) {
        for (AbstractRemoteEntity entity : response) {
            entity.setAccountId(this.account.getId());
        }
    }

    private boolean isRemoteEntity(T response) {
        return response instanceof AbstractRemoteEntity;
    }

    private boolean isListOfRemoteEntity(T response) {
        if (response instanceof List) {
            List<?> collection = (List) response;
            return collection.size() > 0 && collection.get(0) instanceof AbstractRemoteEntity;
        }
        return false;
    }

    public Account getAccount() {
        return account;
    }
}