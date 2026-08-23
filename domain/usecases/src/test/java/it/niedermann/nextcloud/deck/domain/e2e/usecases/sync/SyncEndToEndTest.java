package it.niedermann.nextcloud.deck.domain.e2e.usecases.sync;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.FlowAdapters;

import java.io.IOException;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.e2e.EndToEndTest;

public class SyncEndToEndTest extends EndToEndTest {

    private VirtualDeviceAndAccount DEVICE_A;

    @BeforeEach
    @Override
    public void setup() throws IOException {
        super.setup();

        DEVICE_A = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
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
}
