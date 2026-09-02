package it.niedermann.nextcloud.deck.domain.e2e.usecases.sync;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.FlowAdapters;

import java.io.IOException;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.e2e.EndToEndTest;
import it.niedermann.nextcloud.deck.domain.e2e.EndToEndUtil;
import it.niedermann.nextcloud.deck.domain.repository.MockData;

public class SyncEndToEndTest extends EndToEndTest {

    private VirtualDeviceAndAccount DEVICE_A;
    private VirtualDeviceAndAccount DEVICE_B;

    @BeforeEach
    public void setup() throws IOException {
        DEVICE_A = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
        DEVICE_B = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
    }

    @Test
    public void testSync() {
        final var scheduleSyncUseCase = DEVICE_A.virtualDevice().getScheduleSyncUseCase();
        final var getSyncStatusUseCase = DEVICE_A.virtualDevice().getGetSyncStatusUseCase();

        // Schedule sync
        final var syncResult = Flowable.fromPublisher(FlowAdapters.toPublisher(scheduleSyncUseCase.execute(DEVICE_A.account().id())))
                .blockingLast();
        Assertions.assertNotNull(syncResult);

        // Check sync status
        final var syncStatusOptional = Flowable.fromPublisher(FlowAdapters.toPublisher(getSyncStatusUseCase.execute(DEVICE_A.account().id())))
                .blockingFirst();

        Assertions.assertTrue(syncStatusOptional.isPresent());
        Assertions.assertEquals(DEVICE_A.account().id(), syncStatusOptional.get().account().id());
    }

    @Test
    public void testFullMockDataSync() {
        EndToEndUtil.setupMockData(DEVICE_A);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        // Verify that the first board from MockData exists on Device B
        EndToEndUtil.assertBoardExists(DEVICE_B, MockData.MOCK_BOARDS[0].title());
        EndToEndUtil.assertBoardExists(DEVICE_B, MockData.MOCK_BOARDS[9].title());
    }
}
