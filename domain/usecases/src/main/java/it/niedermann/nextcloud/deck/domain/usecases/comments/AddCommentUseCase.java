package it.niedermann.nextcloud.deck.domain.usecases.comments;

import java.util.concurrent.CompletableFuture;

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

    public CompletableFuture<Void> execute(long cardId, String message) {
        return execute(cardId, message, null);
    }

    public CompletableFuture<Void> execute(long cardId, String message, Long parentCommentId) {
        return commentRepository.createComment(cardId, message, parentCommentId);
    }
}
