package it.niedermann.nextcloud.deck.domain.e2e.usecases.boards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import it.niedermann.nextcloud.deck.domain.e2e.EndToEndTest;
import it.niedermann.nextcloud.deck.domain.e2e.EndToEndUtil;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.CreateBoard;
import it.niedermann.nextcloud.deck.domain.repository.MockData;

public class BoardEndToEndTest extends EndToEndTest {

    private VirtualDeviceAndAccount DEVICE_A_JOHN;
    private VirtualDeviceAndAccount DEVICE_B_JOHN;

    @BeforeEach
    public void setup() throws IOException {
        DEVICE_A_JOHN = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
        DEVICE_B_JOHN = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
    }

    @Test
    public void createBoard() {
        final var boardTitle = randomUtil.randomize(MockData.MOCK_BOARDS[0].title());
        final var createBoard = new CreateBoard(DEVICE_A_JOHN.account().id(), boardTitle);

        DEVICE_A_JOHN.virtualDevice().getAddBoardUseCase().addBoard(createBoard).join();

        EndToEndUtil.assertBoardExists(DEVICE_A_JOHN, boardTitle);

        synchronize(DEVICE_A_JOHN);
        synchronize(DEVICE_B_JOHN);

        EndToEndUtil.assertBoardExists(DEVICE_B_JOHN, boardTitle);
    }

    @Test
    public void updateBoard() {
        final var boardTitle = randomUtil.randomize(MockData.MOCK_BOARDS[1].title());
        var board = EndToEndUtil.createBoard(DEVICE_A_JOHN, boardTitle);

        synchronize(DEVICE_A_JOHN);
        board = EndToEndUtil.getBoard(DEVICE_A_JOHN, board.id());

        synchronize(DEVICE_B_JOHN);
        EndToEndUtil.assertBoardExists(DEVICE_B_JOHN, boardTitle);

        final var newTitle = randomUtil.randomize("updatedBoard");
        final var updatedBoard = new Board(board.id(), newTitle, board.color(), board.ownerId(), board.archived(), board.permissions(), board.accountId(), board.remoteId(), board.status(), board.lastModified(), board.etag());

        DEVICE_A_JOHN.virtualDevice().getUpdateBoardUseCase().execute(updatedBoard).join();

        synchronize(DEVICE_A_JOHN);
        synchronize(DEVICE_B_JOHN);

        EndToEndUtil.assertBoardExists(DEVICE_A_JOHN, newTitle);
        EndToEndUtil.assertBoardExists(DEVICE_B_JOHN, newTitle);
        EndToEndUtil.assertBoardDoesNotExist(DEVICE_B_JOHN, boardTitle);
    }

    @Test
    public void deleteBoard() {
        final var boardTitle = randomUtil.randomize(MockData.MOCK_BOARDS[2].title());
        final var board = EndToEndUtil.createBoard(DEVICE_A_JOHN, boardTitle);

        synchronize(DEVICE_A_JOHN);
        synchronize(DEVICE_B_JOHN);
        EndToEndUtil.assertBoardExists(DEVICE_B_JOHN, boardTitle);

        DEVICE_A_JOHN.virtualDevice().getDeleteBoardUseCase().execute(board.id()).join();

        synchronize(DEVICE_A_JOHN);
        synchronize(DEVICE_B_JOHN);

        EndToEndUtil.assertBoardDoesNotExist(DEVICE_A_JOHN, boardTitle);
        EndToEndUtil.assertBoardDoesNotExist(DEVICE_B_JOHN, boardTitle);
    }

}
