package it.niedermann.nextcloud.deck.domain.repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Comment;
import it.niedermann.nextcloud.deck.domain.model.CreateComment;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewComment;

public interface CommentRepository {

    Flow.Publisher<List<Comment>> getNotDeletedComments(Card.ID cardId);

    Flow.Publisher<List<PreviewComment>> getNotDeletedCommentPreviews(Card.ID cardId);

    CompletableFuture<Void> createComment(CreateComment comment);

    CompletableFuture<Void> updateComment(Comment.ID id, String message);

    CompletableFuture<Void> deleteComment(Comment.ID id);
}
