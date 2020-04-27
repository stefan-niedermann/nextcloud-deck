package it.niedermann.nextcloud.deck.ui.card;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullCard;

@SuppressWarnings("WeakerAccess")
public class EditCardViewModel extends ViewModel {

    private Account account;
    private long boardId;
    private FullCard originalCard;
    private FullCard fullCard;
    private boolean hasCommentsAbility = false;
    private boolean pendingCreation = false;
    private boolean canEdit = false;
    private boolean createMode = false;

    /**
     * Stores a deep copy of the given fullCard to be able to compare the state at every time in #{@link EditCardViewModel#hasChanges()}
     *
     * @param account  Must not be null
     * @param boardId  Local ID, expecting a positive long value
     * @param fullCard The card that is currently edited
     */
    public void initializeExistingCard(@NonNull Account account, long boardId, @NonNull FullCard fullCard) {
        this.account = account;
        this.boardId = boardId;
        this.fullCard = fullCard;
        this.originalCard = new FullCard(this.fullCard);
        hasCommentsAbility = account.getServerDeckVersionAsObject().supportsComments();
    }

    /**
     * Stores a deep copy of the given fullCard to be able to compare the state at every time in #{@link EditCardViewModel#hasChanges()}
     *
     * @param account Must not be null
     * @param boardId Local ID, expecting a positive long value
     * @param stackId Local ID, expecting a positive long value where the card should be created
     */
    public void initializeNewCard(@NonNull Account account, long boardId, long stackId) {
        final FullCard fullCard = new FullCard();
        fullCard.setLabels(new ArrayList<>());
        fullCard.setAssignedUsers(new ArrayList<>());
        fullCard.setAttachments(new ArrayList<>());
        final Card card = new Card();
        card.setStackId(stackId);
        fullCard.setCard(card);
        initializeExistingCard(account, boardId, fullCard);
    }

    public boolean hasChanges() {
        return fullCard.equals(originalCard);
    }

    public boolean hasCommentsAbility() {
        return hasCommentsAbility;
    }

    public Account getAccount() {
        return account;
    }

    public FullCard getFullCard() {
        return fullCard;
    }

    public boolean isPendingCreation() {
        return pendingCreation;
    }

    public void setPendingCreation(boolean pendingCreation) {
        this.pendingCreation = pendingCreation;
    }

    public boolean canEdit() {
        return canEdit;
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
}
