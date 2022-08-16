package it.niedermann.nextcloud.deck.api;

import androidx.annotation.NonNull;

import java.util.concurrent.CancellationException;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.util.ExecutorServiceProvider;

public class RequestHelper {

    static {
        RxJavaPlugins.setErrorHandler(DeckLog::logError);
    }

    public static <T> Disposable request(@NonNull final ApiProvider provider, @NonNull final ObservableProvider<T> call, @NonNull final ResponseCallback<T> callback) {
        if (provider.getDeckAPI() == null) {
            provider.initSsoApi(callback::onError);
        }

        final ResponseConsumer<T> cb = new ResponseConsumer<>(callback);
        return call.getObservableFromCall()
                .subscribeOn(Schedulers.from(ExecutorServiceProvider.getExecutorService()))
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
        private final Consumer<Throwable> exceptionConsumer = new Consumer<>() {
            @Override
            public void accept(final Throwable throwable) {
                callback.onError(ServerCommunicationErrorHandler.translateError(throwable));
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
