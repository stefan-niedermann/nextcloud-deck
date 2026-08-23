package it.niedermann.nextcloud.deck.domain.e2e.usecases.state;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import it.niedermann.nextcloud.deck.domain.e2e.EndToEndTest;
import it.niedermann.nextcloud.deck.domain.e2e.EndToEndUtil;

public class StateEndToEndTest extends EndToEndTest {

    private VirtualDeviceAndAccount DEVICE_A;

    @BeforeEach
    @Override
    public void setup() throws IOException {
        super.setup();

        DEVICE_A = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
    }

    @Test
    public void testAccountState() {
        final var setCurrentAccountUseCase = DEVICE_A.virtualDevice().getSetCurrentAccountUseCase();
        final var getCurrentAccountUseCase = DEVICE_A.virtualDevice().getGetCurrentAccountUseCase();

        setCurrentAccountUseCase.execute(DEVICE_A.account().id()).join();

        final var currentAccountId = getCurrentAccountUseCase.execute().join();

        Assertions.assertEquals(DEVICE_A.account().id(), currentAccountId);
    }

    @Test
    public void testBoardState() {
        final var board = EndToEndUtil.createBoard(DEVICE_A, randomUtil.randomize("boardForState"));
        final var setCurrentBoardUseCase = DEVICE_A.virtualDevice().getSetCurrentBoardUseCase();
        final var getCurrentBoardUseCase = DEVICE_A.virtualDevice().getGetCurrentBoardUseCase();

        setCurrentBoardUseCase.execute(DEVICE_A.account().id(), board.id()).join();

        final var currentBoardId = getCurrentBoardUseCase.execute(DEVICE_A.account().id()).join();

        Assertions.assertEquals(board.id(), currentBoardId);
    }
}
