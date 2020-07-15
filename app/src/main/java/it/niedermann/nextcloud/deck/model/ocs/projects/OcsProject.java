package it.niedermann.nextcloud.deck.model.ocs.projects;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;

public class OcsProject implements IRemoteEntity {
    String name;
    @NonNull
    List<OcsProjectResource> resources = new ArrayList<>();
}
