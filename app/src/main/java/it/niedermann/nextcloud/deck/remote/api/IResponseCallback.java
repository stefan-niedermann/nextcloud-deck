package it.niedermann.nextcloud.deck.remote.api;

import androidx.annotation.CallSuper;

import it.niedermann.nextcloud.deck.DeckLog;
import okhttp3.Headers;

public interface IResponseCallback<T> {

    void onResponse(T response);

    @CallSuper
    default void onError(Throwable throwable) {
        DeckLog.logError(throwable);
    }

    default void onResponseWithHeaders(T response, Headers headers) {
        onResponse(response);
    }
    /**
     * @return a default {@link IResponseCallback} which does nothing {@link #onResponse(Object)} and the default action fo {@link #onError(Throwable)}
     */
    static <T> IResponseCallback<T> empty() {
        return response -> {
            // Does nothing on default
        };
    }
}
