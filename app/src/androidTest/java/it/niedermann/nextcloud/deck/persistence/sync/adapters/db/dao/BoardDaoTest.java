package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.List;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabaseTestUtil;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabaseTestUtil.createAccount;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabaseTestUtil.createBoard;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabaseTestUtil.createUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class BoardDaoTest extends AbstractDaoTest {

    @Test
    public void writeAndReadBoard() {
        final Account account = createAccount(db.getAccountDao());
        final User user = DeckDatabaseTestUtil.createUser(db.getUserDao(), account);

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
    public void testGetBoardsForAccount() throws InterruptedException {
        final Account account = createAccount(db.getAccountDao());
        final User owner = createUser(db.getUserDao(), account);
        final Board boardVisible1= createBoard(db.getBoardDao(), account, owner);
        final Board boardVisible2= createBoard(db.getBoardDao(), account, owner);
        final Board boardVisible3= createBoard(db.getBoardDao(), account, owner);
        final Board boardInVisible1= createBoard(db.getBoardDao(), account, owner);
        boardInVisible1.setDeletedAt(Instant.now());
        final Board boardInVisible2= createBoard(db.getBoardDao(), account, owner);
        boardInVisible2.setStatus(3);
        final Board boardInVisible3= createBoard(db.getBoardDao(), account, owner);
        boardInVisible3.setStatusEnum(DBStatus.LOCAL_DELETED);
        final Board boardVisibleArchived= createBoard(db.getBoardDao(), account, owner);
        boardVisibleArchived.setArchived(true);
        db.getBoardDao().update(boardInVisible1, boardInVisible2, boardInVisible3, boardVisibleArchived);

        final List<Board> boards = DeckDatabaseTestUtil.getOrAwaitValue(db.getBoardDao().getBoardsForAccount(account.getId()));
        assertEquals(4, boards.size());
        assertTrue(boards.stream().anyMatch((board -> boardVisible1.getLocalId().equals(board.getLocalId()))));
        assertTrue(boards.stream().anyMatch((board -> boardVisible2.getLocalId().equals(board.getLocalId()))));
        assertTrue(boards.stream().anyMatch((board -> boardVisible3.getLocalId().equals(board.getLocalId()))));
        assertTrue(boards.stream().anyMatch((board -> boardVisibleArchived.getLocalId().equals(board.getLocalId()))));
        assertFalse(boards.stream().anyMatch((board -> boardInVisible1.getLocalId().equals(board.getLocalId()))));
        assertFalse(boards.stream().anyMatch((board -> boardInVisible2.getLocalId().equals(board.getLocalId()))));
        assertFalse(boards.stream().anyMatch((board -> boardInVisible3.getLocalId().equals(board.getLocalId()))));
    }

    @Test
    public void testGetArchivedBoardsForAccount() throws InterruptedException {
        final Account account = createAccount(db.getAccountDao());
        final User owner = createUser(db.getUserDao(), account);
        final Board boardVisible1= createBoard(db.getBoardDao(), account, owner);
        final Board boardVisible2= createBoard(db.getBoardDao(), account, owner);
        final Board boardVisible3= createBoard(db.getBoardDao(), account, owner);
        final Board boardInVisible1= createBoard(db.getBoardDao(), account, owner);
        boardInVisible1.setDeletedAt(Instant.now());
        boardInVisible1.setArchived(true);
        final Board boardInVisible2= createBoard(db.getBoardDao(), account, owner);
        boardInVisible2.setStatus(3);
        boardInVisible2.setArchived(true);
        final Board boardInVisible3= createBoard(db.getBoardDao(), account, owner);
        boardInVisible3.setStatusEnum(DBStatus.LOCAL_DELETED);
        boardInVisible3.setArchived(true);
        final Board boardVisibleArchived= createBoard(db.getBoardDao(), account, owner);
        boardVisibleArchived.setArchived(true);
        db.getBoardDao().update(boardInVisible1, boardInVisible2, boardInVisible3, boardVisibleArchived);

        final List<Board> boards = DeckDatabaseTestUtil.getOrAwaitValue(db.getBoardDao().getArchivedBoardsForAccount(account.getId()));
        assertEquals(1, boards.size());
        assertFalse(boards.stream().anyMatch((board -> boardVisible1.getLocalId().equals(board.getLocalId()))));
        assertFalse(boards.stream().anyMatch((board -> boardVisible2.getLocalId().equals(board.getLocalId()))));
        assertFalse(boards.stream().anyMatch((board -> boardVisible3.getLocalId().equals(board.getLocalId()))));
        assertTrue(boards.stream().anyMatch((board -> boardVisibleArchived.getLocalId().equals(board.getLocalId()))));
        assertFalse(boards.stream().anyMatch((board -> boardInVisible1.getLocalId().equals(board.getLocalId()))));
        assertFalse(boards.stream().anyMatch((board -> boardInVisible2.getLocalId().equals(board.getLocalId()))));
        assertFalse(boards.stream().anyMatch((board -> boardInVisible3.getLocalId().equals(board.getLocalId()))));
    }
}
