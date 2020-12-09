package it.niedermann.nextcloud.deck.ui.card;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullCardWithProjects;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;

@SuppressWarnings("WeakerAccess")
public class EditCardViewModel extends AndroidViewModel {

    private SyncManager syncManager;
    private Account account;
    private long boardId;
    private FullCardWithProjects originalCard;
    private FullCardWithProjects fullCard;
    private boolean isSupportedVersion = false;
    private boolean hasCommentsAbility = false;
    private boolean pendingCreation = false;
    private boolean canEdit = false;
    private boolean createMode = false;

    public EditCardViewModel(@NonNull Application application) {
        super(application);
        this.syncManager = new SyncManager(application);
    }

    /**
     * Stores a deep copy of the given fullCard to be able to compare the state at every time in #{@link EditCardViewModel#hasChanges()}
     *
     * @param boardId  Local ID, expecting a positive long value
     * @param fullCard The card that is currently edited
     */
    public void initializeExistingCard(long boardId, @NonNull FullCardWithProjects fullCard, boolean isSupportedVersion) {
        this.boardId = boardId;
        this.fullCard = fullCard;
        this.originalCard = new FullCardWithProjects(this.fullCard);
        this.isSupportedVersion = isSupportedVersion;
    }

    /**
     * Stores a deep copy of the given fullCard to be able to compare the state at every time in #{@link EditCardViewModel#hasChanges()}
     *
     * @param boardId Local ID, expecting a positive long value
     * @param stackId Local ID, expecting a positive long value where the card should be created
     */
    public void initializeNewCard(long boardId, long stackId, boolean isSupportedVersion) {
        final FullCardWithProjects fullCard = new FullCardWithProjects();
        fullCard.setLabels(new ArrayList<>());
        fullCard.setAssignedUsers(new ArrayList<>());
        fullCard.setAttachments(new ArrayList<>());
        final Card card = new Card();
        card.setStackId(stackId);
        fullCard.setCard(card);
        initializeExistingCard(boardId, fullCard, isSupportedVersion);
    }

    public void setAccount(@NonNull Account account) {
        this.account = account;
        this.syncManager = new SyncManager(getApplication(), account.getName());
        hasCommentsAbility = account.getServerDeckVersionAsObject().supportsComments();
    }

    public boolean hasChanges() {
        if (fullCard == null) {
            DeckLog.info("Can not check for changes because fullCard is null → assuming no changes have been made yet.");
            return false;
        }
        return fullCard.equals(originalCard);
    }

    public boolean hasCommentsAbility() {
        return hasCommentsAbility;
    }

    public Account getAccount() {
        return account;
    }

    public FullCardWithProjects getFullCard() {
        return fullCard;
    }

    public boolean isPendingCreation() {
        return pendingCreation;
    }

    public void setPendingCreation(boolean pendingCreation) {
        this.pendingCreation = pendingCreation;
    }

    public boolean canEdit() {
        return canEdit && isSupportedVersion;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean isCreateMode() {
        return createMode;
    }

    public void setCreateMode(boolean createMode) {
        this.createMode = createMode;
    }

    public long getBoardId() {
        return boardId;
    }

    public LiveData<FullBoard> getFullBoardById(Long accountId, Long localId) {
        return syncManager.getFullBoardById(accountId, localId);
    }

    public WrappedLiveData<Label> createLabel(long accountId, Label label, long localBoardId) {
        return syncManager.createLabel(accountId, label, localBoardId);
    }

    public LiveData<FullCardWithProjects> getFullCardWithProjectsByLocalId(long accountId, long cardLocalId) {
        return syncManager.getFullCardWithProjectsByLocalId(accountId, cardLocalId);
    }

    public WrappedLiveData<FullCard> createFullCard(long accountId, long localBoardId, long localStackId, @NonNull FullCard card) {
        return syncManager.createFullCard(accountId, localBoardId, localStackId, card);
    }

    public WrappedLiveData<FullCard> updateCard(@NonNull FullCard card) {
        return syncManager.updateCard(card);
    }

    public LiveData<List<Activity>> syncActivitiesForCard(@NonNull Card card) {
        return syncManager.syncActivitiesForCard(card);
    }

    public WrappedLiveData<Attachment> addAttachmentToCard(long accountId, long localCardId, @NonNull String mimeType, @NonNull File file) {
        return syncManager.addAttachmentToCard(accountId, localCardId, mimeType, file);
    }

    public WrappedLiveData<Void> deleteAttachmentOfCard(long accountId, long localCardId, long localAttachmentId) {
        return syncManager.deleteAttachmentOfCard(accountId, localCardId, localAttachmentId);
    }

    public LiveData<Card> getCardByRemoteID(long accountId, long remoteId) {
        return syncManager.getCardByRemoteID(accountId, remoteId);
    }

    public LiveData<Board> getBoardByRemoteId(long accountId, long remoteId) {
        return syncManager.getBoardByRemoteId(accountId, remoteId);
    }
}
