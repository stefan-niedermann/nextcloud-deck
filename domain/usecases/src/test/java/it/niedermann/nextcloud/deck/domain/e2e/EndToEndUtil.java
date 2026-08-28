package it.niedermann.nextcloud.deck.domain.e2e;

import org.junit.jupiter.api.Assertions;
import org.reactivestreams.FlowAdapters;

import java.util.Objects;

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
import it.niedermann.nextcloud.deck.domain.model.Label;

public class EndToEndUtil {

    public static Board createBoard(EndToEndTest.VirtualDeviceAndAccount vda, String title) {
        final var createBoard = new CreateBoard(vda.account().id(), title);
        vda.virtualDevice().getAddBoardUseCase().addBoard(createBoard).join();
        return getBoard(vda, title);
    }

    public static Board getBoard(EndToEndTest.VirtualDeviceAndAccount vda, String title) {
        return Maybe.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getListBoardsUseCase().execute(vda.account().id())))
                .blockingGet()
                .stream()
                .filter(b -> Objects.equals(b.title(), title))
                .findFirst()
                .orElseThrow();
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
        return Maybe.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getListColumnsUseCase().execute(board.id())))
                .blockingGet()
                .stream()
                .filter(c -> Objects.equals(c.title(), title))
                .findFirst()
                .orElseThrow();
    }

    public static Card createCard(EndToEndTest.VirtualDeviceAndAccount vda, Column column, String title) {
        final var createCard = new CreateCard(column.id(), title);
        vda.virtualDevice().getAddCardUseCase().execute(createCard).join();
        return getCard(vda, column, title);
    }

    public static Card getCard(EndToEndTest.VirtualDeviceAndAccount vda, Column column, String title) {
        return Maybe.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getListCardsUseCase().execute(column.id())))
                .blockingGet()
                .stream()
                .filter(c -> Objects.equals(c.title(), title))
                .findFirst()
                .orElseThrow();
    }

    public static Label createLabel(EndToEndTest.VirtualDeviceAndAccount vda, Board board, String title) {
        final var label = new Label(new Label.ID(0), board.id(), title, new Color(0, 0, 0));
        vda.virtualDevice().getAddLabelUseCase().execute(label).join();
        return getLabel(vda, board, title);
    }

    public static Label getLabel(EndToEndTest.VirtualDeviceAndAccount vda, Board board, String title) {
        return Maybe.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getListLabelsUseCase().execute(board.id())))
                .blockingGet()
                .stream()
                .filter(l -> Objects.equals(l.title(), title))
                .findFirst()
                .orElseThrow();
    }

    public static void createComment(EndToEndTest.VirtualDeviceAndAccount vda, Card card, String message) {
        final var createComment = new CreateComment(card.id(), message);
        vda.virtualDevice().getAddCommentUseCase().execute(createComment).join();
    }

    public static Comment getComment(EndToEndTest.VirtualDeviceAndAccount vda, Card card, String message) {
        return Maybe.fromPublisher(FlowAdapters.toPublisher(vda.virtualDevice().getListCommentsUseCase().execute(card.id())))
                .blockingGet()
                .stream()
                .filter(c -> Objects.equals(c.message(), message))
                .findFirst()
                .orElseThrow();
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
