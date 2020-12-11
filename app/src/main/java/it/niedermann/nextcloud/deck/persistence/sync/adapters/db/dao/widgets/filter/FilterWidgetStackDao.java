package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.widgets.filter;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetStack;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.GenericDao;

@Dao
public interface FilterWidgetStackDao extends GenericDao<FilterWidgetStack> {
    @Query("SELECT * FROM FilterWidgetStack where filterBoardId = :filterWidgetBoardId")
    List<FilterWidgetStack> getFilterWidgetStacksByFilterWidgetBoardIdDirectly(Long filterWidgetBoardId);
}
