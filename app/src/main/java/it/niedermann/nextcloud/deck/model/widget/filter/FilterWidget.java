package it.niedermann.nextcloud.deck.model.widget.filter;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.deck.model.enums.EDueType;

@Entity()
public class FilterWidget {

    @PrimaryKey()
    private int id;

    @Nullable
    private String title;

    @Nullable
    @ColorInt
    private Integer titleColor;

    @Nullable
    @ColorInt
    private Integer widgetBackgroundColor;

    @Nullable
    @ColorInt
    private Integer sectionTextColor;

    @Nullable
    @ColorInt
    private Integer listBackgroundColor;

    @Nullable
    @ColorInt
    private Integer entryTextColor;

    @Nullable
    private EDueType dueType;

    @NonNull
    private EWidgetType widgetType = EWidgetType.FILTER_WIDGET;

    @Ignore
    @NonNull
    private final List<FilterWidgetAccount> accounts = new ArrayList<>();

    @Ignore
    @NonNull
    private final List<FilterWidgetSort> sorts = new ArrayList<>();

    @NonNull
    public List<FilterWidgetAccount> getAccounts() {
        return accounts;
    }

    public FilterWidget() {
        // Default constructor
    }

    @Ignore
    public FilterWidget(int appWidgetId, @NonNull EWidgetType widgetType) {
        setId(appWidgetId);
        setWidgetType(widgetType);
    }

    public void setAccounts(@NonNull List<FilterWidgetAccount> accounts) {
        this.accounts.clear();
        this.accounts.addAll(accounts);
    }

    public Integer getId() {
        return id;
    }

    @NonNull
    public List<FilterWidgetSort> getSorts() {
        return sorts;
    }

    @Ignore
    public void setSorts(@NonNull FilterWidgetSort sorts) {
        this.sorts.clear();
        this.sorts.add(sorts);
    }

    public void setSorts(@NonNull List<FilterWidgetSort> sorts) {
        this.sorts.clear();
        this.sorts.addAll(sorts);
    }

    public void setId(int id) {
        this.id = id;
    }

    @Nullable
    public EDueType getDueType() {
        return dueType;
    }

    public void setDueType(@Nullable EDueType dueType) {
        this.dueType = dueType;
    }

    @NonNull
    public EWidgetType getWidgetType() {
        return widgetType;
    }

    public void setWidgetType(@NonNull EWidgetType widgetType) {
        this.widgetType = widgetType;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    @Nullable
    @ColorInt
    public Integer getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(@Nullable @ColorInt Integer titleColor) {
        this.titleColor = titleColor;
    }

    @Nullable
    @ColorInt
    public Integer getWidgetBackgroundColor() {
        return widgetBackgroundColor;
    }

    public void setWidgetBackgroundColor(@Nullable @ColorInt Integer widgetBackgroundColor) {
        this.widgetBackgroundColor = widgetBackgroundColor;
    }

    @Nullable
    @ColorInt
    public Integer getSectionTextColor() {
        return sectionTextColor;
    }

    public void setSectionTextColor(@Nullable @ColorInt Integer sectionTextColor) {
        this.sectionTextColor = sectionTextColor;
    }

    @Nullable
    @ColorInt
    public Integer getListBackgroundColor() {
        return listBackgroundColor;
    }

    public void setListBackgroundColor(@Nullable @ColorInt Integer listBackgroundColor) {
        this.listBackgroundColor = listBackgroundColor;
    }

    @Nullable
    @ColorInt
    public Integer getEntryTextColor() {
        return entryTextColor;
    }

    public void setEntryTextColor(@Nullable @ColorInt Integer entryTextColor) {
        this.entryTextColor = entryTextColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterWidget that = (FilterWidget) o;

        if (id != that.id) return false;
        if (!Objects.equals(title, that.title)) return false;
        if (!Objects.equals(titleColor, that.titleColor)) return false;
        if (!Objects.equals(widgetBackgroundColor, that.widgetBackgroundColor)) return false;
        if (!Objects.equals(sectionTextColor, that.sectionTextColor)) return false;
        if (!Objects.equals(listBackgroundColor, that.listBackgroundColor)) return false;
        if (!Objects.equals(entryTextColor, that.entryTextColor)) return false;
        if (dueType != that.dueType) return false;
        if (widgetType != that.widgetType) return false;
        if (!accounts.equals(that.accounts)) return false;
        return sorts.equals(that.sorts);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (titleColor != null ? titleColor.hashCode() : 0);
        result = 31 * result + (widgetBackgroundColor != null ? widgetBackgroundColor.hashCode() : 0);
        result = 31 * result + (sectionTextColor != null ? sectionTextColor.hashCode() : 0);
        result = 31 * result + (listBackgroundColor != null ? listBackgroundColor.hashCode() : 0);
        result = 31 * result + (entryTextColor != null ? entryTextColor.hashCode() : 0);
        result = 31 * result + (dueType != null ? dueType.hashCode() : 0);
        result = 31 * result + widgetType.hashCode();
        result = 31 * result + accounts.hashCode();
        result = 31 * result + sorts.hashCode();
        return result;
    }

    public enum EChangedEntityType {
        ACCOUNT,
        BOARD,
        STACK,
        LABEL,
        USER,
        PROJECT,
    }
}
