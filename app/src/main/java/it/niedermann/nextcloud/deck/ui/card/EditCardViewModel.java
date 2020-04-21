package it.niedermann.nextcloud.deck.ui.card;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullCard;

@SuppressWarnings("WeakerAccess")
public class EditCardViewModel extends ViewModel {

    private long accountId;
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
     * @param accountId Expecting a positive long value
     * @param boardId   Local ID, expecting a positive long value
     * @param fullCard  The card that is currently edited
     */
    public void initializeExistingCard(long accountId, long boardId, @NonNull FullCard fullCard) {
        this.accountId = accountId;
        this.boardId = boardId;
        this.fullCard = fullCard;
        this.originalCard = new FullCard(this.fullCard);
    }

    /**
     * Stores a deep copy of the given fullCard to be able to compare the state at every time in #{@link EditCardViewModel#hasChanges()}
     *
     * @param accountId Expecting a positive long value
     * @param boardId   Local ID, expecting a positive long value
     * @param stackId   Local ID, expecting a positive long value where the card should be created
     */
    public void initializeNewCard(long accountId, long boardId, long stackId) {
        final FullCard fullCard = new FullCard();
        fullCard.setLabels(new ArrayList<>());
        fullCard.setAssignedUsers(new ArrayList<>());
        fullCard.setAttachments(new ArrayList<>());
        final Card card = new Card();
        card.setStackId(stackId);
        fullCard.setCard(card);
        initializeExistingCard(accountId, boardId, fullCard);
    }

    public boolean hasChanges() {
        return fullCard.equals(originalCard);
    }

    public boolean isHasCommentsAbility() {
        return hasCommentsAbility;
    }

    public void setHasCommentsAbility(boolean hasCommentsAbility) {
        this.hasCommentsAbility = hasCommentsAbility;
    }

    public long getAccountId() {
        return accountId;
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
