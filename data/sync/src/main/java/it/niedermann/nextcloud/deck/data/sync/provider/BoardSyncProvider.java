package it.niedermann.nextcloud.deck.data.sync.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.data.local.dao.BoardDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinBoardWithPermissionDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinBoardWithUserDao;
import it.niedermann.nextcloud.deck.data.local.entity.BoardEntity;
import it.niedermann.nextcloud.deck.data.local.entity.JoinBoardWithPermissionEntity;
import it.niedermann.nextcloud.deck.data.local.entity.JoinBoardWithUserEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.BoardMapper;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;
import it.niedermann.nextcloud.remote.ApiProvider;
import it.niedermann.nextcloud.remote.deck.DeckApi;
import it.niedermann.nextcloud.remote.deck.dto.BoardDTO;
import it.niedermann.nextcloud.remote.deck.mapper.BoardRemoteMapper;
import jakarta.inject.Inject;
import retrofit2.HttpException;

public class BoardSyncProvider implements SyncProvider<Void> {

    private static final Logger logger = Logger.getLogger(BoardSyncProvider.class.getName());

    private final BoardDao boardDao;
    private final JoinBoardWithUserDao joinBoardWithUserDao;
    private final JoinBoardWithPermissionDao joinBoardWithPermissionDao;
    private final UserSyncHelper userSyncHelper;
    private final ApiProvider.Factory apiFactory;
    private final Map<String, CompletableFuture<Long>> inFlightBoardSyncs = new ConcurrentHashMap<>();
    private ColumnSyncProvider columnSyncProvider;
    private LabelSyncProvider labelSyncProvider;
    private AccessControlSyncProvider accessControlSyncProvider;

    @Inject
    public BoardSyncProvider(BoardDao boardDao, JoinBoardWithUserDao joinBoardWithUserDao, JoinBoardWithPermissionDao joinBoardWithPermissionDao, UserSyncHelper userSyncHelper, ApiProvider.Factory apiFactory) {
        this.boardDao = boardDao;
        this.joinBoardWithUserDao = joinBoardWithUserDao;
        this.joinBoardWithPermissionDao = joinBoardWithPermissionDao;
        this.userSyncHelper = userSyncHelper;
        this.apiFactory = apiFactory;
    }

    @Inject
    public void setColumnSyncProvider(ColumnSyncProvider columnSyncProvider) {
        this.columnSyncProvider = columnSyncProvider;
    }

    @Inject
    public void setLabelSyncProvider(LabelSyncProvider labelSyncProvider) {
        this.labelSyncProvider = labelSyncProvider;
    }

    @Inject
    public void setAccessControlSyncProvider(AccessControlSyncProvider accessControlSyncProvider) {
        this.accessControlSyncProvider = accessControlSyncProvider;
    }

    @Override
    public CompletableFuture<Void> upSync(Account account, SyncStatus status, Consumer<SyncStatus> reporter) {
        return boardDao.getChangedBoards(account.id().value())
                .thenCompose(changedBoards -> {
                    if (changedBoards == null || changedBoards.isEmpty()) {
                        logger.info("No changed boards found for account: " + account.id().value());
                        return CompletableFuture.completedFuture(null);
                    }
                    logger.info("Found " + changedBoards.size() + " changed boards for account: " + account.id().value());
                    CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
                    for (BoardEntity localBoard : changedBoards) {
                        final var finalFuture = future;
                        future = finalFuture.thenCompose(v -> upSyncSingle(account, localBoard));
                    }
                    return future;
                });
    }

    private CompletableFuture<Void> upSyncSingle(Account account, BoardEntity localBoard) {
        DeckApi api = apiFactory.create(account).getDeckApi();
        BoardDTO dto = BoardRemoteMapper.INSTANCE.toDTO(BoardMapper.INSTANCE.toTO(localBoard));

        CompletableFuture<BoardDTO> call;
        if (localBoard.getRemoteId() == null) {
            logger.info("Creating board: " + localBoard.getTitle());
            call = api.createBoard(dto);
        } else if (localBoard.getStatus() == DBStatus.LOCAL_DELETED.getId()) {
            logger.info("Deleting board: " + localBoard.getRemoteId());
            if (localBoard.getRemoteId() == null) {
                return boardDao.deleteRx(localBoard).thenApply(v -> null);
            }
            return api.deleteBoard(localBoard.getRemoteId())
                    .thenCompose(v -> boardDao.deleteRx(localBoard))
                    .thenApply(v -> null);
        } else {
            logger.info("Updating board: " + localBoard.getRemoteId());
            call = api.updateBoard(localBoard.getRemoteId(), dto);
        }

        return call.thenCompose(response -> {
            if (response == null) return CompletableFuture.completedFuture((Void) null);
            BoardEntity updatedLocal = BoardMapper.INSTANCE.toEntity(BoardRemoteMapper.INSTANCE.toTO(response));
            updatedLocal = new BoardEntity(
                    localBoard.getLocalId(),
                    localBoard.getAccountId(),
                    updatedLocal.getRemoteId(),
                    DBStatus.UP_TO_DATE.getId(),
                    updatedLocal.getLastModified(),
                    updatedLocal.getLastModified(),
                    updatedLocal.getEtag(),
                    updatedLocal.getTitle(),
                    updatedLocal.getOwnerId(),
                    updatedLocal.getColor(),
                    updatedLocal.getArchived(),
                    updatedLocal.getShared(),
                    updatedLocal.getDeletedAt(),
                    updatedLocal.getPermissionRead(),
                    updatedLocal.getPermissionEdit(),
                    updatedLocal.getPermissionManage(),
                    updatedLocal.getPermissionShare(),
                    null
            );

            CompletableFuture<Void> cleanupFuture = CompletableFuture.completedFuture(null);
            if (localBoard.getStatus() == DBStatus.RESOLVED.getId() && localBoard.getConflictWithId() != null) {
                cleanupFuture = boardDao.deleteById(localBoard.getConflictWithId()).thenApply(v -> null);
            }

            BoardEntity finalUpdatedLocal = updatedLocal;
            return cleanupFuture.thenCompose(v -> boardDao.updateRx(finalUpdatedLocal));
        }).handle((v, throwable) -> {
            if (throwable != null) {
                Throwable cause = throwable.getCause();
                if (cause instanceof HttpException && ((HttpException) cause).code() == 412) {
                    return handleConflict(account, localBoard);
                }
                CompletableFuture<Void> failed = new CompletableFuture<>();
                failed.completeExceptionally(throwable);
                return failed;
            }
            return CompletableFuture.completedFuture((Void) null);
        }).thenCompose(f -> f);
    }

    private CompletableFuture<Void> handleConflict(Account account, BoardEntity localBoard) {
        DeckApi api = apiFactory.create(account).getDeckApi();
        if (localBoard.getRemoteId() == null) return CompletableFuture.completedFuture(null);
        return api.getBoard(localBoard.getRemoteId(), null)
                .thenCompose(serverDto -> {
                    if (serverDto == null) return CompletableFuture.completedFuture(null);
                    BoardEntity serverBoard = BoardMapper.INSTANCE.toEntity(BoardRemoteMapper.INSTANCE.toTO(serverDto));
                    serverBoard = new BoardEntity(
                            0,
                            -1L,
                            serverBoard.getRemoteId(),
                            DBStatus.UP_TO_DATE.getId(),
                            serverBoard.getLastModified(),
                            serverBoard.getLastModified(),
                            serverBoard.getEtag(),
                            serverBoard.getTitle(),
                            serverBoard.getOwnerId(),
                            serverBoard.getColor(),
                            serverBoard.getArchived(),
                            serverBoard.getShared(),
                            serverBoard.getDeletedAt(),
                            serverBoard.getPermissionRead(),
                            serverBoard.getPermissionEdit(),
                            serverBoard.getPermissionManage(),
                            serverBoard.getPermissionShare(),
                            null
                    );

                    return boardDao.insert(serverBoard)
                            .thenCompose(serverLocalId -> {
                                BoardEntity updatedLocal = new BoardEntity(
                                        localBoard.getLocalId(),
                                        localBoard.getAccountId(),
                                        localBoard.getRemoteId(),
                                        DBStatus.CONFLICT.getId(),
                                        localBoard.getLastModified(),
                                        localBoard.getLastModifiedLocal(),
                                        localBoard.getEtag(),
                                        localBoard.getTitle(),
                                        localBoard.getOwnerId(),
                                        localBoard.getColor(),
                                        localBoard.getArchived(),
                                        localBoard.getShared(),
                                        localBoard.getDeletedAt(),
                                        localBoard.getPermissionRead(),
                                        localBoard.getPermissionEdit(),
                                        localBoard.getPermissionManage(),
                                        localBoard.getPermissionShare(),
                                        serverLocalId
                                );
                                return boardDao.updateRx(updatedLocal);
                            });
                });
    }

    @Override
    public CompletableFuture<Void> downSync(Account account, Void parent, Long parentLocalId, SyncStatus status, Consumer<SyncStatus> reporter) {
        DeckApi api = apiFactory.create(account).getDeckApi();
        return api.getBoards(true, null, null)
                .thenCompose(boards -> {
                    if (boards == null) return CompletableFuture.completedFuture(null);

                    // Identify boards to delete locally (present in DB but missing from server response)
                    List<Long> remoteIdsFromServer = new ArrayList<>();
                    for (BoardDTO b : boards) {
                        if (b.getId() != null) {
                            remoteIdsFromServer.add(b.getId());
                        }
                    }

                    return boardDao.getAllBoardsByAccount(account.id().value())
                            .thenCompose(localBoards -> {
                                List<CompletableFuture<?>> deleteFutures = new ArrayList<>();
                                for (BoardEntity local : localBoards) {
                                    if (local.getRemoteId() != null && !remoteIdsFromServer.contains(local.getRemoteId()) && local.getStatus() != DBStatus.LOCAL_EDITED.getId() && local.getStatus() != DBStatus.LOCAL_DELETED.getId()) {
                                        logger.info("Board missing on server, deleting locally: " + local.getRemoteId());
                                        deleteFutures.add(boardDao.deleteRx(local));
                                    }
                                }
                                return CompletableFuture.allOf(deleteFutures.toArray(new CompletableFuture[0]));
                            })
                            .thenCompose(v -> {
                                long total = boards.size();
                                CompletableFuture<?>[] boardFutures = new CompletableFuture[boards.size()];
                                for (int i = 0; i < boards.size(); i++) {
                                    BoardDTO boardDto = boards.get(i);
                                    if (boardDto == null) {
                                        boardFutures[i] = CompletableFuture.completedFuture(null);
                                        continue;
                                    }
                                    final long finished = i + 1;
                                    boardFutures[i] = syncFullBoard(account, boardDto, total, finished, status, reporter);
                                }
                                return CompletableFuture.allOf(boardFutures);
                            });
                });
    }

    private CompletableFuture<Void> syncFullBoard(Account account, BoardDTO boardDto, long total, long finished, SyncStatus status, Consumer<SyncStatus> reporter) {
        final String key = account.id().value() + ":" + boardDto.getId();
        return inFlightBoardSyncs.compute(key, (k, existingFuture) -> {
            if (existingFuture != null && !existingFuture.isCompletedExceptionally()) {
                return existingFuture;
            }
            final var future = mergeBoard(account, boardDto)
                    .thenCompose(localBoardId -> {
                        SyncStatus newStatus = status.withBoards(total, finished, boardDto.getTitle());
                        reporter.accept(newStatus);
                        return CompletableFuture.allOf(
                                syncBoardUsers(account, boardDto, localBoardId),
                                syncBoardPermissions(account, boardDto, localBoardId),
                                labelSyncProvider.downSync(account, boardDto, localBoardId, newStatus, reporter),
                                accessControlSyncProvider.downSync(account, boardDto, localBoardId, newStatus, reporter)
                        ).thenCompose(v -> columnSyncProvider.downSync(account, boardDto, localBoardId, newStatus, reporter))
                                .thenApply(v -> localBoardId);
                    });
            future.whenComplete((v, t) -> inFlightBoardSyncs.remove(key));
            return future;
        }).handle((v, t) -> {
            if (t != null) {
                Throwable cause = t.getCause();
                if (cause instanceof HttpException && (((HttpException) cause).code() == 403 || ((HttpException) cause).code() == 404)) {
                    logger.warning("Board " + boardDto.getId() + " disappeared during sync (403/404)");
                    return boardDao.getBoardByRemoteId(account.id().value(), boardDto.getId())
                            .thenCompose(localBoard -> {
                                if (localBoard != null) {
                                    return boardDao.deleteRx(localBoard);
                                }
                                return CompletableFuture.completedFuture(null);
                            });
                }
                CompletableFuture<Void> f = new CompletableFuture<>();
                f.completeExceptionally(t);
                return f;
            }
            return CompletableFuture.completedFuture((Void) null);
        }).thenCompose(f -> f);
    }

    private CompletableFuture<Void> syncBoardUsers(Account account, BoardDTO boardDto, Long localBoardId) {
        if (boardDto.getUsers() == null) return CompletableFuture.completedFuture(null);
        logger.info("Syncing " + boardDto.getUsers().size() + " users for board " + boardDto.getId());
        return joinBoardWithUserDao.deleteByBoardId(localBoardId)
                .thenCompose(v -> {
                    CompletableFuture<?>[] futures = new CompletableFuture[boardDto.getUsers().size()];
                    for (int i = 0; i < boardDto.getUsers().size(); i++) {
                        var userDto = boardDto.getUsers().get(i);
                        futures[i] = userSyncHelper.syncUser(account, userDto)
                                .thenCompose(localUser -> {
                                    if (localUser == null) return CompletableFuture.completedFuture(null);
                                    return joinBoardWithUserDao.upsert(new JoinBoardWithUserEntity(localBoardId, localUser.getLocalId(), DBStatus.UP_TO_DATE.getId()))
                                            .thenApply(v3 -> null);
                                });
                    }
                    return CompletableFuture.allOf(futures);
                });
    }

    private CompletableFuture<Void> syncBoardPermissions(Account account, BoardDTO boardDto, Long localBoardId) {
        if (boardDto.getPermissions() == null) return CompletableFuture.completedFuture(null);
        logger.info("Syncing permissions for board " + boardDto.getId());
        return joinBoardWithPermissionDao.deleteByBoardId(localBoardId)
                .thenCompose(v -> {
                    var permissions = boardDto.getPermissions();
                    List<CompletableFuture<?>> futures = new ArrayList<>();
                    if (Boolean.TRUE.equals(permissions.getPermissionRead())) {
                        futures.add(joinBoardWithPermissionDao.upsert(new JoinBoardWithPermissionEntity(localBoardId, 1L, DBStatus.UP_TO_DATE.getId())));
                    }
                    if (Boolean.TRUE.equals(permissions.getPermissionEdit())) {
                        futures.add(joinBoardWithPermissionDao.upsert(new JoinBoardWithPermissionEntity(localBoardId, 2L, DBStatus.UP_TO_DATE.getId())));
                    }
                    if (Boolean.TRUE.equals(permissions.getPermissionManage())) {
                        futures.add(joinBoardWithPermissionDao.upsert(new JoinBoardWithPermissionEntity(localBoardId, 3L, DBStatus.UP_TO_DATE.getId())));
                    }
                    if (Boolean.TRUE.equals(permissions.getPermissionShare())) {
                        futures.add(joinBoardWithPermissionDao.upsert(new JoinBoardWithPermissionEntity(localBoardId, 4L, DBStatus.UP_TO_DATE.getId())));
                    }
                    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                });
    }

    private CompletableFuture<Long> mergeBoard(Account account, BoardDTO boardDto) {
        if (boardDto.getId() == null) return CompletableFuture.completedFuture(null);
        logger.info("Merging board: " + boardDto.getId());
        CompletableFuture<Long> userIdFuture = userSyncHelper.syncUser(account, boardDto.getOwner())
                .thenApply(user -> user != null ? user.getLocalId() : null);

        return userIdFuture.thenCompose(ownerLocalId -> boardDao.getBoardByRemoteId(account.id().value(), boardDto.getId())
                .thenCompose(localBoard -> {
                    final long existingLocalId = localBoard != null ? localBoard.getLocalId() : 0;
                    BoardEntity serverBoard = BoardMapper.INSTANCE.toEntity(BoardRemoteMapper.INSTANCE.toTO(boardDto));
                    if (localBoard == null || serverBoard.getEtag() == null || !serverBoard.getEtag().equals(localBoard.getEtag())) {
                        BoardEntity newLocal = new BoardEntity(
                                existingLocalId,
                                account.id().value(),
                                serverBoard.getRemoteId(),
                                DBStatus.UP_TO_DATE.getId(),
                                serverBoard.getLastModified(),
                                serverBoard.getLastModified(),
                                serverBoard.getEtag(),
                                serverBoard.getTitle(),
                                ownerLocalId,
                                serverBoard.getColor(),
                                serverBoard.getArchived(),
                                serverBoard.getShared(),
                                serverBoard.getDeletedAt(),
                                serverBoard.getPermissionRead(),
                                serverBoard.getPermissionEdit(),
                                serverBoard.getPermissionManage(),
                                serverBoard.getPermissionShare(),
                                null
                        );
                        return boardDao.upsert(newLocal).thenCompose(id -> {
                            if (id != -1) {
                                return CompletableFuture.completedFuture(id);
                            } else if (existingLocalId != 0) {
                                return CompletableFuture.completedFuture(existingLocalId);
                            } else {
                                return boardDao.getBoardByRemoteId(account.id().value(), boardDto.getId())
                                        .thenApply(b -> b != null ? b.getLocalId() : null);
                            }
                        });
                    } else {
                        return CompletableFuture.completedFuture(localBoard.getLocalId());
                    }
                }));
    }
}
