package it.niedermann.nextcloud.deck.database.dao.widgets.filter;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.database.dao.GenericDao;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetUser;

@Dao
public interface FilterWidgetUserDao extends GenericDao<FilterWidgetUser> {
    @Query("SELECT * FROM FilterWidgetUser where filterAccountId = :filterWidgetAccountId")
    List<FilterWidgetUser> getFilterWidgetUsersByFilterWidgetAccountIdDirectly(Long filterWidgetAccountId);
}
