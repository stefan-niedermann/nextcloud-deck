package it.niedermann.nextcloud.deck.persistence.sync.adapters.db;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullStack;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabaseTestUtil.createAccount;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabaseTestUtil.createBoard;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabaseTestUtil.createStack;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabaseTestUtil.createUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class DeckDatabaseTest {

    private DeckDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, DeckDatabase.class).build();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void writeAndReadAccount() {
        final Account accountToCreate = new Account();
        accountToCreate.setName("test@example.com");
        accountToCreate.setUserName("test");
        accountToCreate.setUrl("https://example.com");

        db.getAccountDao().insert(accountToCreate);
        final Account account = db.getAccountDao().getAccountByNameDirectly("test@example.com");

        assertEquals("test", account.getUserName());
        assertEquals("https://example.com", account.getUrl());
        assertEquals(Integer.valueOf(0), account.getColor());
        assertEquals(Integer.valueOf(0), account.getTextColor());
        assertEquals("0.6.4", account.getServerDeckVersion());
        assertEquals("https://example.com/index.php/avatar/test/1337", account.getAvatarUrl(1337));
        assertEquals(1, db.getAccountDao().countAccountsDirectly());
        assertNull(account.getEtag());
        assertFalse(account.isMaintenanceEnabled());
    }

    @Test
    public void writeAndReadUser() {
        final Account account = createAccount(db.getAccountDao());

        final User userToCreate = new User();
        userToCreate.setDisplayname("Test Tester");
        userToCreate.setUid("test");
        userToCreate.setAccountId(account.getId());

        db.getUserDao().insert(userToCreate);
        final User user = db.getUserDao().getUserByUidDirectly(account.getId(), "test");

        assertEquals("Test Tester", user.getDisplayname());
    }

    @Test
    public void writeAndReadBoard() {
        final Account account = createAccount(db.getAccountDao());
        final User user = createUser(db.getUserDao(), account);

        final Board boardToCreate = new Board();
        boardToCreate.setAccountId(account.getId());
        boardToCreate.setTitle("Test-Board");
        boardToCreate.setOwnerId(user.getLocalId());
        boardToCreate.setId(1337L);

        long id = db.getBoardDao().insert(boardToCreate);
        final Board board = db.getBoardDao().getBoardByIdDirectly(id);

        assertEquals("Test-Board", board.getTitle());
        assertEquals(board, db.getBoardDao().getBoardByRemoteIdDirectly(account.getId(), board.getId()));
        assertEquals(board, db.getBoardDao().getBoardForAccountByNameDirectly(account.getId(), "Test-Board"));
        assertEquals(board, db.getBoardDao().getFullBoardByLocalIdDirectly(account.getId(), board.getLocalId()).getBoard());
        assertEquals(board, db.getBoardDao().getFullBoardByRemoteIdDirectly(account.getId(), board.getId()).getBoard());
    }

    @Test
    public void writeAndReadStack() {
        final Account account = createAccount(db.getAccountDao());
        final User user = createUser(db.getUserDao(), account);
        final Board board = createBoard(db.getBoardDao(), account, user);

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

    @Test
    public void writeAndReadCard() {
        final Account account = createAccount(db.getAccountDao());
        final User user = createUser(db.getUserDao(), account);
        final Board board = createBoard(db.getBoardDao(), account, user);
        final Stack stack = createStack(db.getStackDao(), account, board);

        final Card cardToCreate = new Card();
        cardToCreate.setAccountId(account.getId());
        cardToCreate.setTitle("Test-Card");
        cardToCreate.setDescription("Holy Moly Description");
        cardToCreate.setStackId(stack.getLocalId());
        cardToCreate.setId(1234L);

        long id = db.getCardDao().insert(cardToCreate);
        final Card card = db.getCardDao().getCardByLocalIdDirectly(account.getId(), id);

        assertEquals("Test-Card", card.getTitle());
        assertEquals(card, db.getCardDao().getCardByRemoteIdDirectly(account.getId(), card.getId()));
        assertEquals(card, db.getCardDao().getFullCardByLocalIdDirectly(account.getId(), card.getLocalId()).getCard());
        assertEquals(card, db.getCardDao().getFullCardByRemoteIdDirectly(account.getId(), card.getId()).getCard());
    }
}
