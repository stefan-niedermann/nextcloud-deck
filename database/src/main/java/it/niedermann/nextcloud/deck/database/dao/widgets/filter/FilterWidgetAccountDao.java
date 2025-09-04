package it.niedermann.nextcloud.deck.database.dao.widgets.filter;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.database.dao.GenericDao;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetAccount;

@Dao
public interface FilterWidgetAccountDao extends GenericDao<FilterWidgetAccount> {
    @Query("DELETE FROM FilterWidgetAccount WHERE filterWidgetId = :filterWidgetId")
    void deleteByFilterWidgetId (Integer filterWidgetId);

    @Query("select * FROM FilterWidgetAccount WHERE filterWidgetId = :filterWidgetId")
    List<FilterWidgetAccount> getFilterWidgetAccountsByFilterWidgetIdDirectly(Integer filterWidgetId);
}
