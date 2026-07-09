package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Comment;
import it.niedermann.nextcloud.deck.domain.repository.CommentRepository;

public class CommentRepositoryImpl implements CommentRepository {

    @Override
    public Flow.Publisher<List<Comment>> getNotDeletedComments(Card.ID cardId) {
        return FlowAdapters.toFlowPublisher(Flowable.just(
                Arrays.stream(MockData.MOCK_COMMENTS)
                        .filter(comment -> Objects.equals(comment.cardId(), cardId))
                        .collect(Collectors.toList())));
    }

    @Override
    public CompletableFuture<Void> createComment(Card.ID cardId, String message, Comment.ID parentCommentId) {
        System.out.println("[Mock][" + CommentRepositoryImpl.class.getSimpleName() + "/createComment]: " + message);
        return CompletableFuture.completedFuture(null);
    }
}