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
    private Board boardA;

    @BeforeEach
    @Override
    public void setup() throws IOException {
        super.setup();

        DEVICE_A = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
        boardA = EndToEndUtil.createBoard(DEVICE_A, randomUtil.randomize("boardForActivity"));
    }

    @Test
    public void testListActivities() {
        final var column = EndToEndUtil.createColumn(DEVICE_A, boardA, randomUtil.randomize("columnForActivity"));
        final var card = EndToEndUtil.createCard(DEVICE_A, column, randomUtil.randomize("cardForActivity"));

        // Ensure the card is on the server so that the activity is generated
        synchronize(DEVICE_A);

        final var listActivityUseCase = DEVICE_A.virtualDevice().getListActivityUseCase();
        final var testSubscriber = Flowable.fromPublisher(FlowAdapters.toPublisher(listActivityUseCase.execute(card.id())))
                .test();

        // The UseCase is expected to look up online and then update the database.
        // We wait for the first emission. If it's empty, we wait for the second one.
        testSubscriber.awaitCount(1);
        if (testSubscriber.values().get(0).isEmpty()) {
            testSubscriber.awaitCount(2);
        }

        final var emissions = testSubscriber.values();

        // Depending on the state, there should be either one or two emissions (e.g. empty cache then remote data)
        Assertions.assertFalse(emissions.isEmpty(), "Expected at least one emission");
        Assertions.assertTrue(emissions.size() <= 2, "Expected 1 or 2 emissions, but got " + emissions.size());

        final var activities = emissions.get(emissions.size() - 1);
        Assertions.assertNotNull(activities);
        Assertions.assertFalse(activities.isEmpty(), "Activities should be fetched and added to the database");
    }
}
