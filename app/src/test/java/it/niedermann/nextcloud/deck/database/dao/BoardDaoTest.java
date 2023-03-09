package it.niedermann.nextcloud.deck.database.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static it.niedermann.nextcloud.deck.database.DeckDatabaseTestUtil.createAccount;
import static it.niedermann.nextcloud.deck.database.DeckDatabaseTestUtil.createBoard;
import static it.niedermann.nextcloud.deck.database.DeckDatabaseTestUtil.createCard;
import static it.niedermann.nextcloud.deck.database.DeckDatabaseTestUtil.createStack;
import static it.niedermann.nextcloud.deck.database.DeckDatabaseTestUtil.createUser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.time.Instant;

import it.niedermann.nextcloud.deck.TestUtil;
import it.niedermann.nextcloud.deck.database.DeckDatabaseTestUtil;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;

@RunWith(RobolectricTestRunner.class)
public class BoardDaoTest extends AbstractDaoTest {

    @Test
    public void writeAndReadBoard() {
        final var account = createAccount(db.getAccountDao());
        final var user = DeckDatabaseTestUtil.createUser(db.getUserDao(), account);

        final var boardToCreate = new Board();
        boardToCreate.setAccountId(account.getId());
        boardToCreate.setTitle("Test-Board");
        boardToCreate.setOwnerId(user.getLocalId());
        boardToCreate.setId(1337L);

        long id = db.getBoardDao().insert(boardToCreate);
        final var board = db.getBoardDao().getBoardByLocalIdDirectly(id);

        assertEquals("Test-Board", board.getTitle());
        assertEquals(board, db.getBoardDao().getBoardByRemoteIdDirectly(account.getId(), board.getId()));
        assertEquals(board, db.getBoardDao().getBoardForAccountByNameDirectly(account.getId(), "Test-Board"));
        assertEquals(board, db.getBoardDao().getFullBoardByLocalIdDirectly(account.getId(), board.getLocalId()).getBoard());
        assertEquals(board, db.getBoardDao().getFullBoardByRemoteIdDirectly(account.getId(), board.getId()).getBoard());
    }

    @Test
    public void testGetNotDeletedBoardsForAccount() throws InterruptedException {
        final var account = createAccount(db.getAccountDao());
        final var owner = createUser(db.getUserDao(), account);
        final var board1 = createBoard(db.getBoardDao(), account, owner);
        final var board2 = createBoard(db.getBoardDao(), account, owner);
        final var board3 = createBoard(db.getBoardDao(), account, owner);
        final var board5 = createBoard(db.getBoardDao(), account, owner);
        board5.setDeletedAt(Instant.now());
        board5.setArchived(true);
        final var board6 = createBoard(db.getBoardDao(), account, owner);
        board6.setStatus(3);
        board6.setArchived(true);
        final var board7 = createBoard(db.getBoardDao(), account, owner);
        board7.setStatusEnum(DBStatus.LOCAL_DELETED);
        board7.setArchived(true);
        final var board4 = createBoard(db.getBoardDao(), account, owner);
        board4.setArchived(true);
        db.getBoardDao().update(board5, board6, board7, board4);

        final var boards = TestUtil.getOrAwaitValue(db.getBoardDao().getNotDeletedBoards(account.getId(), 1));
        assertEquals(1, boards.size());
        assertFalse(boards.stream().anyMatch((board -> board1.getLocalId().equals(board.getLocalId()))));
        assertFalse(boards.stream().anyMatch((board -> board2.getLocalId().equals(board.getLocalId()))));
        assertFalse(boards.stream().anyMatch((board -> board3.getLocalId().equals(board.getLocalId()))));
        assertTrue(boards.stream().anyMatch((board -> board4.getLocalId().equals(board.getLocalId()))));
        assertFalse(boards.stream().anyMatch((board -> board5.getLocalId().equals(board.getLocalId()))));
        assertFalse(boards.stream().anyMatch((board -> board6.getLocalId().equals(board.getLocalId()))));
        assertFalse(boards.stream().anyMatch((board -> board7.getLocalId().equals(board.getLocalId()))));
    }

    @Test
    public void testGetNonArchivedBoardsForAccount() throws InterruptedException {
        final var account = createAccount(db.getAccountDao());
        final var owner = createUser(db.getUserDao(), account);
        final var board1 = createBoard(db.getBoardDao(), account, owner);
        final var board2 = createBoard(db.getBoardDao(), account, owner);
        final var board3 = createBoard(db.getBoardDao(), account, owner);
        final var board5 = createBoard(db.getBoardDao(), account, owner);
        board5.setDeletedAt(Instant.now());
        board5.setArchived(true);
        final var board6 = createBoard(db.getBoardDao(), account, owner);
        board6.setStatus(3);
        board6.setArchived(true);
        final var board7 = createBoard(db.getBoardDao(), account, owner);
        board7.setStatusEnum(DBStatus.LOCAL_DELETED);
        board7.setArchived(true);
        final var board4 = createBoard(db.getBoardDao(), account, owner);
        board4.setArchived(true);
        db.getBoardDao().update(board5, board6, board7, board4);

        final var boards = TestUtil.getOrAwaitValue(db.getBoardDao().getNotDeletedBoards(account.getId(), 0));
        assertEquals(3, boards.size());
        assertTrue(boards.stream().anyMatch((board -> board1.getLocalId().equals(board.getLocalId()))));
        assertTrue(boards.stream().anyMatch((board -> board2.getLocalId().equals(board.getLocalId()))));
        assertTrue(boards.stream().anyMatch((board -> board3.getLocalId().equals(board.getLocalId()))));
        assertFalse(boards.stream().anyMatch((board -> board4.getLocalId().equals(board.getLocalId()))));
        assertFalse(boards.stream().anyMatch((board -> board5.getLocalId().equals(board.getLocalId()))));
        assertFalse(boards.stream().anyMatch((board -> board6.getLocalId().equals(board.getLocalId()))));
        assertFalse(boards.stream().anyMatch((board -> board7.getLocalId().equals(board.getLocalId()))));
    }

    @Test
    public void testGetLocalBoardIdByCardRemoteIdAndAccountId() throws InterruptedException {
        final var account = createAccount(db.getAccountDao());
        final var owner = createUser(db.getUserDao(), account);
        final var board = createBoard(db.getBoardDao(), account, owner);
        final var stack = createStack(db.getStackDao(), account, board);
        final var card = createCard(db.getCardDao(), account, stack);

        assertEquals(board.getLocalId(), TestUtil.getOrAwaitValue(db.getBoardDao().getLocalBoardIdByCardRemoteIdAndAccountId(card.getId(), account.getId())));
    }

    @Test
    public void testCountArchivedBoards() throws InterruptedException {
        final var account = createAccount(db.getAccountDao());
        final var owner = createUser(db.getUserDao(), account);
        final var board1 = createBoard(db.getBoardDao(), account, owner);
        final var board2 = createBoard(db.getBoardDao(), account, owner);
        final var board3 = createBoard(db.getBoardDao(), account, owner);
        final var board5 = createBoard(db.getBoardDao(), account, owner);
        board5.setDeletedAt(Instant.now());
        board5.setArchived(true);
        final var board6 = createBoard(db.getBoardDao(), account, owner);
        board6.setStatus(3);
        board6.setArchived(true);
        final var board7 = createBoard(db.getBoardDao(), account, owner);
        board7.setStatusEnum(DBStatus.LOCAL_DELETED);
        board7.setArchived(true);
        final var board4 = createBoard(db.getBoardDao(), account, owner);
        board4.setArchived(true);
        db.getBoardDao().update(board5, board6, board7, board4);

        assertEquals(Integer.valueOf(1), TestUtil.getOrAwaitValue(db.getBoardDao().countArchivedBoards(account.getId())));
    }
}
