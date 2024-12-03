package it.niedermann.nextcloud.deck.database.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import it.niedermann.nextcloud.deck.model.ocs.user.UserForAssignment;
import it.niedermann.nextcloud.deck.model.relations.UserInGroup;

@Dao
public interface UserInGroupDao extends GenericDao<UserInGroup> {
    @Query("DELETE FROM useringroup WHERE groupId = :localId")
    void deleteByGroupId(long localId);

    @RawQuery
    UserForAssignment getUserForAssignment(SupportSQLiteQuery query);
}