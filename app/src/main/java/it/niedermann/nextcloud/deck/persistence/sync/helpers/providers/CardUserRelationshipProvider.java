package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.JoinCardWithUser;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class CardUserRelationshipProvider implements IRelationshipProvider {

    Card card;
    List<User> labels;

    public CardUserRelationshipProvider(Card card, List<User> users) {
        this.card = card;
        this.labels = users;
    }

    @Override
    public void insertAllNecessary(DataBaseAdapter dataBaseAdapter, long accountId) {
        if (labels== null){
            return;
        }
        Card card = dataBaseAdapter.getCardByRemoteIdDirectly(accountId, this.card.getId());
        for (User label : labels){
            User existingUser = dataBaseAdapter.getUserByRemoteIdDirectly(accountId, label.getId());
            JoinCardWithUser join = new JoinCardWithUser();
            join.setUserId(existingUser.getLocalId());
            join.setCardId(card.getLocalId());

        }
    }

    @Override
    public void deleteAllExisting(DataBaseAdapter dataBaseAdapter, long accountId) {
        Card card = dataBaseAdapter.getCardByRemoteIdDirectly(accountId, this.card.getId());
        dataBaseAdapter.deleteJoinedUsersForCard(card.getLocalId());
    }
}
