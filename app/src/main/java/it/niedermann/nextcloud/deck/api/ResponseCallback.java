package it.niedermann.nextcloud.deck.api;

import androidx.annotation.CallSuper;

import it.niedermann.nextcloud.deck.DeckLog;

public interface ResponseCallback<T> {

    void onResponse(T response);

    @CallSuper
    default void onError(Throwable throwable) {
        DeckLog.logError(throwable);
    }
}
