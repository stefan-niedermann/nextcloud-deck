package it.niedermann.nextcloud.deck.domain.e2e.usecases.cards;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

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
    public void setup() throws IOException {
        DEVICE_A = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
        DEVICE_B = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");

        // Use the default board created by the server
        boardA = EndToEndUtil.getBoard(DEVICE_A, "Welcome to Nextcloud Deck!");
        columnA = EndToEndUtil.getColumn(DEVICE_A, boardA, "To Do");

        // Ensure DEVICE_B is in sync
        synchronize(DEVICE_B);
    }

    @Test
    public void createCard() {
        final var cardTitle = randomUtil.randomize("createCard");
        final var createCard = new CreateCard(columnA.id(), cardTitle);

        DEVICE_A.virtualDevice().getAddCardUseCase().execute(createCard).join();

        EndToEndUtil.assertCardExists(DEVICE_A, columnA, cardTitle);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        final var boardB = EndToEndUtil.getBoard(DEVICE_B, boardA.title());
        final var columnB = EndToEndUtil.getColumn(DEVICE_B, boardB, columnA.title());
        EndToEndUtil.assertCardExists(DEVICE_B, columnB, cardTitle);
    }

    @Test
    public void updateCard() {
        final var cardTitle = randomUtil.randomize("cardToUpdate");
        final var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);
        final var boardB = EndToEndUtil.getBoard(DEVICE_B, boardA.title());
        final var columnB = EndToEndUtil.getColumn(DEVICE_B, boardB, columnA.title());
        EndToEndUtil.assertCardExists(DEVICE_B, columnB, cardTitle);

        final var newTitle = randomUtil.randomize("updatedCard");
        final var updatedCard = card.withTitle(newTitle);

        DEVICE_A.virtualDevice().getUpdateCardUseCase().execute(updatedCard).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertCardExists(DEVICE_A, columnA, newTitle);
        EndToEndUtil.assertCardExists(DEVICE_B, columnB, newTitle);
        EndToEndUtil.assertCardDoesNotExist(DEVICE_B, columnB, cardTitle);
    }

    @Test
    public void deleteCard() {
        final var cardTitle = randomUtil.randomize("cardToDelete");
        final var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);
        final var boardB = EndToEndUtil.getBoard(DEVICE_B, boardA.title());
        final var columnB = EndToEndUtil.getColumn(DEVICE_B, boardB, columnA.title());
        EndToEndUtil.assertCardExists(DEVICE_B, columnB, cardTitle);

        DEVICE_A.virtualDevice().getDeleteCardUseCase().execute(card.id()).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertCardDoesNotExist(DEVICE_A, columnA, cardTitle);
        EndToEndUtil.assertCardDoesNotExist(DEVICE_B, columnB, cardTitle);
    }

    @Test
    public void moveCardWithinColumn() {
        final var card1Title = randomUtil.randomize("card1");
        final var card2Title = randomUtil.randomize("card2");
        EndToEndUtil.createCard(DEVICE_A, columnA, card1Title);
        final var card2 = EndToEndUtil.createCard(DEVICE_A, columnA, card2Title);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);
        final var boardB = EndToEndUtil.getBoard(DEVICE_B, boardA.title());
        final var columnB = EndToEndUtil.getColumn(DEVICE_B, boardB, columnA.title());
        EndToEndUtil.assertCardExists(DEVICE_B, columnB, card2Title);

        // Swap positions
        DEVICE_A.virtualDevice().getMoveCardUseCase().execute(card2.id(), columnA.id(), 0).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        final var card2A = EndToEndUtil.getCard(DEVICE_A, columnA, card2Title);
        assertEquals(0, card2A.order());
        final var card2B = EndToEndUtil.getCard(DEVICE_B, columnB, card2Title);
        assertEquals(0, card2B.order());
    }

    @Test
    public void moveCardToAnotherColumn() {
        final var columnBTitle = "In Progress";
        final var columnB = EndToEndUtil.getColumn(DEVICE_A, boardA, columnBTitle);
        final var cardTitle = randomUtil.randomize("cardToMove");
        final var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);
        final var boardB = EndToEndUtil.getBoard(DEVICE_B, boardA.title());
        final var columnB_on_B = EndToEndUtil.getColumn(DEVICE_B, boardB, columnBTitle);
        EndToEndUtil.assertCardExists(DEVICE_B, EndToEndUtil.getColumn(DEVICE_B, boardB, columnA.title()), cardTitle);

        DEVICE_A.virtualDevice().getMoveCardUseCase().execute(card.id(), columnB.id(), 0).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertCardExists(DEVICE_A, columnB, cardTitle);
        EndToEndUtil.assertCardDoesNotExist(DEVICE_A, columnA, cardTitle);
        EndToEndUtil.assertCardExists(DEVICE_B, columnB_on_B, cardTitle);
        EndToEndUtil.assertCardDoesNotExist(DEVICE_B, EndToEndUtil.getColumn(DEVICE_B, boardB, columnA.title()), cardTitle);
    }

    @Test
    public void moveCardToAnotherBoard() {
        final var boardBTitle = randomUtil.randomize("boardB");
        final var boardB = EndToEndUtil.createBoard(DEVICE_A, boardBTitle);
        final var columnBTitle = randomUtil.randomize("columnB");
        final var columnB = EndToEndUtil.createColumn(DEVICE_A, boardB, columnBTitle);

        final var cardTitle = randomUtil.randomize("cardToMoveToBoard");
        final var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);
        final var boardB_on_B = EndToEndUtil.getBoard(DEVICE_B, boardBTitle);
        final var columnB_on_B = EndToEndUtil.getColumn(DEVICE_B, boardB_on_B, columnBTitle);
        EndToEndUtil.assertCardExists(DEVICE_B, EndToEndUtil.getColumn(DEVICE_B, EndToEndUtil.getBoard(DEVICE_B, boardA.title()), columnA.title()), cardTitle);

        DEVICE_A.virtualDevice().getMoveCardUseCase().execute(card.id(), columnB.id(), 0).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertCardExists(DEVICE_A, columnB, cardTitle);
        EndToEndUtil.assertCardDoesNotExist(DEVICE_A, columnA, cardTitle);
        EndToEndUtil.assertCardExists(DEVICE_B, columnB_on_B, cardTitle);
        EndToEndUtil.assertCardDoesNotExist(DEVICE_B, EndToEndUtil.getColumn(DEVICE_B, EndToEndUtil.getBoard(DEVICE_B, boardA.title()), columnA.title()), cardTitle);
    }

    @Test
    public void moveCardToAnotherAccount() throws IOException {
        final var DEVICE_C = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "userC");
        final var boardCTitle = randomUtil.randomize("boardC");
        final var boardC = EndToEndUtil.createBoard(DEVICE_C, boardCTitle);
        final var columnCTitle = randomUtil.randomize("columnC");
        final var columnC = EndToEndUtil.createColumn(DEVICE_C, boardC, columnCTitle);

        // Share boardC with johndoe (DEVICE_A)
        final var permissions = new Board.Permissions(true, true, true, true);
        DEVICE_C.virtualDevice().getAddBoardShareUseCase().execute(boardC.id(), new User.ID(DEVICE_A.account().username()), permissions).join();

        synchronize(DEVICE_C);
        synchronize(DEVICE_A);

        final var boardC_on_A = EndToEndUtil.getBoard(DEVICE_A, boardCTitle);
        final var columnC_on_A = EndToEndUtil.getColumn(DEVICE_A, boardC_on_A, columnCTitle);

        final var cardTitle = randomUtil.randomize("cardToMoveToSharedBoard");
        final var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        DEVICE_A.virtualDevice().getMoveCardUseCase().execute(card.id(), columnC_on_A.id(), 0).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);
        synchronize(DEVICE_C);

        EndToEndUtil.assertCardExists(DEVICE_A, columnC_on_A, cardTitle);
        EndToEndUtil.assertCardExists(DEVICE_B, EndToEndUtil.getColumn(DEVICE_B, EndToEndUtil.getBoard(DEVICE_B, boardCTitle), columnCTitle), cardTitle);
        EndToEndUtil.assertCardExists(DEVICE_C, columnC, cardTitle);
    }

    @Test
    public void assignCard() {
        final var cardTitle = randomUtil.randomize("cardToAssign");
        final var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);
        final var userId = new User.ID(DEVICE_A.account().username());

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);
        final var boardB = EndToEndUtil.getBoard(DEVICE_B, boardA.title());
        final var columnB = EndToEndUtil.getColumn(DEVICE_B, boardB, columnA.title());
        final var cardB = EndToEndUtil.getCard(DEVICE_B, columnB, cardTitle);

        DEVICE_A.virtualDevice().getAssignCardUseCase().execute(card.id(), userId).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertCardAssignedTo(DEVICE_A, card.id(), userId);
        EndToEndUtil.assertCardAssignedTo(DEVICE_B, cardB.id(), userId);
    }

    @Test
    public void unassignCard() {
        final var cardTitle = randomUtil.randomize("cardToUnassign");
        final var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);
        final var userId = new User.ID(DEVICE_A.account().username());

        DEVICE_A.virtualDevice().getAssignCardUseCase().execute(card.id(), userId).join();
        synchronize(DEVICE_A);
        synchronize(DEVICE_B);
        final var boardB = EndToEndUtil.getBoard(DEVICE_B, boardA.title());
        final var columnB = EndToEndUtil.getColumn(DEVICE_B, boardB, columnA.title());
        final var cardB = EndToEndUtil.getCard(DEVICE_B, columnB, cardTitle);
        EndToEndUtil.assertCardAssignedTo(DEVICE_B, cardB.id(), userId);

        DEVICE_A.virtualDevice().getUnassignCardUseCase().execute(card.id(), userId).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertCardNotAssignedTo(DEVICE_A, card.id(), userId);
        EndToEndUtil.assertCardNotAssignedTo(DEVICE_B, cardB.id(), userId);
    }

    @Test
    public void assignLabel() {
        final var labelTitle = "Action needed";
        final var label = EndToEndUtil.getLabel(DEVICE_A, boardA, labelTitle);
        final var cardTitle = randomUtil.randomize("cardForLabel");
        var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);
        final var boardB = EndToEndUtil.getBoard(DEVICE_B, boardA.title());
        final var columnB = EndToEndUtil.getColumn(DEVICE_B, boardB, columnA.title());
        final var label_on_B = EndToEndUtil.getLabel(DEVICE_B, boardB, labelTitle);
        final var cardB = EndToEndUtil.getCard(DEVICE_B, columnB, cardTitle);

        final var updatedCard = card.withLabels(Set.of(label.id()));
        DEVICE_A.virtualDevice().getUpdateCardUseCase().execute(updatedCard).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertCardHasLabel(DEVICE_A, card.id(), label.id());
        EndToEndUtil.assertCardHasLabel(DEVICE_B, cardB.id(), label_on_B.id());
    }

    @Test
    public void archiveCard() {
        final var cardTitle = randomUtil.randomize("cardToArchive");
        var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);
        final var boardB = EndToEndUtil.getBoard(DEVICE_B, boardA.title());
        final var columnB = EndToEndUtil.getColumn(DEVICE_B, boardB, columnA.title());
        final var cardB = EndToEndUtil.getCard(DEVICE_B, columnB, cardTitle);
        EndToEndUtil.assertCardArchived(DEVICE_B, cardB.id(), false);

        final var archivedCard = card.withArchived(true);
        DEVICE_A.virtualDevice().getUpdateCardUseCase().execute(archivedCard).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertCardArchived(DEVICE_A, card.id(), true);
        EndToEndUtil.assertCardArchived(DEVICE_B, cardB.id(), true);
    }

    @Test
    public void copyCard() {
        final var cardTitle = randomUtil.randomize("cardToCopy");
        final var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);
        
        final var columnBTitle = "In Progress";
        final var columnB = EndToEndUtil.getColumn(DEVICE_A, boardA, columnBTitle);

        DEVICE_A.virtualDevice().getCopyCardUseCase().execute(card.id(), columnB.id(), 0).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        final var boardB = EndToEndUtil.getBoard(DEVICE_B, boardA.title());
        final var columnB_on_B = EndToEndUtil.getColumn(DEVICE_B, boardB, columnBTitle);

        // Original card should still exist in column A
        EndToEndUtil.assertCardExists(DEVICE_A, columnA, cardTitle);
        EndToEndUtil.assertCardExists(DEVICE_B, EndToEndUtil.getColumn(DEVICE_B, boardB, columnA.title()), cardTitle);

        // Copied card should exist in column B
        EndToEndUtil.assertCardExists(DEVICE_A, columnB, cardTitle);
        EndToEndUtil.assertCardExists(DEVICE_B, columnB_on_B, cardTitle);
    }

    @Test
    public void updateCardDetails() {
        final var cardTitle = randomUtil.randomize("cardDetails");
        var card = EndToEndUtil.createCard(DEVICE_A, columnA, cardTitle);

        final var description = "Some description";
        final var dueDate = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC).plusDays(1).withNano(0);

        final var updatedCard = card.withDescription(description).withDueDate(dueDate);
        DEVICE_A.virtualDevice().getUpdateCardUseCase().execute(updatedCard).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        final var boardB = EndToEndUtil.getBoard(DEVICE_B, boardA.title());
        final var columnB = EndToEndUtil.getColumn(DEVICE_B, boardB, columnA.title());
        final var cardB = EndToEndUtil.getCard(DEVICE_B, columnB, cardTitle);

        EndToEndUtil.assertCardDescription(DEVICE_A, card.id(), description);
        EndToEndUtil.assertCardDescription(DEVICE_B, cardB.id(), description);
        EndToEndUtil.assertCardDueDate(DEVICE_B, cardB.id(), dueDate);
    }
}
