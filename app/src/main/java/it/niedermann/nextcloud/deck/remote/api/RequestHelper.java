package it.niedermann.nextcloud.deck.remote.api;

import android.content.Context;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.util.function.Supplier;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
import it.niedermann.nextcloud.deck.remote.helpers.util.ConnectivityUtil;
import it.niedermann.nextcloud.deck.util.ExecutorServiceProvider;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestHelper {

    private static final int RETRY_TIMES = 2;

    @NonNull
    private final ApiProvider apiProvider;

    @NonNull
    private final ConnectivityUtil connectivityUtil;

    public RequestHelper(
            @NonNull ApiProvider apiProvider,
            @NonNull ConnectivityUtil connectivityUtil
    ) {
        this.apiProvider = apiProvider;
        this.connectivityUtil = connectivityUtil;
    }

    public <T> void request(@NonNull final Supplier<Call<T>> callProvider,
                            @NonNull final ResponseCallback<T> callback) {

        if (!connectivityUtil.hasInternetConnection()) {
            throw new OfflineException();
        }

        if (this.apiProvider.getDeckAPI() == null) {
            this.apiProvider.initSsoApi(callback::onError);
        }

        final var call = callProvider.get();
        final var cb = retryable(call, new ResponseConsumer<>(this.apiProvider.getContext(), callback));
        ExecutorServiceProvider.getLinkedBlockingQueueExecutor().submit(() -> call.enqueue(cb));
    }

    private <T> Callback<T> retryable(final Call<T> originalCall, final ResponseConsumer<T> responseConsumer) {
        return new Callback<T>() {
            private int retries = RETRY_TIMES;

            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                responseConsumer.onResponse(call, response);
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable throwable) {
                if (retries > 0) {
                    ExecutorServiceProvider.getLinkedBlockingQueueExecutor().submit(() -> originalCall.enqueue(this));
                } else {
                    responseConsumer.onFailure(call, throwable);
                }
                retries--;
            }
        };
    }


    private static class ResponseConsumer<T> implements Callback<T> {
        @NonNull
        private final Context context;
        @NonNull
        private final ResponseCallback<T> callback;

        private ResponseConsumer(@NonNull Context context, @NonNull ResponseCallback<T> callback) {
            this.context = context;
            this.callback = callback;
        }

        @Override
        public void onResponse(@NonNull Call<T> call, Response<T> response) {
            if (response.isSuccessful()) {
                T responseObject = response.body();
                callback.fillAccountIDs(responseObject);
                callback.onResponse(responseObject, response.headers());
            } else {
                onFailure(call, new NextcloudHttpRequestFailedException(context, response.code(), buildCause(response)));
            }
        }

        private RuntimeException buildCause(Response<T> response) {
            Request request = response.raw().request();
            String url = request.url().toString();
            String method = request.method();
            int code = response.code();
            String responseBody = "<empty>";
            try (ResponseBody body = response.errorBody()) {
                if (body != null) {
                    responseBody = body.string();
                }
            } catch (Exception e) {
                responseBody = "<unable to build response body: " + e.getMessage() + ">";
            }
            return new RuntimeException("HTTP StatusCode wasn't 2xx:\n" +
                    "Got [HTTP " + code + "] for Call [" + method + " " + url + "] with Message:\n" +
                    "[" + responseBody + "]");
        }

        @Override
        public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
            DeckLog.logError(t);
            callback.onError(ServerCommunicationErrorHandler.translateError(t));
        }
    }
}
