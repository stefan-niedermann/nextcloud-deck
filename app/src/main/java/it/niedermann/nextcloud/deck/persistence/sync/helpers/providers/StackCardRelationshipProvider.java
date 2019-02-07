package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class StackCardRelationshipProvider implements IRelationshipProvider {

    Stack stack;
    Card card;

    public StackCardRelationshipProvider(Stack stack, Card card) {
        this.stack = stack;
        this.card = card;
    }

    @Override
    public void insertAllNecessary(DataBaseAdapter dataBaseAdapter, long accountId) {
        if (card == null){
            return;
        }
        dataBaseAdapter.createJoinStackWithCard(card.getLocalId(), this.stack.getLocalId());
    }

    @Override
    public void deleteAllExisting(DataBaseAdapter dataBaseAdapter, long accountId) {
        dataBaseAdapter.deleteJoinedCardForStackById(card.getLocalId());
    }
}
