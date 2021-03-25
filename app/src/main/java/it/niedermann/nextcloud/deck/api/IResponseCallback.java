package it.niedermann.nextcloud.deck.api;

import android.annotation.SuppressLint;

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

    /**
     * Forwards responses and errors to the given {@param callback}
     */
    public static <T> IResponseCallback<T> from(@NonNull Account account, ResponseCallback<T> callback) {
        return new IResponseCallback<T>(account) {
            @Override
            public void onResponse(T response) {
                callback.onResponse(response);
            }

            @SuppressLint("MissingSuperCall")
            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }
}