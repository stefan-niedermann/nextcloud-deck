package it.niedermann.nextcloud.deck.domain.repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Comment;
import it.niedermann.nextcloud.deck.domain.model.CreateComment;

public interface CommentRepository {

    Flow.Publisher<List<Comment>> getNotDeletedComments(Card.ID cardId);

    CompletableFuture<Void> createComment(CreateComment comment);
}
