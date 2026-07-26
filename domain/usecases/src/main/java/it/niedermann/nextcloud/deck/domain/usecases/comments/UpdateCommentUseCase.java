package it.niedermann.nextcloud.deck.domain.usecases.comments;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Comment;
import it.niedermann.nextcloud.deck.domain.repository.CommentRepository;
import jakarta.inject.Inject;

public class UpdateCommentUseCase {

    private final CommentRepository commentRepository;

    @Inject
    public UpdateCommentUseCase(
            CommentRepository commentRepository
    ) {
        this.commentRepository = commentRepository;
    }

    public CompletableFuture<Void> execute(Comment.ID id, String message) {
        return commentRepository.updateComment(id, message);
    }
}
