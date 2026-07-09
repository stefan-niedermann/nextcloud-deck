package it.niedermann.nextcloud.deck.domain.usecases.comments;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Comment;
import it.niedermann.nextcloud.deck.domain.repository.CommentRepository;
import jakarta.inject.Inject;

public class AddCommentUseCase {

    private final CommentRepository commentRepository;

    @Inject
    public AddCommentUseCase(
            CommentRepository commentRepository
    ) {
        this.commentRepository = commentRepository;
    }

    public CompletableFuture<Void> execute(Card.ID cardId, String message) {
        return execute(cardId, message, null);
    }

    public CompletableFuture<Void> execute(Card.ID cardId, String message, Comment.ID parentCommentId) {
        return commentRepository.createComment(cardId, message, parentCommentId);
    }
}
