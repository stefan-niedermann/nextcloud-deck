package it.niedermann.nextcloud.deck.model.widget.filter;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import it.niedermann.nextcloud.deck.model.User;

@Entity(
        indices = {
                @Index(value = "filterAccountId", name = "idx_FilterWidgetUser_filterAccountId"),
                @Index(value = "userId", name = "idx_FilterWidgetUser_userId")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "localId",
                        childColumns = "userId", onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = FilterWidgetAccount.class,
                        parentColumns = "id",
                        childColumns = "filterAccountId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class FilterWidgetUser {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Long filterAccountId;
    private Long userId;

    public FilterWidgetUser() {
        // Default constructor
    }

    @Ignore
    public FilterWidgetUser(Long userId) {
        this.userId = userId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterWidgetUser that = (FilterWidgetUser) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (filterAccountId != null ? !filterAccountId.equals(that.filterAccountId) : that.filterAccountId != null)
            return false;
        return userId != null ? userId.equals(that.userId) : that.userId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (filterAccountId != null ? filterAccountId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }
}
