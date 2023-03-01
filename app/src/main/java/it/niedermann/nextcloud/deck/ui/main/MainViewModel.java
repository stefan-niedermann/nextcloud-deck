package it.niedermann.nextcloud.deck.ui.main;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.app.Application;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.api.ResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.viewmodel.BaseViewModel;

@SuppressWarnings("WeakerAccess")
public class MainViewModel extends BaseViewModel {

    @Nullable
    private SyncManager syncManager;

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public void recreateSyncManager(@NonNull Account account) throws NextcloudFilesAppAccountNotFoundException {
        try {
            this.syncManager = new SyncManager(getApplication(), account);
        } catch (NextcloudFilesAppAccountNotFoundException e) {
            this.syncManager = null;
            throw e;
        }
    }

    private Exception getInvalidSyncManagerException() {
        return new IllegalStateException("SyncManager is null");
    }

    public void synchronize(@NonNull Account account, @NonNull IResponseCallback<Boolean> callback) {
        if (syncManager == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncManager.synchronize(ResponseCallback.from(account, callback));
        }
    }

    public void refreshCapabilities(@NonNull ResponseCallback<Capabilities> callback) {
        if (syncManager == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncManager.refreshCapabilities(callback);
        }
    }

    public LiveData<Boolean> hasAccounts() {
        return baseRepository.hasAccounts();
    }

    public CompletableFuture<Account> getAccount(long accountId) {
        return supplyAsync(() -> baseRepository.readAccountDirectly(accountId), executor);
    }

    public CompletableFuture<Integer> getCurrentBoardColor(long accountId, long boardId) {
        return baseRepository.getCurrentBoardColor(accountId, boardId);
    }

    public void saveCurrentBoardId(long accountId, long boardId) {
        baseRepository.saveCurrentBoardId(accountId, boardId);
    }

    public void createBoard(@NonNull Account account, @NonNull Board board, @NonNull IResponseCallback<FullBoard> callback) {
        if (syncManager == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncManager.createBoard(account, board, callback);
        }
    }

    public void updateBoard(@NonNull FullBoard board, @NonNull IResponseCallback<FullBoard> callback) {
        if (syncManager == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncManager.updateBoard(board, callback);
        }
    }

    public void archiveBoard(@NonNull Board board, @NonNull IResponseCallback<FullBoard> callback) {
        if (syncManager == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncManager.archiveBoard(board, callback);
        }
    }

    public void cloneBoard(long originAccountId, long originBoardLocalId, long targetAccountId, @ColorInt int targetBoardColor, boolean cloneCards, @NonNull IResponseCallback<FullBoard> callback) {
        if (syncManager == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncManager.cloneBoard(originAccountId, originBoardLocalId, targetAccountId, targetBoardColor, cloneCards, callback);
        }
    }

    public void deleteBoard(@NonNull Board board, @NonNull IResponseCallback<Void> callback) {
        if (syncManager == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncManager.deleteBoard(board, callback);
        }
    }

    public void saveCurrentStackId(long accountId, long boardId, long stackId) {
        baseRepository.saveCurrentStackId(accountId, boardId, stackId);
    }

    public void createStack(long accountId, long boardId, @NonNull String title, @NonNull IResponseCallback<FullStack> callback) {
        if (syncManager == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncManager.createStack(accountId, boardId, title, callback);
        }
    }

    public LiveData<FullStack> getStack(long accountId, long localStackId) {
        if (syncManager == null) {
            return new MutableLiveData<>();
        }
        return syncManager.getStack(accountId, localStackId);
    }

    public void reorderStack(long accountId, long boardId, long stackLocalId, boolean moveToRight) {
        if (syncManager == null) {
            DeckLog.logError(getInvalidSyncManagerException());
        } else {
            syncManager.reorderStack(accountId, boardId, stackLocalId, moveToRight);
        }
    }

    public void updateStackTitle(long localStackId, @NonNull String newTitle, @NonNull IResponseCallback<FullStack> callback) {
        if (syncManager == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncManager.updateStackTitle(localStackId, newTitle, callback);
        }
    }

    public void deleteStack(long accountId, long boardId, long stackLocalId, @NonNull IResponseCallback<Void> callback) {
        if (syncManager == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncManager.deleteStack(accountId, boardId, stackLocalId, callback);
        }
    }

    public void reorder(@NonNull FullCard movedCard, long newStackId, int newIndex) {
        if (syncManager == null) {
            DeckLog.logError(getInvalidSyncManagerException());
        } else {
            syncManager.reorder(movedCard.getAccountId(), movedCard, newStackId, newIndex);
        }
    }

    public void countCardsInStack(long accountId, long stackId, @NonNull IResponseCallback<Integer> callback) {
        if (syncManager == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncManager.countCardsInStackDirectly(accountId, stackId, callback);
        }
    }

    public void archiveCardsInStack(long accountId, long stackId, @NonNull FilterInformation filterInformation, @NonNull IResponseCallback<Void> callback) {
        if (syncManager == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncManager.archiveCardsInStack(accountId, stackId, filterInformation, callback);
        }
    }

    public void updateCard(@NonNull FullCard fullCard, @NonNull IResponseCallback<FullCard> callback) {
        if (syncManager == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncManager.updateCard(fullCard, callback);
        }
    }

    public void addCommentToCard(long accountId, String message, long cardId) {
        if (syncManager == null) {
            DeckLog.logError(getInvalidSyncManagerException());
        } else {
            supplyAsync(() -> syncManager.readAccountDirectly(accountId))
                    .thenAcceptAsync(account -> syncManager.addCommentToCard(account.getId(), cardId, new DeckComment(message, account.getUserName(), Instant.now())));
        }
    }

    public void addAttachmentToCard(long accountId, long localCardId, @NonNull String mimeType, @NonNull File file, @NonNull IResponseCallback<Attachment> callback) {
        if (syncManager == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncManager.addAttachmentToCard(accountId, localCardId, mimeType, file, callback);
        }
    }

    public void addOrUpdateSingleCardWidget(int widgetId, long accountId, long boardId, long localCardId) {
        if (syncManager == null) {
            DeckLog.logError(getInvalidSyncManagerException());
        } else {
            syncManager.addOrUpdateSingleCardWidget(widgetId, accountId, boardId, localCardId);
        }
    }

    public LiveData<Account> getCurrentAccount$() {
        return new ReactiveLiveData<>(baseRepository.getCurrentAccountId$())
                .flatMap(baseRepository::readAccount);
    }

    public LiveData<Pair<List<FullBoard>, Boolean>> getBoards(long accountId) {
        return new ReactiveLiveData<>(baseRepository.getFullBoards(accountId, false))
                .combineWith(() -> baseRepository.hasArchivedBoards(accountId));
    }

    public LiveData<FullBoard> getCurrentFullBoard(long accountId) {
        return new ReactiveLiveData<>(baseRepository.getCurrentBoardId$(accountId))
                .flatMap(boardId -> baseRepository.getFullBoardById(accountId, boardId));
    }

    public LiveData<List<Stack>> getStacks(long accountId, long boardId) {
        return new ReactiveLiveData<>(baseRepository.getStacksForBoard(accountId, boardId))
                .distinctUntilChanged();
    }

    public LiveData<Long> getCurrentStackId$(long accountId, long boardId) {
        return baseRepository.getCurrentStackId$(accountId, boardId);
    }
}
