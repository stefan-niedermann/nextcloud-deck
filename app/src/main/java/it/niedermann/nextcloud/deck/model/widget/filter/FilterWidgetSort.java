package it.niedermann.nextcloud.deck.model.widget.filter;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import it.niedermann.nextcloud.deck.model.enums.ESortCriteria;

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
    private boolean direction = true;
    @NonNull
    private ESortCriteria criteria;
    private int ruleOrder;

    public FilterWidgetSort() {
        // Default constructor
    }

    @Ignore
    public FilterWidgetSort(ESortCriteria criteria, boolean ascending) {
        setCriteria(criteria);
        setDirection(ascending);
    }

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

    public boolean isDirectionAscending() {
        return direction;
    }

    public boolean isDirectionDescending() {
        return !direction;
    }

    public void setDirectionAscending() {
        direction = true;
    }

    public void setDirectionDescending() {
        direction = false;
    }

    public ESortCriteria getCriteria() {
        return criteria;
    }

    public void setCriteria(ESortCriteria criteria) {
        this.criteria = criteria;
    }

    public int getRuleOrder() {
        return ruleOrder;
    }

    public void setRuleOrder(int ruleOrder) {
        this.ruleOrder = ruleOrder;
    }

    public boolean isDirection() {
        return direction;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilterWidgetSort)) return false;

        FilterWidgetSort that = (FilterWidgetSort) o;

        if (direction != that.direction) return false;
        if (ruleOrder != that.ruleOrder) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (filterWidgetId != null ? !filterWidgetId.equals(that.filterWidgetId) : that.filterWidgetId != null)
            return false;
        return criteria == that.criteria;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (filterWidgetId != null ? filterWidgetId.hashCode() : 0);
        result = 31 * result + (direction ? 1 : 0);
        result = 31 * result + (criteria != null ? criteria.hashCode() : 0);
        result = 31 * result + ruleOrder;
        return result;
    }
}
