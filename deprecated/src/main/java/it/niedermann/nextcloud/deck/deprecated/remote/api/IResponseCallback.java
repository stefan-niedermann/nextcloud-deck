package it.niedermann.nextcloud.deck.remote.api;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.DeckLog;
import okhttp3.Headers;

public interface IResponseCallback<T> {

    Headers EMPTY_HEADERS = Headers.of();

    void onResponse(T response, Headers headers);

    @CallSuper
    default void onError(Throwable throwable) {
        DeckLog.logError(throwable);
    }

    /**
     * @return a default {@link IResponseCallback} which does nothing {@link #onResponse(Object, Headers)} and the default action fo {@link #onError(Throwable)}
     */
    static <T> IResponseCallback<T> empty() {
        return (response, headers) -> {
            // Does nothing on default
        };
    }

    static <T> IResponseCallback<T> forwardTo(@NonNull CompletableFuture<T> future) {
        return new IResponseCallback<>() {

            @Override
            public void onResponse(T response, Headers headers) {
                future.complete(response);
            }

            @Override
            public void onError(Throwable throwable) {
                IResponseCallback.super.onError(throwable);
                future.completeExceptionally(throwable);
            }
        };
    }
}
