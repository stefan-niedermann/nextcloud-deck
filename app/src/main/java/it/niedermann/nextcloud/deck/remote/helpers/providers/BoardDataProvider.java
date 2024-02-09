package it.niedermann.nextcloud.deck.remote.helpers.providers;

import android.annotation.SuppressLint;
import android.util.Pair;

import androidx.lifecycle.MutableLiveData;

import com.nextcloud.android.sso.api.EmptyResponse;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.database.DataBaseAdapter;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.remote.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import it.niedermann.nextcloud.deck.remote.helpers.SyncHelper;
import it.niedermann.nextcloud.deck.remote.helpers.util.AsyncUtil;
import okhttp3.Headers;

public class BoardDataProvider extends AbstractSyncDataProvider<FullBoard> {

    static Instant Y2K = LocalDate.parse("2000-01-01").atStartOfDay().toInstant(ZoneOffset.UTC);
    private int progressTotal = 0;
    private int progressDone = 0;
    private boolean isParallel = true;
    private MutableLiveData<Pair<Integer, Integer>> progress = null;

    private ResponseCallback<Boolean> stepByStepCallback;

    public BoardDataProvider() {
        super(null);
    }

    public BoardDataProvider(MutableLiveData<Pair<Integer, Integer>> progress) {
        this();
        this.progress = progress;
    }

    public BoardDataProvider(MutableLiveData<Pair<Integer, Integer>> progress$, boolean isParallel) {
        this(progress$);
        this.isParallel = isParallel;
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<List<FullBoard>> responder, Instant lastSync) {
        serverAdapter.getBoards(new ResponseCallback<>(responder.getAccount()) {
            @Override
            public void onResponse(List<FullBoard> response, Headers headers) {
                progressTotal = response.size();
                updateProgress();
                String etag = headers.get("ETag");
                if (etag != null && !etag.equals(account.getBoardsEtag())) {
                    account.setBoardsEtag(etag);
                    dataBaseAdapter.updateAccount(account);
                }
                List<FullBoard> ret = response;
                if (response != null) {
                    ret = response.stream()
                            .filter(b -> b.getBoard().getDeletedAt() == null || b.getBoard().getDeletedAt().isBefore(Y2K))
                            .toList();
                }
                responder.onResponse(ret, headers);
            }

            @SuppressLint("MissingSuperCall")
            @Override
            public void onError(Throwable throwable) {
                responder.onError(throwable);
            }
        });
    }

    private void updateProgress() {
        if (progress != null) {
            DeckLog.log("New progress post", progressDone, progressTotal);
            progress.postValue(Pair.create(progressDone, progressTotal));
        }
    }

    @Override
    protected boolean removeChild(AbstractSyncDataProvider<?> child) {
        boolean isRemoved = super.removeChild(child);
        if (isRemoved && child.getClass() == StackDataProvider.class) {
            progressDone++;
            updateProgress();
        }
        return isRemoved;
    }

    @Override
    public FullBoard getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard entity) {
        return dataBaseAdapter.getFullBoardByRemoteIdDirectly(accountId, entity.getEntity().getId());
    }

    @Override
    public long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard entity) {
        handleOwner(dataBaseAdapter, accountId, entity);
        final long localId = dataBaseAdapter.createBoardDirectly(accountId, entity.getBoard());
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
    public void goDeeper(SyncHelper syncHelper, FullBoard existingEntity, FullBoard entityFromServer, ResponseCallback<Boolean> callback) {
        if (!isParallel) {
            stepByStepCallback = callback;
        }
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
    public void childDone(AbstractSyncDataProvider<?> child, ResponseCallback<Boolean> responseCallback, boolean syncChangedSomething) {
        removeChild(child);
        if (!stillGoingDeeper && children.isEmpty()) {
            if (parent != null) {
                parent.childDone(this, responseCallback, syncChangedSomething);
            } else {
                responseCallback.onResponse(syncChangedSomething, IResponseCallback.EMPTY_HEADERS);
            }
        } else if (!isParallel && children.isEmpty()) {
            stepByStepCallback.onResponse(syncChangedSomething, IResponseCallback.EMPTY_HEADERS);
        }
    }

    @Override
    public void createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<FullBoard> responder, FullBoard entity) {
        serverAdapter.createBoard(entity.getBoard(), responder);
    }

    @Override
    public List<FullBoard> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Instant lastSync) {
        return dataBaseAdapter.getLocallyChangedBoards(accountId);
    }

    @Override
    public void goDeeperForUpSync(SyncHelper syncHelper, ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, ResponseCallback<Boolean> callback) {
        Long accountId = callback.getAccount().getId();
        List<Label> locallyChangedLabels = dataBaseAdapter.getLocallyChangedLabels(accountId);
        AsyncUtil.awaitAsyncWork(locallyChangedLabels.size(), latch -> {
            for (Label label : locallyChangedLabels) {
                Board board = dataBaseAdapter.getBoardByLocalIdDirectly(label.getBoardId());
                label.setBoardId(board.getId());
                syncHelper.doUpSyncFor(new LabelDataProvider(this, board, Collections.singletonList(label)), latch);
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
    public void updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<FullBoard> callback, FullBoard entity) {
        serverAdapter.updateBoard(entity.getBoard(), callback);
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard board) {
        dataBaseAdapter.saveNeighbourOfBoard(board.getAccountId(), board.getLocalId());
        dataBaseAdapter.removeCurrentStackId(board.getAccountId(), board.getLocalId());
        dataBaseAdapter.deleteBoard(board.getBoard(), true);
    }

    @Override
    public void deletePhysicallyInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard board) {
        dataBaseAdapter.saveNeighbourOfBoard(board.getAccountId(), board.getLocalId());
        dataBaseAdapter.removeCurrentStackId(board.getAccountId(), board.getLocalId());
        dataBaseAdapter.deleteBoardPhysically(board.getBoard());
    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, ResponseCallback<EmptyResponse> callback, FullBoard entity, DataBaseAdapter dataBaseAdapter) {
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

            dataBaseAdapter.saveNeighbourOfBoard(board.getAccountId(), board.getLocalId());
            dataBaseAdapter.deleteBoardPhysically(board.getBoard());
        }
    }
}
