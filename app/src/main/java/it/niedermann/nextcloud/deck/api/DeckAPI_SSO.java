package it.niedermann.nextcloud.deck.api;


import com.nextcloud.android.sso.aidl.NextcloudRequest;
import com.nextcloud.android.sso.api.NextcloudAPI;

import java.util.List;

import io.reactivex.Observable;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;

public class DeckAPI_SSO implements DeckAPI {

    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";

    private static final String API_ENDPOINT = "/index.php/apps/deck/api/v1.0/";
    private NextcloudAPI nextcloudAPI;

    public DeckAPI_SSO(NextcloudAPI nextcloudAPI) {
        this.nextcloudAPI = nextcloudAPI;
    }

    private NextcloudRequest.Builder buildRequest(String method, String path) {
        return new NextcloudRequest.Builder()
                .setMethod(method)
                .setUrl(API_ENDPOINT + path)
                .setFollowRedirects(true);
    }

    @Override
    public Observable<List<Board>> boards() {
        NextcloudRequest request = buildRequest(GET, "boards").build();
        return nextcloudAPI.performRequestObservable(Board.class, request);
    }

    @Override
    public Observable<Label> getLabel(long boardId, long labelId) {
        NextcloudRequest request = buildRequest(GET, "boards/"+boardId+"labels/"+labelId).build();
        return nextcloudAPI.performRequestObservable(Board.class, request);
    }

    @Override
    public Observable<Label> updateLabel(long boardId, long labelId, Label label) {
        NextcloudRequest request = buildRequest(PUT, "boards/"+boardId+"labels/"+labelId)
                .setRequestBody(GsonConfig.GetGson().toJson(label)).build();
        return nextcloudAPI.performRequestObservable(Board.class, request);
    }

    @Override
    public Observable<Label> createLabel(long boardId, Label label) {
        NextcloudRequest request = buildRequest(POST, "boards/"+boardId+"labels")
                .setRequestBody(GsonConfig.GetGson().toJson(label)).build();
        return nextcloudAPI.performRequestObservable(Board.class, request);
    }

    @Override
    public Observable<Label> deleteLabel(long boardId, long labelId) {
        NextcloudRequest request = buildRequest(DELETE, "boards/"+boardId+"labels/"+labelId).build();
        return nextcloudAPI.performRequestObservable(Board.class, request);
    }

    @Override
    public Observable createBoard(Board board) {
        NextcloudRequest request = buildRequest(POST, "boards")
                .setRequestBody(GsonConfig.GetGson().toJson(board))
                .build();
        return nextcloudAPI.performRequestObservable(Board.class, request);
    }

    @Override
    public Observable<Board> getBoard(long id) {
        NextcloudRequest request = buildRequest(GET, "boards/" + id).build();
        return nextcloudAPI.performRequestObservable(Board.class, request);
    }


}