package it.niedermann.nextcloud.deck.model.ocs.projects;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;

public class OcsProject implements IRemoteEntity {
    private String name;
    @NonNull
    private ArrayList<OcsProjectResource> resources = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    public ArrayList<OcsProjectResource> getResources() {
        return resources;
    }

    public void setResources(@NonNull List<OcsProjectResource> resources) {
        this.resources.clear();
        this.resources.addAll(resources);
    }
}
