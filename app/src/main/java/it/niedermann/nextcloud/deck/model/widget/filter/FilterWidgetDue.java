package it.niedermann.nextcloud.deck.model.widget.filter;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        indices = {
                @Index(value = "filterBoardId", name = "unique_idx_FilterWidgetDue_filterBoardId", unique = true),
        },
        foreignKeys = {
                @ForeignKey(
                        entity = FilterWidgetBoard.class,
                        parentColumns = "id",
                        childColumns = "filterBoardId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class FilterWidgetDue {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Long filterBoardId;
    private int dueType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFilterBoardId() {
        return filterBoardId;
    }

    public void setFilterBoardId(Long filterBoardId) {
        this.filterBoardId = filterBoardId;
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
        return filterBoardId != null ? filterBoardId.equals(that.filterBoardId) : that.filterBoardId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (filterBoardId != null ? filterBoardId.hashCode() : 0);
        result = 31 * result + dueType;
        return result;
    }
}
