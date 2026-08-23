package it.niedermann.nextcloud.deck.remote.helpers.providers;

import java.util.List;

import it.niedermann.nextcloud.deck.database.DataBaseAdapter;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.JoinCardWithDependentCard;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;

public class CardDependantCardRelationshipProvider implements IRelationshipProvider {

    private Card card;
    private List<Long> dependentCardRemoteIDs;

    public CardDependantCardRelationshipProvider(Card card, List<Long> labels) {
        this.card = card;
        this.dependentCardRemoteIDs = labels;
    }

    @Override
    public void insertAllNecessary(DataBaseAdapter dataBaseAdapter, long accountId) {
        if (dependentCardRemoteIDs == null){
            return;
        }
        for (Long remoteId : dependentCardRemoteIDs) {
            JoinCardWithDependentCard existingLink = dataBaseAdapter.getDependentCardsForCard(accountId, remoteId);
            if (existingLink == null) { // maybe not synced yet, skipping this time. next sync will be able to push it up
                dataBaseAdapter.createJoinCardWithDependent(card.getLocalId(), remoteId, DBStatus.UP_TO_DATE);
            }
        }
    }

    @Override
    public void deleteAllExisting(DataBaseAdapter dataBaseAdapter, long accountId) {
        dataBaseAdapter.deleteDependentCardsForCard(card.getLocalId());
    }
}
