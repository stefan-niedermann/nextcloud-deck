package it.niedermann.nextcloud.deck.model.widget.filter;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import it.niedermann.nextcloud.deck.model.Stack;

@Entity(
        indices = {
                @Index(value = "filterBoardId", name = "idx_FilterWidgetStack_filterBoardId"),
                @Index(value = "stackId", name = "idx_FilterWidgetStack_stackId")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = Stack.class,
                        parentColumns = "localId",
                        childColumns = "stackId", onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = FilterWidgetBoard.class,
                        parentColumns = "id",
                        childColumns = "filterBoardId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class FilterWidgetStack {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Long filterBoardId;
    private Long stackId;

    public FilterWidgetStack() {
        // Default constructor
    }

    @Ignore
    public FilterWidgetStack(Long stackId) {
        this.stackId = stackId;
    }

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

    public Long getStackId() {
        return stackId;
    }

    public void setStackId(Long stackId) {
        this.stackId = stackId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterWidgetStack that = (FilterWidgetStack) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (filterBoardId != null ? !filterBoardId.equals(that.filterBoardId) : that.filterBoardId != null)
            return false;
        return stackId != null ? stackId.equals(that.stackId) : that.stackId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (filterBoardId != null ? filterBoardId.hashCode() : 0);
        result = 31 * result + (stackId != null ? stackId.hashCode() : 0);
        return result;
    }
}
