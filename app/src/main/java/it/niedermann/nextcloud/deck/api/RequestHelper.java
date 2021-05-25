package it.niedermann.nextcloud.deck.api;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.api.NextcloudAPI;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.DeckLog;

public class RequestHelper {

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    static {
        RxJavaPlugins.setErrorHandler(DeckLog::logError);
    }

    public static <T> Disposable request(@NonNull final ApiProvider provider, @NonNull final ObservableProvider<T> call, @NonNull final ResponseCallback<T> callback) {

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

        return runRequest(call.getObservableFromCall(), new ResponseConsumer<>(callback));
    }

    private static <T> Disposable runRequest(final Observable<T> request, final ResponseConsumer<T> cb) {
        return request
                .subscribeOn(Schedulers.from(executor))
                .doOnDispose(() -> cb.getExceptionConsumer().accept(new CancellationException()))
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
