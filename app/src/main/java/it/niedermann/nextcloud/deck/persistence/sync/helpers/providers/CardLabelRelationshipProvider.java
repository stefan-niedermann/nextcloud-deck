package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class CardLabelRelationshipProvider implements IRelationshipProvider {

    Card card;
    List<Label> labels;

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
        for (Label label : labels){
            Label existingLabel = dataBaseAdapter.getLabelByRemoteIdDirectly(accountId, label.getId());
            dataBaseAdapter.createJoinCardWithLabel(existingLabel.getLocalId(), card.getLocalId());
        }
    }

    @Override
    public void deleteAllExisting(DataBaseAdapter dataBaseAdapter, long accountId) {
        Card card = dataBaseAdapter.getCardByRemoteIdDirectly(accountId, this.card.getId());
        dataBaseAdapter.deleteJoinedLabelsForCard(card.getLocalId());
    }
}
