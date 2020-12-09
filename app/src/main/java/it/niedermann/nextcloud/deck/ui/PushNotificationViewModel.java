package it.niedermann.nextcloud.deck.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.helper.SingleAccountHelper;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

public class PushNotificationViewModel extends AndroidViewModel {

    private final SyncManager readAccountSyncManager;
    private SyncManager accountSpecificSyncManager;

    public PushNotificationViewModel(@NonNull Application application) {
        super(application);
        this.readAccountSyncManager = new SyncManager(application);
    }

    public LiveData<Account> readAccount(@Nullable String name) {
        return readAccountSyncManager.readAccount(name);
    }

    public void setAccount(@NonNull String accountName) {
        SingleAccountHelper.setCurrentAccount(getApplication(), accountName);
        accountSpecificSyncManager = new SyncManager(getApplication());
    }

    public LiveData<Board> getBoardByRemoteId(long accountId, long remoteId) {
        return accountSpecificSyncManager.getBoardByRemoteId(accountId, remoteId);
    }

    public LiveData<Card> getCardByRemoteID(long accountId, long remoteId) {
        return accountSpecificSyncManager.getCardByRemoteID(accountId, remoteId);
    }

    public void synchronizeCard(@NonNull IResponseCallback<Boolean> responseCallback, Card card) {
        accountSpecificSyncManager.synchronizeCard(responseCallback, card);
    }

    public void synchronizeBoard(@NonNull IResponseCallback<Boolean> responseCallback, long localBoadId) {
        accountSpecificSyncManager.synchronizeBoard(responseCallback, localBoadId);
    }
}
