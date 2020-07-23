package it.niedermann.nextcloud.deck.model.ocs.projects.full;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProject;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectResource;

public class OcsProjectWithResources implements IRemoteEntity {
    @Embedded
    public OcsProject project;


    @Relation(entity = OcsProjectResource.class, parentColumn = "localId", entityColumn = "projectId")
    public List<OcsProjectResource> resources;

    public OcsProject getProject() {
        return project;
    }

    public void setProject(OcsProject project) {
        this.project = project;
    }

    @NonNull
    public List<OcsProjectResource> getResources() {
        return resources;
    }

    public void setResources(List<OcsProjectResource> resources) {
        this.resources = resources;
    }

    public String getName() {
        return project.getName();
    }

    public void setName(String name) {
        project.setName(name);
    }

    @Override
    public IRemoteEntity getEntity() {
        return project;
    }
}
