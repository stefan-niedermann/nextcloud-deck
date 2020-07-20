package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.projects;

import androidx.room.Dao;
import androidx.room.Query;

import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectResource;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.GenericDao;

@Dao
public interface OcsProjectResourceDao extends GenericDao<OcsProjectResource> {
    @Query("delete from OcsProjectResource where projectId = :localProjectId")
    void deleteByProjectId(Long localProjectId);
}
