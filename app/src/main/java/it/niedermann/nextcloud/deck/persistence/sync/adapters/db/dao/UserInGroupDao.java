package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import it.niedermann.nextcloud.deck.model.relations.UserInGroup;

@Dao
public interface UserInGroupDao extends GenericDao<UserInGroup> {
    @Query("DELETE FROM useringroup WHERE groupId = :localId")
    void deleteByGroupId(long localId);
}