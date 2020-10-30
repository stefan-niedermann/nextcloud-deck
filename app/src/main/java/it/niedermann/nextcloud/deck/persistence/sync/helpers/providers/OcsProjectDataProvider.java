package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProject;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectList;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectResource;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class OcsProjectDataProvider extends AbstractSyncDataProvider<OcsProject> {
    private Card card;

    public OcsProjectDataProvider(AbstractSyncDataProvider<?> parent, Card card) {
        super(parent);
        this.card = card;
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<OcsProject>> responder, Instant lastSync) {
        serverAdapter.getProjectsForCard(card.getId(), new IResponseCallback<OcsProjectList>(responder.getAccount()) {
            @Override
            public void onResponse(OcsProjectList response) {
                responder.onResponse(response.getProjects());
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                // dont break the sync!
                DeckLog.logError(throwable);
                responder.onResponse(Collections.emptyList());
            }
        });
    }

    @Override
    public OcsProject getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, OcsProject entity) {
        return dataBaseAdapter.getProjectByRemoteIdDirectly(accountId, entity.getId());
    }

    @Override
    public long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, OcsProject entity) {
        Long newId = dataBaseAdapter.createProjectDirectly(accountId, entity);
        entity.setLocalId(newId);
        updateResources(dataBaseAdapter, accountId, entity);
        return newId;
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, OcsProject entity, boolean setStatus) {
        dataBaseAdapter.updateProjectDirectly(accountId, entity);
        dataBaseAdapter.deleteProjectResourcesForProjectIdDirectly(entity.getLocalId());
        updateResources(dataBaseAdapter, accountId, entity);
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, OcsProject ocsProject) {
        if (ocsProject != null && ocsProject.getLocalId() != null) {
            dataBaseAdapter.deleteProjectDirectly(ocsProject);
        }
    }

    private void updateResources(DataBaseAdapter dataBaseAdapter, Long accountId, OcsProject entity) {
        if (entity.getResources() != null) {
            for (OcsProjectResource resource : entity.getResources()) {
                resource.setProjectId(entity.getLocalId());
                resource.setLocalId(dataBaseAdapter.createProjectResourceDirectly(accountId, resource));
                if ("deck-card".equals(resource.getType())) {
                    dataBaseAdapter.assignCardToProjectIfMissng(accountId, entity.getLocalId(), resource.getId());
                }
            }
        }
    }

    @Override
    public void createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<OcsProject> responder, OcsProject entity) {
        // Do Nothing
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<OcsProject> callback, OcsProject entity) {
        // Do Nothing
    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Void> callback, OcsProject entity, DataBaseAdapter dataBaseAdapter) {
        // Do Nothing
    }

    @Override
    public List<OcsProject> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Instant lastSync) {
        return Collections.emptyList();
    }
}
