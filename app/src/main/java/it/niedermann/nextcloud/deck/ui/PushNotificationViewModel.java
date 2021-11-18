package it.niedermann.nextcloud.deck.ui;

import static androidx.lifecycle.Transformations.distinctUntilChanged;
import static androidx.lifecycle.Transformations.map;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nextcloud.android.sso.helper.SingleAccountHelper;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.api.ResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import kotlin.Triple;

public class PushNotificationViewModel extends AndroidViewModel {

    private final SyncManager readAccountSyncManager;
    private final MutableLiveData<Account> account = new MutableLiveData<>();

    public PushNotificationViewModel(@NonNull Application application) {
        super(application);
        this.readAccountSyncManager = new SyncManager(application);
    }

    @WorkerThread
    public void getCardInformation(
            @Nullable String accountString,
            @Nullable String cardRemoteIdString,
            @NonNull IResponseCallback<Triple<Account, Long, Long>> callback) {
        if (cardRemoteIdString == null) {
            callback.onError(new NumberFormatException("cardRemoteIdString is null"));
            return;
        }
        try {
            final long cardRemoteId;
            try {
                DeckLog.verbose("cardRemoteIdString = ", cardRemoteIdString);
                cardRemoteId = Long.parseLong(cardRemoteIdString);
            } catch (NumberFormatException e) {
                callback.onError(e);
                return;
            }
            final var account = readAccountSyncManager.readAccountDirectly(accountString);
            if (account == null) {
                callback.onError(new RuntimeException("Given account for" + accountString + "is null."));
                return;
            }
            SingleAccountHelper.setCurrentAccount(getApplication(), account.getName());
            final var syncManager = new SyncManager(getApplication());
            DeckLog.verbose("account:", account);
            final var board = syncManager.getBoardByAccountAndCardRemoteIdDirectly(account.getId(), cardRemoteId);
            DeckLog.verbose("BoardLocalId:", board);
            if (board == null) {
                callback.onError(new RuntimeException("Given localBoardId for cardRemoteId" + cardRemoteId + "is null."));
                return;
            }
            final var card = syncManager.getCardByRemoteIDDirectly(account.getId(), cardRemoteId);
            DeckLog.verbose("Card:", card);
            if (card != null) {
                syncManager.synchronizeCard(new ResponseCallback<>(account) {
                    @Override
                    public void onResponse(Boolean response) {
                        callback.onResponse(new Triple<>(account, board.getLocalId(), card.getLocalId()));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        callback.onResponse(new Triple<>(account, board.getLocalId(), card.getLocalId()));
                    }
                }, card);
            } else {
                DeckLog.info("Card is not yet available locally. Synchronize board with localId", board);

                syncManager.synchronizeBoard(board.getLocalId(), new ResponseCallback<>(account) {
                    @Override
                    public void onResponse(Boolean response) {
                        final var card = syncManager.getCardByRemoteIDDirectly(account.getId(), cardRemoteId);
                        DeckLog.verbose("Card:", card);
                        if (card != null) {
                            callback.onResponse(new Triple<>(account, board.getLocalId(), card.getLocalId()));
                        } else {
                            callback.onError(new RuntimeException("Something went wrong while synchronizing the card" + cardRemoteId + " (cardRemoteId). Given fullCard is null."));
                        }
                    }

                    @SuppressLint("MissingSuperCall")
                    @Override
                    public void onError(Throwable throwable) {
                        callback.onError(new RuntimeException("Something went wrong while synchronizing the board with localId" + board));
                    }
                });
            }
        } catch (Throwable t) {
            callback.onError(t);
        }
    }

    public LiveData<Integer> getAccount() {
        return distinctUntilChanged(map(this.account, Account::getColor));
    }
}
