package it.niedermann.nextcloud.deck.ui;

import android.app.Application;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.util.List;

import it.niedermann.android.sharedpreferences.SharedPreferenceBooleanLiveData;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.api.ResponseCallback;
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

    public LiveData<Boolean> isDebugModeEnabled() {
        return new SharedPreferenceBooleanLiveData(PreferenceManager.getDefaultSharedPreferences(getApplication()), getApplication().getString(R.string.pref_key_debugging), false);
    }

    public Account getCurrentAccount() {
        return currentAccount.getValue();
    }

    public LiveData<Account> getCurrentAccountLiveData() {
        return this.currentAccount;
    }

    public void setCurrentAccount(Account currentAccount) {
        this.currentAccount.setValue(currentAccount);
        this.currentAccountIsSupportedVersion = currentAccount.getServerDeckVersionAsObject().isSupported();
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

    @ColorInt
    public Integer getCurrentBoardColor() {
        if (currentBoard == null) {
            throw new IllegalStateException("getCurrentBoardColor() called before setCurrentBoard()");
        }
        return this.currentBoard.getColor();
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

    public void synchronize(@NonNull ResponseCallback<Boolean> responseCallback) {
        syncManager.synchronize(responseCallback);
    }

    public void refreshCapabilities(@NonNull ResponseCallback<Capabilities> callback) {
        syncManager.refreshCapabilities(callback);
    }

    public LiveData<Boolean> hasAccounts() {
        return syncManager.hasAccounts();
    }

    public void createAccount(@NonNull Account account, @NonNull IResponseCallback<Account> callback) {
        syncManager.createAccount(account, callback);
    }

    public void deleteAccount(long id) {
        syncManager.deleteAccount(id);
    }

    public LiveData<List<Account>> readAccounts() {
        return syncManager.readAccounts();
    }

    public void createBoard(long accountId, @NonNull Board board, @NonNull IResponseCallback<FullBoard> callback) {
        syncManager.createBoard(accountId, board, callback);
    }

    public void updateBoard(@NonNull FullBoard board, @NonNull IResponseCallback<FullBoard> callback) {
        syncManager.updateBoard(board, callback);
    }

    public LiveData<List<Board>> getBoards(long accountId, boolean archived) {
        return syncManager.getBoards(accountId, archived);
    }

    public LiveData<FullBoard> getFullBoardById(Long accountId, Long localId) {
        return syncManager.getFullBoardById(accountId, localId);
    }

    public void archiveBoard(@NonNull Board board, @NonNull IResponseCallback<FullBoard> callback) {
        syncManager.archiveBoard(board, callback);
    }

    public void dearchiveBoard(@NonNull Board board, @NonNull IResponseCallback<FullBoard> callback) {
        syncManager.dearchiveBoard(board, callback);
    }

    public void cloneBoard(long originAccountId, long originBoardLocalId, long targetAccountId, @ColorInt int targetBoardColor, boolean cloneCards, @NonNull IResponseCallback<FullBoard> callback) {
        syncManager.cloneBoard(originAccountId, originBoardLocalId, targetAccountId, targetBoardColor, cloneCards, callback);
    }

    public void deleteBoard(@NonNull Board board, @NonNull IResponseCallback<Void> callback) {
        syncManager.deleteBoard(board, callback);
    }

    public LiveData<Boolean> hasArchivedBoards(long accountId) {
        return syncManager.hasArchivedBoards(accountId);
    }

    public void createAccessControl(long accountId, @NonNull AccessControl entity, @NonNull IResponseCallback<AccessControl> callback) {
        syncManager.createAccessControl(accountId, entity, callback);
    }

    public void updateAccessControl(@NonNull AccessControl entity, @NonNull IResponseCallback<AccessControl> callback) {
        syncManager.updateAccessControl(entity, callback);
    }

    public LiveData<List<AccessControl>> getAccessControlByLocalBoardId(long accountId, Long id) {
        return syncManager.getAccessControlByLocalBoardId(accountId, id);
    }

    public void deleteAccessControl(@NonNull AccessControl entity, @NonNull IResponseCallback<Void> callback) {
        syncManager.deleteAccessControl(entity, callback);
    }

    public void createLabel(long accountId, Label label, long localBoardId, @NonNull IResponseCallback<Label> callback) {
        syncManager.createLabel(accountId, label, localBoardId, callback);
    }

    public void countCardsWithLabel(long localLabelId, @NonNull IResponseCallback<Integer> callback) {
        syncManager.countCardsWithLabel(localLabelId, callback);
    }

    public void updateLabel(@NonNull Label label, @NonNull IResponseCallback<Label> callback) {
        syncManager.updateLabel(label, callback);
    }

    public void deleteLabel(@NonNull Label label, @NonNull IResponseCallback<Void> callback) {
        syncManager.deleteLabel(label, callback);
    }

    public LiveData<List<Stack>> getStacksForBoard(long accountId, long localBoardId) {
        return syncManager.getStacksForBoard(accountId, localBoardId);
    }

    public void createStack(long accountId, @NonNull String title, long boardLocalId, @NonNull IResponseCallback<FullStack> callback) {
        syncManager.createStack(accountId, title, boardLocalId, callback);
    }

    public LiveData<FullStack> getStack(long accountId, long localStackId) {
        return syncManager.getStack(accountId, localStackId);
    }

    public void reorderStack(long accountId, long boardLocalId, long stackLocalId, boolean moveToRight) {
        syncManager.reorderStack(accountId, boardLocalId, stackLocalId, moveToRight);
    }

    public void updateStackTitle(long localStackId, @NonNull String newTitle, @NonNull IResponseCallback<FullStack> callback) {
        syncManager.updateStackTitle(localStackId, newTitle, callback);
    }

    public void deleteStack(long accountId, long stackLocalId, long boardLocalId, @NonNull IResponseCallback<Void> callback) {
        syncManager.deleteStack(accountId, stackLocalId, boardLocalId, callback);
    }

    public void reorder(long accountId, @NonNull FullCard movedCard, long newStackId, int newIndex) {
        syncManager.reorder(accountId, movedCard, newStackId, newIndex);
    }

    public void countCardsInStack(long accountId, long localStackId, @NonNull IResponseCallback<Integer> callback) {
        syncManager.countCardsInStackDirectly(accountId, localStackId, callback);
    }

    public void archiveCardsInStack(long accountId, long stackLocalId, @NonNull FilterInformation filterInformation, @NonNull IResponseCallback<Void> callback) {
        syncManager.archiveCardsInStack(accountId, stackLocalId, filterInformation, callback);
    }

    public void updateCard(@NonNull FullCard fullCard, @NonNull IResponseCallback<FullCard> callback) {
        syncManager.updateCard(fullCard, callback);
    }

    public void addCommentToCard(long accountId, long cardId, @NonNull DeckComment comment) {
        syncManager.addCommentToCard(accountId, cardId, comment);
    }

    public void addAttachmentToCard(long accountId, long localCardId, @NonNull String mimeType, @NonNull File file, @NonNull IResponseCallback<Attachment> callback) {
        syncManager.addAttachmentToCard(accountId, localCardId, mimeType, file, callback);
    }

    public void addOrUpdateSingleCardWidget(int widgetId, long accountId, long boardId, long localCardId) {
        syncManager.addOrUpdateSingleCardWidget(widgetId, accountId, boardId, localCardId);
    }

    public LiveData<List<FullCard>> getFullCardsForStack(long accountId, long localStackId, @Nullable FilterInformation filter) {
        return syncManager.getFullCardsForStack(accountId, localStackId, filter);
    }

    public void moveCard(long originAccountId, long originCardLocalId, long targetAccountId, long targetBoardLocalId, long targetStackLocalId, @NonNull IResponseCallback<Void> callback) {
        syncManager.moveCard(originAccountId, originCardLocalId, targetAccountId, targetBoardLocalId, targetStackLocalId, callback);
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

    public void archiveCard(@NonNull FullCard card, @NonNull IResponseCallback<FullCard> callback) {
        syncManager.archiveCard(card, callback);
    }

    public void dearchiveCard(@NonNull FullCard card, @NonNull IResponseCallback<FullCard> callback) {
        syncManager.dearchiveCard(card, callback);
    }

    public void deleteCard(@NonNull Card card, @NonNull IResponseCallback<Void> callback) {
        syncManager.deleteCard(card, callback);
    }

    public void saveCard(long accountId, long boardLocalId, long stackLocalId, @NonNull FullCard fullCard, @NonNull IResponseCallback<FullCard> callback) {
        syncManager.createFullCard(accountId, boardLocalId, stackLocalId, fullCard, callback);
    }
}
