package it.niedermann.nextcloud.deck.domain.e2e.usecases.boards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import it.niedermann.nextcloud.deck.domain.e2e.EndToEndTest;
import it.niedermann.nextcloud.deck.domain.e2e.EndToEndUtil;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.User;

public class BoardShareEndToEndTest extends EndToEndTest {

    private VirtualDeviceAndAccount DEVICE_A;
    private VirtualDeviceAndAccount DEVICE_B;
    private Board boardA;

    @BeforeEach
    public void setup() throws IOException {
        DEVICE_A = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "userA");
        DEVICE_B = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "userB");

        boardA = EndToEndUtil.createBoard(DEVICE_A, randomUtil.randomize("boardToShare"));
    }

    @Test
    public void testShareBoard() {
        final var addBoardShareUseCase = DEVICE_A.virtualDevice().getAddBoardShareUseCase();

        final var permissions = new Board.Permissions(true, true, true, true);
        addBoardShareUseCase.execute(boardA.id(), new User.ID(DEVICE_B.account().username()), permissions).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertBoardExists(DEVICE_B, boardA.title());
    }
}
