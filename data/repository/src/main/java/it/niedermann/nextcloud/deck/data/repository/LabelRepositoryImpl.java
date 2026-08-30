package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.data.local.dao.BoardDao;
import it.niedermann.nextcloud.deck.data.local.dao.LabelDao;
import it.niedermann.nextcloud.deck.data.local.entity.LabelEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.LabelMapper;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.CreateLabel;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.repository.LabelRepository;
import jakarta.inject.Inject;

public class LabelRepositoryImpl implements LabelRepository {

    private final LabelDao labelDao;
    private final BoardDao boardDao;
    private final LabelMapper labelMapper;

    @Inject
    public LabelRepositoryImpl(LabelDao labelDao,
                               BoardDao boardDao,
                               LabelMapper labelMapper) {
        this.labelDao = labelDao;
        this.boardDao = boardDao;
        this.labelMapper = labelMapper;
    }

    @Override
    public CompletableFuture<Void> createLabel(CreateLabel label) {
        return boardDao.getBoardById(label.boardId().value())
                .thenCompose(board -> {
                    if (board == null) {
                        final var future = new CompletableFuture<Void>();
                        future.completeExceptionally(new IllegalArgumentException("Board not found: " + label.boardId().value()));
                        return future;
                    }
                    final var newEntity = new LabelEntity(
                            0,
                            board.getAccountId(),
                            null,
                            DBStatus.LOCAL_EDITED.getId(),
                            null,
                            OffsetDateTime.now(),
                            null,
                            label.boardId().value(),
                            label.title(),
                            label.color(),
                            null
                    );
                    return labelDao.insertOrReplace(newEntity).thenApply(v -> null);
                });
    }

    @Override
    public CompletableFuture<Void> updateLabel(Label label) {
        return labelDao.getLabelById(label.id().value())
                .thenCompose(oldEntity -> {
                    if (oldEntity == null) {
                        final var future = new CompletableFuture<Void>();
                        future.completeExceptionally(new IllegalArgumentException("Label not found: " + label.id().value()));
                        return future;
                    }
                    final var entity = labelMapper.toEntity(label);
                    final var updatedEntity = new LabelEntity(
                            oldEntity.getLocalId(),
                            oldEntity.getAccountId(),
                            entity.getRemoteId() != null ? entity.getRemoteId() : oldEntity.getRemoteId(),
                            DBStatus.LOCAL_EDITED.getId(),
                            oldEntity.getLastModified(),
                            OffsetDateTime.now(),
                            (entity.getEtag() != null && !entity.getEtag().isBlank()) ? entity.getEtag() : oldEntity.getEtag(),
                            oldEntity.getBoardId(),
                            label.title(),
                            label.color(),
                            oldEntity.getConflictWithId()
                    );
                    return labelDao.updateRx(updatedEntity).thenApply(v -> null);
                });
    }

    @Override
    public CompletableFuture<Void> deleteLabel(Label.ID labelId) {
        return labelDao.getLabelById(labelId.value())
                .thenCompose(entity -> {
                    if (entity == null) {
                        return CompletableFuture.completedFuture(null);
                    }
                    if (entity.getRemoteId() == null) {
                        return labelDao.deleteRx(entity).thenApply(v -> null);
                    } else {
                        final var deletedEntity = new LabelEntity(
                                entity.getLocalId(),
                                entity.getAccountId(),
                                entity.getRemoteId(),
                                DBStatus.LOCAL_DELETED.getId(),
                                entity.getLastModified(),
                                OffsetDateTime.now(),
                                entity.getEtag(),
                                entity.getBoardId(),
                                entity.getTitle(),
                                entity.getColor(),
                                entity.getConflictWithId()
                        );
                        return labelDao.updateRx(deletedEntity).thenApply(v -> null);
                    }
                });
    }

    @Override
    public Flow.Publisher<Set<Label>> getNotDeletedLabels(Board.ID boardId) {
        return FlowAdapters.toFlowPublisher(
                labelDao.getLabelsByBoard(boardId.value())
                        .map(entities -> Set.copyOf(labelMapper.toTOList(entities)))
                        .subscribeOn(Schedulers.io())
        );
    }

    @Override
    public Flow.Publisher<Set<Label>> getLabel(Label.ID labelId) {
        return FlowAdapters.toFlowPublisher(
                labelDao.getLabelByIdRx(labelId.value())
                        .map(entity -> Set.of(labelMapper.toTO(entity)))
                        .subscribeOn(Schedulers.io())
        );
    }

    @Override
    public Flow.Publisher<Collection<Label>> find(String userText) {
        return FlowAdapters.toFlowPublisher(
                labelDao.find(userText)
                        .map(entities -> (Collection<Label>) labelMapper.toTOList(entities))
                        .subscribeOn(Schedulers.io())
        );
    }
}
