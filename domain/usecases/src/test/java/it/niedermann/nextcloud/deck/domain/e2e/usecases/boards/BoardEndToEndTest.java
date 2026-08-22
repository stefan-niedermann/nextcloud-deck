package it.niedermann.nextcloud.deck.domain.e2e.usecases.boards;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.FlowAdapters;

import java.io.IOException;
import java.util.Objects;

import io.reactivex.rxjava3.core.Maybe;
import it.niedermann.nextcloud.deck.domain.e2e.EndToEndTest;
import it.niedermann.nextcloud.deck.domain.model.CreateBoard;

public class BoardEndToEndTest extends EndToEndTest {

    private VirtualDeviceAndAccount DEVICE_A_JOHN;
    private VirtualDeviceAndAccount DEVICE_B_JOHN;

    @BeforeEach
    @Override
    public void setup() throws IOException {
        super.setup();

        DEVICE_A_JOHN = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
        DEVICE_B_JOHN = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
    }

    @Test
    public void createBoard() {
        final var boardTitle = randomUtil.randomize("createBoard");
        final var createBoard = new CreateBoard(DEVICE_A_JOHN.account().id(), boardTitle);

        DEVICE_A_JOHN.virtualDevice().getAddBoardUseCase().addBoard(createBoard).join();

        assertBoardExists(DEVICE_A_JOHN, boardTitle);

//        synchronize(DEVICE_A_JOHN);
//        synchronize(DEVICE_B_JOHN);
//
//        assertBoardExists(DEVICE_B_JOHN, boardTitle);
    }

    private void assertBoardExists(VirtualDeviceAndAccount virtualDeviceAndAccount, String title) {
        final var virtualDevice = virtualDeviceAndAccount.virtualDevice();
        final var account = virtualDeviceAndAccount.account();
        final var listBoardsUseCase = virtualDevice.getListBoardsUseCase();
        final var boardsOnDeviceB = Maybe.fromPublisher(FlowAdapters.toPublisher(listBoardsUseCase.execute(account.id()))).blockingGet();
        Assertions.assertTrue(boardsOnDeviceB.stream().anyMatch(board -> Objects.equals(board.title(), title)), "Should contain board \"" + title + "\" in account \"" + account.accountName() + "\" on device \"" + virtualDevice.getDeviceName() + "\"");
    }
}
