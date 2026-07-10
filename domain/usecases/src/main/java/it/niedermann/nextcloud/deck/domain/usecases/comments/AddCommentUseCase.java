package it.niedermann.nextcloud.deck.domain.usecases.comments;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.CreateComment;
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

    public CompletableFuture<Void> execute(CreateComment comment) {
        return commentRepository.createComment(comment);
    }
}
