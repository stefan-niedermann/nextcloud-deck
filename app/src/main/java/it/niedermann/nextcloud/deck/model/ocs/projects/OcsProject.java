package it.niedermann.nextcloud.deck.model.ocs.projects;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;

@Entity(inheritSuperIndices = true,
        indices = {
                @Index(value = "accountId", name = "index_project_accID"),
        },
        foreignKeys = {
        }
)
public class OcsProject extends AbstractRemoteEntity {
    @NonNull
    private String name;

    @Ignore
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
