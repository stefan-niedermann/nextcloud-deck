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

import it.niedermann.nextcloud.deck.data.local.entity.CommentEntity;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import java.time.OffsetDateTime;

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
        return accountRepository.findAccountIdByCardId(comment.cardId())
                .thenCompose(accountId -> {
                    final var entity = new CommentEntity(
                            0,
                            accountId.value(),
                            null,
                            DBStatus.LOCAL_EDITED.getId(),
                            null,
                            OffsetDateTime.now(),
                            null,
                            comment.cardId().value(),
                            null, // actorType
                            null, // actorId
                            null, // actorDisplayName
                            comment.message(),
                            null, // parentId
                            OffsetDateTime.now(),
                            null
                    );
                    return commentDao.insertOrReplace(entity).thenApply(v -> null);
                });
    }

    @Override
    public CompletableFuture<Void> updateComment(Comment.ID id, String message) {
        return commentDao.getCommentByLocalId(id.value())
                .thenCompose(oldEntity -> {
                    if (oldEntity == null) return CompletableFuture.completedFuture(null);
                    final var updatedEntity = new CommentEntity(
                            oldEntity.getLocalId(),
                            oldEntity.getAccountId(),
                            oldEntity.getRemoteId(),
                            DBStatus.LOCAL_EDITED.getId(),
                            oldEntity.getLastModified(),
                            OffsetDateTime.now(),
                            oldEntity.getEtag(),
                            oldEntity.getCardId(),
                            oldEntity.getActorType(),
                            oldEntity.getActorId(),
                            oldEntity.getActorDisplayName(),
                            message,
                            oldEntity.getParentId(),
                            oldEntity.getCreatedAt(),
                            oldEntity.getConflictWithId()
                    );
                    return commentDao.updateRx(updatedEntity).thenApply(v -> null);
                });
    }

    @Override
    public CompletableFuture<Void> deleteComment(Comment.ID id) {
        return commentDao.getCommentByLocalId(id.value())
                .thenCompose(entity -> {
                    if (entity == null) return CompletableFuture.completedFuture(null);
                    if (entity.getRemoteId() == null) {
                        return commentDao.deleteById(entity.getLocalId());
                    } else {
                        final var deletedEntity = new CommentEntity(
                                entity.getLocalId(),
                                entity.getAccountId(),
                                entity.getRemoteId(),
                                DBStatus.LOCAL_DELETED.getId(),
                                entity.getLastModified(),
                                OffsetDateTime.now(),
                                entity.getEtag(),
                                entity.getCardId(),
                                entity.getActorType(),
                                entity.getActorId(),
                                entity.getActorDisplayName(),
                                entity.getMessage(),
                                entity.getParentId(),
                                entity.getCreatedAt(),
                                entity.getConflictWithId()
                        );
                        return commentDao.updateRx(deletedEntity).thenApply(v -> null);
                    }
                });
    }
}
