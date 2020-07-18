package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.widgets;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import it.niedermann.nextcloud.deck.model.appwidgets.StackWidgetModel;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.GenericDao;

@Dao
public interface StackWidgetModelDao extends GenericDao<StackWidgetModel> {

    @Query("SELECT * FROM stackwidgetmodel WHERE appwidgetid = :appWidgetId")
    StackWidgetModel getStackWidgetByAppWidgetIdDirectly(final int appWidgetId);

    @Transaction
    @Query("SELECT EXISTS (SELECT 1 FROM stackwidgetmodel WHERE stackId in (:stackLocalIds))")
    boolean containsStackLocalId(final long... stackLocalIds);
}
