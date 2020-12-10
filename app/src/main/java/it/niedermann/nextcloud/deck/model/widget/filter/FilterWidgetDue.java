package it.niedermann.nextcloud.deck.model.widget.filter;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        indices = {
                @Index(value = "filterWidgetId", name = "unique_idx_FilterWidgetDue_filterWidgetId", unique = true),
        },
        foreignKeys = {
                @ForeignKey(
                        entity = FilterWidget.class,
                        parentColumns = "id",
                        childColumns = "filterWidgetId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class FilterWidgetDue {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Long filterWidgetId;
    private int dueType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFilterWidgetId() {
        return filterWidgetId;
    }

    public void setFilterWidgetId(Long filterWidgetId) {
        this.filterWidgetId = filterWidgetId;
    }

    public int getDueType() {
        return dueType;
    }

    public void setDueType(int dueType) {
        this.dueType = dueType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterWidgetDue that = (FilterWidgetDue) o;

        if (dueType != that.dueType) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return filterWidgetId != null ? filterWidgetId.equals(that.filterWidgetId) : that.filterWidgetId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (filterWidgetId != null ? filterWidgetId.hashCode() : 0);
        result = 31 * result + dueType;
        return result;
    }
}
