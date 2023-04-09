package it.niedermann.nextcloud.deck.remote.api;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.util.ExecutorServiceProvider;
import okhttp3.Request;
import okhttp3.ResponseBody;
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
        ExecutorServiceProvider.getLinkedBlockingQueueExecutor().submit(() -> call.getObservableFromCall().enqueue(cb));
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
            ExecutorServiceProvider.getLinkedBlockingQueueExecutor().submit( () -> {
                if(response.isSuccessful()) {
                    T responseObject = response.body();
                    callback.fillAccountIDs(responseObject);
                    callback.onResponseWithHeaders(responseObject, response.headers());
                } else {

                    onFailure(call, new NextcloudHttpRequestFailedException(response.code(), buildCause(call, response)));
                }
            });
        }

        private RuntimeException buildCause(Call<T> call, Response<T> response){
            Request request = call.request();
            String url = request.url().redact();
            String method = request.method();
            int code = response.code();
            String responseBody = "<empty>";
            try (ResponseBody body = response.errorBody()) {
                if (body != null) {
                    responseBody = body.string();
                }
            } catch (Exception e) {
                responseBody = "<unable to build response body: "+e.getMessage()+">";
            }
            return new RuntimeException("HTTP StatusCode wasn't 2xx:\n" +
                    "Got [HTTP " + code + "] for Call [" + method + " " + url + "] with Message:\n" +
                    "[" + responseBody + "]");
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            ExecutorServiceProvider.getLinkedBlockingQueueExecutor().submit( () -> {
                DeckLog.logError(t);
                callback.onError(ServerCommunicationErrorHandler.translateError(t));
            });
        }
    }
}
