package it.niedermann.nextcloud.deck.model.widget.filter;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import it.niedermann.nextcloud.deck.model.Account;

@Entity(
        indices = {
                @Index(value = "filterWidgetId", name = "index_FilterWidgetAccount_filterWidgetId"),
                @Index(value = "accountId", name = "idx_FilterWidgetAccount_accountId"),
        },
        foreignKeys = {
                @ForeignKey(
                        entity = Account.class,
                        parentColumns = "id",
                        childColumns = "accountId", onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = FilterWidget.class,
                        parentColumns = "id",
                        childColumns = "filterWidgetId", onDelete = ForeignKey.CASCADE
                ),
        }
)
public class FilterWidgetAccount {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Long filterWidgetId;
    private Long accountId;

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

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterWidgetAccount that = (FilterWidgetAccount) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (filterWidgetId != null ? !filterWidgetId.equals(that.filterWidgetId) : that.filterWidgetId != null)
            return false;
        return accountId != null ? accountId.equals(that.accountId) : that.accountId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (filterWidgetId != null ? filterWidgetId.hashCode() : 0);
        result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
        return result;
    }
}
