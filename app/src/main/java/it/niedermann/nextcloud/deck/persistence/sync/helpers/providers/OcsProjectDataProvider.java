package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.ResponseCallback;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProject;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectList;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectResource;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class OcsProjectDataProvider extends AbstractSyncDataProvider<OcsProject> {
    private final Card card;

    public OcsProjectDataProvider(AbstractSyncDataProvider<?> parent, Card card) {
        super(parent);
        this.card = card;
    }

    @Override
    public Disposable getAllFromServer(ServerAdapter serverAdapter, long accountId, ResponseCallback<List<OcsProject>> responder, Instant lastSync) {
        return serverAdapter.getProjectsForCard(card.getId(), new ResponseCallback<>(responder.getAccount()) {
            @Override
            public void onResponse(OcsProjectList response) {
                responder.onResponse(response.getProjects());
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                // dont break the sync!
                // TODO i got here HTTP 404 once, maybe this should be considered?
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

    @Override
    public void handleDeletes(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, List<OcsProject> entitiesFromServer) {
        if (entitiesFromServer.isEmpty()){
            dataBaseAdapter.deleteProjectResourcesByCardIdDirectly(card.getLocalId());
            return;
        }
        List<Long> remoteProjectIDs = entitiesFromServer.stream()
                .map(OcsProject::getId)
                .collect(Collectors.toList());
        if (!remoteProjectIDs.isEmpty()) {
            dataBaseAdapter.deleteProjectResourcesByCardIdExceptGivenProjectIdsDirectly(accountId, card.getLocalId(), remoteProjectIDs);
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
    public Disposable createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<OcsProject> responder, OcsProject entity) {
        return new CompositeDisposable();
    }

    @Override
    public Disposable updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<OcsProject> callback, OcsProject entity) {
        return new CompositeDisposable();
    }

    @Override
    public Disposable deleteOnServer(ServerAdapter serverAdapter, long accountId, ResponseCallback<Void> callback, OcsProject entity, DataBaseAdapter dataBaseAdapter) {
        return new CompositeDisposable();
    }

    @Override
    public List<OcsProject> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Instant lastSync) {
        return Collections.emptyList();
    }
}
