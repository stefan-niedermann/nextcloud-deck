package it.niedermann.nextcloud.deck.api;

import android.app.Activity;

import androidx.annotation.Nullable;

import com.nextcloud.android.sso.api.NextcloudAPI;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RequestHelper {

    public static <T> void request(@Nullable final Activity sourceActivity, final ApiProvider provider, final ObservableProvider<T> call, final IResponseCallback<T> callback){

        if (provider.getDeckAPI() == null){
            provider.initSsoApi(new NextcloudAPI.ApiConnectedListener() {
                @Override public void onConnected() { /* great, nothing to do. */}
                @Override
                public void onError(Exception e) {
                    callback.onError(e);
                }
            });
        }

        runRequest(sourceActivity, call.getObservableFromCall(), callback);
    }

    private static <T> void runRequest(@Nullable final Activity sourceActivity, final Observable<T> request, final IResponseCallback<T> callback){
        ResponseConsumer<T> cb = new ResponseConsumer<>(sourceActivity, callback);
        request.subscribeOn(Schedulers.newThread())
                .subscribe(cb, cb.getExceptionConsumer());
    }


    public interface ObservableProvider <T> {
        Observable<T> getObservableFromCall();
    }

    public static class ResponseConsumer<T> implements Consumer<T> {

        @Nullable private Activity sourceActivity;
        private IResponseCallback<T> callback;
        private Consumer<Throwable> exceptionConsumer = new Consumer<Throwable>() {
            @Override
            public void accept(final Throwable throwable) {
                if(sourceActivity == null) {
                    callback.onError(throwable);
                } else {
                    sourceActivity.runOnUiThread(() -> callback.onError(throwable));
                }
            }
        };

        public ResponseConsumer(@Nullable Activity sourceActivity, IResponseCallback<T> callback) {
            this.sourceActivity = sourceActivity;
            this.callback = callback;
        }

        @Override
        public void accept(final T t) {
            callback.fillAccountIDs(t);
            callback.onResponse(t);
//            sourceActivity.runOnUiThread(() -> callback.onResponse(t) );
        }

        public Consumer<Throwable> getExceptionConsumer() {
            return exceptionConsumer;
        }
    }
}
