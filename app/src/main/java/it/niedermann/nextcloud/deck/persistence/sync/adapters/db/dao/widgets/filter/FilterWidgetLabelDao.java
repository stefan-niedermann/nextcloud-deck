package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.widgets.filter;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetLabel;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.GenericDao;

@Dao
public interface FilterWidgetLabelDao extends GenericDao<FilterWidgetLabel> {
    @Query("SELECT * FROM FilterWidgetLabel where filterBoardId = :filterWidgetBoardId")
    List<FilterWidgetLabel> getFilterWidgetLabelsByFilterWidgetBoardIdDirectly(Long filterWidgetBoardId);
}
