package it.niedermann.nextcloud.deck.ui;

import android.app.Application;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;

@SuppressWarnings("WeakerAccess")
public class MainViewModel extends AndroidViewModel {

    private SyncManager syncManager;

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

    public void recreateSyncManager() {
        this.syncManager = new SyncManager(getApplication());
    }

    public void setSyncManager(@NonNull SyncManager syncManager) {
        this.syncManager = syncManager;
    }

    public void synchronize(@NonNull IResponseCallback<Boolean> responseCallback) {
        syncManager.synchronize(responseCallback);
    }

    public void refreshCapabilities(@NonNull IResponseCallback<Capabilities> callback) {
        syncManager.refreshCapabilities(callback);
    }

    public LiveData<Boolean> hasAccounts() {
        return syncManager.hasAccounts();
    }

    public WrappedLiveData<Account> createAccount(@NonNull Account accout) {
        return syncManager.createAccount(accout);
    }

    public void deleteAccount(long id) {
        syncManager.deleteAccount(id);
    }

    public LiveData<List<Account>> readAccounts() {
        return syncManager.readAccounts();
    }

    public WrappedLiveData<FullBoard> createBoard(long accountId, @NonNull Board board) {
        return syncManager.createBoard(accountId, board);
    }

    public WrappedLiveData<FullBoard> updateBoard(@NonNull FullBoard board) {
        return syncManager.updateBoard(board);
    }

    public LiveData<List<Board>> getBoards(long accountId, boolean archived) {
        return syncManager.getBoards(accountId, archived);
    }

    public LiveData<FullBoard> getFullBoardById(Long accountId, Long localId) {
        return syncManager.getFullBoardById(accountId, localId);
    }

    public WrappedLiveData<FullBoard> archiveBoard(@NonNull Board board) {
        return syncManager.archiveBoard(board);
    }

    public WrappedLiveData<FullBoard> dearchiveBoard(@NonNull Board board) {
        return syncManager.dearchiveBoard(board);
    }

    public WrappedLiveData<FullBoard> cloneBoard(long originAccountId, long originBoardLocalId, long targetAccountId, @ColorInt int targetBoardColor, boolean cloneCards) {
        return syncManager.cloneBoard(originAccountId, originBoardLocalId, targetAccountId, targetBoardColor, cloneCards);
    }

    public WrappedLiveData<Void> deleteBoard(@NonNull Board board) {
        return syncManager.deleteBoard(board);
    }

    public LiveData<Boolean> hasArchivedBoards(long accountId) {
        return syncManager.hasArchivedBoards(accountId);
    }

    public WrappedLiveData<AccessControl> createAccessControl(long accountId, AccessControl entity) {
        return syncManager.createAccessControl(accountId, entity);
    }

    public WrappedLiveData<AccessControl> updateAccessControl(@NonNull AccessControl entity) {
        return syncManager.updateAccessControl(entity);
    }

    public LiveData<List<AccessControl>> getAccessControlByLocalBoardId(long accountId, Long id) {
        return syncManager.getAccessControlByLocalBoardId(accountId, id);
    }

    public WrappedLiveData<Void> deleteAccessControl(@NonNull AccessControl entity) {
        return syncManager.deleteAccessControl(entity);
    }

    public WrappedLiveData<Label> createLabel(long accountId, Label label, long localBoardId) {
        return syncManager.createLabel(accountId, label, localBoardId);
    }

    public LiveData<Integer> countCardsWithLabel(long localLabelId) {
        return syncManager.countCardsWithLabel(localLabelId);
    }

    public WrappedLiveData<Label> updateLabel(@NonNull Label label) {
        return syncManager.updateLabel(label);
    }

    public WrappedLiveData<Void> deleteLabel(@NonNull Label label) {
        return syncManager.deleteLabel(label);
    }

    public LiveData<List<Stack>> getStacksForBoard(long accountId, long localBoardId) {
        return syncManager.getStacksForBoard(accountId, localBoardId);
    }

    public WrappedLiveData<FullStack> createStack(long accountId, @NonNull String title, long boardLocalId) {
        return syncManager.createStack(accountId, title, boardLocalId);
    }

    public LiveData<FullStack> getStack(long accountId, long localStackId) {
        return syncManager.getStack(accountId, localStackId);
    }

    public void swapStackOrder(long accountId, long boardLocalId, @NonNull Pair<Long, Long> stackLocalIds) {
        syncManager.swapStackOrder(accountId, boardLocalId, stackLocalIds);
    }

    public WrappedLiveData<FullStack> updateStackTitle(long localStackId, @NonNull String newTitle) {
        return syncManager.updateStackTitle(localStackId, newTitle);
    }

    public WrappedLiveData<Void> deleteStack(long accountId, long stackLocalId, long boardLocalId) {
        return syncManager.deleteStack(accountId, stackLocalId, boardLocalId);
    }

    public void reorder(long accountId, @NonNull FullCard movedCard, long newStackId, int newIndex) {
        syncManager.reorder(accountId, movedCard, newStackId, newIndex);
    }

    public LiveData<Integer> countCardsInStack(long accountId, long localStackId) {
        return syncManager.countCardsInStack(accountId, localStackId);
    }

    public WrappedLiveData<Void> archiveCardsInStack(long accountId, long stackLocalId, @NonNull FilterInformation filterInformation) {
        return syncManager.archiveCardsInStack(accountId, stackLocalId, filterInformation);
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

    public LiveData<List<FullCard>> getFullCardsForStack(long accountId, long localStackId, @Nullable FilterInformation filter) {
        return syncManager.getFullCardsForStack(accountId, localStackId, filter);
    }

    public WrappedLiveData<Void> moveCard(long originAccountId, long originCardLocalId, long targetAccountId, long targetBoardLocalId, long targetStackLocalId) {
        return syncManager.moveCard(originAccountId, originCardLocalId, targetAccountId, targetBoardLocalId, targetStackLocalId);
    }

    public LiveData<List<FullCard>> getArchivedFullCardsForBoard(long accountId, long localBoardId) {
        return syncManager.getArchivedFullCardsForBoard(accountId, localBoardId);
    }

    public void assignUserToCard(@NonNull User user, @NonNull Card card) {
        syncManager.assignUserToCard(user, card);
    }

    public void unassignUserFromCard(@NonNull User user, @NonNull Card card) {
        syncManager.unassignUserFromCard(user, card);
    }

    public User getUserByUidDirectly(long accountId, String uid) {
        return syncManager.getUserByUidDirectly(accountId, uid);
    }

    public WrappedLiveData<FullCard> archiveCard(@NonNull FullCard card) {
        return syncManager.archiveCard(card);
    }

    public WrappedLiveData<FullCard> dearchiveCard(@NonNull FullCard card) {
        return syncManager.dearchiveCard(card);
    }

    public WrappedLiveData<Void> deleteCard(@NonNull Card card) {
        return syncManager.deleteCard(card);
    }
}
