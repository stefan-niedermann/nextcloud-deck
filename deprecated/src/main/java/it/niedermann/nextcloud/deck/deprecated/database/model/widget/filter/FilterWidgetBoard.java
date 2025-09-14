package it.niedermann.nextcloud.deck.database.entity.widget.filter;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.deck.database.entity.Board;

@Entity(
        indices = {
                @Index(value = "filterAccountId", name = "idx_FilterWidgetBoard_filterAccountId"),
                @Index(value = "boardId", name = "idx_FilterWidgetBoard_boardId")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = Board.class,
                        parentColumns = "localId",
                        childColumns = "boardId", onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = FilterWidgetAccount.class,
                        parentColumns = "id",
                        childColumns = "filterAccountId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class FilterWidgetBoard {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Long filterAccountId;
    private Long boardId;
    private boolean includeNoLabel = true;

    public FilterWidgetBoard() {
        // Default constructor
    }

    @Ignore
    public FilterWidgetBoard(Long boardId, List<FilterWidgetStack> stacks) {
        this.boardId = boardId;
        this.stacks = stacks;
    }

    @Ignore
    private List<FilterWidgetStack> stacks = new ArrayList<>();

    @Ignore
    private List<FilterWidgetLabel> labels = new ArrayList<>();

    public List<FilterWidgetLabel> getLabels() {
        return labels;
    }

    public void setLabels(List<FilterWidgetLabel> labels) {
        this.labels = labels;
    }

    public List<FilterWidgetStack> getStacks() {
        return stacks;
    }

    public void setStacks(List<FilterWidgetStack> stacks) {
        this.stacks = stacks;
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

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public boolean isIncludeNoLabel() {
        return includeNoLabel;
    }

    public void setIncludeNoLabel(boolean includeNoLabel) {
        this.includeNoLabel = includeNoLabel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterWidgetBoard that = (FilterWidgetBoard) o;

        if (includeNoLabel != that.includeNoLabel) return false;
        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(filterAccountId, that.filterAccountId))
            return false;
        if (!Objects.equals(boardId, that.boardId)) return false;
        if (!Objects.equals(stacks, that.stacks)) return false;
        return Objects.equals(labels, that.labels);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (filterAccountId != null ? filterAccountId.hashCode() : 0);
        result = 31 * result + (boardId != null ? boardId.hashCode() : 0);
        result = 31 * result + (includeNoLabel ? 1 : 0);
        result = 31 * result + (stacks != null ? stacks.hashCode() : 0);
        result = 31 * result + (labels != null ? labels.hashCode() : 0);
        return result;
    }
}
