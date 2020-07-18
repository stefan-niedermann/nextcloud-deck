package it.niedermann.nextcloud.deck.model.ocs.projects;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.io.Serializable;

import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;

@Entity(inheritSuperIndices = true,
        indices = {
                @Index(value = "accountId", name = "index_projectResource_accID"),
                @Index(value = "projectId", name = "index_projectResource_projectId"),
        },
        foreignKeys = {
                @ForeignKey(
                        entity = OcsProject.class,
                        parentColumns = "localId",
                        childColumns = "projectId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class OcsProjectResource extends AbstractRemoteEntity implements Serializable {
    private String type;
    private String name;
    private String link;
    private String iconUrl;
    @Nullable
    private String mimetype;
    @Nullable
    private Boolean previewAvailable;

    private Long projectId;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    @Nullable
    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(@Nullable String mimetype) {
        this.mimetype = mimetype;
    }

    public boolean getPreviewAvailable() {
        return Boolean.TRUE.equals(previewAvailable);
    }

    public void setPreviewAvailable(@Nullable Boolean previewAvailable) {
        this.previewAvailable = previewAvailable;
    }
}
