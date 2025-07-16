package it.niedermann.nextcloud.deck.remote.api;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
import it.niedermann.nextcloud.deck.remote.helpers.util.ConnectivityUtil;
import it.niedermann.nextcloud.deck.shared.SharedExecutors;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestHelper {

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

        final CountDownLatch latch = new CountDownLatch(1);
        final var cb = new ResponseConsumer<>(this.apiProvider.getContext(), callback, latch);
        SharedExecutors.getIONetExecutor(Uri.parse(this.apiProvider.getSsoAccount().url)).submit(() -> {
            callProvider.get().enqueue(cb);
            try {
                latch.await(20, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                DeckLog.logError(e);
            }
        });
        // use this to track Executor load:
        // DeckLog.log(DeckLog.Severity.DEBUG, "#Executor: " + ExecutorServiceProvider.getLinkedBlockingQueueExecutor().toString());
    }

    private static class ResponseConsumer<T> implements Callback<T> {
        @NonNull
        private final Context context;
        @NonNull
        private final ResponseCallback<T> callback;
        @NonNull
        private final CountDownLatch latch;

        private ResponseConsumer(@NonNull Context context, @NonNull ResponseCallback<T> callback, @NonNull CountDownLatch latch) {
            this.context = context;
            this.callback = callback;
            this.latch = latch;
        }

        @Override
        public void onResponse(@NonNull Call<T> call, Response<T> response) {
            if (response.isSuccessful()) {
                T responseObject = response.body();
                latch.countDown();
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
            latch.countDown();
            callback.onError(ServerCommunicationErrorHandler.translateError(t));
        }
    }
}
