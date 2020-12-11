package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.widgets.filter;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetUser;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.GenericDao;

@Dao
public interface FilterWidgetUserDao extends GenericDao<FilterWidgetUser> {
    @Query("SELECT * FROM FilterWidgetUser where filterAccountId = :filterWidgetAccountId")
    List<FilterWidgetUser> getFilterWidgetUsersByFilterWidgetAccountIdDirectly(Long filterWidgetAccountId);
}
