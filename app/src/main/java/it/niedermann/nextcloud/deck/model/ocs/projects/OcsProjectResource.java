package it.niedermann.nextcloud.deck.model.ocs.projects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.RoomWarnings;

import java.io.Serializable;

import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;

@SuppressWarnings(RoomWarnings.INDEX_FROM_PARENT_IS_DROPPED)
@Entity(
        indices = {
                @Index(value = "id", name = "index_OcsProjectResource_id"),
                @Index(value = "lastModifiedLocal", name = "index_OcsProjectResource_lastModifiedLocal"),
                @Index(value = {"accountId", "id", "idString", "projectId"}, name = "index_OcsProjectResource_accountId_id", unique = true),
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
    @Nullable
    private String type;
    @Nullable
    private String name;
    @Nullable
    private String link;
    @Nullable
    private String path;
    @Nullable
    private String iconUrl;
    @Nullable
    private String mimetype;
    @Nullable
    private Boolean previewAvailable;
    @Nullable
    private String idString;


    @NonNull
    private Long projectId;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Nullable
    public String getType() {
        return type;
    }

    public void setType(@Nullable String type) {
        this.type = type;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    /**
     * Caution: the Link might be a full url or only the relative path!
     * @return The link to the Resource
     */
    @Nullable
    public String getLink() {
        return link;
    }

    public void setLink(@Nullable String link) {
        this.link = link;
    }

    @Nullable
    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(@Nullable String iconUrl) {
        this.iconUrl = iconUrl;
    }

    @Nullable
    public String getPath() {
        return path;
    }

    public void setPath(@Nullable String path) {
        this.path = path;
    }

    @Nullable
    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(@Nullable String mimetype) {
        this.mimetype = mimetype;
    }

    public Boolean getPreviewAvailable() {
        return Boolean.TRUE.equals(previewAvailable);
    }

    public void setPreviewAvailable(@Nullable Boolean previewAvailable) {
        this.previewAvailable = previewAvailable;
    }

    @Nullable
    public String getIdString() {
        return idString;
    }

    public void setIdString(@Nullable String idString) {
        this.idString = idString;
    }
}
