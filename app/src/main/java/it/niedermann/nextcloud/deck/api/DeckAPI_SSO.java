package it.niedermann.nextcloud.deck.api;


import com.nextcloud.android.sso.aidl.NextcloudRequest;
import com.nextcloud.android.sso.api.NextcloudAPI;

import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.Observable;
import it.niedermann.nextcloud.deck.model.board.Board;
import it.niedermann.nextcloud.deck.model.board.Card;

public class DeckAPI_SSO implements DeckAPI {

    private static final String mApiEndpoint = "/index.php/apps/deck/api/v1.0/";
    private NextcloudAPI nextcloudAPI;

    public DeckAPI_SSO(NextcloudAPI nextcloudAPI) {
        this.nextcloudAPI = nextcloudAPI;
    }

    @Override
    public Observable<List<Board>> boards() {
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("GET")
                .setUrl(mApiEndpoint + "boards")
                .setFollowRedirects(true)
                .build();
        return nextcloudAPI.performRequestObservable(Board.class, request);
    }

    @Override
    public Observable createBoard(Board board) {
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("GET")
                .setUrl(mApiEndpoint + "boards")
                .setFollowRedirects(true)
                .setRequestBody(GsonConfig.GetGson().toJson(board))
                .build();
        return nextcloudAPI.performRequestObservable(Board.class, request);
    }

    @Override
    public Observable<Board> getBoard(long id) {
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("GET")
                .setUrl(mApiEndpoint + "boards/" + id)
                .setFollowRedirects(true)
                .build();
        return nextcloudAPI.performRequestObservable(Board.class, request);
    }

    @Override
    public Observable<List<Card>> getTasks() {
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("GET")
                .setUrl(mApiEndpoint + "boards")
                .setFollowRedirects(true)
                .build();
        return nextcloudAPI.performRequestObservable(Board.class, request);
    }
}