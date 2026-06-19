package it.niedermann.nextcloud.deck.domain.repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Comment;

public interface CommentRepository {

    Flow.Publisher<List<Comment>> getNotDeletedComments(long cardId);

    CompletableFuture<Void> createComment(long cardId, String message, Long parentCommentId);
}
