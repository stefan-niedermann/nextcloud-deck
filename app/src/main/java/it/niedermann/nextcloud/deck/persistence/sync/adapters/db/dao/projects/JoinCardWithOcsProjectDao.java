package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.projects;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.ocs.projects.JoinCardWithProject;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.GenericDao;

@Dao
public interface JoinCardWithOcsProjectDao extends GenericDao<JoinCardWithProject> {
    @Query("select * from JoinCardWithProject where projectId = :localProjectId and cardId = :localCardId")
    JoinCardWithProject getAssignmentByCardIdAndProjectIdDirectly(Long localCardId, Long localProjectId);

    @Query("delete from JoinCardWithProject where cardId = :localCardId and projectId NOT in (select p.localId from OcsProject p where p.accountId = :accountId and p.id in (:remoteProjectIDs))")
    void deleteProjectResourcesByCardIdExceptGivenProjectIdsDirectly(long accountId, Long localCardId, List<Long> remoteProjectIDs);

    @Query("delete from JoinCardWithProject where cardId = :localCardId")
    void deleteProjectResourcesByCardIdDirectly(Long localCardId);
}
