package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import androidx.annotation.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.ResponseCallback;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.ocs.user.GroupMemberUIDs;
import it.niedermann.nextcloud.deck.model.ocs.user.OcsUser;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.util.AsyncUtil;

public class AccessControlDataProvider extends AbstractSyncDataProvider<AccessControl> {

    private static final Long TYPE_GROUP = 1L;
    private final List<AccessControl> acl;
    private final FullBoard board;

    public AccessControlDataProvider(@Nullable AbstractSyncDataProvider<?> parent, FullBoard board, List<AccessControl> acl) {
        super(parent);
        this.board = board;
        this.acl = acl;
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<List<AccessControl>> responder, Instant lastSync) {
        AsyncUtil.awaitAsyncWork(acl.size(), latch -> {
            for (AccessControl accessControl : acl) {
                if (TYPE_GROUP.equals(accessControl.getType())) {
                    serverAdapter.searchGroupMembers(accessControl.getUser().getUid(), new ResponseCallback<>(responder.getAccount()) {
                        @Override
                        public void onResponse(GroupMemberUIDs response) {
                            accessControl.setGroupMemberUIDs(response);
                            if (response.getUids().size() > 0) {
                                ensureGroupMembersInDB(getAccount(), dataBaseAdapter, serverAdapter, response);
                            }
                            latch.countDown();
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            super.onError(throwable);
                            latch.countDown();
                        }
                    });
                } else latch.countDown();
            }
        });

        responder.onResponse(acl);
    }

    private void ensureGroupMembersInDB(Account account, DataBaseAdapter dataBaseAdapter, ServerAdapter serverAdapter, GroupMemberUIDs response) {
        CountDownLatch memberLatch = new CountDownLatch(response.getUids().size());
        for (String uid : response.getUids()) {
            User user = dataBaseAdapter.getUserByUidDirectly(account.getId(), uid);
            if (user == null) {
                // unknown user. fetch!
                serverAdapter.getSingleUserData(uid, new ResponseCallback<>(account) {
                    @Override
                    public void onResponse(OcsUser response) {
                        DeckLog.log(response);
                        User user = new User();
                        user.setUid(response.getId());
                        user.setPrimaryKey(response.getId());
                        user.setDisplayname(response.getDisplayName());
                        dataBaseAdapter.createUser(account.getId(), user);
                        memberLatch.countDown();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        memberLatch.countDown();
                    }
                });
            } else memberLatch.countDown();
        }
        try {
            memberLatch.await();
        } catch (InterruptedException e) {
            DeckLog.logError(e);
        }
    }

    @Override
    public AccessControl getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, AccessControl entity) {
        return dataBaseAdapter.getAccessControlByRemoteIdDirectly(accountId, entity.getEntity().getId());
    }

    @Override
    public long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, AccessControl entity) {
        prepareUser(dataBaseAdapter, accountId, entity);
        long newId = dataBaseAdapter.createAccessControl(accountId, entity);
        entity.setLocalId(newId);
        handleGroupMemberships(dataBaseAdapter, entity);
        return newId;
    }

    private void handleGroupMemberships(DataBaseAdapter dataBaseAdapter, AccessControl entity) {
        if (!TYPE_GROUP.equals(entity.getType())) {
            return;
        }
        dataBaseAdapter.deleteGroupMembershipsOfGroup(entity.getUser().getLocalId());
        if (entity.getGroupMemberUIDs() == null) {
            return;
        }
        for (String groupMemberUID : entity.getGroupMemberUIDs().getUids()) {
            User member = dataBaseAdapter.getUserByUidDirectly(entity.getAccountId(), groupMemberUID);
            if (member != null) {
                dataBaseAdapter.addUserToGroup(entity.getUserId(), member.getLocalId());
            }
        }
    }

    private void prepareUser(DataBaseAdapter dataBaseAdapter, long accountId, AccessControl entity) {
        User user = dataBaseAdapter.getUserByUidDirectly(accountId, entity.getUser().getUid());
        if (user == null) {
            long userId = dataBaseAdapter.createUser(accountId, entity.getUser());
            entity.setUserId(userId);
            entity.getUser().setLocalId(userId);
        } else {
            entity.setUserId(user.getLocalId());
            entity.getUser().setLocalId(user.getLocalId());
            dataBaseAdapter.updateUser(accountId, entity.getUser(), false);
        }
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, AccessControl entity, boolean setStatus) {
        prepareUser(dataBaseAdapter, accountId, entity);
        entity.setBoardId(board.getLocalId());
        dataBaseAdapter.updateAccessControl(entity, setStatus);
        handleGroupMemberships(dataBaseAdapter, entity);
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, AccessControl entity) {
        updateInDB(dataBaseAdapter, accountId, entity, false);
    }

    @Override
    public void createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<AccessControl> responder, AccessControl entity) {
        AccessControl acl = new AccessControl(entity);
        acl.setBoardId(board.getBoard().getId());
        if (acl.getUser() == null && acl.getUserId() != null) {
            acl.setUser(dataBaseAdapter.getUserByLocalIdDirectly(acl.getUserId()));
        }
        serverAdapter.createAccessControl(board.getBoard().getId(), acl, responder);
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<AccessControl> callback, AccessControl entity) {
        serverAdapter.updateAccessControl(board.getBoard().getId(), entity, callback);
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, AccessControl accessControl) {
        dataBaseAdapter.deleteAccessControl(accessControl, true);
    }

    @Override
    public void deletePhysicallyInDB(DataBaseAdapter dataBaseAdapter, long accountId, AccessControl accessControl) {
        dataBaseAdapter.deleteGroupMembershipsOfGroup(accessControl.getUser().getLocalId());
        dataBaseAdapter.deleteAccessControl(accessControl, false);
    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, ResponseCallback<Void> callback, AccessControl entity, DataBaseAdapter dataBaseAdapter) {
        serverAdapter.deleteAccessControl(board.getBoard().getId(), entity, callback);
    }

    @Override
    public List<AccessControl> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Instant lastSync) {
        return dataBaseAdapter.getLocallyChangedAccessControl(accountId, board.getLocalId());
    }
}
