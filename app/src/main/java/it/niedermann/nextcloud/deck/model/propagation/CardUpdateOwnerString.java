package it.niedermann.nextcloud.deck.model.propagation;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Card;

public class CardUpdateOwnerString extends Card {

    private String owner;
    private List<Long> dependentCards;

    public CardUpdateOwnerString(CardUpdate card) {
        super();
        setTitle(card.getTitle());
        setDescription(card.getDescription());
        setStackId(card.getStackId());
        setType(card.getType());
        setCreatedAt(card.getCreatedAt());
        setDeletedAt(card.getDeletedAt());
        setAttachmentCount(card.getAttachmentCount());
        setUserId(card.getUserId());
        setOrder(card.getOrder());
        setArchived(card.isArchived());
        setDueDate(card.getDueDate());
        setNotified(card.isNotified());
        setOverdue(card.getOverdue());
        setCommentsUnread(card.getCommentsUnread());
        setAccountId(card.getAccountId());
        setId(card.getId());
        setLocalId(card.getLocalId());
        setDone(card.getDone());
        setOwner(card.getOwner().getUid());
        setStartDate(card.getStartDate());
        setColor(card.getColor());
        dependentCards = card.getDependentCards();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<Long> getDependentCards() {
        return dependentCards;
    }

    public void setDependentCards(List<Long> dependentCards) {
        this.dependentCards = dependentCards;
    }

    @Override
    public String toString() {
        return "CardUpdate{" +
                "owner=" + owner +
                ", localId=" + localId +
                ", accountId=" + accountId +
                ", id=" + id +
                ", status=" + status +
                ", lastModified=" + lastModified +
                ", lastModifiedLocal=" + lastModifiedLocal +
                "} " + super.toString();
    }
}
