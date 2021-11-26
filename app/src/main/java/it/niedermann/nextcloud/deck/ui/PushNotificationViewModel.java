package it.niedermann.nextcloud.deck.ui;

import static androidx.lifecycle.Transformations.distinctUntilChanged;
import static androidx.lifecycle.Transformations.map;

import android.annotation.SuppressLint;
import android.app.Application;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nextcloud.android.sso.helper.SingleAccountHelper;

import java.util.Optional;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.api.ResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.util.ProjectUtil;

public class PushNotificationViewModel extends AndroidViewModel {

    // Provided by Files app NotificationJob
    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_LINK = "link";
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
            callback.onError(new IllegalArgumentException("Bundle is null"));
            return;
        }

        try {
            final long cardRemoteId = extractCardRemoteId(bundle)
                    .orElseThrow(() -> new IllegalArgumentException("Could not extract cardRemoteId"));
            final var account = extractAccount(bundle)
                    .orElseThrow(() -> new IllegalArgumentException("Account not found"));

            SingleAccountHelper.setCurrentAccount(getApplication(), account.getName());
            final var syncManager = new SyncManager(getApplication());

            final var card = syncManager.getCardByRemoteIDDirectly(account.getId(), cardRemoteId);

            if (card.isPresent()) {
                syncManager.synchronizeCard(new ResponseCallback<>(account) {
                    @Override
                    public void onResponse(Boolean response) {
                        final var boardLocalId = extractBoardLocalId(syncManager, account.getId(), cardRemoteId, bundle);
                        if (boardLocalId.isPresent()) {
                            callback.onResponse(new CardInformation(account, boardLocalId.get(), card.get().getLocalId()));
                        } else {
                            DeckLog.wtf("Card with local ID", card.get().getLocalId(), "and remote ID", card.get().getId(), "is present, but could not find board for it.");
                            publishErrorToCallback("Given localBoardId for cardRemoteId" + cardRemoteId + "is null.", null, callback, bundle);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        final var boardLocalId = extractBoardLocalId(syncManager, account.getId(), cardRemoteId, bundle);
                        if (boardLocalId.isPresent()) {
                            Toast.makeText(getApplication(), R.string.card_outdated, Toast.LENGTH_LONG).show();
                            callback.onResponse(new CardInformation(account, boardLocalId.get(), card.get().getLocalId()));
                        } else {
                            DeckLog.wtf("Card with local ID", card.get().getLocalId(), "and remote ID", card.get().getId(), "is present, but could not find board for it.");
                            publishErrorToCallback("Given localBoardId for cardRemoteId" + cardRemoteId + "is null.", null, callback, bundle);
                        }
                    }
                }, card.get());
            } else {
                final var boardLocalId = extractBoardLocalId(syncManager, account.getId(), cardRemoteId, bundle);
                if (boardLocalId.isPresent()) {
                    DeckLog.info("Card is not yet available locally. Synchronize board with localId", boardLocalId);
                    syncManager.synchronizeBoard(boardLocalId.get(), new ResponseCallback<>(account) {
                        @Override
                        public void onResponse(Boolean response) {
                            final var card = syncManager.getCardByRemoteIDDirectly(account.getId(), cardRemoteId);
                            if (card.isPresent()) {
                                callback.onResponse(new CardInformation(account, boardLocalId.get(), card.get().getLocalId()));
                            } else {
                                publishErrorToCallback("Something went wrong while synchronizing the card" + cardRemoteId + " (cardRemoteId). Given fullCard is null.", null, callback, bundle);
                            }
                        }

                        @SuppressLint("MissingSuperCall")
                        @Override
                        public void onError(Throwable throwable) {
                            publishErrorToCallback("Something went wrong while synchronizing the board with localId" + boardLocalId, throwable, callback, bundle);
                        }
                    });
                } else {
                    final var boardRemoteId = extractBoardRemoteId(bundle);
                    if (boardRemoteId.isPresent()) {
                        // TODO It should be enough to only fetch the board with the given boardRemoteId, not sure though whether it's worth the effort
                        syncManager.synchronize(new ResponseCallback<>(account) {
                            @Override
                            public void onResponse(Boolean response) {
                                final var card = syncManager.getCardByRemoteIDDirectly(account.getId(), cardRemoteId);
                                if (card.isPresent()) {
                                    final var boardLocalId = extractBoardLocalId(syncManager, account.getId(), cardRemoteId, bundle);
                                    if (boardLocalId.isPresent()) {
                                        callback.onResponse(new CardInformation(account, boardLocalId.get(), card.get().getLocalId()));
                                    } else {
                                        DeckLog.wtf("Card with local ID", card.get().getLocalId(), "and remote ID", card.get().getId(), "is present, but could not find board for it.");
                                        publishErrorToCallback("Could not find board locally for card with remote ID" + cardRemoteId + "even after full synchronization", null, callback, bundle);
                                    }
                                } else {
                                    publishErrorToCallback("Could not find card with remote ID" + cardRemoteId + "even after full synchronization", null, callback, bundle);
                                }
                            }

                            @Override
                            @SuppressLint("MissingSuperCall")
                            public void onError(Throwable throwable) {
                                publishErrorToCallback("Could not extract boardRemoteId", null, callback, bundle);
                            }
                        });
                    } else {
                        syncManager.synchronize(new ResponseCallback<>(account) {
                            @Override
                            public void onResponse(Boolean response) {
                                final var card = syncManager.getCardByRemoteIDDirectly(account.getId(), cardRemoteId);
                                if (card.isPresent()) {
                                    final var boardLocalId = extractBoardLocalId(syncManager, account.getId(), cardRemoteId, bundle);
                                    if (boardLocalId.isPresent()) {
                                        callback.onResponse(new CardInformation(account, boardLocalId.get(), card.get().getLocalId()));
                                    } else {
                                        DeckLog.wtf("Card with local ID", card.get().getLocalId(), "and remote ID", card.get().getId(), "is present, but could not find board for it.");
                                        publishErrorToCallback("Could not find board locally for card with remote ID" + cardRemoteId + "even after full synchronization", null, callback, bundle);
                                    }
                                } else {
                                    publishErrorToCallback("Could not find card with remote ID" + cardRemoteId + "even after full synchronization", null, callback, bundle);
                                }
                            }

                            @Override
                            @SuppressLint("MissingSuperCall")
                            public void onError(Throwable throwable) {
                                publishErrorToCallback("Could not extract boardRemoteId", null, callback, bundle);
                            }
                        });
                    }
                }
            }
        } catch (Throwable throwable) {
            publishErrorToCallback("", throwable, callback, bundle);
        }
    }

    /**
     * If a browser fallback is possible, {@link PushNotificationCallback#fallbackToBrowser(Uri)}
     * will be invoked, otherwise {@link PushNotificationCallback#onError(Throwable)}.
     */
    private void publishErrorToCallback(@NonNull String message, @Nullable Throwable cause, @NonNull PushNotificationCallback callback, @NonNull Bundle bundle) {
        try {
            // TODO check behavior of Uri.parse for an empty string
            callback.fallbackToBrowser(Uri.parse(bundle.getString(KEY_LINK)));
        } catch (Throwable t) {
            final var info = "Error while receiving push notification:\n"
                    + message + "\n"
                    + KEY_SUBJECT + ": [" + bundle.getString(KEY_SUBJECT) + "]\n"
                    + KEY_MESSAGE + ": [" + bundle.getString(KEY_MESSAGE) + "]\n"
                    + KEY_LINK + ": [" + bundle.getString(KEY_LINK) + "]\n"
                    + KEY_CARD_REMOTE_ID + ": [" + bundle.getString(KEY_CARD_REMOTE_ID) + "]\n"
                    + KEY_ACCOUNT + ": [" + bundle.getString(KEY_ACCOUNT) + "]";
            callback.onError(cause == null
                    ? new Exception(info)
                    : new Exception(info, cause));
        }
    }

    private Optional<Long> extractCardRemoteId(@NonNull Bundle bundle) {
        try {
            final String cardRemoteIdString = bundle.getString(KEY_CARD_REMOTE_ID);
            return Optional.of(Long.parseLong(cardRemoteIdString));
        } catch (NumberFormatException e) {
            DeckLog.warn(e);
            final long[] ids = ProjectUtil.extractBoardIdAndCardIdFromUrl(bundle.getString(KEY_LINK));
            return ids.length == 2
                    ? Optional.of(ids[1])
                    : Optional.empty();
        }
    }

    private Optional<Account> extractAccount(@NonNull Bundle bundle) {
        return Optional.ofNullable(readAccountSyncManager.readAccountDirectly(bundle.getString(KEY_ACCOUNT)));
    }

    private Optional<Long> extractBoardLocalId(@NonNull SyncManager syncManager, long accountId, long cardRemoteId, @NonNull Bundle bundle) {
        return Optional.ofNullable(syncManager.getBoardLocalIdByAccountAndCardRemoteIdDirectly(accountId, cardRemoteId));
    }

    private Optional<Long> extractBoardRemoteId(@NonNull Bundle bundle) {
        try {
            final long[] ids = ProjectUtil.extractBoardIdAndCardIdFromUrl(bundle.getString(KEY_LINK));
            return ids.length > 0
                    ? Optional.of(ids[0])
                    : Optional.empty();
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public Optional<String> extractSubject(@Nullable Bundle bundle) {
        return extractProperty(bundle, KEY_SUBJECT);
    }

    public Optional<String> extractMessage(@Nullable Bundle bundle) {
        return extractProperty(bundle, KEY_MESSAGE);
    }

    private Optional<String> extractProperty(@Nullable Bundle bundle, @NonNull String property) {
        if (bundle == null) {
            return Optional.empty();
        }
        final String val = bundle.getString(property);
        if (val == null) {
            return Optional.empty();
        }
        if (val.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(val);
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
