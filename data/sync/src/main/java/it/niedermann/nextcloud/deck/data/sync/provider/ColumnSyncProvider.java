package it.niedermann.nextcloud.deck.data.sync.provider;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.data.local.dao.BoardDao;
import it.niedermann.nextcloud.deck.data.local.dao.ColumnDao;
import it.niedermann.nextcloud.deck.data.local.entity.ColumnEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.ColumnMapper;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;
import it.niedermann.nextcloud.remote.ApiProvider;
import it.niedermann.nextcloud.remote.deck.DeckApi;
import it.niedermann.nextcloud.remote.deck.dto.BoardDTO;
import it.niedermann.nextcloud.remote.deck.dto.ColumnDTO;
import it.niedermann.nextcloud.remote.deck.mapper.ColumnRemoteMapper;
import jakarta.inject.Inject;
import retrofit2.HttpException;

public class ColumnSyncProvider implements SyncProvider<BoardDTO> {

    private static final Logger logger = Logger.getLogger(ColumnSyncProvider.class.getName());

    private final ColumnDao columnDao;
    private final BoardDao boardDao;
    private final ApiProvider.Factory apiFactory;
    private CardSyncProvider cardSyncProvider;

    @Inject
    public ColumnSyncProvider(ColumnDao columnDao, BoardDao boardDao, ApiProvider.Factory apiFactory) {
        this.columnDao = columnDao;
        this.boardDao = boardDao;
        this.apiFactory = apiFactory;
    }

    @Inject
    public void setCardSyncProvider(CardSyncProvider cardSyncProvider) {
        this.cardSyncProvider = cardSyncProvider;
    }

    @Override
    public CompletableFuture<Void> upSync(Account account, SyncStatus status, Consumer<SyncStatus> reporter) {
        return columnDao.getChangedColumns(account.id().value())
                .thenCompose(changedColumns -> {
                    if (changedColumns == null) return CompletableFuture.completedFuture(null);
                    CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
                    for (ColumnEntity localColumn : changedColumns) {
                        final var finalFuture = future;
                        future = finalFuture.thenCompose(v -> upSyncSingle(account, localColumn));
                    }
                    return future;
                });
    }

    private CompletableFuture<Void> upSyncSingle(Account account, ColumnEntity localColumn) {
        return boardDao.getBoardById(localColumn.getBoardId())
                .thenCompose(board -> {
                    if (board == null || board.getRemoteId() == null) {
                        return CompletableFuture.completedFuture(null);
                    }
                    long remoteBoardId = board.getRemoteId();
                    DeckApi api = apiFactory.create(account).getDeckApi();
                    ColumnDTO dto = ColumnRemoteMapper.INSTANCE.toDTO(ColumnMapper.INSTANCE.toTO(localColumn));

                    CompletableFuture<ColumnDTO> call;
                    if (localColumn.getRemoteId() == null) {
                        call = api.createStack(remoteBoardId, dto);
                    } else if (localColumn.getStatus() == DBStatus.LOCAL_DELETED.getId()) {
                        return api.deleteStack(remoteBoardId, localColumn.getRemoteId())
                                .thenCompose(v -> columnDao.deleteRx(localColumn))
                                .thenApply(v -> null);
                    } else {
                        call = api.updateStack(remoteBoardId, localColumn.getRemoteId(), dto);
                    }

                    return call.thenCompose(response -> {
                        if (response == null) return CompletableFuture.completedFuture((Void) null);
                        ColumnEntity updatedLocal = ColumnMapper.INSTANCE.toEntity(ColumnRemoteMapper.INSTANCE.toTO(response));
                        updatedLocal = new ColumnEntity(
                                localColumn.getLocalId(),
                                localColumn.getAccountId(),
                                updatedLocal.getRemoteId(),
                                DBStatus.UP_TO_DATE.getId(),
                                updatedLocal.getLastModified(),
                                updatedLocal.getLastModified(),
                                updatedLocal.getEtag(),
                                localColumn.getBoardId(),
                                updatedLocal.getTitle(),
                                updatedLocal.getOrder(),
                                updatedLocal.getArchived(),
                                updatedLocal.getDeletedAt(),
                                null
                        );

                        CompletableFuture<Void> cleanupFuture = CompletableFuture.completedFuture(null);
                        if (localColumn.getStatus() == DBStatus.RESOLVED.getId() && localColumn.getConflictWithId() != null) {
                            cleanupFuture = columnDao.deleteById(localColumn.getConflictWithId()).thenApply(v -> null);
                        }

                        ColumnEntity finalUpdatedLocal = updatedLocal;
                        return cleanupFuture.thenCompose(v -> columnDao.updateRx(finalUpdatedLocal));
                    }).handle((v, throwable) -> {
                        if (throwable != null) {
                            Throwable cause = throwable.getCause();
                            if (cause instanceof HttpException && ((HttpException) cause).code() == 412) {
                                return handleConflict(account, localColumn);
                            }
                            CompletableFuture<Void> failed = new CompletableFuture<>();
                            failed.completeExceptionally(throwable);
                            return failed;
                        }
                        return CompletableFuture.completedFuture((Void) null);
                    }).thenCompose(f -> f);
                });
    }

    private CompletableFuture<Void> handleConflict(Account account, ColumnEntity localColumn) {
        DeckApi api = apiFactory.create(account).getDeckApi();
        if (localColumn.getRemoteId() == null) return CompletableFuture.completedFuture(null);
        return api.getStack(localColumn.getBoardId(), localColumn.getRemoteId(), null)
                .thenCompose(serverDto -> {
                    if (serverDto == null) return CompletableFuture.completedFuture(null);
                    ColumnEntity serverColumn = ColumnMapper.INSTANCE.toEntity(ColumnRemoteMapper.INSTANCE.toTO(serverDto));
                    serverColumn = new ColumnEntity(
                            0,
                            -1L,
                            serverColumn.getRemoteId(),
                            DBStatus.UP_TO_DATE.getId(),
                            serverColumn.getLastModified(),
                            serverColumn.getLastModified(),
                            serverColumn.getEtag(),
                            localColumn.getBoardId(),
                            serverColumn.getTitle(),
                            serverColumn.getOrder(),
                            serverColumn.getArchived(),
                            serverColumn.getDeletedAt(),
                            null
                    );

                    return columnDao.insert(serverColumn)
                            .thenCompose(serverLocalId -> {
                                ColumnEntity updatedLocal = new ColumnEntity(
                                        localColumn.getLocalId(),
                                        localColumn.getAccountId(),
                                        localColumn.getRemoteId(),
                                        DBStatus.CONFLICT.getId(),
                                        localColumn.getLastModified(),
                                        localColumn.getLastModifiedLocal(),
                                        localColumn.getEtag(),
                                        localColumn.getBoardId(),
                                        localColumn.getTitle(),
                                        localColumn.getOrder(),
                                        localColumn.getArchived(),
                                        localColumn.getDeletedAt(),
                                        serverLocalId
                                );
                                return columnDao.updateRx(updatedLocal);
                            });
                });
    }

    @Override
    public CompletableFuture<Void> downSync(Account account, BoardDTO parent, Long parentLocalId, SyncStatus status, Consumer<SyncStatus> reporter) {
        if (parent == null) return CompletableFuture.completedFuture(null);
        if (parent.getStacks() != null && !parent.getStacks().isEmpty()) {
            CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
            long total = parent.getStacks().size();
            for (int i = 0; i < parent.getStacks().size(); i++) {
                ColumnDTO columnDto = parent.getStacks().get(i);
                if (columnDto == null) continue;
                final long finished = i + 1;
                final var finalFuture = future;
                future = finalFuture.thenCompose(v -> mergeColumn(account, columnDto, parentLocalId))
                        .thenCompose(localColumnId -> {
                            SyncStatus newStatus = status.withColumns(total, finished, columnDto.getTitle());
                            reporter.accept(newStatus);
                            return cardSyncProvider.downSync(account, columnDto, localColumnId, newStatus, reporter);
                        });
            }
            return future;
        } else {
            DeckApi api = apiFactory.create(account).getDeckApi();
            if (parent.getId() == null) return CompletableFuture.completedFuture(null);
            return api.getStacks(parent.getId(), null)
                    .thenCompose(columns -> {
                        if (columns == null) return CompletableFuture.completedFuture(null);
                        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
                        long total = columns.size();
                        for (int i = 0; i < columns.size(); i++) {
                            ColumnDTO columnDto = columns.get(i);
                            if (columnDto == null) continue;
                            final long finished = i + 1;
                            final var finalFuture = future;
                            future = finalFuture.thenCompose(v -> mergeColumn(account, columnDto, parentLocalId))
                                    .thenCompose(localColumnId -> {
                                        SyncStatus newStatus = status.withColumns(total, finished, columnDto.getTitle());
                                        reporter.accept(newStatus);
                                        return cardSyncProvider.downSync(account, columnDto, localColumnId, newStatus, reporter);
                                    });
                        }
                        return future;
                    });
        }
    }

    private CompletableFuture<Long> mergeColumn(Account account, ColumnDTO columnDto, Long boardId) {
        if (columnDto.getId() == null) return CompletableFuture.completedFuture(null);
        return columnDao.getColumnByRemoteId(account.id().value(), columnDto.getId())
                .handle((localColumn, throwable) -> {
                    ColumnEntity serverColumn = ColumnMapper.INSTANCE.toEntity(ColumnRemoteMapper.INSTANCE.toTO(columnDto));
                    if (throwable != null || localColumn == null) {
                        ColumnEntity newLocal = new ColumnEntity(
                                0,
                                account.id().value(),
                                serverColumn.getRemoteId(),
                                DBStatus.UP_TO_DATE.getId(),
                                serverColumn.getLastModified(),
                                serverColumn.getLastModified(),
                                serverColumn.getEtag(),
                                boardId,
                                serverColumn.getTitle(),
                                serverColumn.getOrder(),
                                serverColumn.getArchived(),
                                serverColumn.getDeletedAt(),
                                null
                        );
                        return columnDao.insert(newLocal);
                    } else {
                        if (localColumn.getStatus() == DBStatus.CONFLICT.getId()) {
                            return CompletableFuture.completedFuture(localColumn.getLocalId());
                        }
                        if (serverColumn.getEtag() != null && serverColumn.getEtag().equals(localColumn.getEtag())) {
                            return CompletableFuture.completedFuture(localColumn.getLocalId());
                        }
                        ColumnEntity updatedLocal = new ColumnEntity(
                                localColumn.getLocalId(),
                                localColumn.getAccountId(),
                                serverColumn.getRemoteId(),
                                DBStatus.UP_TO_DATE.getId(),
                                serverColumn.getLastModified(),
                                serverColumn.getLastModified(),
                                serverColumn.getEtag(),
                                boardId,
                                serverColumn.getTitle(),
                                serverColumn.getOrder(),
                                serverColumn.getArchived(),
                                serverColumn.getDeletedAt(),
                                null
                        );
                        return columnDao.updateRx(updatedLocal).thenApply(v -> localColumn.getLocalId());
                    }
                }).thenCompose(f -> f);
    }
}
