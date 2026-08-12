package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.data.local.dao.CommentDao;
import it.niedermann.nextcloud.deck.data.local.mapper.CommentMapper;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Comment;
import it.niedermann.nextcloud.deck.domain.model.CreateComment;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewComment;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.repository.CommentRepository;
import jakarta.inject.Inject;

public class CommentRepositoryImpl implements CommentRepository {

    private final AccountRepository accountRepository;
    private final CommentDao commentDao;
    private final CommentMapper commentMapper;

    @Inject
    public CommentRepositoryImpl(AccountRepository accountRepository,
                                 CommentDao commentDao,
                                 CommentMapper commentMapper) {
        this.accountRepository = accountRepository;
        this.commentDao = commentDao;
        this.commentMapper = commentMapper;
    }

    @Override
    public Flow.Publisher<List<Comment>> getNotDeletedComments(Card.ID cardId) {
        return FlowAdapters.toFlowPublisher(
                commentDao.getCommentsByCard(cardId.value())
                        .map(commentMapper::toTOList)
                        .subscribeOn(Schedulers.io())
        );
    }

    @Override
    public Flow.Publisher<List<PreviewComment>> getNotDeletedCommentPreviews(Card.ID cardId) {
        return FlowAdapters.toFlowPublisher(
                Maybe.fromCompletionStage(accountRepository.findAccountIdByCardId(cardId))
                        .toFlowable()
                        .switchMap(accountId -> Maybe.fromCompletionStage(accountRepository.getAccountSync(accountId)).toFlowable())
                        .switchMap(account -> commentDao.getCommentsByCard(cardId.value())
                                .map(entities -> entities.stream()
                                        .map(entity -> new PreviewComment(commentMapper.toTO(entity), account))
                                        .collect(Collectors.toList())))
                        .subscribeOn(Schedulers.io())
        );
    }

    @Override
    public CompletableFuture<Void> createComment(CreateComment comment) {
        // TODO: Local-first or Sync?
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> updateComment(Comment.ID id, String message) {
        // TODO: Implement update in CommentDao or fetch and update
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> deleteComment(Comment.ID id) {
        return commentDao.deleteById(id.value());
    }
}
