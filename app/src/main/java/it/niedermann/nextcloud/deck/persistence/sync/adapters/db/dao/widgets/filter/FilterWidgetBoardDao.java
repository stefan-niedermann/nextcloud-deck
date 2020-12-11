package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.widgets.filter;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetBoard;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.GenericDao;

@Dao
public interface FilterWidgetBoardDao extends GenericDao<FilterWidgetBoard> {
    @Query("SELECT * FROM FilterWidgetBoard where filterAccountId = :filterWidgetAccountId")
    List<FilterWidgetBoard> getFilterWidgetBoardsByFilterWidgetAccountIdDirectly(Long filterWidgetAccountId);
}
