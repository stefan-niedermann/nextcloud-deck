package it.niedermann.nextcloud.deck.database.dao.widgets.filter;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.database.dao.GenericDao;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetSort;

@Dao
public interface FilterWidgetSortDao extends GenericDao<FilterWidgetSort> {
    @Query("DELETE FROM FilterWidgetSort WHERE filterWidgetId = :filterWidgetId")
    void deleteByFilterWidgetId (Integer filterWidgetId);

    @Query("select * FROM FilterWidgetSort WHERE filterWidgetId = :filterWidgetId order by ruleOrder asc")
    List<FilterWidgetSort> getFilterWidgetSortByFilterWidgetIdDirectly(Integer filterWidgetId);
}
