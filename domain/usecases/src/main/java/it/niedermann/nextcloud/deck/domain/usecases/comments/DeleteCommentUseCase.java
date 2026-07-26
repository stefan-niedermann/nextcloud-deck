package it.niedermann.nextcloud.deck.domain.usecases.comments;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Comment;
import it.niedermann.nextcloud.deck.domain.repository.CommentRepository;
import jakarta.inject.Inject;

public class DeleteCommentUseCase {

    private final CommentRepository commentRepository;

    @Inject
    public DeleteCommentUseCase(
            CommentRepository commentRepository
    ) {
        this.commentRepository = commentRepository;
    }

    public CompletableFuture<Void> execute(Comment.ID id) {
        return commentRepository.deleteComment(id);
    }
}
