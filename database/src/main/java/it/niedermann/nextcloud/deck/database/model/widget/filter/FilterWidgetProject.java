package it.niedermann.nextcloud.deck.database.model.widget.filter;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

import it.niedermann.nextcloud.deck.database.model.ocs.projects.OcsProject;

@Entity(
        indices = {
                @Index(value = "filterAccountId", name = "idx_FilterWidgetProject_filterAccountId"),
                @Index(value = "projectId", name = "idx_FilterWidgetProject_projectId")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = OcsProject.class,
                        parentColumns = "localId",
                        childColumns = "projectId", onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = FilterWidgetAccount.class,
                        parentColumns = "id",
                        childColumns = "filterAccountId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class FilterWidgetProject {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Long filterAccountId;
    private Long projectId;

    public FilterWidgetProject() {
        // Default constructor
    }

    @Ignore
    public FilterWidgetProject(Long projectId) {
        this.projectId = projectId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFilterAccountId() {
        return filterAccountId;
    }

    public void setFilterAccountId(Long filterAccountId) {
        this.filterAccountId = filterAccountId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterWidgetProject that = (FilterWidgetProject) o;

        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(filterAccountId, that.filterAccountId))
            return false;
        return Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (filterAccountId != null ? filterAccountId.hashCode() : 0);
        result = 31 * result + (projectId != null ? projectId.hashCode() : 0);
        return result;
    }
}
