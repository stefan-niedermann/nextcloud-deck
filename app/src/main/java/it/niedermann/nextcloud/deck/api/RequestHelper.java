package it.niedermann.nextcloud.deck.api;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.api.NextcloudAPI;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.DeckLog;

public class RequestHelper {

    static {
        RxJavaPlugins.setErrorHandler(DeckLog::logError);
    }

    public static <T> void request(@NonNull final ApiProvider provider, @NonNull final ObservableProvider<T> call, @NonNull final ResponseCallback<T> callback) {

        if (provider.getDeckAPI() == null) {
            provider.initSsoApi(new NextcloudAPI.ApiConnectedListener() {
                @Override
                public void onConnected() { /* great, nothing to do. */}

                @Override
                public void onError(Exception e) {
                    callback.onError(e);
                }
            });
        }

        runRequest(call.getObservableFromCall(), callback);
    }

    private static <T> void runRequest(final Observable<T> request, final ResponseCallback<T> callback) {
        final ResponseConsumer<T> cb = new ResponseConsumer<>(callback);
        request.subscribeOn(Schedulers.newThread())
                .subscribe(cb, cb.getExceptionConsumer());
    }


    public interface ObservableProvider<T> {
        Observable<T> getObservableFromCall();
    }

    public static class ResponseConsumer<T> implements Consumer<T> {

        @NonNull
        private final ResponseCallback<T> callback;
        @NonNull
        private final Consumer<Throwable> exceptionConsumer = new Consumer<Throwable>() {
            @Override
            public void accept(final Throwable throwable) {
                callback.onError(throwable);
            }
        };

        private ResponseConsumer(@NonNull ResponseCallback<T> callback) {
            this.callback = callback;
        }

        @Override
        public void accept(final T t) {
            callback.fillAccountIDs(t);
            callback.onResponse(t);
        }

        @NonNull
        private Consumer<Throwable> getExceptionConsumer() {
            return exceptionConsumer;
        }
    }
}
