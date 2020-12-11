package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.widgets.filter;

import androidx.room.Dao;
import androidx.room.Query;

import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetAccount;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.GenericDao;

@Dao
public interface FilterWidgetAccountDao extends GenericDao<FilterWidgetAccount> {
    @Query("DELETE FROM FilterWidgetAccount WHERE filterWidgetId = :filterWidgetId")
    void deleteByFilterWidgetId (Long filterWidgetId);
}
