package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.JoinCardWithLabel;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class CardLabelRelationshipProvider implements IRelationshipProvider {

    private Card card;
    private List<Label> labels;

    public CardLabelRelationshipProvider(Card card, List<Label> labels) {
        this.card = card;
        this.labels = labels;
    }

    @Override
    public void insertAllNecessary(DataBaseAdapter dataBaseAdapter, long accountId) {
        if (labels== null){
            return;
        }
        Card card = dataBaseAdapter.getCardByRemoteIdDirectly(accountId, this.card.getId());
        for (Label label : labels) {
            Label existingLabel = dataBaseAdapter.getLabelByRemoteIdDirectly(accountId, label.getId());
            if (existingLabel != null) { // maybe not synced yet, skipping this time. next sync will be able to push it up
                JoinCardWithLabel existingJoin = dataBaseAdapter.getJoinCardWithLabel(existingLabel.getLocalId(), card.getLocalId());
                if (existingJoin == null){
                    dataBaseAdapter.createJoinCardWithLabel(existingLabel.getLocalId(), card.getLocalId(), DBStatus.UP_TO_DATE);
                }
            }
        }
    }

    @Override
    public void deleteAllExisting(DataBaseAdapter dataBaseAdapter, long accountId) {
        Card card = dataBaseAdapter.getCardByRemoteIdDirectly(accountId, this.card.getId());
        dataBaseAdapter.deleteJoinedLabelsForCard(card.getLocalId());
    }
}
