package it.niedermann.nextcloud.deck.data.sync.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.data.local.dao.BoardDao;
import it.niedermann.nextcloud.deck.data.local.dao.LabelDao;
import it.niedermann.nextcloud.deck.data.local.entity.LabelEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.LabelMapper;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;
import it.niedermann.nextcloud.remote.ApiProvider;
import it.niedermann.nextcloud.remote.deck.DeckApi;
import it.niedermann.nextcloud.remote.deck.dto.BoardDTO;
import it.niedermann.nextcloud.remote.deck.dto.LabelDTO;
import it.niedermann.nextcloud.remote.deck.mapper.LabelRemoteMapper;
import jakarta.inject.Inject;
import retrofit2.HttpException;

public class LabelSyncProvider implements SyncProvider<BoardDTO> {

    private static final Logger logger = Logger.getLogger(LabelSyncProvider.class.getName());

    private final LabelDao labelDao;
    private final BoardDao boardDao;
    private final ApiProvider.Factory apiFactory;

    @Inject
    public LabelSyncProvider(LabelDao labelDao, BoardDao boardDao, ApiProvider.Factory apiFactory) {
        this.labelDao = labelDao;
        this.boardDao = boardDao;
        this.apiFactory = apiFactory;
    }

    @Override
    public CompletableFuture<Void> upSync(Account account, SyncStatus status, Consumer<SyncStatus> reporter) {
        return labelDao.getChangedLabels(account.id().value())
                .thenCompose(changedLabels -> {
                    if (changedLabels == null) return CompletableFuture.completedFuture(null);
                    CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
                    for (LabelEntity localLabel : changedLabels) {
                        final var finalFuture = future;
                        future = finalFuture.thenCompose(v -> upSyncSingle(account, localLabel));
                    }
                    return future;
                });
    }

    private CompletableFuture<Void> upSyncSingle(Account account, LabelEntity localLabel) {
        return boardDao.getBoardById(localLabel.getBoardId())
                .thenCompose(board -> {
                    if (board == null || board.getRemoteId() == null) {
                        return CompletableFuture.completedFuture(null);
                    }
                    final long remoteBoardId = board.getRemoteId();
                    DeckApi api = apiFactory.create(account).getDeckApi();
                    LabelDTO dto = LabelRemoteMapper.INSTANCE.toDTO(LabelMapper.INSTANCE.toTO(localLabel));

                    CompletableFuture<LabelDTO> call;
                    if (localLabel.getRemoteId() == null) {
                        call = api.createLabel(remoteBoardId, dto);
                    } else if (localLabel.getStatus() == DBStatus.LOCAL_DELETED.getId()) {
                        return api.deleteLabel(remoteBoardId, localLabel.getRemoteId())
                                .thenCompose(v -> labelDao.deleteRx(localLabel))
                                .thenApply(v -> null);
                    } else {
                        call = api.updateLabel(remoteBoardId, localLabel.getRemoteId(), dto);
                    }

                    return call.thenCompose(response -> {
                        if (response == null) return CompletableFuture.completedFuture((Void) null);
                        LabelEntity updatedLocal = LabelMapper.INSTANCE.toEntity(LabelRemoteMapper.INSTANCE.toTO(response));
                        updatedLocal = new LabelEntity(
                                localLabel.getLocalId(),
                                localLabel.getAccountId(),
                                updatedLocal.getRemoteId(),
                                DBStatus.UP_TO_DATE.getId(),
                                updatedLocal.getLastModified(),
                                updatedLocal.getLastModified(),
                                response.getEtag(),
                                localLabel.getBoardId(),
                                updatedLocal.getTitle(),
                                updatedLocal.getColor(),
                                null
                        );

                        CompletableFuture<Void> cleanupFuture = CompletableFuture.completedFuture(null);
                        if (localLabel.getStatus() == DBStatus.RESOLVED.getId() && localLabel.getConflictWithId() != null) {
                            cleanupFuture = labelDao.deleteById(localLabel.getConflictWithId()).thenApply(v -> null);
                        }

                        LabelEntity finalUpdatedLocal = updatedLocal;
                        return cleanupFuture.thenCompose(v -> labelDao.updateRx(finalUpdatedLocal));
                    }).handle((v, throwable) -> {
                        if (throwable != null) {
                            Throwable cause = throwable.getCause();
                            if (cause instanceof HttpException && ((HttpException) cause).code() == 412) {
                                return handleConflict(account, localLabel, remoteBoardId);
                            }
                            CompletableFuture<Void> failed = new CompletableFuture<>();
                            failed.completeExceptionally(throwable);
                            return failed;
                        }
                        return CompletableFuture.completedFuture((Void) null);
                    }).thenCompose(f -> f);
                });
    }

    private CompletableFuture<Void> handleConflict(Account account, LabelEntity localLabel, long remoteBoardId) {
        DeckApi api = apiFactory.create(account).getDeckApi();
        if (localLabel.getRemoteId() == null) return CompletableFuture.completedFuture(null);
        return api.getLabel(remoteBoardId, localLabel.getRemoteId(), null)
                .thenCompose(serverDto -> {
                    if (serverDto == null) return CompletableFuture.completedFuture(null);
                    LabelEntity serverLabel = LabelMapper.INSTANCE.toEntity(LabelRemoteMapper.INSTANCE.toTO(serverDto));
                    serverLabel = new LabelEntity(
                            0,
                            -1L,
                            serverLabel.getRemoteId(),
                            DBStatus.UP_TO_DATE.getId(),
                            serverLabel.getLastModified(),
                            serverLabel.getLastModified(),
                            serverDto.getEtag(),
                            localLabel.getBoardId(),
                            serverLabel.getTitle(),
                            serverLabel.getColor(),
                            null
                    );

                    return labelDao.insert(serverLabel)
                            .thenCompose(serverLocalId -> {
                                LabelEntity updatedLocal = new LabelEntity(
                                        localLabel.getLocalId(),
                                        localLabel.getAccountId(),
                                        localLabel.getRemoteId(),
                                        DBStatus.CONFLICT.getId(),
                                        localLabel.getLastModified(),
                                        localLabel.getLastModifiedLocal(),
                                        localLabel.getEtag(),
                                        localLabel.getBoardId(),
                                        localLabel.getTitle(),
                                        localLabel.getColor(),
                                        serverLocalId
                                );
                                return labelDao.updateRx(updatedLocal);
                            });
                });
    }

    @Override
    public CompletableFuture<Void> downSync(Account account, BoardDTO parent, Long parentLocalId, SyncStatus status, Consumer<SyncStatus> reporter) {
        if (parent == null || parent.getId() == null) return CompletableFuture.completedFuture(null);
        logger.info("Syncing labels for board " + parent.getId());

        final List<LabelDTO> serverLabels = parent.getLabels() != null ? parent.getLabels() : java.util.Collections.emptyList();

        List<Long> remoteIdsFromServer = new ArrayList<>();
        for (LabelDTO l : serverLabels) {
            if (l.getId() != null) {
                remoteIdsFromServer.add(l.getId());
            }
        }

        return labelDao.getLabelsByBoardSync(parentLocalId)
                .thenCompose(localLabels -> {
                    List<CompletableFuture<?>> futures = new ArrayList<>();
                    // Delete local labels that are missing on server
                    for (LabelEntity local : localLabels) {
                        if (local.getRemoteId() != null && !remoteIdsFromServer.contains(local.getRemoteId()) && local.getStatus() != DBStatus.LOCAL_EDITED.getId() && local.getStatus() != DBStatus.LOCAL_DELETED.getId()) {
                            logger.info("Label missing on server, deleting locally: " + local.getRemoteId());
                            futures.add(labelDao.deleteRx(local));
                        }
                    }
                    // Merge server labels
                    for (LabelDTO labelDto : serverLabels) {
                        futures.add(mergeLabel(account, labelDto, parentLocalId));
                    }
                    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                });
    }

    private CompletableFuture<Void> mergeLabel(Account account, LabelDTO labelDto, Long boardId) {
        if (labelDto.getId() == null) return CompletableFuture.completedFuture(null);
        logger.info("Merging label " + labelDto.getId() + " for board " + boardId);
        return labelDao.getLabelByRemoteId(account.id().value(), labelDto.getId())
                .handle((localLabel, throwable) -> {
                    if (throwable != null) {
                        logger.log(java.util.logging.Level.SEVERE, "Failed to get local label " + labelDto.getId(), throwable);
                    }
                    LabelEntity serverLabel = LabelMapper.INSTANCE.toEntity(LabelRemoteMapper.INSTANCE.toTO(labelDto));
                    if (throwable != null || localLabel == null) {
                        logger.info("Inserting new label " + labelDto.getId());
                        LabelEntity newLocal = new LabelEntity(
                                0,
                                account.id().value(),
                                serverLabel.getRemoteId(),
                                DBStatus.UP_TO_DATE.getId(),
                                serverLabel.getLastModified(),
                                serverLabel.getLastModified(),
                                labelDto.getEtag(),
                                boardId,
                                serverLabel.getTitle(),
                                serverLabel.getColor(),
                                null
                        );
                        return labelDao.upsert(newLocal).thenApply(v -> (Void) null);
                    } else {
                        logger.info("Updating existing label " + labelDto.getId());
                        if (localLabel.getStatus() == DBStatus.CONFLICT.getId()) {
                            return CompletableFuture.<Void>completedFuture(null);
                        }
                        if (labelDto.getEtag() != null && labelDto.getEtag().equals(localLabel.getEtag())
                            // Crucial workaround for Nextcloud Deck versions that return identical or default ETags for labels even after modifications.
                                && Objects.equals(serverLabel.getTitle(), localLabel.getTitle())
                                && Objects.equals(serverLabel.getColor(), localLabel.getColor())) {
                            return CompletableFuture.<Void>completedFuture(null);
                        }
                        LabelEntity updatedLocal = new LabelEntity(
                                localLabel.getLocalId(),
                                localLabel.getAccountId(),
                                serverLabel.getRemoteId(),
                                DBStatus.UP_TO_DATE.getId(),
                                serverLabel.getLastModified(),
                                serverLabel.getLastModified(),
                                labelDto.getEtag(),
                                boardId,
                                serverLabel.getTitle(),
                                serverLabel.getColor(),
                                null
                        );
                        return labelDao.updateRx(updatedLocal);
                    }
                }).thenCompose(f -> f);
    }
}
