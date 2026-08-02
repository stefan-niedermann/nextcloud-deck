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
import it.niedermann.nextcloud.deck.domain.model.CreateComment;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewComment;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.repository.CommentRepository;
import jakarta.inject.Inject;

public class CommentRepositoryImpl implements CommentRepository {

    private final AccountRepository accountRepository;

    @Inject
    public CommentRepositoryImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Flow.Publisher<List<Comment>> getNotDeletedComments(Card.ID cardId) {
        return FlowAdapters.toFlowPublisher(Flowable.just(
                Arrays.stream(MockData.MOCK_COMMENTS)
                        .filter(comment -> Objects.equals(comment.cardId(), cardId))
                        .collect(Collectors.toList())));
    }

    @Override
    public Flow.Publisher<List<PreviewComment>> getNotDeletedCommentPreviews(Card.ID cardId) {
        return FlowAdapters.toFlowPublisher(
                Flowable.fromCompletionStage(accountRepository.findAccountIdByCardId(cardId))
                        .switchMap(accountId -> Flowable.fromCompletionStage(accountRepository.getAccountSync(accountId)))
                        .switchMap(account -> Flowable.fromCallable(() -> Arrays.stream(MockData.MOCK_COMMENTS)
                                .filter(comment -> Objects.equals(comment.cardId(), cardId))
                                .map(comment -> new PreviewComment(comment, account))
                                .collect(Collectors.toList())))
        );
    }

    @Override
    public CompletableFuture<Void> createComment(CreateComment comment) {
        System.out.println("[Mock][" + CommentRepositoryImpl.class.getSimpleName() + "/createComment]: " + comment.message());
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> updateComment(Comment.ID id, String message) {
        System.out.println("[Mock][" + CommentRepositoryImpl.class.getSimpleName() + "/updateComment]: " + id + " -> " + message);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> deleteComment(Comment.ID id) {
        System.out.println("[Mock][" + CommentRepositoryImpl.class.getSimpleName() + "/deleteComment]: " + id);
        return CompletableFuture.completedFuture(null);
    }
}