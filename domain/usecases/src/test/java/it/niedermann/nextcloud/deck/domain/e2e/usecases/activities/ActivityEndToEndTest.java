package it.niedermann.nextcloud.deck.domain.e2e.usecases.activities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.FlowAdapters;

import java.io.IOException;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.e2e.EndToEndTest;
import it.niedermann.nextcloud.deck.domain.e2e.EndToEndUtil;
import it.niedermann.nextcloud.deck.domain.model.Board;

public class ActivityEndToEndTest extends EndToEndTest {

    private VirtualDeviceAndAccount DEVICE_A;
    private VirtualDeviceAndAccount DEVICE_B;
    private Board boardA;

    @BeforeEach
    @Override
    public void setup() throws IOException {
        super.setup();

        DEVICE_A = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
        DEVICE_B = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
        boardA = EndToEndUtil.createBoard(DEVICE_A, randomUtil.randomize("boardForActivity"));
    }

    @Test
    public void testListActivities() {
        final var column = EndToEndUtil.createColumn(DEVICE_A, boardA, randomUtil.randomize("columnForActivity"));
        final var card = EndToEndUtil.createCard(DEVICE_A, column, randomUtil.randomize("cardForActivity"));
        
        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        final var listActivityUseCaseB = DEVICE_B.virtualDevice().getListActivityUseCase();
        final var activitiesB = Flowable.fromPublisher(FlowAdapters.toPublisher(listActivityUseCaseB.execute(card.id())))
                .blockingFirst();

        Assertions.assertNotNull(activitiesB);
    }
}
