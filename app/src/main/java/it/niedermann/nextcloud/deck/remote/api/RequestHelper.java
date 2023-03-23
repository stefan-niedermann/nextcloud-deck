package it.niedermann.nextcloud.deck.remote.api;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.util.ExecutorServiceProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestHelper {

//    static {
//        RxJavaPlugins.setErrorHandler(DeckLog::logError);
//    }

    public static <T> void request(@NonNull final ApiProvider provider, @NonNull final ObservableProvider<T> call, @NonNull final ResponseCallback<T> callback) {
        if (provider.getDeckAPI() == null) {
            provider.initSsoApi(callback::onError);
        }

        final ResponseConsumer<T> cb = new ResponseConsumer<>(callback);
        ExecutorServiceProvider.getExecutorService().submit(() -> call.getObservableFromCall().enqueue(cb));
//                .subscribeOn(Schedulers.from(ExecutorServiceProvider.getExecutorService()))
//                .subscribe(cb, cb.getExceptionConsumer());
    }

    public interface ObservableProvider<T> {
        Call<T> getObservableFromCall();
    }

    private static class ResponseConsumer<T> implements Callback<T> {
        @NonNull
        private final ResponseCallback<T> callback;

        private ResponseConsumer(@NonNull ResponseCallback<T> callback) {
            this.callback = callback;
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            ExecutorServiceProvider.getExecutorService().submit( () -> {
                if(response.isSuccessful()) {
                    T responseObject = response.body();
                    callback.fillAccountIDs(responseObject);
                    callback.onResponseWithHeaders(responseObject, response.headers());
                } else {
                    onFailure(call, new NextcloudHttpRequestFailedException(response.code(), new RuntimeException("HTTP StatusCode wasn't 2xx")));
                }
            });
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            ExecutorServiceProvider.getExecutorService().submit( () -> {
                DeckLog.logError(t);
                callback.onError(ServerCommunicationErrorHandler.translateError(t));
            });
        }
    }
}
