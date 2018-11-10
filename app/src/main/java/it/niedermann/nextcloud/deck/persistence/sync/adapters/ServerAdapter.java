package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import android.app.Activity;
import android.content.Context;

import java.util.List;

import it.niedermann.nextcloud.deck.api.ApiProvider;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.api.RequestHelper;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;

public class ServerAdapter implements IPersistenceAdapter {

    private Context applicationContext;
    private ApiProvider provider;
    private Activity sourceActivity;

    public ServerAdapter(Context applicationContext, Activity sourceActivity) {
        this.applicationContext = applicationContext;
        this.sourceActivity = sourceActivity;
        provider = new ApiProvider(applicationContext);
    }


    @Override
    public void getBoards(long accountId, IResponseCallback<List<Board>> responseCallback) {
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().getBoards(), responseCallback);
    }

    @Override
    public void getStacks(long accountId, long boardId, IResponseCallback<List<Stack>> responseCallback) {
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().getStacks(boardId), responseCallback);
    }

    @Override
    public void getStack(long accountId, long boardId, long stackId, IResponseCallback<Stack> responseCallback) {
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().getStack(boardId, stackId), responseCallback);
    }

    @Override
    public void getCard(long accountId, long boardId, long stackId, long cardId, IResponseCallback<Card> responseCallback) {
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().getCard(boardId, stackId, cardId), responseCallback);
    }
}
