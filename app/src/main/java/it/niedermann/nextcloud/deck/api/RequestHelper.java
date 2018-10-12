package it.niedermann.nextcloud.deck.api;

import android.app.Activity;

import com.nextcloud.android.sso.api.NextcloudAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class RequestHelper {
    private static Queue<PendingRequest> requestQueue = new LinkedBlockingQueue<>();

    public static <T> void request(final Activity sourceActivity, final ApiProvider provider, final ApiCalls.ApiCall call, final ResponseCallback<T> callback){

        if (!provider.isConnected()){
            provider.initSsoApi(new NextcloudAPI.ApiConnectedListener() {
                @Override
                public void onConnected() {
                    call.setData(provider, new ApiCalls.ApiCallable() {
                        @Override
                        public void onCallable(Observable request) {
                            requestQueue.add(new PendingRequest(sourceActivity, request, callback));
                            while (!requestQueue.isEmpty()){
                                PendingRequest pendingRequest = requestQueue.poll();
                                runRequest(pendingRequest.getSourceActivity(), pendingRequest.getRequest(), pendingRequest.getCallback());
                            }
                        }
                    });
                    new Thread(call).start();
                }

                @Override
                public void onError(Exception e) {
                    callback.onError(e);
                }
            });
        } else {
            call.setData(provider, new ApiCalls.ApiCallable() {
                @Override
                public void onCallable(Observable request) {
                    requestQueue.add(new PendingRequest(sourceActivity, request, callback));
                    while (!requestQueue.isEmpty()){
                        PendingRequest pendingRequest = requesxtQueue.poll();
                        runRequest(pendingRequest.getSourceActivity(), pendingRequest.getRequest(), pendingRequest.getCallback());
                    }
                }
            });
            new Thread(call).start();
        }
    }

    private static <T> void runRequest(final Activity sourceActivity, final Observable<T> request, final ResponseCallback<T> callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ResponseConsumer<T> cb = new ResponseConsumer<T>(sourceActivity, callback);
                request.subscribe(cb, cb.getExceptionConsumer());
            }
        }).start();
    }

    private static class PendingRequest  <T> {
        private Observable<T> request;
        private Activity sourceActivity;
        private ResponseCallback<T> callback;

        public PendingRequest(Activity sourceActivity, Observable<T> request, ResponseCallback<T> callback) {
            this.request = request;
            this.callback = callback;
            this.sourceActivity = sourceActivity;
        }

        public Observable<T> getRequest() {
            return request;
        }

        public ResponseCallback<T> getCallback() {
            return callback;
        }

        public Activity getSourceActivity() {
            return sourceActivity;
        }
    }

    public interface ResponseCallback<T> {
        void onResponse(T response);
        void onError(Throwable throwable);
    }

    public static class ResponseConsumer<T> implements Consumer<T> {

        private Activity sourceActivity;
        private ResponseCallback<T> callback;
        private Consumer<Throwable> exceptionConsumer = new Consumer<Throwable>() {
            @Override
            public void accept(final Throwable throwable) throws Exception {
                sourceActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(throwable);
                    }
                });
            }
        };

        public ResponseConsumer(Activity sourceActivity, ResponseCallback<T> callback) {
            this.sourceActivity = sourceActivity;
            this.callback = callback;
        }

        @Override
        public void accept(final T t) throws Exception {
            sourceActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onResponse(t);
                }
            });
        }

        public Consumer<Throwable> getExceptionConsumer() {
            return exceptionConsumer;
        }
    }
}
