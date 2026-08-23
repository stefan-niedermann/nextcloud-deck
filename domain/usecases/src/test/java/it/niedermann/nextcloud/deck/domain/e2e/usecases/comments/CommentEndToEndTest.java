package it.niedermann.nextcloud.deck.domain.e2e.usecases.comments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import it.niedermann.nextcloud.deck.domain.e2e.EndToEndTest;
import it.niedermann.nextcloud.deck.domain.e2e.EndToEndUtil;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateComment;

public class CommentEndToEndTest extends EndToEndTest {

    private VirtualDeviceAndAccount DEVICE_A;
    private VirtualDeviceAndAccount DEVICE_B;
    private Card cardA;

    @BeforeEach
    @Override
    public void setup() throws IOException {
        super.setup();

        DEVICE_A = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
        DEVICE_B = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");

        final var boardTitle = randomUtil.randomize("boardForComment");
        final Board board = EndToEndUtil.createBoard(DEVICE_A, boardTitle);

        final var columnTitle = randomUtil.randomize("columnForComment");
        final Column column = EndToEndUtil.createColumn(DEVICE_A, board, columnTitle);

        final var cardTitle = randomUtil.randomize("cardForComment");
        cardA = EndToEndUtil.createCard(DEVICE_A, column, cardTitle);
    }

    @Test
    public void createComment() {
        final var message = randomUtil.randomize("createComment");
        final var createComment = new CreateComment(cardA.id(), message);

        DEVICE_A.virtualDevice().getAddCommentUseCase().execute(createComment).join();

        EndToEndUtil.assertCommentExists(DEVICE_A, cardA, message);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertCommentExists(DEVICE_B, cardA, message);
    }

    @Test
    public void updateComment() {
        final var message = randomUtil.randomize("commentToUpdate");
        EndToEndUtil.createComment(DEVICE_A, cardA, message);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);
        final var comment = EndToEndUtil.getComment(DEVICE_B, cardA, message);
        
        final var newMessage = randomUtil.randomize("updatedComment");

        DEVICE_A.virtualDevice().getUpdateCommentUseCase().execute(comment.id(), newMessage).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertCommentExists(DEVICE_A, cardA, newMessage);
        EndToEndUtil.assertCommentExists(DEVICE_B, cardA, newMessage);
        EndToEndUtil.assertCommentDoesNotExist(DEVICE_B, cardA, message);
    }

    @Test
    public void deleteComment() {
        final var message = randomUtil.randomize("commentToDelete");
        EndToEndUtil.createComment(DEVICE_A, cardA, message);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);
        final var comment = EndToEndUtil.getComment(DEVICE_B, cardA, message);

        DEVICE_A.virtualDevice().getDeleteCommentUseCase().execute(comment.id()).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertCommentDoesNotExist(DEVICE_A, cardA, message);
        EndToEndUtil.assertCommentDoesNotExist(DEVICE_B, cardA, message);
    }
}
