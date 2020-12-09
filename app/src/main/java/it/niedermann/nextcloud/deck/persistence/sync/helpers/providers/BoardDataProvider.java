package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import android.annotation.SuppressLint;

import com.nextcloud.android.sso.api.ParsedResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.util.AsyncUtil;

public class BoardDataProvider extends AbstractSyncDataProvider<FullBoard> {

    public BoardDataProvider() {
        super(null);
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<List<FullBoard>> responder, Instant lastSync) {
        serverAdapter.getBoards(new IResponseCallback<ParsedResponse<List<FullBoard>>>(responder.getAccount()) {
            @Override
            public void onResponse(ParsedResponse<List<FullBoard>> response) {
                String etag = response.getHeaders().get("ETag");
                if (etag != null && !etag.equals(account.getBoardsEtag())) {
                    account.setBoardsEtag(etag);
                    dataBaseAdapter.updateAccount(account);
                }
                responder.onResponse(response.getResponse());
            }

            @SuppressLint("MissingSuperCall")
            @Override
            public void onError(Throwable throwable) {
                responder.onError(throwable);
            }
        });
    }

    @Override
    public FullBoard getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard entitiy) {
        return dataBaseAdapter.getFullBoardByRemoteIdDirectly(accountId, entitiy.getEntity().getId());
    }

    @Override
    public long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard entity) {
        handleOwner(dataBaseAdapter, accountId, entity);
        Long localId = dataBaseAdapter.createBoardDirectly(accountId, entity.getBoard());
        entity.getBoard().setLocalId(localId);
        handleUsers(dataBaseAdapter, accountId, entity);
        return localId;
    }

    private void handleOwner(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard entity) {
        if (entity.getOwner() != null) {
            User owner = createOrUpdateUser(dataBaseAdapter, accountId, entity.getOwner());
            entity.getBoard().setOwnerId(owner.getLocalId());
        }
    }

    private void handleUsers(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard entity) {
        dataBaseAdapter.deleteBoardMembershipsOfBoard(entity.getLocalId());
        if (entity.getUsers() != null && !entity.getUsers().isEmpty()) {
            for (User user : entity.getUsers()) {
                if (user == null) {
                    continue;
                }
                User existing = createOrUpdateUser(dataBaseAdapter, accountId, user);
                dataBaseAdapter.addUserToBoard(existing.getLocalId(), entity.getLocalId());
            }
        }
    }

    private User createOrUpdateUser(DataBaseAdapter dataBaseAdapter, long accountId, User remoteUser) {
        User owner = dataBaseAdapter.getUserByUidDirectly(accountId, remoteUser.getUid());
        if (owner == null) {
            dataBaseAdapter.createUser(accountId, remoteUser);
        } else {
            dataBaseAdapter.updateUser(accountId, remoteUser, false);
        }
        return dataBaseAdapter.getUserByUidDirectly(accountId, remoteUser.getUid());
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard entity, boolean setStatus) {
        handleDefaultLabels(dataBaseAdapter, entity);
        handleOwner(dataBaseAdapter, accountId, entity);
        dataBaseAdapter.updateBoard(entity.getBoard(), setStatus);
        handleUsers(dataBaseAdapter, accountId, entity);
    }

    private void handleDefaultLabels(DataBaseAdapter dataBaseAdapter, FullBoard entity) {
        // ## merge labels (created at board creation):
        // the server creates four default labels. if a board is copied, they will also be copied. At sync, after creating the board, the labels are already there.
        // this merges the created default ones with the ones i already have.
        if (entity != null && entity.getLabels() != null) {
            for (Label label : entity.getLabels()) {
                // does this label exist and unknown to server yet?
                Label existing = dataBaseAdapter.getLabelByBoardIdAndTitleDirectly(entity.getLocalId(), label.getTitle());
                if (existing != null && existing.getId() == null) {
                    // take our label and lets say it IS the same as on server (but use the local color, no matter what the server says)
                    existing.setId(label.getId());
                    dataBaseAdapter.updateLabel(existing, false);
                }
            }
        }
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard entity) {
        updateInDB(dataBaseAdapter, accountId, entity, false);
    }


    @Override
    public void goDeeper(SyncHelper syncHelper, FullBoard existingEntity, FullBoard entityFromServer, IResponseCallback<Boolean> callback) {
        List<Label> labels = entityFromServer.getLabels();
        if (labels != null && !labels.isEmpty()) {
            syncHelper.doSyncFor(new LabelDataProvider(this, existingEntity.getBoard(), labels));
        }

        List<AccessControl> acl = entityFromServer.getParticipants();
        if (acl != null && !acl.isEmpty()) {
            for (AccessControl ac : acl) {
                ac.setBoardId(existingEntity.getLocalId());
            }
            syncHelper.doSyncFor(new AccessControlDataProvider(this, existingEntity, acl));
        }

        if (entityFromServer.getStacks() != null && !entityFromServer.getStacks().isEmpty()) {
            syncHelper.doSyncFor(new StackDataProvider(this, existingEntity));
        }
    }

    @Override
    public void createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<FullBoard> responder, FullBoard entity) {
        serverAdapter.createBoard(entity.getBoard(), responder);
    }

    @Override
    public List<FullBoard> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Instant lastSync) {
        return dataBaseAdapter.getLocallyChangedBoards(accountId);
    }

    @Override
    public void goDeeperForUpSync(SyncHelper syncHelper, ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, IResponseCallback<Boolean> callback) {
        Long accountId = callback.getAccount().getId();
        List<Label> locallyChangedLabels = dataBaseAdapter.getLocallyChangedLabels(accountId);
        AsyncUtil.awaitAsyncWork(locallyChangedLabels.size(), (countDownLatch) -> {
            for (Label label : locallyChangedLabels) {
                Board board = dataBaseAdapter.getBoardByLocalIdDirectly(label.getBoardId());
                label.setBoardId(board.getId());
                syncHelper.doUpSyncFor(new LabelDataProvider(this, board, Collections.singletonList(label)), countDownLatch);
            }
        });

        List<Long> localBoardIDsWithChangedACL = dataBaseAdapter.getBoardIDsOfLocallyChangedAccessControl(accountId);
        for (Long boardId : localBoardIDsWithChangedACL) {
            syncHelper.doUpSyncFor(new AccessControlDataProvider(this, dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, boardId), new ArrayList<>()));
        }

        Set<Long> syncedBoards = new HashSet<>();
        List<FullStack> locallyChangedStacks = dataBaseAdapter.getLocallyChangedStacks(accountId);
        if (locallyChangedStacks.size() < 1) {
            // no changed stacks? maybe cards! So we have to go deeper!
            new StackDataProvider(this, null).goDeeperForUpSync(syncHelper, serverAdapter, dataBaseAdapter, callback);
        } else {
            for (FullStack locallyChangedStack : locallyChangedStacks) {
                long boardId = locallyChangedStack.getStack().getBoardId();
                boolean added = syncedBoards.add(boardId);
                if (added) {
                    FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, boardId);
                    locallyChangedStack.getStack().setBoardId(board.getId());
                    syncHelper.doUpSyncFor(new StackDataProvider(this, board));
                }
            }
        }
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<FullBoard> callback, FullBoard entity) {
        serverAdapter.updateBoard(entity.getBoard(), callback);
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard fullBoard) {
        dataBaseAdapter.deleteBoard(fullBoard.getBoard(), true);
    }

    @Override
    public void deletePhysicallyInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard fullBoard) {
        dataBaseAdapter.deleteBoardPhysically(fullBoard.getBoard());
    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Void> callback, FullBoard entity, DataBaseAdapter dataBaseAdapter) {
        serverAdapter.deleteBoard(entity.getBoard(), callback);
    }

    @Override
    public void handleDeletes(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, List<FullBoard> entitiesFromServer) {
        List<FullBoard> localBoards = dataBaseAdapter.getAllFullBoards(accountId);
        List<FullBoard> delta = findDelta(entitiesFromServer, localBoards);
        for (FullBoard board : delta) {
            if (board.getId() == null) {
                // not pushed up yet so:
                continue;
            }
            dataBaseAdapter.deleteBoardPhysically(board.getBoard());
        }
    }
}
