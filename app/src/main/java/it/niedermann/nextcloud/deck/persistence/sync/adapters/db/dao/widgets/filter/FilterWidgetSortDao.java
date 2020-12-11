package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.widgets.filter;

import androidx.room.Dao;
import androidx.room.Query;

import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetSort;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.GenericDao;

@Dao
public interface FilterWidgetSortDao extends GenericDao<FilterWidgetSort> {
    @Query("DELETE FROM FilterWidgetSort WHERE filterWidgetId = :filterWidgetId")
    void deleteByFilterWidgetId (Long filterWidgetId);
}
