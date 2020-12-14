package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.widgets.filter;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.GenericDao;

@Dao
public interface FilterWidgetDao extends GenericDao<FilterWidget> {
    @Query("DELETE FROM filterwidget WHERE id = :filterWidgetId")
    void delete (Integer filterWidgetId);

    @Query("SELECT * FROM FilterWidget where id = :filterWidgetId")
    FilterWidget getFilterWidgetByIdDirectly(Integer filterWidgetId);

    @Query("SELECT EXISTS (SELECT 1 FROM FilterWidget WHERE id = :filterWidgetId)")
    boolean filterWidgetExists(int filterWidgetId);

    @Query("SELECT id FROM FilterWidget WHERE widgetType = :type")
    List<Integer> getFilterWidgetIdsByType(int type);
}
