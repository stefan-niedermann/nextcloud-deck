package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import it.niedermann.nextcloud.deck.model.appwidgets.StackWidgetModel;

@Dao
public interface StackWidgetModelDao extends GenericDao<StackWidgetModel> {

    @Transaction
    @Query("SELECT * FROM stackwidgetmodel WHERE appwidgetid = :appWidgetId")
    StackWidgetModel getStackWidgetByRemoteId(final int appWidgetId);
}
