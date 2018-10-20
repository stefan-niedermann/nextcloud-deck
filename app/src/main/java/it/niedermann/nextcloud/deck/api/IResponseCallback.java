package it.niedermann.nextcloud.deck.api;

public interface IResponseCallback<T> {
        void onResponse(T response);
        void onError(Throwable throwable);
    }