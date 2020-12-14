package it.niedermann.nextcloud.deck.model.widget.filter;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.model.enums.EDueType;

@Entity()
public class FilterWidget {

    @PrimaryKey()
    @NonNull
    private int id;

    private EDueType dueType;

    @NonNull
    private EWidgetType widgetType;

    @Ignore
    private List<FilterWidgetAccount> accounts = new ArrayList<>();

    @Ignore
    private List<FilterWidgetSort> sorts = new ArrayList<>();

    public List<FilterWidgetAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<FilterWidgetAccount> accounts) {
        this.accounts = accounts;
    }

    public Integer getId() {
        return id;
    }

    public List<FilterWidgetSort> getSorts() {
        return sorts;
    }

    public void setSorts(List<FilterWidgetSort> sorts) {
        this.sorts = sorts;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EDueType getDueType() {
        return dueType;
    }

    public void setDueType(EDueType dueType) {
        this.dueType = dueType;
    }

    @NonNull
    public EWidgetType getWidgetType() {
        return widgetType;
    }

    public void setWidgetType(@NonNull EWidgetType widgetType) {
        this.widgetType = widgetType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterWidget that = (FilterWidget) o;

        if (id != that.id) return false;
        if (dueType != null ? !dueType.equals(that.dueType) : that.dueType != null) return false;
        if (accounts != null ? !accounts.equals(that.accounts) : that.accounts != null)
            return false;
        return sorts != null ? sorts.equals(that.sorts) : that.sorts == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (dueType != null ? dueType.hashCode() : 0);
        result = 31 * result + (accounts != null ? accounts.hashCode() : 0);
        result = 31 * result + (sorts != null ? sorts.hashCode() : 0);
        return result;
    }
}
