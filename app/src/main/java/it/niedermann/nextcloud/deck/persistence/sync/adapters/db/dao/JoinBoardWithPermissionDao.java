package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import it.niedermann.nextcloud.deck.model.JoinBoardWithPermission;

@Dao
public interface JoinBoardWithPermissionDao extends GenericDao<JoinBoardWithPermission> {
    @Query("DELETE FROM joinboardwithpermission WHERE boardId = :localId")
    void deleteByBoardId(long localId);

}