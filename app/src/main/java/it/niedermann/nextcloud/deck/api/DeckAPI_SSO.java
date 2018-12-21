package it.niedermann.nextcloud.deck.api;


import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.nextcloud.android.sso.aidl.NextcloudRequest;
import com.nextcloud.android.sso.api.NextcloudAPI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private DateFormat API_FORMAT = new SimpleDateFormat("E, d MMM yyyy hh:mm:ss z");

    public DeckAPI_SSO(NextcloudAPI nextcloudAPI) {
        this.nextcloudAPI = nextcloudAPI;
    }

    private NextcloudRequest.Builder buildRequest(String method, String path, Date lastSync) {
        NextcloudRequest.Builder builder = new NextcloudRequest.Builder()
                .setMethod(method)
                .setUrl(API_ENDPOINT + path)
                .setFollowRedirects(true);
        if (lastSync != null) {
            String lastSyncHeader = API_FORMAT.format(lastSync);
            // omit Offset of timezone (e.g.: +01:00)
            if (lastSyncHeader.matches("^.*\\+[0-9]{2}:[0-9]{2}$")) {
                lastSyncHeader = lastSyncHeader.substring(0, lastSyncHeader.length() - 6);
            }
            Log.d("deck lastSync", lastSyncHeader);

            Map<String, List<String>> header = new HashMap<>(); //concurrency, new one is needed!
            List<String> hdr = new ArrayList<>();
            hdr.add(lastSyncHeader);
            header.put("If-Modified-Since", hdr);
            builder.setHeader(header);
        }
        return builder;
    }

    private String buildEndpointPath(String path, Object... params) {
        String[] pathFragments = path.split("\\{[a-zA-Z0-9]*\\}");
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for (; i < pathFragments.length; i++) {
            sb.append(pathFragments[i]);
            sb.append(params.length > i ? params[i] : "");
        }
        return sb.toString();
    }

    private NextcloudRequest.Builder buildRequest(String method, String path, Date lastSync, Object... params) {
        return buildRequest(method, buildEndpointPath(path, params), lastSync);
    }

    private NextcloudRequest.Builder buildRequest(String method, String path, Object... params) {
        return buildRequest(method, path, null, params);
    }

    @Override
    public Observable<List<Board>> getBoards(Date lastSync) {
        NextcloudRequest request = buildRequest(GET, "boards", lastSync).build();

        return nextcloudAPI.performRequestObservable(TypeToken.getParameterized(List.class, Board.class).getType(), request);
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
    public Observable<Stack> getStack(long boardId, long id, Date lastSync) {
        NextcloudRequest request = buildRequest(GET, "boards/{boardId}/stacks/{stackId}", lastSync, boardId, id).build();
        return nextcloudAPI.performRequestObservable(Stack.class, request);
    }

    @Override
    public Observable<List<Stack>> getStacks(long boardId, Date lastSync) {
        NextcloudRequest request = buildRequest(GET, "boards/{boardId}/stacks", lastSync, boardId).build();
        return nextcloudAPI.performRequestObservable(TypeToken.getParameterized(List.class, Stack.class).getType(), request);
    }

    @Override
    public Observable<List<Stack>> getArchivedStacks(long boardId, Date lastSync) {
        NextcloudRequest request = buildRequest(GET, "boards/{boardId}/stacks/archived", lastSync, boardId).build();
        return nextcloudAPI.performRequestObservable(TypeToken.getParameterized(List.class, Stack.class).getType(), request);
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
    public Observable<Card> getCard(long boardId, long stackId, long cardId, Date lastSync) {
        NextcloudRequest request = buildRequest(GET, "boards/{boardId}/stacks/{stackId}/cards/{cardId}", lastSync, boardId, stackId, cardId).build();
        return nextcloudAPI.performRequestObservable(Card.class, request);
    }

    @Override
    public Observable<Label> getLabel(long boardId, long labelId, Date lastSync) {
        NextcloudRequest request = buildRequest(GET, "boards/" + boardId + "labels/" + labelId, lastSync).build();
        return nextcloudAPI.performRequestObservable(Label.class, request);
    }

    @Override
    public Observable<Label> updateLabel(long boardId, long labelId, Label label) {
        NextcloudRequest request = buildRequest(PUT, "boards/" + boardId + "/labels/" + labelId)
                .setRequestBody(GsonConfig.GetGson().toJson(label)).build();
        return nextcloudAPI.performRequestObservable(Label.class, request);
    }

    @Override
    public Observable<Label> createLabel(long boardId, Label label) {
        NextcloudRequest request = buildRequest(POST, "boards/" + boardId + "/labels")
                .setRequestBody(GsonConfig.GetGson().toJson(label)).build();
        return nextcloudAPI.performRequestObservable(Label.class, request);
    }

    @Override
    public Observable<Label> deleteLabel(long boardId, long labelId) {
        NextcloudRequest request = buildRequest(DELETE, "boards/" + boardId + "/labels/" + labelId).build();
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
    public Observable<Board> getBoard(long id, Date lastSync) {
        NextcloudRequest request = buildRequest(GET, "boards/" + id, lastSync).build();
        return nextcloudAPI.performRequestObservable(Board.class, request);
    }


}