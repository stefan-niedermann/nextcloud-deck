package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.ApiProvider;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.api.RequestHelper;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;

public class ServerAdapter implements IServerOnlyAdapter {

    private Context applicationContext;
    private ApiProvider provider;
    private Activity sourceActivity;
    private SharedPreferences lastSyncPref;

    public ServerAdapter(Context applicationContext, Activity sourceActivity) {
        this.applicationContext = applicationContext;
        this.sourceActivity = sourceActivity;
        provider = new ApiProvider(applicationContext);
        lastSyncPref = applicationContext.getSharedPreferences(
                applicationContext.getString(R.string.shared_preference_last_sync), Context.MODE_PRIVATE);
    }

    private Date getLastSync() {
        return null;
        // FIXME: reactivate, when lastSync is working in REST-API
//        Date lastSync = new Date();
//        lastSync.setTime(lastSyncPref.getLong(DeckConsts.LAST_SYNC_KEY, 0L));
//        return lastSync;
    }

    @Override
    public void getBoards(long accountId, IResponseCallback<List<Board>> responseCallback) {
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().getBoards(getLastSync()), responseCallback);
    }

    @Override
    public void createBoard(long accountId, Board board) {
        // throw new IllegalStateException // when offline /
    }

    @Override
    public void deleteBoard(Board board) {

    }

    @Override
    public void updateBoard(Board board) {

    }

    @Override
    public void getStacks(long accountId, long boardId, IResponseCallback<List<Stack>> responseCallback) {
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().getStacks(boardId, getLastSync()), responseCallback);
    }

    @Override
    public void getStack(long accountId, long boardId, long stackId, IResponseCallback<Stack> responseCallback) {
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().getStack(boardId, stackId, getLastSync()), responseCallback);
    }

    @Override
    public void createStack(long accountId, Stack stack) {

    }

    @Override
    public void deleteStack(Stack stack) {

    }

    @Override
    public void updateStack(Stack stack) {

    }

    @Override
    public void getCard(long accountId, long boardId, long stackId, long cardId, IResponseCallback<FullCard> responseCallback) {
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().getCard(boardId, stackId, cardId, getLastSync()), responseCallback);
    }

    @Override
    public void createCard(long accountId, Card card) {

    }

    @Override
    public void deleteCard(Card card) {

    }

    @Override
    public void updateCard(Card card) {

    }
}
