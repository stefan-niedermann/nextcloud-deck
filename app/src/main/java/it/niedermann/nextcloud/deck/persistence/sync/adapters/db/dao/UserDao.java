package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.User;

@Dao
public interface UserDao extends GenericDao<User> {

    @Query("SELECT * FROM user WHERE accountId = :accountId")
    LiveData<List<User>> getUsersForAccount(final long accountId);

    @Query("SELECT * FROM user WHERE accountId = :accountId and localId = :localId")
    LiveData<User> getUserByLocalId(final long accountId, final long localId);

    @Query("SELECT * FROM user WHERE accountId = :accountId and uid = :uid")
    LiveData<User> getUserByUid(final long accountId, final String uid);

    @Query("SELECT u.* FROM user u WHERE accountId = :accountId " +
            "    AND NOT EXISTS (" +
            "            select 1 from joincardwithuser ju" +
            "            where ju.userId = u.localId" +
            "            and ju.cardId = :notYetAssignedToLocalCardId AND status <> 3" + // not LOCAL_DELETED
            "    )" +
            "  AND ( " +
            "    EXISTS (" +
            "            select 1 from userinboard where boardId = :boardId AND userId = u.localId" +
            "    )" +
            "    OR" +
            "    EXISTS (" +
            "       select 1 from accesscontrol" + //  v GROUP!
            "       where (userId = u.localId OR (type = 1 and exists(select 1 from UserInGroup uig where uig.memberId = u.localId and uig.groupId = userId))) " +
            "           and boardId = :boardId and status <> 3" +
            "    )" +
            "    OR" +
            "    EXISTS (" +
            "            select 1 from board where localId = :boardId AND ownerId = u.localId" +
            "    )" +
            ")" +
            "and ( uid LIKE :searchTerm or displayname LIKE :searchTerm or primaryKey LIKE :searchTerm )")
    LiveData<List<User>> searchUserByUidOrDisplayName(final long accountId, final long boardId, final long notYetAssignedToLocalCardId, final String searchTerm);

    @Query("SELECT u.* FROM user u WHERE accountId = :accountId " +
            "    AND NOT EXISTS (" +
            "            select 1 from accesscontrol ju" +
            "            where ju.userId = u.localId and ju.boardId = :boardId and status <> 3" + // not LOCAL_DELETED
            "    ) " +
            "and ( uid LIKE :searchTerm or displayname LIKE :searchTerm or primaryKey LIKE :searchTerm ) " +
            "and u.localId <> (select b.ownerId from board b where localId = :boardId)" +
            "ORDER BY u.displayname")
    List<User> searchUserByUidOrDisplayNameForACLDirectly(final long accountId, final long boardId, final String searchTerm);

    @Query("SELECT * FROM user WHERE accountId = :accountId and uid = :uid")
    User getUserByUidDirectly(final long accountId, final String uid);

    @Query("SELECT * FROM user WHERE localId IN (:assignedUserIDs) and status <> 3") // not LOCAL_DELETED
    List<User> getUsersByIdDirectly(List<Long> assignedUserIDs);

    @Query("SELECT * FROM user WHERE localId = :localUserId")
    User getUserByLocalIdDirectly(long localUserId);

    @Query("    SELECT u.* FROM user u" +
            "    WHERE u.accountId = :accountId" +
            "    AND NOT EXISTS (" +
            "            select 1 from joincardwithuser ju" +
            "            where ju.userId = u.localId" +
            "            and ju.cardId = :notAssignedToLocalCardId AND status <> 3" + // not LOCAL_DELETED
            "    )" +
            "  AND ( " +
            "    EXISTS (" +
            "            select 1 from userinboard where boardId = :boardId AND userId = u.localId" +
            "    )" +
            "    OR" +
            "    EXISTS (" +
            "       select 1 from accesscontrol" + //  v GROUP!
            "       where (userId = u.localId OR (type = 1 and exists(select 1 from UserInGroup uig where uig.memberId = u.localId and uig.groupId = userId))) " +
            "           and boardId = :boardId and status <> 3" +
            "    )" +
            "    OR" +
            "    EXISTS (" +
            "            select 1 from board where localId = :boardId AND ownerId = u.localId" +
            "    )" +
            ")" +
            "    ORDER BY (" +
            "            select count(*) from joincardwithuser j" +
            "    where userId = u.localId and cardId in (select c.localId from card c inner join stack s on s.localId = c.stackId where s.boardId = :boardId)" +
            ") DESC" +
            "    LIMIT :topX")
    LiveData<List<User>> findProposalsForUsersToAssign(long accountId, long boardId, long notAssignedToLocalCardId, int topX);


    @Query("SELECT u.* FROM user u WHERE accountId = :accountId " +
            "    AND NOT EXISTS (" +
            "            select 1 from accesscontrol ju" +
            "            where ju.userId = u.localId and ju.boardId = :boardId and status <> 3" + // not LOCAL_DELETED
            "    ) " +
            "and u.localId <> (select b.ownerId from board b where localId = :boardId)" +
            "ORDER BY u.displayname " +
            "LIMIT :topX")
    LiveData<List<User>> findProposalsForUsersToAssignForACL(long accountId, long boardId, int topX);


    @Query("SELECT * FROM user WHERE localId IN (:userIDs) and status <> 3") // not LOCAL_DELETED
    List<User> getUsersByIdsDirectly(List<Long> userIDs);
}