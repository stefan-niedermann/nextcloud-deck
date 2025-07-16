package it.niedermann.nextcloud.deck.remote.api;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;
import okhttp3.Headers;

/**
 * Abstract implementation of {@link IResponseCallback} which is aware of an {@link Account}.
 *
 * @param <T> payload type of the {@link IResponseCallback#onResponse(Object, Headers)} method
 */
public abstract class ResponseCallback<T> implements IResponseCallback<T> {
    @NonNull
    protected Account account;

    public ResponseCallback(@NonNull Account account) {
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
        if (response instanceof List<?> collection) {
            return !collection.isEmpty() && collection.get(0) instanceof AbstractRemoteEntity;
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
    public static <T> ResponseCallback<T> from(@NonNull Account account, IResponseCallback<T> callback) {
        return new ResponseCallback<>(account) {
            @Override
            public void onResponse(T response, Headers headers) {
                callback.onResponse(response, headers);
            }

            @SuppressLint("MissingSuperCall")
            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    /**
     * Forwards responses and errors to the given {@param CompletableFuture}
     */
    public static <T> ResponseCallback<T> forwardTo(@NonNull Account account, @NonNull CompletableFuture<T> future) {
        return from(account, IResponseCallback.forwardTo(future));
    }
}