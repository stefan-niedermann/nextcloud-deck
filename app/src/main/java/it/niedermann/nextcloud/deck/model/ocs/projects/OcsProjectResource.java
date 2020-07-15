package it.niedermann.nextcloud.deck.model.ocs.projects;

import androidx.annotation.Nullable;

import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;

public class OcsProjectResource implements IRemoteEntity {
    String type;
    String name;
    String link;
    String iconUrl;
    @Nullable
    String mimetype;
    @Nullable
    Boolean previewAvailable;
}
