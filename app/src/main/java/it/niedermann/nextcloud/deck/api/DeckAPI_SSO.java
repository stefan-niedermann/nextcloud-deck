package it.niedermann.nextcloud.deck.api;


import com.nextcloud.android.sso.aidl.NextcloudRequest;
import com.nextcloud.android.sso.api.NextcloudAPI;

import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.Observable;
import it.niedermann.nextcloud.deck.model.board.Board;

public class DeckAPI_SSO implements DeckAPI {

    private static final String mApiEndpoint = "/index.php/apps/deck/api/v1.0/";
    private NextcloudAPI nextcloudAPI;

    public DeckAPI_SSO(NextcloudAPI nextcloudAPI) {
        this.nextcloudAPI = nextcloudAPI;
    }

    @Override
    public Observable<List<Board>> boards() {
        final Type type = Board.class;
        NextcloudRequest request = new NextcloudRequest.Builder()
                .setMethod("GET")
                .setUrl(mApiEndpoint + "boards")
                .setFollowRedirects(true)
                .build();
        return nextcloudAPI.performRequestObservable(type, request);
    }
}