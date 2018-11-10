package it.niedermann.nextcloud.deck.api;


import com.nextcloud.android.sso.aidl.NextcloudRequest;
import com.nextcloud.android.sso.api.NextcloudAPI;

import java.util.List;

import io.reactivex.Observable;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;

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
    private String buildEndpointPath(String path, Object... params) {
        String[] pathFragments = path.split("\\{[a-zA-Z0-9]*\\}");
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for (; i<pathFragments.length; i++) {
            sb.append(pathFragments[i]);
            sb.append(params.length > i ? params[i] : "");
        }
        return sb.toString();
    }
    private NextcloudRequest.Builder buildRequest(String method, String path, Object... params) {
        return buildRequest(method, buildEndpointPath(path, params));
    }

    @Override
    public Observable<List<Board>> getBoards() {
        NextcloudRequest request = buildRequest(GET, "boards").build();
        return nextcloudAPI.performRequestObservable(Board.class, request);
    }

    @Override
    public Observable createStack(long boardId, Stack stack) {
        NextcloudRequest request = buildRequest(POST, "boards/{boardId}/stacks", boardId)
                .setRequestBody(GsonConfig.GetGson().toJson(stack)).build();
        return nextcloudAPI.performRequestObservable(Stack.class, request);
    }

    @Override
    public Observable<Stack> updateStack(long boardId, long id, Stack stack) {
        NextcloudRequest request = buildRequest(PUT, "boards/{boardId}/stacks/{stackId}", boardId, id)
                .setRequestBody(GsonConfig.GetGson().toJson(stack)).build();
        return nextcloudAPI.performRequestObservable(Stack.class, request);
    }

    @Override
    public Observable<Stack> deleteStack(long boardId, long id) {
        NextcloudRequest request = buildRequest(DELETE, "boards/{boardId}/stacks/{stackId}", boardId, id).build();
        return nextcloudAPI.performRequestObservable(Stack.class, request);
    }

    @Override
    public Observable<Stack> getStack(long boardId, long id) {
        NextcloudRequest request = buildRequest(GET, "boards/{boardId}/stacks/{stackId}", boardId, id).build();
        return nextcloudAPI.performRequestObservable(Stack.class, request);
    }

    @Override
    public Observable<List<Stack>> getStacks(long boardId) {
        NextcloudRequest request = buildRequest(GET, "boards/{boardId}/stacks", boardId).build();
        return nextcloudAPI.performRequestObservable(Stack.class, request);
    }

    @Override
    public Observable<List<Stack>> getArchivedStacks(long boardId) {
        NextcloudRequest request = buildRequest(GET, "boards/{boardId}/stacks/archived", boardId).build();
        return nextcloudAPI.performRequestObservable(Stack.class, request);
    }

    @Override
    public Observable createCard(long boardId, long stackId, Card card) {
        NextcloudRequest request = buildRequest(POST, "boards/{boardId}/stacks/{stackId}/cards", boardId, stackId)
                .setRequestBody(GsonConfig.GetGson().toJson(card)).build();
        return nextcloudAPI.performRequestObservable(Card.class, request);
    }

    @Override
    public Observable<Card> updateCard(long boardId, long stackId, long cardId, Card card) {
        NextcloudRequest request = buildRequest(PUT, "boards/{boardId}/stacks/{stackId}/cards/{cardId}", boardId, stackId, cardId)
                .setRequestBody(GsonConfig.GetGson().toJson(card)).build();
        return nextcloudAPI.performRequestObservable(Card.class, request);
    }

    @Override
    public Observable<Card> deleteCard(long boardId, long stackId, long cardId) {
        NextcloudRequest request = buildRequest(DELETE, "boards/{boardId}/stacks/{stackId}/cards/{cardId}", boardId, stackId, cardId).build();
        return nextcloudAPI.performRequestObservable(Card.class, request);
    }

    @Override
    public Observable<Card> getCard(long boardId, long stackId, long cardId) {
        NextcloudRequest request = buildRequest(GET, "boards/{boardId}/stacks/{stackId}/cards/{cardId}", boardId, stackId, cardId).build();
        return nextcloudAPI.performRequestObservable(Stack.class, request);
    }

    @Override
    public Observable<Label> getLabel(long boardId, long labelId) {
        NextcloudRequest request = buildRequest(GET, "boards/"+boardId+"labels/"+labelId).build();
        return nextcloudAPI.performRequestObservable(Label.class, request);
    }

    @Override
    public Observable<Label> updateLabel(long boardId, long labelId, Label label) {
        NextcloudRequest request = buildRequest(PUT, "boards/"+boardId+"/labels/"+labelId)
                .setRequestBody(GsonConfig.GetGson().toJson(label)).build();
        return nextcloudAPI.performRequestObservable(Label.class, request);
    }

    @Override
    public Observable<Label> createLabel(long boardId, Label label) {
        NextcloudRequest request = buildRequest(POST, "boards/"+boardId+"/labels")
                .setRequestBody(GsonConfig.GetGson().toJson(label)).build();
        return nextcloudAPI.performRequestObservable(Label.class, request);
    }

    @Override
    public Observable<Label> deleteLabel(long boardId, long labelId) {
        NextcloudRequest request = buildRequest(DELETE, "boards/"+boardId+"/labels/"+labelId).build();
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