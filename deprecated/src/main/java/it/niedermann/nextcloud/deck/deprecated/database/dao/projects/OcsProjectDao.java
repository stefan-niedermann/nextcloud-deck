package it.niedermann.nextcloud.deck.database.dao.projects;

import androidx.room.Dao;
import androidx.room.Query;

import it.niedermann.nextcloud.deck.database.dao.GenericDao;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProject;

@Dao
public interface OcsProjectDao  extends GenericDao<OcsProject> {
    @Query("select * from OcsProject where accountId = :accountId and id = :remoteId")
    OcsProject getProjectByRemoteIdDirectly(long accountId, Long remoteId);
}
