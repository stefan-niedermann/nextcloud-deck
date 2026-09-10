package it.niedermann.nextcloud.deck.domain.e2e.usecases.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Set;

import it.niedermann.nextcloud.deck.domain.e2e.EndToEndTest;
import it.niedermann.nextcloud.deck.domain.e2e.EndToEndUtil;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.model.User;

public class UpdateCardEndToEndTest extends EndToEndTest {

    private VirtualDeviceAndAccount DEVICE_A;
    private VirtualDeviceAndAccount DEVICE_B;

    @BeforeEach
    public void setup() throws IOException {
        DEVICE_A = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
        DEVICE_B = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
        synchronize(DEVICE_B);
    }

    @Test
    public void testUpdateCardProperties() {
        // 1. Setup board, label, column and card
        final String boardTitle = randomUtil.randomize("UpdateTestBoard");
        final Board board = EndToEndUtil.createBoard(DEVICE_A, boardTitle);
        final String labelTitle = randomUtil.randomize("UpdateTestLabel");
        EndToEndUtil.createLabel(DEVICE_A, board, labelTitle);
        final String columnTitle = randomUtil.randomize("UpdateTestColumn");
        final Column column = EndToEndUtil.createColumn(DEVICE_A, board, columnTitle);
        final String cardTitle = randomUtil.randomize("UpdateTestCard");
        EndToEndUtil.createCard(DEVICE_A, column, cardTitle);

        // Ensure all created entities have remote IDs before updating
        synchronize(DEVICE_A);

        // 2. Prepare update data
        final String newDescription = "Updated description with some content";
        final OffsetDateTime startDate = OffsetDateTime.now(ZoneOffset.UTC).plusDays(1).withNano(0);
        final OffsetDateTime dueDate = OffsetDateTime.now(ZoneOffset.UTC).plusDays(2).withNano(0);
        final User.ID userId = new User.ID(DEVICE_A.account().username());

        // Refresh card and label to get remote IDs and updated state
        final Card cardWithRemoteId = EndToEndUtil.getCard(DEVICE_A, column, cardTitle);
        final Label labelWithRemoteId = EndToEndUtil.getLabel(DEVICE_A, board, labelTitle);

        // 3. Update the card
        final Card updatedCard = cardWithRemoteId.withDescription(newDescription)
                .withStartDate(startDate)
                .withDueDate(dueDate)
                .withLabels(Set.of(labelWithRemoteId.id()))
                .assign(userId);

        DEVICE_A.virtualDevice().getUpdateCardUseCase().execute(updatedCard).join();

        // 4. Synchronize
        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        // 5. Verify on second device
        final Board boardB = EndToEndUtil.getBoard(DEVICE_B, boardTitle);
        final Label labelB = EndToEndUtil.getLabel(DEVICE_B, boardB, labelTitle);
        final Column columnB = EndToEndUtil.getColumn(DEVICE_B, boardB, columnTitle);
        final Card cardB = EndToEndUtil.getCard(DEVICE_B, columnB, cardTitle);

        EndToEndUtil.assertCardDescription(DEVICE_B, cardB.id(), newDescription);
        EndToEndUtil.assertCardStartDate(DEVICE_B, cardB.id(), startDate);
        EndToEndUtil.assertCardDueDate(DEVICE_B, cardB.id(), dueDate);
        EndToEndUtil.assertCardHasLabel(DEVICE_B, cardB.id(), labelB.id());
        EndToEndUtil.assertCardAssignedTo(DEVICE_B, cardB.id(), userId);
    }
}
