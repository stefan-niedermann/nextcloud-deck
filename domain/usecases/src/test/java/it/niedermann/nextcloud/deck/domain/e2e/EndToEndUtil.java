package it.niedermann.nextcloud.deck.domain.e2e;

import org.junit.jupiter.api.Assertions;
import org.reactivestreams.FlowAdapters;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Color;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.Comment;
import it.niedermann.nextcloud.deck.domain.model.CreateBoard;
import it.niedermann.nextcloud.deck.domain.model.CreateCard;
import it.niedermann.nextcloud.deck.domain.model.CreateColumn;
import it.niedermann.nextcloud.deck.domain.model.CreateComment;
import it.niedermann.nextcloud.deck.domain.model.CreateLabel;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.MockData;

public class EndToEndUtil {

    public static void setupMockData(EndToEndTest.VirtualDeviceAndAccount vda) {
        final Map<Board.ID, Board> boardIdMap = new java.util.HashMap<>();
        final Map<Column.ID, Column> columnIdMap = new java.util.HashMap<>();

        for (Board mockBoard : MockData.MOCK_BOARDS) {
            final Board createdBoard = createBoard(vda, mockBoard.title());
            boardIdMap.put(mockBoard.id(), createdBoard);

            for (Label mockLabel : MockData.MOCK_LABELS) {
                if (mockLabel.boardId().equals(mockBoard.id())) {
                    final CreateLabel label = new CreateLabel(createdBoard.id(), mockLabel.title(), mockLabel.color());
                    vda.virtualDevice().getAddLabelUseCase().execute(label).join();
                }
            }
        }

        for (Column mockColumn : MockData.MOCK_COLUMNS) {
            final Board board = boardIdMap.get(mockColumn.boardId());
            if (board != null) {
                final Column createdColumn = createColumn(vda, board, mockColumn.title());
                columnIdMap.put(mockColumn.id(), createdColumn);
            }
        }

        for (Card mockCard : MockData.MOCK_CARDS) {
            final Column column = columnIdMap.get(mockCard.columnId());
            if (column != null) {
                createCard(vda, column, mockCard.title());
                // For performance reasons in E2E tests, we only create the cards with titles for now.
                // In a real E2E test, we could further update them with descriptions, labels etc.
            }
        }
    }

    public static Board createBoard(EndToEndTest.VirtualDeviceAndAccount vda, String title) {
        final var createBoard = new CreateBoard(vda.account().id(), title);
        vda.virtualDevice().getAddBoardUseCase().addBoard(createBoard).join();
        return getBoard(vda, title);
    }

    public static Board getBoard(EndToEndTest.VirtualDeviceAndAccount vda, String title) {
        for (int i = 0; i < 10; i++) {
            final var boards = Maybe.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getListBoardsUseCase().execute(vda.account().id()))).blockingGet();
            final var board = boards.stream().filter(b -> Objects.equals(b.title(), title)).findFirst();
            if (board.isPresent()) {
                return board.get();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        throw new NoSuchElementException("Board not found: " + title);
    }

    public static Board getBoard(EndToEndTest.VirtualDeviceAndAccount vda, Board.ID id) {
        return Maybe.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getGetBoardUseCase().execute(id)))
                .blockingGet();
    }

    public static Column createColumn(EndToEndTest.VirtualDeviceAndAccount vda, Board board, String title) {
        final var createColumn = new CreateColumn(board.id(), title, 0);
        vda.virtualDevice().getAddColumnUseCase().execute(createColumn).join();
        return getColumn(vda, board, title);
    }

    public static Column getColumn(EndToEndTest.VirtualDeviceAndAccount vda, Board board, String title) {
        for (int i = 0; i < 10; i++) {
            final var columns = Maybe.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getListColumnsUseCase().execute(board.id()))).blockingGet();
            final var column = columns.stream().filter(c -> Objects.equals(c.title(), title)).findFirst();
            if (column.isPresent()) {
                return column.get();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        throw new NoSuchElementException("Column not found: " + title);
    }

    public static Card createCard(EndToEndTest.VirtualDeviceAndAccount vda, Column column, String title) {
        final var createCard = new CreateCard(column.id(), title);
        vda.virtualDevice().getAddCardUseCase().execute(createCard).join();
        return getCard(vda, column, title);
    }

    public static Card getCard(EndToEndTest.VirtualDeviceAndAccount vda, Card.ID id) {
        for (int i = 0; i < 20; i++) {
            final var card = Maybe.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getGetCardUseCase().execute(id))).blockingGet();
            if (card != null) {
                return card;
            }
            if (i == 10) {
                System.err.println("Card ID " + id.value() + " not found on device " + vda.virtualDevice().getDeviceName() + " after 10 attempts.");
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        throw new NoSuchElementException("Card not found: " + id.value() + " on device " + vda.virtualDevice().getDeviceName());
    }

    public static Card getCard(EndToEndTest.VirtualDeviceAndAccount vda, Column column, String title) {
        for (int i = 0; i < 20; i++) {
            final var cards = Maybe.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getListCardsUseCase().execute(column.id()))).blockingGet();
            if (cards == null) {
                System.err.println("getListCardsUseCase returned null for column " + column.id());
            } else {
                final var card = cards.stream().filter(c -> Objects.equals(c.title(), title)).findFirst();
                if (card.isPresent()) {
                    return card.get();
                }
                if (i == 10) {
                    System.err.println("Card \"" + title + "\" not found in column \"" + column.title() + "\" after 10 attempts. Available cards: " + cards.stream().map(Card::title).collect(java.util.stream.Collectors.joining(", ")));
                }
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        throw new NoSuchElementException("Card not found: " + title + " in column " + column.title() + " (" + column.id() + ") on device " + vda.virtualDevice().getDeviceName());
    }

    public static Label createLabel(EndToEndTest.VirtualDeviceAndAccount vda, Board board, String title) {
        final var label = new CreateLabel(board.id(), title, new Color(0, 0, 0));
        vda.virtualDevice().getAddLabelUseCase().execute(label).join();
        return getLabel(vda, board, title);
    }

    public static Label getLabel(EndToEndTest.VirtualDeviceAndAccount vda, Board board, String title) {
        return Flowable.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getListLabelsUseCase().execute(board.id())))
                .filter(list -> list.stream().anyMatch(l -> Objects.equals(l.title(), title)))
                .map(list -> list.stream().filter(l -> Objects.equals(l.title(), title)).findFirst().orElseThrow())
                .firstOrError()
                .blockingGet();
    }

    public static void createComment(EndToEndTest.VirtualDeviceAndAccount vda, Card card, String message) {
        final var createComment = new CreateComment(card.id(), message);
        vda.virtualDevice().getAddCommentUseCase().execute(createComment).join();
    }

    public static Comment getComment(EndToEndTest.VirtualDeviceAndAccount vda, Card card, String message) {
        return Flowable.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getListCommentsUseCase().execute(card.id())))
                .filter(list -> list.stream().anyMatch(c -> Objects.equals(c.message(), message)))
                .map(list -> list.stream().filter(c -> Objects.equals(c.message(), message)).findFirst().orElseThrow())
                .firstOrError()
                .blockingGet();
    }

    public static void assertBoardExists(EndToEndTest.VirtualDeviceAndAccount vda, String title) {
        final var boards = Maybe.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getListBoardsUseCase().execute(vda.account().id()))).blockingGet();
        Assertions.assertTrue(boards.stream().anyMatch(b -> Objects.equals(b.title(), title)), "Should contain board \"" + title + "\" on device \"" + vda.virtualDevice().getDeviceName() + "\"");
    }

    public static void assertColumnExists(EndToEndTest.VirtualDeviceAndAccount vda, Board board, String title) {
        final var columns = Maybe.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getListColumnsUseCase().execute(board.id()))).blockingGet();
        Assertions.assertTrue(columns.stream().anyMatch(c -> Objects.equals(c.title(), title)), "Should contain column \"" + title + "\" in board \"" + board.title() + "\" on device \"" + vda.virtualDevice().getDeviceName() + "\"");
    }

    public static void assertCardExists(EndToEndTest.VirtualDeviceAndAccount vda, Column column, String title) {
        final var cards = Maybe.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getListCardsUseCase().execute(column.id()))).blockingGet();
        Assertions.assertTrue(cards.stream().anyMatch(c -> Objects.equals(c.title(), title)), "Should contain card \"" + title + "\" in column \"" + column.title() + "\" on device \"" + vda.virtualDevice().getDeviceName() + "\"");
    }

    public static void assertCardAssignedTo(EndToEndTest.VirtualDeviceAndAccount vda, Card.ID cardId, User.ID userId) {
        final var card = getCard(vda, cardId);
        Assertions.assertTrue(card.assignees().contains(userId), "Card \"" + card.title() + "\" should be assigned to \"" + userId.value() + "\" on device \"" + vda.virtualDevice().getDeviceName() + "\"");
    }

    public static void assertCardNotAssignedTo(EndToEndTest.VirtualDeviceAndAccount vda, Card.ID cardId, User.ID userId) {
        final var card = getCard(vda, cardId);
        Assertions.assertFalse(card.assignees().contains(userId), "Card \"" + card.title() + "\" should NOT be assigned to \"" + userId.value() + "\" on device \"" + vda.virtualDevice().getDeviceName() + "\"");
    }

    public static void assertCardHasLabel(EndToEndTest.VirtualDeviceAndAccount vda, Card.ID cardId, Label.ID labelId) {
        final var card = getCard(vda, cardId);
        Assertions.assertTrue(card.labels().contains(labelId), "Card \"" + card.title() + "\" should have label \"" + labelId.value() + "\" on device \"" + vda.virtualDevice().getDeviceName() + "\"");
    }

    public static void assertCardDoesNotHaveLabel(EndToEndTest.VirtualDeviceAndAccount vda, Card.ID cardId, Label.ID labelId) {
        final var card = getCard(vda, cardId);
        Assertions.assertFalse(card.labels().contains(labelId), "Card \"" + card.title() + "\" should NOT have label \"" + labelId.value() + "\" on device \"" + vda.virtualDevice().getDeviceName() + "\"");
    }

    public static void assertCardArchived(EndToEndTest.VirtualDeviceAndAccount vda, Card.ID cardId, boolean archived) {
        final var card = getCard(vda, cardId);
        Assertions.assertEquals(archived, card.archived(), "Card \"" + card.title() + "\" archived state should be " + archived + " on device \"" + vda.virtualDevice().getDeviceName() + "\"");
    }

    public static void assertCardDescription(EndToEndTest.VirtualDeviceAndAccount vda, Card.ID cardId, String expectedDescription) {
        final var card = getCard(vda, cardId);
        Assertions.assertEquals(expectedDescription, card.description(), "Card \"" + card.title() + "\" description should be \"" + expectedDescription + "\" on device \"" + vda.virtualDevice().getDeviceName() + "\"");
    }

    public static void assertCardDueDate(EndToEndTest.VirtualDeviceAndAccount vda, Card.ID cardId, java.time.OffsetDateTime expectedDueDate) {
        final var card = getCard(vda, cardId);
        if (expectedDueDate == null) {
            Assertions.assertNull(card.dueDate(), "Card \"" + card.title() + "\" due date should be null on device \"" + vda.virtualDevice().getDeviceName() + "\"");
        } else {
            Assertions.assertNotNull(card.dueDate(), "Card \"" + card.title() + "\" due date should NOT be null on device \"" + vda.virtualDevice().getDeviceName() + "\"");
            Assertions.assertTrue(expectedDueDate.isEqual(card.dueDate()), "Card \"" + card.title() + "\" due date should be \"" + expectedDueDate + "\" but was \"" + card.dueDate() + "\" on device \"" + vda.virtualDevice().getDeviceName() + "\"");
        }
    }

    public static void assertLabelExists(EndToEndTest.VirtualDeviceAndAccount vda, Board board, String title) {
        final var labels = Maybe.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getListLabelsUseCase().execute(board.id()))).blockingGet();
        Assertions.assertTrue(labels.stream().anyMatch(l -> Objects.equals(l.title(), title)), "Should contain label \"" + title + "\" in board \"" + board.title() + "\" on device \"" + vda.virtualDevice().getDeviceName() + "\"");
    }

    public static void assertCommentExists(EndToEndTest.VirtualDeviceAndAccount vda, Card card, String message) {
        final var comments = Maybe.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getListCommentsUseCase().execute(card.id()))).blockingGet();
        Assertions.assertTrue(comments.stream().anyMatch(c -> Objects.equals(c.message(), message)), "Should contain comment \"" + message + "\" in card \"" + card.title() + "\" on device \"" + vda.virtualDevice().getDeviceName() + "\"");
    }

    public static void assertBoardDoesNotExist(EndToEndTest.VirtualDeviceAndAccount vda, String title) {
        final var boards = Maybe.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getListBoardsUseCase().execute(vda.account().id()))).blockingGet();
        Assertions.assertTrue(boards.stream().noneMatch(b -> Objects.equals(b.title(), title)), "Should NOT contain board \"" + title + "\" on device \"" + vda.virtualDevice().getDeviceName() + "\"");
    }

    public static void assertColumnDoesNotExist(EndToEndTest.VirtualDeviceAndAccount vda, Board board, String title) {
        final var columns = Maybe.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getListColumnsUseCase().execute(board.id()))).blockingGet();
        Assertions.assertTrue(columns.stream().noneMatch(c -> Objects.equals(c.title(), title)), "Should NOT contain column \"" + title + "\" in board \"" + board.title() + "\" on device \"" + vda.virtualDevice().getDeviceName() + "\"");
    }

    public static void assertCardDoesNotExist(EndToEndTest.VirtualDeviceAndAccount vda, Column column, String title) {
        final var cards = Maybe.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getListCardsUseCase().execute(column.id()))).blockingGet();
        Assertions.assertTrue(cards.stream().noneMatch(c -> Objects.equals(c.title(), title)), "Should NOT contain card \"" + title + "\" in column \"" + column.title() + "\" on device \"" + vda.virtualDevice().getDeviceName() + "\"");
    }

    public static void assertLabelDoesNotExist(EndToEndTest.VirtualDeviceAndAccount vda, Board board, String title) {
        final var labels = Maybe.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getListLabelsUseCase().execute(board.id()))).blockingGet();
        Assertions.assertTrue(labels.stream().noneMatch(l -> Objects.equals(l.title(), title)), "Should NOT contain label \"" + title + "\" in board \"" + board.title() + "\" on device \"" + vda.virtualDevice().getDeviceName() + "\"");
    }

    public static void assertCommentDoesNotExist(EndToEndTest.VirtualDeviceAndAccount vda, Card card, String message) {
        final var comments = Maybe.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getListCommentsUseCase().execute(card.id()))).blockingGet();
        Assertions.assertTrue(comments.stream().noneMatch(c -> Objects.equals(c.message(), message)), "Should NOT contain comment \"" + message + "\" in card \"" + card.title() + "\" on device \"" + vda.virtualDevice().getDeviceName() + "\"");
    }
}
