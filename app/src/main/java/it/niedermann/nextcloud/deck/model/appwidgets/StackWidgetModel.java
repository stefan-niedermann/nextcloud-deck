package it.niedermann.nextcloud.deck.model.appwidgets;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Stack;

@Entity(
        indices = {
                @Index("stackId")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = Account.class,
                        parentColumns = "id",
                        childColumns = "accountId", onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Stack.class,
                        parentColumns = "localId",
                        childColumns = "stackId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class StackWidgetModel {
    @Ignore
    private static final long serialVersionUID = 0;

    @PrimaryKey()
    private Integer appWidgetId;
    private Long accountId;
    private Long stackId;
    private boolean darkTheme;

    public Integer getAppWidgetId() {
        return appWidgetId;
    }

    public void setAppWidgetId(Integer appWidgetId) {
        this.appWidgetId = appWidgetId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getStackId() {
        return stackId;
    }

    public void setStackId(Long stackId) {
        this.stackId = stackId;
    }

    public boolean getDarkTheme() {
        return darkTheme;
    }

    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
    }
}
