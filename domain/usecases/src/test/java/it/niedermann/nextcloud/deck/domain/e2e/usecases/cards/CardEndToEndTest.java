package it.niedermann.nextcloud.deck.domain.e2e.usecases.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import it.niedermann.nextcloud.deck.domain.e2e.EndToEndTest;
import it.niedermann.nextcloud.deck.domain.e2e.EndToEndUtil;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateCard;
import it.niedermann.nextcloud.deck.domain.model.User;

public class CardEndToEndTest extends EndToEndTest {

    private VirtualDeviceAndAccount DEVICE_A;
    private Board boardA;
    private Column columnA;

    @BeforeEach
    @Override
    public void setup() throws IOException {
        super.setup();

        DEVICE_A = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "userA");

        // Use the default board created by the server
        boardA = EndToEndUtil.getBoard(DEVICE_A, "Welcome to Nextcloud Deck!");
        columnA = EndToEndUtil.getColumn(DEVICE_A, boardA, "To Do");
    }

    @Test
    public void createCard() {
        final var cardTitle = randomUtil.randomize("createCard");
        final var createCard = new CreateCard(columnA.id(), cardTitle);

        DEVICE_A.virtualDevice().getAddCardUseCase().execute(createCard).join();

        EndToEndUtil.assertCardExists(DEVICE_A, columnA, cardTitle);
    }

    @Test
    public void updateCard() {
        final var cardTitle = randomUtil.randomize("cardToUpdate");
        final var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);

        final var newTitle = randomUtil.randomize("updatedCard");
        final var updatedCard = card.withTitle(newTitle);

        DEVICE_A.virtualDevice().getUpdateCardUseCase().execute(updatedCard).join();

        EndToEndUtil.assertCardExists(DEVICE_A, columnA, newTitle);
        EndToEndUtil.assertCardDoesNotExist(DEVICE_A, columnA, cardTitle);
    }

    @Test
    public void deleteCard() {
        final var cardTitle = randomUtil.randomize("cardToDelete");
        final var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);

        DEVICE_A.virtualDevice().getDeleteCardUseCase().execute(card.id()).join();

        EndToEndUtil.assertCardDoesNotExist(DEVICE_A, columnA, cardTitle);
    }

    @Test
    public void moveCard() {
        final var columnBTitle = "In Progress";
        final var columnB = EndToEndUtil.getColumn(DEVICE_A, boardA, columnBTitle);
        final var cardTitle = randomUtil.randomize("cardToMove");
        final var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);

        DEVICE_A.virtualDevice().getMoveCardUseCase().execute(card.id(), columnB.id(), 0).join();

        EndToEndUtil.assertCardExists(DEVICE_A, columnB, cardTitle);
        EndToEndUtil.assertCardDoesNotExist(DEVICE_A, columnA, cardTitle);
    }

    @Test
    public void assignCard() {
        final var cardTitle = randomUtil.randomize("cardToAssign");
        final var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);
        final var userId = new User.ID(DEVICE_A.account().username());

        DEVICE_A.virtualDevice().getAssignCardUseCase().execute(card.id(), userId).join();

        EndToEndUtil.assertCardAssignedTo(DEVICE_A, card.id(), userId);
    }

    @Test
    public void unassignCard() {
        final var cardTitle = randomUtil.randomize("cardToUnassign");
        final var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);
        final var userId = new User.ID(DEVICE_A.account().username());

        DEVICE_A.virtualDevice().getAssignCardUseCase().execute(card.id(), userId).join();
        EndToEndUtil.assertCardAssignedTo(DEVICE_A, card.id(), userId);

        DEVICE_A.virtualDevice().getUnassignCardUseCase().execute(card.id(), userId).join();

        EndToEndUtil.assertCardNotAssignedTo(DEVICE_A, card.id(), userId);
    }

    @Test
    public void assignLabel() {
        final var labelTitle = "Action needed";
        final var label = EndToEndUtil.getLabel(DEVICE_A, boardA, labelTitle);
        final var cardTitle = randomUtil.randomize("cardForLabel");
        var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);

        final var updatedCard = card.withLabels(java.util.Set.of(label.id()));
        DEVICE_A.virtualDevice().getUpdateCardUseCase().execute(updatedCard).join();

        EndToEndUtil.assertCardHasLabel(DEVICE_A, card.id(), label.id());
    }

    @Test
    public void archiveCard() {
        final var cardTitle = randomUtil.randomize("cardToArchive");
        var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);

        final var archivedCard = card.withArchived(true);
        DEVICE_A.virtualDevice().getUpdateCardUseCase().execute(archivedCard).join();

        EndToEndUtil.assertCardArchived(DEVICE_A, card.id(), true);
    }

    @Test
    public void updateCardDetails() {
        final var cardTitle = randomUtil.randomize("cardDetails");
        var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);

        final var description = "Some description";
        final var dueDate = java.time.OffsetDateTime.now().plusDays(1).withNano(0);

        final var updatedCard = card.withDescription(description).withDueDate(dueDate);
        DEVICE_A.virtualDevice().getUpdateCardUseCase().execute(updatedCard).join();

        EndToEndUtil.assertCardDescription(DEVICE_A, card.id(), description);
    }
}
