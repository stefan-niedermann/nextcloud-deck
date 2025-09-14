package it.niedermann.nextcloud.deck.deprecated.ui.main;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.app.Application;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.api.EmptyResponse;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.deprecated.util.DeckLog;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import it.niedermann.nextcloud.deck.repository.AccountRepository;
import it.niedermann.nextcloud.deck.repository.BoardRepository;
import it.niedermann.nextcloud.deck.repository.CardRepository;
import it.niedermann.nextcloud.deck.repository.CommentRepository;
import it.niedermann.nextcloud.deck.repository.StackRepository;
import it.niedermann.nextcloud.deck.repository.SyncRepository;
import it.niedermann.nextcloud.deck.repository.WidgetRepository;
import it.niedermann.nextcloud.deck.deprecated.ui.viewmodel.BaseViewModel;

@SuppressWarnings("WeakerAccess")
public class MainViewModel extends BaseViewModel {

    @Nullable
    private SyncRepository syncRepository;
    private final AccountRepository accountRepository;
    private final BoardRepository boardRepository;
    private final StackRepository stackRepository;
    private final CardRepository cardRepository;
    private final CommentRepository commentRepository;
    private final WidgetRepository widgetRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.accountRepository = new AccountRepository(application);
        this.boardRepository = new BoardRepository(application);
        this.stackRepository = new StackRepository(application);
        this.cardRepository = new CardRepository(application);
        this.commentRepository = new CommentRepository(application);
        this.widgetRepository = new WidgetRepository(application);
    }

    public void recreateSyncManager(@NonNull Account account) throws NextcloudFilesAppAccountNotFoundException {
        try {
            this.syncRepository = new SyncRepository(getApplication(), account);
        } catch (NextcloudFilesAppAccountNotFoundException e) {
            this.syncRepository = null;
            throw e;
        }
    }

    private IllegalStateException getInvalidSyncManagerException() {
        return new IllegalStateException("SyncManager is null");
    }

    public LiveData<Map<Stack, List<FullCard>>> searchCards(long accountId, long boardId, @NonNull String term, int limit) {
        return cardRepository.searchCards(accountId, boardId, term, limit);
    }

    public void synchronize(@NonNull Account account, @NonNull IResponseCallback<Boolean> callback) {
        if (syncRepository == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncRepository.synchronize(ResponseCallback.from(account, callback));
        }
    }

    public void refreshCapabilities(@NonNull ResponseCallback<Capabilities> callback) {
        if (syncRepository == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncRepository.refreshCapabilities(callback);
        }
    }

    public LiveData<Boolean> hasAccounts() {
        return accountRepository.hasAccounts();
    }

    public CompletableFuture<Account> getAccount(long accountId) {
        return supplyAsync(() -> accountRepository.readAccountDirectly(accountId), executor);
    }

    public CompletableFuture<Integer> getCurrentBoardColor(long accountId, long boardId) {
        return baseRepository.getCurrentBoardColor(accountId, boardId);
    }

    public void saveCurrentBoardId(long accountId, long boardId) {
        baseRepository.saveCurrentBoardId(accountId, boardId);
    }

    public void createBoard(@NonNull Account account, @NonNull Board board, @NonNull IResponseCallback<FullBoard> callback) {
        boardRepository.createBoard(account, board, callback);
    }

    public void updateBoard(@NonNull FullBoard board, @NonNull IResponseCallback<FullBoard> callback) {
        boardRepository.updateBoard(board, callback);
    }

    public void archiveBoard(@NonNull Board board, @NonNull IResponseCallback<FullBoard> callback) {
        if (syncRepository == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncRepository.archiveBoard(board, callback);
        }
    }

    public void cloneBoard(long originAccountId, long originBoardLocalId, long targetAccountId, @ColorInt int targetBoardColor, boolean cloneCards, @NonNull IResponseCallback<FullBoard> callback) {
        if (syncRepository == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncRepository.cloneBoard(originAccountId, originBoardLocalId, targetAccountId, targetBoardColor, cloneCards, callback);
        }
    }

    public void deleteBoard(@NonNull Board board, @NonNull IResponseCallback<EmptyResponse> callback) {
        boardRepository.deleteBoard(board, callback);
    }

    public void saveCurrentStackId(long accountId, long boardId, long stackId) {
        baseRepository.saveCurrentStackId(accountId, boardId, stackId);
    }

    public void createStack(long accountId, long boardId, @NonNull String title, @NonNull IResponseCallback<FullStack> callback) {
        stackRepository.createStack(accountId, boardId, title, callback);
    }

    public LiveData<FullStack> getStack(long accountId, long localStackId) {
        return stackRepository.getStack(accountId, localStackId);
    }

    public void reorderStack(long accountId, long boardId, long stackLocalId, boolean moveToRight) {
        stackRepository.reorderStack(accountId, boardId, stackLocalId, moveToRight);
    }

    public void updateStackTitle(long localStackId, @NonNull String newTitle, @NonNull IResponseCallback<FullStack> callback) {
        stackRepository.updateStackTitle(localStackId, newTitle, callback);
    }

    public void deleteStack(long accountId, long boardId, long stackLocalId, @NonNull IResponseCallback<EmptyResponse> callback) {
        stackRepository.deleteStack(accountId, boardId, stackLocalId, callback);
    }

    public void reorder(@NonNull FullCard movedCard, long newStackId, int newIndex) {
        if (syncRepository == null) {
            DeckLog.logError(getInvalidSyncManagerException());
        } else {
            syncRepository.reorder(movedCard.getAccountId(), movedCard, newStackId, newIndex);
        }
    }

    public void countCardsInStack(long accountId, long stackId, @NonNull IResponseCallback<Integer> callback) {
        stackRepository.countCardsInStackDirectly(accountId, stackId, callback);
    }

    public void archiveCardsInStack(long accountId, long stackId, @NonNull FilterInformation filterInformation, @NonNull IResponseCallback<EmptyResponse> callback) {
        if (syncRepository == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncRepository.archiveCardsInStack(accountId, stackId, filterInformation, callback);
        }
    }

    public void updateCard(@NonNull FullCard fullCard, @NonNull IResponseCallback<FullCard> callback) {
        if (syncRepository == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncRepository.updateCard(fullCard, callback);
        }
    }

    public void addCommentToCard(long accountId, String message, long cardId) {
        if (syncRepository == null) {
            DeckLog.logError(getInvalidSyncManagerException());
        } else {
            supplyAsync(() -> accountRepository.readAccountDirectly(accountId))
                    .thenAcceptAsync(account -> commentRepository.addCommentToCard(account.getId(), cardId, new DeckComment(message, account.getUserName(), Instant.now())));
        }
    }

    public void addAttachmentToCard(long accountId, long localCardId, @NonNull String mimeType, @NonNull File file, @NonNull IResponseCallback<Attachment> callback) {
        if (syncRepository == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncRepository.addAttachmentToCard(accountId, localCardId, mimeType, file, callback);
        }
    }

    public void addOrUpdateSingleCardWidget(int widgetId, long accountId, long boardId, long localCardId) {
        widgetRepository.addOrUpdateSingleCardWidget(widgetId, accountId, boardId, localCardId);
    }

    public LiveData<Account> getCurrentAccount$() {
        return new ReactiveLiveData<>(baseRepository.getCurrentAccountId$())
                .flatMap(accountRepository::readAccount);
    }

    public LiveData<Pair<List<FullBoard>, Boolean>> getBoards(long accountId) {
        return new ReactiveLiveData<>(boardRepository.getFullBoards(accountId, false))
                .combineWith(() -> boardRepository.hasArchivedBoards(accountId));
    }

    public LiveData<FullBoard> getCurrentFullBoard(long accountId) {
        return new ReactiveLiveData<>(baseRepository.getCurrentBoardId$(accountId))
                .flatMap(boardId -> boardRepository.getFullBoardById(accountId, boardId));
    }

    public LiveData<List<Stack>> getStacks(long accountId, long boardId) {
        return new ReactiveLiveData<>(stackRepository.getStacksForBoard(accountId, boardId))
                .distinctUntilChanged();
    }

    public LiveData<Long> getCurrentStackId$(long accountId, long boardId) {
        return baseRepository.getCurrentStackId$(accountId, boardId);
    }

    public CompletableFuture<Account> getAccountFuture(long accountId) {
        return supplyAsync(() -> accountRepository.readAccountDirectly(accountId));
    }


    public void archiveCard(@NonNull FullCard card, @NonNull IResponseCallback<FullCard> callback) {
        cardRepository.archiveCard(card, callback);
    }

    public void deleteCard(@NonNull Card card, @NonNull IResponseCallback<EmptyResponse> callback) {
        cardRepository.deleteCard(card, callback);
    }

    public void assignUserToCard(@NonNull FullCard fullCard) {
        if (syncRepository == null) {
            throw getInvalidSyncManagerException();
        } else {
            final var syncRepositoryRef = syncRepository;
            getAccountFuture(fullCard.getAccountId()).thenAcceptAsync(account -> syncRepositoryRef.assignUserToCard(getUserByUidDirectly(fullCard.getCard().getAccountId(), account.getUserName()), fullCard.getCard()));
        }
    }

    public void unassignUserFromCard(@NonNull FullCard fullCard) {
        if (syncRepository == null) {
            throw getInvalidSyncManagerException();
        } else {
            final var syncRepositoryRef = syncRepository;
            getAccountFuture(fullCard.getAccountId()).thenAcceptAsync(account -> syncRepositoryRef.unassignUserFromCard(getUserByUidDirectly(fullCard.getCard().getAccountId(), account.getUserName()), fullCard.getCard()));
        }
    }

    private User getUserByUidDirectly(long accountId, String uid) {
        return userRepository.getUserByUidDirectly(accountId, uid);
    }

    public void moveCard(long originAccountId, long originCardLocalId, long targetAccountId, long targetBoardLocalId, long targetStackLocalId, @NonNull IResponseCallback<EmptyResponse> callback) {
        if (syncRepository == null) {
            callback.onError(getInvalidSyncManagerException());
        } else {
            syncRepository.moveCard(originAccountId, originCardLocalId, targetAccountId, targetBoardLocalId, targetStackLocalId, callback);
        }
    }
}
