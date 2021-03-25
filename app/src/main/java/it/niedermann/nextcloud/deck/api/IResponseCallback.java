package it.niedermann.nextcloud.deck.api;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.List;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;


public abstract class IResponseCallback<T> implements ResponseCallback<T> {
    @NonNull
    protected Account account;

    public IResponseCallback(@NonNull Account account) {
        this.account = account;
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

    @NonNull
    public Account getAccount() {
        return account;
    }
}