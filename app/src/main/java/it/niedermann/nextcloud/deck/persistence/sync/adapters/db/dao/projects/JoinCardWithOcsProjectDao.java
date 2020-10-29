package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.projects;

import androidx.room.Dao;
import androidx.room.Query;

import it.niedermann.nextcloud.deck.model.ocs.projects.JoinCardWithProject;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.GenericDao;

@Dao
public interface JoinCardWithOcsProjectDao extends GenericDao<JoinCardWithProject> {
    @Query("select * from JoinCardWithProject where projectId = :localProjectId and cardId = :localCardId")
    JoinCardWithProject getAssignmentByCardIdAndProjectIdDirectly(Long localCardId, Long localProjectId);
}
