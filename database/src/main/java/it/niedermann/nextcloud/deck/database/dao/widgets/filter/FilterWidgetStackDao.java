package it.niedermann.nextcloud.deck.database.dao.widgets.filter;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.database.dao.GenericDao;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetStack;

@Dao
public interface FilterWidgetStackDao extends GenericDao<FilterWidgetStack> {
    @Query("SELECT * FROM FilterWidgetStack where filterBoardId = :filterWidgetBoardId")
    List<FilterWidgetStack> getFilterWidgetStacksByFilterWidgetBoardIdDirectly(Long filterWidgetBoardId);
}
