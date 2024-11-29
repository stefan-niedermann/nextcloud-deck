package it.niedermann.nextcloud.deck.remote.helpers.providers;

import java.util.List;

import it.niedermann.nextcloud.deck.database.DataBaseAdapter;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.JoinCardWithUser;
import it.niedermann.nextcloud.deck.model.User;

public class CardUserRelationshipProvider implements IRelationshipProvider {

    private Card card;
    private List<User> users;

    public CardUserRelationshipProvider(Card card, List<User> users) {
        this.card = card;
        this.users = users;
    }

    @Override
    public void insertAllNecessary(DataBaseAdapter dataBaseAdapter, long accountId) {
        if (users == null){
            return;
        }
//        Card card = dataBaseAdapter.getCardByRemoteIdDirectly(accountId, this.card.getId());
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
//        Card card = dataBaseAdapter.getCardByRemoteIdDirectly(accountId, this.card.getId());
        dataBaseAdapter.deleteJoinedUsersForCard(card.getLocalId());
    }
}
