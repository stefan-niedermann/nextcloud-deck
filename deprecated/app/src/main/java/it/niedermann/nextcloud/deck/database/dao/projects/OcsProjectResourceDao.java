package it.niedermann.nextcloud.deck.database.dao.projects;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.database.dao.GenericDao;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectResource;

@Dao
public interface OcsProjectResourceDao extends GenericDao<OcsProjectResource> {
    @Query("delete from OcsProjectResource where projectId = :localProjectId")
    void deleteByProjectId(Long localProjectId);

    @Query("select * from OcsProjectResource where projectId = :localProjectId")
    LiveData<List<OcsProjectResource>> getResourcesByLocalProjectId(Long localProjectId);

    @Query("select count(id) from OcsProjectResource where projectId = :localProjectId")
    int countProjectResourcesInProjectDirectly(Long localProjectId);

    @Query("select count(id) from OcsProjectResource where projectId = :localProjectId")
    LiveData<Integer> countProjectResourcesInProject(Long localProjectId);
}
