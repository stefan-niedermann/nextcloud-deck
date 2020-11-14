package it.niedermann.nextcloud.deck.model.widget.filter;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        indices = {
                @Index(value = "filterBoardId", name = "idx_FilterWidgetSort_filterBoardId"),
                @Index(value = {"filterBoardId", "criteria"}, name = "unique_idx_FilterWidgetSort_filterBoardId_criteria"),
                @Index(value = {"filterBoardId", "ruleOrder"}, name = "unique_idx_FilterWidgetSort_filterBoardId_ruleOrder"),
        },
        foreignKeys = {
                @ForeignKey(
                        entity = FilterWidgetBoard.class,
                        parentColumns = "id",
                        childColumns = "filterBoardId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class FilterWidgetSort {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Long filterBoardId;
    private boolean direction;
    private int criteria;
    private int ruleOrder;

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

    public boolean isDirection() {
        return direction;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    public int getCriteria() {
        return criteria;
    }

    public void setCriteria(int criteria) {
        this.criteria = criteria;
    }

    public int getRuleOrder() {
        return ruleOrder;
    }

    public void setRuleOrder(int ruleOrder) {
        this.ruleOrder = ruleOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterWidgetSort that = (FilterWidgetSort) o;

        if (direction != that.direction) return false;
        if (criteria != that.criteria) return false;
        if (ruleOrder != that.ruleOrder) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return filterBoardId != null ? filterBoardId.equals(that.filterBoardId) : that.filterBoardId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (filterBoardId != null ? filterBoardId.hashCode() : 0);
        result = 31 * result + (direction ? 1 : 0);
        result = 31 * result + criteria;
        result = 31 * result + ruleOrder;
        return result;
    }
}
