package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabaseTestUtil;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class StackDaoTest extends AbstractDaoTest {

    @Test
    public void writeAndReadStack() {
        final Account account = DeckDatabaseTestUtil.createAccount(db.getAccountDao());
        final User user = DeckDatabaseTestUtil.createUser(db.getUserDao(), account);
        final Board board = DeckDatabaseTestUtil.createBoard(db.getBoardDao(), account, user);

        final Stack stackToCreate = new Stack();
        stackToCreate.setAccountId(account.getId());
        stackToCreate.setTitle("Test-Stack");
        stackToCreate.setBoardId(board.getLocalId());
        stackToCreate.setId(1337L);

        long id = db.getStackDao().insert(stackToCreate);
        final Stack stack = db.getStackDao().getStackByLocalIdDirectly(id);

        assertEquals("Test-Stack", stack.getTitle());
        assertEquals(stack, db.getStackDao().getFullStackByLocalIdDirectly(id).getStack());
        assertEquals(stack, db.getStackDao().getFullStackByRemoteIdDirectly(account.getId(), board.getLocalId(), stack.getId()).getStack());
        final List<FullStack> stacksOfBoard = db.getStackDao().getFullStacksForBoardDirectly(account.getId(), board.getLocalId());
        assertEquals(1, stacksOfBoard.size());
        assertEquals(stack, stacksOfBoard.get(0).getStack());
    }
}
