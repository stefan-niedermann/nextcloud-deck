package it.niedermann.nextcloud.deck.data.sync.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.data.local.dao.BoardDao;
import it.niedermann.nextcloud.deck.data.local.dao.UserDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinBoardWithUserDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinBoardWithPermissionDao;
import it.niedermann.nextcloud.deck.data.local.entity.BoardEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.BoardMapper;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;
import it.niedermann.nextcloud.remote.ApiProvider;
import it.niedermann.nextcloud.remote.deck.DeckApi;
import it.niedermann.nextcloud.remote.deck.dto.BoardDTO;
import it.niedermann.nextcloud.deck.data.local.entity.JoinBoardWithUserEntity;
import it.niedermann.nextcloud.deck.data.local.entity.JoinBoardWithPermissionEntity;
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
                    if (changedBoards == null) return CompletableFuture.completedFuture(null);
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
            call = api.createBoard(dto);
        } else if (localBoard.getStatus() == DBStatus.LOCAL_DELETED.getId()) {
            return api.deleteBoard(localBoard.getRemoteId())
                    .thenCompose(v -> boardDao.deleteRx(localBoard))
                    .thenApply(v -> null);
        } else {
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
                    long total = boards.size();
                    CompletableFuture<?>[] boardFutures = new CompletableFuture[boards.size()];
                    for (int i = 0; i < boards.size(); i++) {
                        BoardDTO boardDto = boards.get(i);
                        if (boardDto == null) {
                            boardFutures[i] = CompletableFuture.completedFuture(null);
                            continue;
                        }
                        final long finished = i + 1;
                        boardFutures[i] = mergeBoard(account, boardDto)
                                .thenCompose(localBoardId -> {
                                    SyncStatus newStatus = status.withBoards(total, finished, boardDto.getTitle());
                                    reporter.accept(newStatus);
                                    return CompletableFuture.allOf(
                                            syncBoardUsers(account, boardDto, localBoardId),
                                            syncBoardPermissions(account, boardDto, localBoardId),
                                            labelSyncProvider.downSync(account, boardDto, localBoardId, newStatus, reporter),
                                            accessControlSyncProvider.downSync(account, boardDto, localBoardId, newStatus, reporter)
                                    ).thenCompose(v -> columnSyncProvider.downSync(account, boardDto, localBoardId, newStatus, reporter));
                                });
                    }
                    return CompletableFuture.allOf(boardFutures);
                });
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
        CompletableFuture<Long> userIdFuture = userSyncHelper.syncUser(account, boardDto.getOwner())
                .thenApply(user -> user != null ? user.getLocalId() : null);
        return userIdFuture.thenCompose(ownerLocalId -> boardDao.getBoardByRemoteId(account.id().value(), boardDto.getId())
                .handle((localBoard, throwable) -> {
                    BoardEntity serverBoard = BoardMapper.INSTANCE.toEntity(BoardRemoteMapper.INSTANCE.toTO(boardDto));
                    if (throwable != null || localBoard == null) {
                        BoardEntity newLocal = new BoardEntity(
                                0,
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
                        return boardDao.insertOrReplace(newLocal);
                    } else {
                        if (localBoard.getStatus() == DBStatus.CONFLICT.getId()) {
                            return CompletableFuture.completedFuture(localBoard.getLocalId());
                        }
                        if (serverBoard.getEtag() != null && serverBoard.getEtag().equals(localBoard.getEtag())) {
                            return CompletableFuture.completedFuture(localBoard.getLocalId());
                        }
                        BoardEntity updatedLocal = new BoardEntity(
                                localBoard.getLocalId(),
                                localBoard.getAccountId(),
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
                        return boardDao.updateRx(updatedLocal).thenApply(v -> localBoard.getLocalId());
                    }
                }).thenCompose(f -> f));
    }
}
