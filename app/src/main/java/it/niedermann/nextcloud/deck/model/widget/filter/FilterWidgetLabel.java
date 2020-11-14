package it.niedermann.nextcloud.deck.model.widget.filter;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import it.niedermann.nextcloud.deck.model.Label;

@Entity(
        indices = {
                @Index(value = "filterBoardId", name = "idx_FilterWidgetLabel_filterBoardId"),
                @Index(value = "labelId", name = "idx_FilterWidgetLabel_labelId")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = Label.class,
                        parentColumns = "localId",
                        childColumns = "labelId", onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = FilterWidgetBoard.class,
                        parentColumns = "id",
                        childColumns = "filterBoardId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class FilterWidgetLabel {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Long filterBoardId;
    private Long labelId;

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

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterWidgetLabel that = (FilterWidgetLabel) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (filterBoardId != null ? !filterBoardId.equals(that.filterBoardId) : that.filterBoardId != null)
            return false;
        return labelId != null ? labelId.equals(that.labelId) : that.labelId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (filterBoardId != null ? filterBoardId.hashCode() : 0);
        result = 31 * result + (labelId != null ? labelId.hashCode() : 0);
        return result;
    }
}
