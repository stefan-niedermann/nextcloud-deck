package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Comment;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.CommentRepository;

public class CommentRepositoryImpl implements CommentRepository {

    @Override
    public Flow.Publisher<List<Comment>> getNotDeletedComments(long cardId) {
        return FlowAdapters.toFlowPublisher(Flowable.just(List.of(new Comment(
                cardId,
                new User("sample", "Sampson Sample"),
                LocalDateTime.now(),
                "This is a creative comment.",
                Optional.empty()))));
    }

    @Override
    public CompletableFuture<Void> createComment(long cardId, String message, Long parentCommentId) {
        System.out.println("[Mock][" + CommentRepositoryImpl.class.getSimpleName() + "/createComment]: " + message);
        return CompletableFuture.completedFuture(null);
    }
}