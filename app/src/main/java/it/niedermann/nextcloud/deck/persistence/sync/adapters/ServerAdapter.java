package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.ApiProvider;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.api.RequestHelper;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;

public class ServerAdapter {

    private static final DateFormat API_FORMAT = new SimpleDateFormat("E, d MMM yyyy hh:mm:ss z");

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


    private String getLastSyncDateFormatted() {
        String lastSyncHeader = API_FORMAT.format(getLastSync());
        // omit Offset of timezone (e.g.: +01:00)
        if (lastSyncHeader.matches("^.*\\+[0-9]{2}:[0-9]{2}$")) {
            lastSyncHeader = lastSyncHeader.substring(0, lastSyncHeader.length()-6);
        }
        Log.d("deck lastSync", lastSyncHeader);
        return lastSyncHeader;
    }

    private Date getLastSync() {
        return new Date(1000000000000l);
        //return null;
        // FIXME: reactivate, when lastSync is working in REST-API
//        Date lastSync = new Date();
//        lastSync.setTime(lastSyncPref.getLong(DeckConsts.LAST_SYNC_KEY, 0L));
//        return lastSync;
    }

    public void getBoards(long accountId, IResponseCallback<List<Board>> responseCallback) {
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().getBoards(getLastSyncDateFormatted()), responseCallback);
    }

    public void createBoard(long accountId, Board board) {
        // throw new IllegalStateException // when offline /
    }

    public void deleteBoard(Board board) {

    }

    public void updateBoard(Board board) {

    }

    public void getStacks(long accountId, long boardId, IResponseCallback<List<FullStack>> responseCallback) {
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().getStacks(boardId, getLastSyncDateFormatted()), responseCallback);
    }

    public void getStack(long accountId, long boardId, long stackId, IResponseCallback<FullStack> responseCallback) {
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().getStack(boardId, stackId, getLastSyncDateFormatted()), responseCallback);
    }

    public void createStack(long accountId, Stack stack) {

    }

    public void deleteStack(Stack stack) {

    }

    public void updateStack(Stack stack) {

    }

    public void getCard(long accountId, long boardId, long stackId, long cardId, IResponseCallback<FullCard> responseCallback) {
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().getCard(boardId, stackId, cardId, getLastSyncDateFormatted()), responseCallback);
    }

    public void createCard(long accountId, Card card) {

    }

    public void deleteCard(Card card) {

    }

    public void updateCard(Card card) {

    }
}
