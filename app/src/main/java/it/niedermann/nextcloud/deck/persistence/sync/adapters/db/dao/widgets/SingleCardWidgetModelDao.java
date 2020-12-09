package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.widgets;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import it.niedermann.nextcloud.deck.model.full.FullSingleCardWidgetModel;
import it.niedermann.nextcloud.deck.model.widget.singlecard.SingleCardWidgetModel;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.GenericDao;

@Dao
public interface SingleCardWidgetModelDao extends GenericDao<SingleCardWidgetModel> {

    @Transaction
    @Query("SELECT * FROM singlecardwidgetmodel WHERE widgetId = :widgetId")
    FullSingleCardWidgetModel getFullCardByRemoteIdDirectly(final int widgetId);

    @Transaction
    @Query("SELECT EXISTS (SELECT 1 FROM singlecardwidgetmodel WHERE cardId = :cardLocalId)")
    boolean containsCardLocalId(final Long cardLocalId);
}
