package it.niedermann.nextcloud.deck.model.widget.filter;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private boolean includeNoUser = true;
    private boolean includeNoProject = true;

    @Ignore
    private List<FilterWidgetBoard> boards = new ArrayList<>();

    @Ignore
    private List<FilterWidgetUser> users = new ArrayList<>();

    @Ignore
    private List<FilterWidgetProject> projects = new ArrayList<>();

    public FilterWidgetAccount() {
        // Default constructor
    }

    @Ignore
    public FilterWidgetAccount(Long accountId, boolean includeNoUser) {
        this.setAccountId(accountId);
        this.setIncludeNoUser(includeNoUser);
    }

    public void setBoards(List<FilterWidgetBoard> boards) {
        this.boards = boards;
    }

    public List<FilterWidgetBoard> getBoards() {
        return boards;
    }

    public List<FilterWidgetUser> getUsers() {
        return users;
    }

    public void setUsers(List<FilterWidgetUser> users) {
        this.users = users;
    }

    @Ignore
    public void setUsers(FilterWidgetUser user) {
        this.users = Collections.singletonList(user);
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

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public boolean isIncludeNoUser() {
        return includeNoUser;
    }

    public void setIncludeNoUser(boolean includeNoUser) {
        this.includeNoUser = includeNoUser;
    }

    public boolean isIncludeNoProject() {
        return includeNoProject;
    }

    public void setIncludeNoProject(boolean includeNoProject) {
        this.includeNoProject = includeNoProject;
    }

    public List<FilterWidgetProject> getProjects() {
        return projects;
    }

    public void setProjects(List<FilterWidgetProject> projects) {
        this.projects = projects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterWidgetAccount that = (FilterWidgetAccount) o;

        if (includeNoUser != that.includeNoUser) return false;
        if (includeNoProject != that.includeNoProject) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (filterWidgetId != null ? !filterWidgetId.equals(that.filterWidgetId) : that.filterWidgetId != null)
            return false;
        if (accountId != null ? !accountId.equals(that.accountId) : that.accountId != null)
            return false;
        if (boards != null ? !boards.equals(that.boards) : that.boards != null) return false;
        if (users != null ? !users.equals(that.users) : that.users != null) return false;
        return projects != null ? projects.equals(that.projects) : that.projects == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (filterWidgetId != null ? filterWidgetId.hashCode() : 0);
        result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
        result = 31 * result + (includeNoUser ? 1 : 0);
        result = 31 * result + (includeNoProject ? 1 : 0);
        result = 31 * result + (boards != null ? boards.hashCode() : 0);
        result = 31 * result + (users != null ? users.hashCode() : 0);
        result = 31 * result + (projects != null ? projects.hashCode() : 0);
        return result;
    }
}
