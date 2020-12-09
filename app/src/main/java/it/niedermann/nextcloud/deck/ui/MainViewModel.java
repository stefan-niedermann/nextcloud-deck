package it.niedermann.nextcloud.deck.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;

@SuppressWarnings("WeakerAccess")
public class MainViewModel extends AndroidViewModel {

    private final SyncManager syncManager;

    private final MutableLiveData<Account> currentAccount = new MutableLiveData<>();
    @Nullable
    private Board currentBoard;
    private boolean currentAccountHasArchivedBoards = false;

    private boolean currentAccountIsSupportedVersion = false;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.syncManager = new SyncManager(application);
    }

    public Account getCurrentAccount() {
        return currentAccount.getValue();
    }

    public LiveData<Account> getCurrentAccountLiveData() {
        return this.currentAccount;
    }

    public void setCurrentAccount(Account currentAccount) {
        this.currentAccount.setValue(currentAccount);
        this.currentAccountIsSupportedVersion = currentAccount.getServerDeckVersionAsObject().isSupported(getApplication().getApplicationContext());
    }

    public void setCurrentBoard(@NonNull Board currentBoard) {
        this.currentBoard = currentBoard;
    }

    public Long getCurrentBoardLocalId() {
        if (currentBoard == null) {
            throw new IllegalStateException("getCurrentBoardLocalId() called before setCurrentBoard()");
        }
        return this.currentBoard.getLocalId();
    }

    public Long getCurrentBoardRemoteId() {
        if (currentBoard == null) {
            throw new IllegalStateException("getCurrentBoardRemoteId() called before setCurrentBoard()");
        }
        return this.currentBoard.getId();
    }

    public boolean currentBoardHasEditPermission() {
        return this.currentBoard != null && this.currentBoard.isPermissionEdit() && currentAccountIsSupportedVersion;
    }

    public boolean currentAccountHasArchivedBoards() {
        return currentAccountHasArchivedBoards;
    }

    public void setCurrentAccountHasArchivedBoards(boolean currentAccountHasArchivedBoards) {
        this.currentAccountHasArchivedBoards = currentAccountHasArchivedBoards;
    }

    public boolean isCurrentAccountIsSupportedVersion() {
        return currentAccountIsSupportedVersion;
    }

    public WrappedLiveData<FullCard> updateCard(@NonNull FullCard fullCard) {
        return syncManager.updateCard(fullCard);
    }

    public void addCommentToCard(long accountId, long cardId, @NonNull DeckComment comment) {
        syncManager.addCommentToCard(accountId, cardId, comment);
    }

    public WrappedLiveData<Attachment> addAttachmentToCard(long accountId, long localCardId, @NonNull String mimeType, @NonNull File file) {
        return syncManager.addAttachmentToCard(accountId, localCardId, mimeType, file);
    }

    public void addOrUpdateSingleCardWidget(int widgetId, long accountId, long boardId, long localCardId) {
        syncManager.addOrUpdateSingleCardWidget(widgetId, accountId, boardId, localCardId);
    }
}
