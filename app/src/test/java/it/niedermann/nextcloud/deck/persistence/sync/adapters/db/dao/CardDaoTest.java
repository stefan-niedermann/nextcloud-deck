package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabaseTestUtil;

@RunWith(RobolectricTestRunner.class)
public class CardDaoTest extends AbstractDaoTest {

    private Account account;
    private User user;
    private Board board;

    @Before
    public void setupAccount() {
        account = DeckDatabaseTestUtil.createAccount(db.getAccountDao());
        user = DeckDatabaseTestUtil.createUser(db.getUserDao(), account);
        board = DeckDatabaseTestUtil.createBoard(db.getBoardDao(), account, user);
    }

    @Test
    public void writeAndReadCard() {
        final var stack = DeckDatabaseTestUtil.createStack(db.getStackDao(), account, board);

        final var cardToCreate = new Card();
        cardToCreate.setAccountId(account.getId());
        cardToCreate.setTitle("Test-Card");
        cardToCreate.setDescription("Holy Moly Description");
        cardToCreate.setStackId(stack.getLocalId());
        cardToCreate.setId(1234L);

        long id = db.getCardDao().insert(cardToCreate);
        final var card = db.getCardDao().getCardByLocalIdDirectly(account.getId(), id);

        assertEquals("Test-Card", card.getTitle());
        assertEquals(card, db.getCardDao().getCardByRemoteIdDirectly(account.getId(), card.getId()));
        assertEquals(card, db.getCardDao().getFullCardByLocalIdDirectly(account.getId(), card.getLocalId()).getCard());
        assertEquals(card, db.getCardDao().getFullCardByRemoteIdDirectly(account.getId(), card.getId()).getCard());

        card.setTitle("Changed Title");

        db.getCardDao().update(card);

        assertEquals("Changed Title", db.getCardDao().getCardByLocalIdDirectly(account.getId(), id).getTitle());
    }

    @Test
    public void testGetLocallyChangedCards() {
        final var stack1 = DeckDatabaseTestUtil.createStack(db.getStackDao(), account, board);
        final var stack2 = DeckDatabaseTestUtil.createStack(db.getStackDao(), account, board);
        final var card1 = DeckDatabaseTestUtil.createCard(db.getCardDao(), account, stack1);
        final var card2 = DeckDatabaseTestUtil.createCard(db.getCardDao(), account, stack1);
        final var card3 = DeckDatabaseTestUtil.createCard(db.getCardDao(), account, stack2);
        final var card4 = DeckDatabaseTestUtil.createCard(db.getCardDao(), account, stack2);
        final var card5 = DeckDatabaseTestUtil.createCard(db.getCardDao(), account, stack2);

        card1.setStatusEnum(DBStatus.LOCAL_EDITED);
        card2.setStatusEnum(DBStatus.LOCAL_MOVED);
        card3.setStatusEnum(DBStatus.LOCAL_EDITED_SILENT);
        card4.setStatusEnum(DBStatus.LOCAL_DELETED);
        card5.setStatusEnum(DBStatus.UP_TO_DATE);
        db.getCardDao().update(card1, card2, card3, card4, card5);

        final var locallyChangedCards = db.getCardDao().getLocallyChangedCardsDirectly(account.getId());
        assertEquals(4, locallyChangedCards.size());
        assertTrue(locallyChangedCards.stream().anyMatch((fullCard -> fullCard.getCard().equals(card1))));
        assertTrue(locallyChangedCards.stream().anyMatch((fullCard -> fullCard.getCard().equals(card2))));
        assertTrue(locallyChangedCards.stream().anyMatch((fullCard -> fullCard.getCard().equals(card3))));
        assertTrue(locallyChangedCards.stream().anyMatch((fullCard -> fullCard.getCard().equals(card4))));
        assertFalse(locallyChangedCards.stream().anyMatch((fullCard -> fullCard.getCard().equals(card5))));

        final var locallyChangedCardsOfStack1 = db.getCardDao().getLocallyChangedCardsByLocalStackIdDirectly(account.getId(), stack1.getLocalId());
        assertEquals(2, locallyChangedCardsOfStack1.size());
        assertTrue(locallyChangedCardsOfStack1.stream().anyMatch((fullCard -> fullCard.getCard().equals(card1))));
        assertTrue(locallyChangedCardsOfStack1.stream().anyMatch((fullCard -> fullCard.getCard().equals(card2))));
        assertFalse(locallyChangedCardsOfStack1.stream().anyMatch((fullCard -> fullCard.getCard().equals(card3))));
        assertFalse(locallyChangedCardsOfStack1.stream().anyMatch((fullCard -> fullCard.getCard().equals(card4))));
        assertFalse(locallyChangedCardsOfStack1.stream().anyMatch((fullCard -> fullCard.getCard().equals(card5))));

        final var locallyChangedCardsOfStack2 = db.getCardDao().getLocallyChangedCardsByLocalStackIdDirectly(account.getId(), stack2.getLocalId());
        assertEquals(2, locallyChangedCardsOfStack2.size());
        assertFalse(locallyChangedCardsOfStack2.stream().anyMatch((fullCard -> fullCard.getCard().equals(card1))));
        assertFalse(locallyChangedCardsOfStack2.stream().anyMatch((fullCard -> fullCard.getCard().equals(card2))));
        assertTrue(locallyChangedCardsOfStack2.stream().anyMatch((fullCard -> fullCard.getCard().equals(card3))));
        assertTrue(locallyChangedCardsOfStack2.stream().anyMatch((fullCard -> fullCard.getCard().equals(card4))));
        assertFalse(locallyChangedCardsOfStack2.stream().anyMatch((fullCard -> fullCard.getCard().equals(card5))));
    }

    @Test
    public void testGetFullCardsForStackDirectly() {
        final var stack1 = DeckDatabaseTestUtil.createStack(db.getStackDao(), account, board);
        final var stack2 = DeckDatabaseTestUtil.createStack(db.getStackDao(), account, board);
        final var card1 = DeckDatabaseTestUtil.createCard(db.getCardDao(), account, stack1);
        final var card2 = DeckDatabaseTestUtil.createCard(db.getCardDao(), account, stack1);
        final var card3 = DeckDatabaseTestUtil.createCard(db.getCardDao(), account, stack2);
        final var card4 = DeckDatabaseTestUtil.createCard(db.getCardDao(), account, stack2);
        final var card5 = DeckDatabaseTestUtil.createCard(db.getCardDao(), account, stack2);

        final var locallyChangedCardsOfStack1 = db.getCardDao().getFullCardsForStackDirectly(account.getId(), stack1.getLocalId());
        assertEquals(2, locallyChangedCardsOfStack1.size());
        assertTrue(locallyChangedCardsOfStack1.stream().anyMatch((fullCard -> fullCard.getCard().equals(card1))));
        assertTrue(locallyChangedCardsOfStack1.stream().anyMatch((fullCard -> fullCard.getCard().equals(card2))));
        assertFalse(locallyChangedCardsOfStack1.stream().anyMatch((fullCard -> fullCard.getCard().equals(card3))));
        assertFalse(locallyChangedCardsOfStack1.stream().anyMatch((fullCard -> fullCard.getCard().equals(card4))));
        assertFalse(locallyChangedCardsOfStack1.stream().anyMatch((fullCard -> fullCard.getCard().equals(card5))));

        final var locallyChangedCardsOfStack2 = db.getCardDao().getFullCardsForStackDirectly(account.getId(), stack2.getLocalId());
        assertEquals(3, locallyChangedCardsOfStack2.size());
        assertFalse(locallyChangedCardsOfStack2.stream().anyMatch((fullCard -> fullCard.getCard().equals(card1))));
        assertFalse(locallyChangedCardsOfStack2.stream().anyMatch((fullCard -> fullCard.getCard().equals(card2))));
        assertTrue(locallyChangedCardsOfStack2.stream().anyMatch((fullCard -> fullCard.getCard().equals(card3))));
        assertTrue(locallyChangedCardsOfStack2.stream().anyMatch((fullCard -> fullCard.getCard().equals(card4))));
        assertTrue(locallyChangedCardsOfStack2.stream().anyMatch((fullCard -> fullCard.getCard().equals(card5))));
    }

    @Test
    public void testGetHighestStackOrder() {
        final var stack = DeckDatabaseTestUtil.createStack(db.getStackDao(), account, board);
        final var card1 = DeckDatabaseTestUtil.createCard(db.getCardDao(), account, stack);
        final var card2 = DeckDatabaseTestUtil.createCard(db.getCardDao(), account, stack);
        final var card3 = DeckDatabaseTestUtil.createCard(db.getCardDao(), account, stack);
        final var card4 = DeckDatabaseTestUtil.createCard(db.getCardDao(), account, stack);
        card1.setOrder(20);
        card2.setOrder(10);
        card3.setOrder(50);
        card4.setOrder(40);
        db.getCardDao().update(card1, card2, card3, card4);
        assertEquals(Integer.valueOf(50), db.getCardDao().getHighestOrderInStack(stack.getLocalId()));
    }

    @Test
    public void testGetLocalStackIdByLocalCardId() {
        final var stack = DeckDatabaseTestUtil.createStack(db.getStackDao(), account, board);
        final var card = DeckDatabaseTestUtil.createCard(db.getCardDao(), account, stack);
        assertEquals(stack.getLocalId(), card.getLocalId());
    }
}
