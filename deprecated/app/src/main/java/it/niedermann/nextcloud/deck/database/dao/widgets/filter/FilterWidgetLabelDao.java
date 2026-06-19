package it.niedermann.nextcloud.deck.database.dao.widgets.filter;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.database.dao.GenericDao;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetLabel;

@Dao
public interface FilterWidgetLabelDao extends GenericDao<FilterWidgetLabel> {
    @Query("SELECT * FROM FilterWidgetLabel where filterBoardId = :filterWidgetBoardId")
    List<FilterWidgetLabel> getFilterWidgetLabelsByFilterWidgetBoardIdDirectly(Long filterWidgetBoardId);
}
