package it.niedermann.nextcloud.deck.ui;

import static androidx.lifecycle.Transformations.distinctUntilChanged;
import static androidx.lifecycle.Transformations.map;

import android.annotation.SuppressLint;
import android.app.Application;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nextcloud.android.sso.helper.SingleAccountHelper;

import java.util.NoSuchElementException;
import java.util.Optional;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.api.ResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

public class PushNotificationViewModel extends AndroidViewModel {

    // Provided by Files app NotificationJob
    static final String KEY_SUBJECT = "subject";
    static final String KEY_MESSAGE = "message";
    static final String KEY_LINK = "link";
    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_CARD_REMOTE_ID = "objectId";

    private final SyncManager readAccountSyncManager;
    private final MutableLiveData<Account> account = new MutableLiveData<>();

    public PushNotificationViewModel(@NonNull Application application) {
        super(application);
        this.readAccountSyncManager = new SyncManager(application);
    }

    @WorkerThread
    public void getCardInformation(@Nullable Bundle bundle, @NonNull PushNotificationCallback callback) {
        if (bundle == null) {
            callback.onError(new NullPointerException("Bundle is null"));
            return;
        }

        final var uri = getUriFromBundle(bundle);
        try {
            final long cardRemoteId = getCardRemoteId(bundle);
            final var account = getAccount(bundle);
            SingleAccountHelper.setCurrentAccount(getApplication(), account.getName());
            final var syncManager = new SyncManager(getApplication());
            final long boardLocalId = syncManager.getBoardLocalIdByAccountAndCardRemoteIdDirectly(account.getId(), cardRemoteId)
                    .orElseThrow(() -> new NoSuchElementException("Given localBoardId for cardRemoteId" + cardRemoteId + "is null."));
            DeckLog.verbose("boardLocalId:", boardLocalId);
            final var card = syncManager.getCardByRemoteIDDirectly(account.getId(), cardRemoteId);
            DeckLog.verbose("Card:", card);
            if (card.isPresent()) {
                syncManager.synchronizeCard(new ResponseCallback<>(account) {
                    @Override
                    public void onResponse(Boolean response) {
                        callback.onResponse(new CardInformation(account, boardLocalId, card.get().getLocalId()));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        callback.onResponse(new CardInformation(account, boardLocalId, card.get().getLocalId()));
                    }
                }, card.get());
            } else {
                DeckLog.info("Card is not yet available locally. Synchronize board with localId", boardLocalId);

                syncManager.synchronizeBoard(boardLocalId, new ResponseCallback<>(account) {
                    @Override
                    public void onResponse(Boolean response) {
                        final var card = syncManager.getCardByRemoteIDDirectly(account.getId(), cardRemoteId);
                        DeckLog.verbose("Card:", card);
                        if (card.isPresent()) {
                            callback.onResponse(new CardInformation(account, boardLocalId, card.get().getLocalId()));
                        } else {
                            if(uri.isPresent()) {
                                callback.fallbackToBrowser(uri.get());
                            } else {
                                callback.onError(generateException("Something went wrong while synchronizing the card" + cardRemoteId + " (cardRemoteId). Given fullCard is null.", bundle));
                            }
                        }
                    }

                    @SuppressLint("MissingSuperCall")
                    @Override
                    public void onError(Throwable throwable) {
                        if(uri.isPresent()) {
                            callback.fallbackToBrowser(uri.get());
                        } else {
                            callback.onError(generateException("Something went wrong while synchronizing the board with localId" + boardLocalId, bundle));
                        }
                    }
                });
            }
        } catch (Throwable t) {
            if(uri.isPresent()) {
                callback.fallbackToBrowser(uri.get());
            } else {
                callback.onError(generateException("", bundle, t));
            }
        }
    }

    private long getCardRemoteId(@NonNull Bundle bundle) throws IllegalArgumentException {
        final String cardRemoteIdString = bundle.getString(KEY_CARD_REMOTE_ID);
        if (cardRemoteIdString == null) {
            throw new IllegalArgumentException("cardRemoteIdString is null");
        }
        DeckLog.verbose("cardRemoteIdString = ", cardRemoteIdString);
        return Long.parseLong(cardRemoteIdString);
    }

    private long getCardRemoteIdFromLink(@NonNull Bundle bundle) {
        // TODO parse with ProjectUtils
        return -1L;
    }

    private Account getAccount(@NonNull Bundle bundle) throws NoSuchElementException {
        final var accountString = bundle.getString(KEY_ACCOUNT);
        final var account = readAccountSyncManager.readAccountDirectly(accountString);
        if (account == null) {
            throw new NoSuchElementException("Given account for" + accountString + "is null.");
        }
        DeckLog.verbose("account:", account);
        return account;
    }

    private Optional<Uri> getUriFromBundle(@NonNull Bundle bundle) {
        try {
            return Optional.of(Uri.parse(bundle.getString(KEY_LINK)));
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

    private Exception generateException(@NonNull String message, @Nullable Bundle bundle) {
        return generateException(message, bundle, null);
    }

    private Exception generateException(@NonNull String message, @Nullable Bundle bundle, @Nullable Throwable cause) {
        if (bundle == null) {
            return new Exception("Bundle is null");
        }
        final var info = "Error while receiving push notification:\n"
                + message + "\n"
                + KEY_SUBJECT + ": [" + bundle.getString(KEY_SUBJECT) + "]\n"
                + KEY_MESSAGE + ": [" + bundle.getString(KEY_MESSAGE) + "]\n"
                + KEY_LINK + ": [" + bundle.getString(KEY_LINK) + "]\n"
                + KEY_CARD_REMOTE_ID + ": [" + bundle.getString(KEY_CARD_REMOTE_ID) + "]\n"
                + KEY_ACCOUNT + ": [" + bundle.getString(KEY_ACCOUNT) + "]";
        return cause == null
                ? new Exception(info)
                : new Exception(info, cause);
    }

    public LiveData<Integer> getAccount() {
        return distinctUntilChanged(map(this.account, Account::getColor));
    }

    public interface PushNotificationCallback extends IResponseCallback<CardInformation> {
        void fallbackToBrowser(@NonNull Uri uri);
    }

    public static class CardInformation {
        @NonNull
        public final Account account;
        public final long localBoardId;
        public final long localCardId;

        public CardInformation(@NonNull Account account, long localBoardId, long localCardId) {
            this.account = account;
            this.localBoardId = localBoardId;
            this.localCardId = localCardId;
        }
    }
}
