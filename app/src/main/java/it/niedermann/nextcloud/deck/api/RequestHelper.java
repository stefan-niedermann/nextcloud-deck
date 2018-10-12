package it.niedermann.nextcloud.deck.api;

import android.app.Activity;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import it.niedermann.nextcloud.deck.model.Board;

public class RequestHelper {

    public static <T> void request(final Activity sourceActivity, final Observable<T> request, final ResponseCallback<T> callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ResponseConsumer<T> cb = new ResponseConsumer<T>(sourceActivity, callback);
                request.subscribe(cb, cb.getExceptionConsumer());
            }
        }).start();
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
