package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.Collection;

import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.JoinCardWithUser;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class CardUserRelationshipProvider implements IRelationshipProvider {

    private final Card card;
    private final Collection<User> users;

    public CardUserRelationshipProvider(Card card, Collection<User> users) {
        this.card = card;
        this.users = users;
    }

    @Override
    public void insertAllNecessary(DataBaseAdapter dataBaseAdapter, long accountId) {
        if (users == null){
            return;
        }
        Card card = dataBaseAdapter.getCardByRemoteIdDirectly(accountId, this.card.getId());
        for (User user : users){
            User existingUser = dataBaseAdapter.getUserByUidDirectly(accountId, user.getUid());
            JoinCardWithUser existingJoin = dataBaseAdapter.getJoinCardWithUser(existingUser.getLocalId(), card.getLocalId());
            if (existingJoin == null) {
                dataBaseAdapter.createJoinCardWithUser(existingUser.getLocalId(), card.getLocalId());
            }
        }
    }

    @Override
    public void deleteAllExisting(DataBaseAdapter dataBaseAdapter, long accountId) {
        Card card = dataBaseAdapter.getCardByRemoteIdDirectly(accountId, this.card.getId());
        dataBaseAdapter.deleteJoinedUsersForCard(card.getLocalId());
    }
}
