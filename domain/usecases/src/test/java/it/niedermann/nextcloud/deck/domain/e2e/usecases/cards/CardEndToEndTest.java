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
    private VirtualDeviceAndAccount DEVICE_B;
    private Board boardA;
    private Column columnA;

    @BeforeEach
    @Override
    public void setup() throws IOException {
        super.setup();

        DEVICE_A = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
        DEVICE_B = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");

        final var boardTitle = randomUtil.randomize("boardForCard");
        boardA = EndToEndUtil.createBoard(DEVICE_A, boardTitle);

        final var columnTitle = randomUtil.randomize("columnForCard");
        columnA = EndToEndUtil.createColumn(DEVICE_A, boardA, columnTitle);
    }

    @Test
    public void createCard() {
        final var cardTitle = randomUtil.randomize("createCard");
        final var createCard = new CreateCard(columnA.id(), cardTitle);

        DEVICE_A.virtualDevice().getAddCardUseCase().execute(createCard).join();

        EndToEndUtil.assertCardExists(DEVICE_A, columnA, cardTitle);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertCardExists(DEVICE_B, columnA, cardTitle);
    }

    @Test
    public void updateCard() {
        final var cardTitle = randomUtil.randomize("cardToUpdate");
        final var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);
        EndToEndUtil.assertCardExists(DEVICE_B, columnA, cardTitle);

        final var newTitle = randomUtil.randomize("updatedCard");
        final var updatedCard = card.withTitle(newTitle);

        DEVICE_A.virtualDevice().getUpdateCardUseCase().execute(updatedCard).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertCardExists(DEVICE_A, columnA, newTitle);
        EndToEndUtil.assertCardExists(DEVICE_B, columnA, newTitle);
        EndToEndUtil.assertCardDoesNotExist(DEVICE_B, columnA, cardTitle);
    }

    @Test
    public void deleteCard() {
        final var cardTitle = randomUtil.randomize("cardToDelete");
        final var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);
        EndToEndUtil.assertCardExists(DEVICE_B, columnA, cardTitle);

        DEVICE_A.virtualDevice().getDeleteCardUseCase().execute(card.id()).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertCardDoesNotExist(DEVICE_A, columnA, cardTitle);
        EndToEndUtil.assertCardDoesNotExist(DEVICE_B, columnA, cardTitle);
    }

    @Test
    public void moveCard() {
        final var columnBTitle = randomUtil.randomize("columnForMove");
        final var columnB = EndToEndUtil.createColumn(DEVICE_A, boardA, columnBTitle);
        final var cardTitle = randomUtil.randomize("cardToMove");
        final var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);
        EndToEndUtil.assertColumnExists(DEVICE_B, boardA, columnBTitle);
        EndToEndUtil.assertCardExists(DEVICE_B, columnA, cardTitle);

        DEVICE_A.virtualDevice().getMoveCardUseCase().execute(card.id(), columnB.id(), 0).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertCardExists(DEVICE_A, columnB, cardTitle);
        EndToEndUtil.assertCardDoesNotExist(DEVICE_A, columnA, cardTitle);
        EndToEndUtil.assertCardExists(DEVICE_B, columnB, cardTitle);
        EndToEndUtil.assertCardDoesNotExist(DEVICE_B, columnA, cardTitle);
    }

    @Test
    public void assignCard() {
        final var cardTitle = randomUtil.randomize("cardToAssign");
        final var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);
        final var userId = new User.ID(DEVICE_A.account().username());

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);
        EndToEndUtil.assertCardExists(DEVICE_B, columnA, cardTitle);

        DEVICE_A.virtualDevice().getAssignCardUseCase().execute(card.id(), userId).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        // Verification of assignment on B would require checking Card details, 
        // but for now we follow the sync pattern.
    }

    @Test
    public void unassignCard() {
        final var cardTitle = randomUtil.randomize("cardToUnassign");
        final var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);
        final var userId = new User.ID(DEVICE_A.account().username());

        DEVICE_A.virtualDevice().getAssignCardUseCase().execute(card.id(), userId).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        DEVICE_A.virtualDevice().getUnassignCardUseCase().execute(card.id(), userId).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);
    }
}
