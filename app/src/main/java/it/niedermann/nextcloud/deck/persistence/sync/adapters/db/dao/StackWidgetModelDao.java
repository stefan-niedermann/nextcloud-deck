package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import it.niedermann.nextcloud.deck.model.appwidgets.StackWidgetModel;

@Dao
public interface StackWidgetModelDao extends GenericDao<StackWidgetModel> {

    @Query("SELECT * FROM stackwidgetmodel WHERE appwidgetid = :appWidgetId")
    StackWidgetModel getStackWidgetByAppWidgetIdDirectly(final int appWidgetId);
}
