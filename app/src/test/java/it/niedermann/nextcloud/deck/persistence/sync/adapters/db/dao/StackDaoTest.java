package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabaseTestUtil;

@RunWith(RobolectricTestRunner.class)
public class StackDaoTest extends AbstractDaoTest {

    @Test
    public void writeAndReadStack() {
        final var account = DeckDatabaseTestUtil.createAccount(db.getAccountDao());
        final var user = DeckDatabaseTestUtil.createUser(db.getUserDao(), account);
        final var board = DeckDatabaseTestUtil.createBoard(db.getBoardDao(), account, user);

        final var stackToCreate = new Stack();
        stackToCreate.setAccountId(account.getId());
        stackToCreate.setTitle("Test-Stack");
        stackToCreate.setBoardId(board.getLocalId());
        stackToCreate.setId(1337L);

        long id = db.getStackDao().insert(stackToCreate);
        final var stack = db.getStackDao().getStackByLocalIdDirectly(id);

        assertEquals("Test-Stack", stack.getTitle());
        assertEquals(stack, db.getStackDao().getFullStackByLocalIdDirectly(id).getStack());
        assertEquals(stack, db.getStackDao().getFullStackByRemoteIdDirectly(account.getId(), board.getLocalId(), stack.getId()).getStack());
        final var stacksOfBoard = db.getStackDao().getFullStacksForBoardDirectly(account.getId(), board.getLocalId());
        assertEquals(1, stacksOfBoard.size());
        assertEquals(stack, stacksOfBoard.get(0).getStack());
    }
}
