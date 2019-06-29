package it.niedermann.nextcloud.deck.model.propagation;

import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;

public class CardUpdate extends Card {

    User owner;

    public CardUpdate(FullCard card) {
        super();
        setTitle(card.getCard().getTitle());
        setDescription(card.getCard().getDescription());
        setStackId(card.getCard().getStackId());
        setType(card.getCard().getType());
        setCreatedAt(card.getCard().getCreatedAt());
        setDeletedAt(card.getCard().getDeletedAt());
        setAttachmentCount(card.getCard().getAttachmentCount());
        setUserId(card.getCard().getUserId());
        setOrder(card.getCard().getOrder());
        setArchived(card.getCard().isArchived());
        setDueDate(card.getCard().getDueDate());
        setNotified(card.getCard().isNotified());
        setOverdue(card.getCard().getOverdue());
        setCommentsUnread(card.getCard().getCommentsUnread());
        setAccountId(card.getAccountId());
        setId(card.getId());
        setLocalId(card.getLocalId());
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
