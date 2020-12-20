package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.widgets.filter;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import it.niedermann.nextcloud.deck.model.widget.filter.EWidgetType;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.GenericDao;

@Dao
public interface FilterWidgetDao extends GenericDao<FilterWidget> {
    @Query("DELETE FROM filterwidget WHERE id = :filterWidgetId")
    void delete (Integer filterWidgetId);

    @Query("SELECT * FROM FilterWidget where id = :filterWidgetId")
    FilterWidget getFilterWidgetByIdDirectly(Integer filterWidgetId);

    @Query("SELECT EXISTS (SELECT 1 FROM FilterWidget WHERE id = :filterWidgetId)")
    boolean filterWidgetExists(int filterWidgetId);

    @Query("SELECT id FROM FilterWidget WHERE widgetType = :type")
    List<Integer> getFilterWidgetIdsByType(int type);

    @Transaction
    @Query("SELECT DISTINCT w.widgetType " +
            "FROM FilterWidget w " +
            "LEFT JOIN FilterWidgetAccount a ON w.id = a.filterWidgetId " +
            "LEFT JOIN FilterWidgetBoard b ON a.id = b.filterAccountId " +
            "LEFT JOIN FilterWidgetStack s ON b.id = s.filterBoardId " +
            "LEFT JOIN FilterWidgetUser u ON a.id = u.filterAccountId " +
            "LEFT JOIN FilterWidgetProject p ON a.id = p.filterAccountId " +
            "LEFT JOIN FilterWidgetLabel l ON b.id = l.filterBoardId " +
            "WHERE (:changedEntityType = 'ACCOUNT' AND (a.accountId = :localIdOfChangedEntity OR a.accountId IS NULL)) " +
            "OR (:changedEntityType = 'BOARD' AND (b.boardId = :localIdOfChangedEntity OR b.boardId IS NULL)) " +
            "OR (:changedEntityType = 'STACK' AND (s.stackId = :localIdOfChangedEntity OR s.stackId IS NULL)) " +
            "OR (:changedEntityType = 'USER' AND (u.userId = :localIdOfChangedEntity OR u.userId IS NULL)) " +
            "OR (:changedEntityType = 'PROJECT' AND (p.projectId = :localIdOfChangedEntity OR p.projectId IS NULL)) " +
            "OR (:changedEntityType = 'LABEL' AND (l.labelId = :localIdOfChangedEntity OR l.labelId IS NULL)) "
    )
    List<EWidgetType> getChangedListTypesByEntity(String changedEntityType, Long localIdOfChangedEntity);
}
