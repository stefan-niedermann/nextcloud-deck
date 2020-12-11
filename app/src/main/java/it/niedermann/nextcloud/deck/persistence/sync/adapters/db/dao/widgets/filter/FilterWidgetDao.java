package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.widgets.filter;

import androidx.room.Dao;
import androidx.room.Query;

import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.GenericDao;

@Dao
public interface FilterWidgetDao extends GenericDao<FilterWidget> {
    @Query("DELETE FROM filterwidget WHERE id = :filterWidgetId")
    void delete (Long filterWidgetId);
}
