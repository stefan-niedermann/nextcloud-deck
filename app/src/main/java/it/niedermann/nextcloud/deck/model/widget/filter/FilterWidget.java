package it.niedermann.nextcloud.deck.model.widget.filter;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity()
public class FilterWidget {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private Integer dueType;

    @Ignore
    private List<FilterWidgetAccount> accounts;

    @Ignore
    private List<FilterWidgetSort> sorts;

    public List<FilterWidgetAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<FilterWidgetAccount> accounts) {
        this.accounts = accounts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<FilterWidgetSort> getSorts() {
        return sorts;
    }

    public void setSorts(List<FilterWidgetSort> sorts) {
        this.sorts = sorts;
    }

    public Integer getDueType() {
        return dueType;
    }

    public void setDueType(Integer dueType) {
        this.dueType = dueType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterWidget that = (FilterWidget) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
