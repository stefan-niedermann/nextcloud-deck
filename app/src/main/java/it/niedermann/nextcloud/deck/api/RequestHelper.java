package it.niedermann.nextcloud.deck.api;

import android.app.Activity;

import com.nextcloud.android.sso.api.NextcloudAPI;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class RequestHelper {
    private static Queue<PendingRequest> requestQueue = new LinkedBlockingQueue<>();

    public static <T> void request(final Activity sourceActivity, final ApiProvider provider, final ObservableProvider<T> call, final IResponseCallback<T> callback){

        if (!provider.isConnected()){
            provider.initSsoApi(new NextcloudAPI.ApiConnectedListener() {
                @Override
                public void onConnected() {
                    requestQueue.add(new PendingRequest(sourceActivity, call.getObservableFromCall(), callback));
                    while (!requestQueue.isEmpty()){
                        PendingRequest pendingRequest = requestQueue.poll();
                        runRequest(pendingRequest.getSourceActivity(), pendingRequest.getRequest(), pendingRequest.getCallback());
                    }
                }

                @Override
                public void onError(Exception e) {
                    callback.onError(e);
                }
            });
        } else {
            runRequest(sourceActivity, call.getObservableFromCall(), callback);
        }
    }

    private static <T> void runRequest(final Activity sourceActivity, final Observable<T> request, final IResponseCallback<T> callback){
        new Thread(() -> {
                ResponseConsumer<T> cb = new ResponseConsumer<T>(sourceActivity, callback);
                request.subscribe(cb, cb.getExceptionConsumer());
        }).start();
    }

    private static class PendingRequest  <T> {
        private Observable<T> request;
        private Activity sourceActivity;
        private IResponseCallback<T> callback;

        public PendingRequest(Activity sourceActivity, Observable<T> request, IResponseCallback<T> callback) {
            this.request = request;
            this.callback = callback;
            this.sourceActivity = sourceActivity;
        }

        public Observable<T> getRequest() {
            return request;
        }

        public IResponseCallback<T> getCallback() {
            return callback;
        }

        public Activity getSourceActivity() {
            return sourceActivity;
        }
    }



    public interface ObservableProvider <T> {
        Observable<T> getObservableFromCall();
    }

    public static class ResponseConsumer<T> implements Consumer<T> {

        private Activity sourceActivity;
        private IResponseCallback<T> callback;
        private Consumer<Throwable> exceptionConsumer = new Consumer<Throwable>() {
            @Override
            public void accept(final Throwable throwable) {
                sourceActivity.runOnUiThread(() -> callback.onError(throwable) );
            }
        };

        public ResponseConsumer(Activity sourceActivity, IResponseCallback<T> callback) {
            this.sourceActivity = sourceActivity;
            this.callback = callback;
        }

        @Override
        public void accept(final T t) {
            callback.fillAccountIDs(t);
            sourceActivity.runOnUiThread(() -> callback.onResponse(t) );
        }

        public Consumer<Throwable> getExceptionConsumer() {
            return exceptionConsumer;
        }
    }
}
