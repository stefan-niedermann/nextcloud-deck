package it.niedermann.nextcloud.deck.database.dao.widgets.filter;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.database.dao.GenericDao;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetProject;

@Dao
public interface FilterWidgetProjectDao extends GenericDao<FilterWidgetProject> {
    @Query("SELECT * FROM FilterWidgetProject where filterAccountId = :filterWidgetAccountId")
    List<FilterWidgetProject> getFilterWidgetProjectsByFilterWidgetAccountIdDirectly(Long filterWidgetAccountId);
}
