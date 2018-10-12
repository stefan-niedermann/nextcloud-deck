package it.niedermann.nextcloud.deck.api;


import io.reactivex.Observable;
import it.niedermann.nextcloud.deck.model.Board;

public class ApiCalls {

    public static ApiCall getBoardsCall(){
        return new ApiCall() {
            @Override
            public void run() {
                getCallback().onCallable(getProvider().getAPI().boards());
            }
        };
    }

    public static ApiCall getBoardCall(final long id){
        return new ApiCall() {
            @Override
            public void run() {
                getCallback().onCallable(getProvider().getAPI().getBoard(id));
            }
        };
    }

    public static ApiCall getBoardsCall(final Board board){
        return new ApiCall() {
            @Override
            public void run() {
                getCallback().onCallable(getProvider().getAPI().createBoard(board));
            }
        };
    }




    // #### Helpers
    public interface ApiCallable <T>{
        void onCallable(Observable<T> observable);
    }

    public static abstract class ApiCall implements Runnable {

        private ApiProvider provider;
        private ApiCallable callback;

        public ApiProvider getProvider() {
            return provider;
        }

        public ApiCallable getCallback() {
            return callback;
        }

        public void setData(ApiProvider provider, ApiCallable callback){
            this.provider = provider;
            this.callback = callback;
        }
    }
}
