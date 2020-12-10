package it.niedermann.nextcloud.deck.model.widget.filter;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        indices = {
                @Index(value = "filterWidgetId", name = "idx_FilterWidgetSort_filterWidgetId"),
                @Index(value = {"filterWidgetId", "criteria"}, name = "unique_idx_FilterWidgetSort_filterWidgetId_criteria"),
                @Index(value = {"filterWidgetId", "ruleOrder"}, name = "unique_idx_FilterWidgetSort_filterWidgetId_ruleOrder"),
        },
        foreignKeys = {
                @ForeignKey(
                        entity = FilterWidget.class,
                        parentColumns = "id",
                        childColumns = "filterWidgetId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class FilterWidgetSort {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Long filterWidgetId;
    private boolean direction;
    private int criteria;
    private int ruleOrder;

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
        return filterWidgetId != null ? filterWidgetId.equals(that.filterWidgetId) : that.filterWidgetId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (filterWidgetId != null ? filterWidgetId.hashCode() : 0);
        result = 31 * result + (direction ? 1 : 0);
        result = 31 * result + criteria;
        result = 31 * result + ruleOrder;
        return result;
    }
}
