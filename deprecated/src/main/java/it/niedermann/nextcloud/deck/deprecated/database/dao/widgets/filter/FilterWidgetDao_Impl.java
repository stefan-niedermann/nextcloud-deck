package it.niedermann.nextcloud.deck.database.dao.widgets.filter;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.database.converter.EnumConverter;
import it.niedermann.nextcloud.deck.model.enums.EDueType;
import it.niedermann.nextcloud.deck.model.widget.filter.EWidgetType;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class FilterWidgetDao_Impl implements FilterWidgetDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<FilterWidget> __insertAdapterOfFilterWidget;

  private final EntityDeleteOrUpdateAdapter<FilterWidget> __deleteAdapterOfFilterWidget;

  private final EntityDeleteOrUpdateAdapter<FilterWidget> __updateAdapterOfFilterWidget;

  public FilterWidgetDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfFilterWidget = new EntityInsertAdapter<FilterWidget>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `FilterWidget` (`id`,`title`,`dueType`,`widgetType`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final FilterWidget entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getTitle());
        }
        final Integer _tmp = EnumConverter.fromDueTypeEnum(entity.getDueType());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, _tmp);
        }
        final Integer _tmp_1 = EnumConverter.fromWidgetTypeEnum(entity.getWidgetType());
        if (_tmp_1 == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, _tmp_1);
        }
      }
    };
    this.__deleteAdapterOfFilterWidget = new EntityDeleteOrUpdateAdapter<FilterWidget>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `FilterWidget` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final FilterWidget entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfFilterWidget = new EntityDeleteOrUpdateAdapter<FilterWidget>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `FilterWidget` SET `id` = ?,`title` = ?,`dueType` = ?,`widgetType` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final FilterWidget entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getTitle());
        }
        final Integer _tmp = EnumConverter.fromDueTypeEnum(entity.getDueType());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, _tmp);
        }
        final Integer _tmp_1 = EnumConverter.fromWidgetTypeEnum(entity.getWidgetType());
        if (_tmp_1 == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, _tmp_1);
        }
        if (entity.getId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getId());
        }
      }
    };
  }

  @Override
  public long insert(final FilterWidget entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfFilterWidget.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final FilterWidget... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfFilterWidget.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final FilterWidget... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfFilterWidget.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final FilterWidget... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfFilterWidget.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public FilterWidget getFilterWidgetByIdDirectly(final Integer filterWidgetId) {
    final String _sql = "SELECT * FROM FilterWidget where id = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (filterWidgetId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, filterWidgetId);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDueType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueType");
        final int _columnIndexOfWidgetType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "widgetType");
        final FilterWidget _result;
        if (_stmt.step()) {
          _result = new FilterWidget();
          final int _tmpId;
          _tmpId = (int) (_stmt.getLong(_columnIndexOfId));
          _result.setId(_tmpId);
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _result.setTitle(_tmpTitle);
          final EDueType _tmpDueType;
          final Integer _tmp;
          if (_stmt.isNull(_columnIndexOfDueType)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(_columnIndexOfDueType));
          }
          _tmpDueType = EnumConverter.toDueTypeEnum(_tmp);
          _result.setDueType(_tmpDueType);
          final EWidgetType _tmpWidgetType;
          final Integer _tmp_1;
          if (_stmt.isNull(_columnIndexOfWidgetType)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = (int) (_stmt.getLong(_columnIndexOfWidgetType));
          }
          _tmpWidgetType = EnumConverter.toWidgetTypeEnum(_tmp_1);
          _result.setWidgetType(_tmpWidgetType);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public boolean filterWidgetExists(final int filterWidgetId) {
    final String _sql = "SELECT EXISTS (SELECT 1 FROM FilterWidget WHERE id = ?)";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, filterWidgetId);
        final boolean _result;
        if (_stmt.step()) {
          final int _tmp;
          _tmp = (int) (_stmt.getLong(0));
          _result = _tmp != 0;
        } else {
          _result = false;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<Integer> getFilterWidgetIdsByType(final int type) {
    final String _sql = "SELECT id FROM FilterWidget WHERE widgetType = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, type);
        final List<Integer> _result = new ArrayList<Integer>();
        while (_stmt.step()) {
          final Integer _item;
          if (_stmt.isNull(0)) {
            _item = null;
          } else {
            _item = (int) (_stmt.getLong(0));
          }
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<EWidgetType> getChangedListTypesByEntity(final String changedEntityType,
      final Long localIdOfChangedEntity) {
    final String _sql = "SELECT DISTINCT w.widgetType FROM FilterWidget w LEFT JOIN FilterWidgetAccount a ON w.id = a.filterWidgetId LEFT JOIN FilterWidgetBoard b ON a.id = b.filterAccountId LEFT JOIN FilterWidgetStack s ON b.id = s.filterBoardId LEFT JOIN FilterWidgetUser u ON a.id = u.filterAccountId LEFT JOIN FilterWidgetProject p ON a.id = p.filterAccountId LEFT JOIN FilterWidgetLabel l ON b.id = l.filterBoardId WHERE (? = 'ACCOUNT' AND (a.accountId = ? OR a.accountId IS NULL)) OR (? = 'BOARD' AND (b.boardId = ? OR b.boardId IS NULL)) OR (? = 'STACK' AND (s.stackId = ? OR s.stackId IS NULL)) OR (? = 'USER' AND (u.userId = ? OR u.userId IS NULL)) OR (? = 'PROJECT' AND (p.projectId = ? OR p.projectId IS NULL)) OR (? = 'LABEL' AND (l.labelId = ? OR l.labelId IS NULL)) ";
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (changedEntityType == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, changedEntityType);
        }
        _argIndex = 2;
        if (localIdOfChangedEntity == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localIdOfChangedEntity);
        }
        _argIndex = 3;
        if (changedEntityType == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, changedEntityType);
        }
        _argIndex = 4;
        if (localIdOfChangedEntity == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localIdOfChangedEntity);
        }
        _argIndex = 5;
        if (changedEntityType == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, changedEntityType);
        }
        _argIndex = 6;
        if (localIdOfChangedEntity == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localIdOfChangedEntity);
        }
        _argIndex = 7;
        if (changedEntityType == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, changedEntityType);
        }
        _argIndex = 8;
        if (localIdOfChangedEntity == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localIdOfChangedEntity);
        }
        _argIndex = 9;
        if (changedEntityType == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, changedEntityType);
        }
        _argIndex = 10;
        if (localIdOfChangedEntity == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localIdOfChangedEntity);
        }
        _argIndex = 11;
        if (changedEntityType == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, changedEntityType);
        }
        _argIndex = 12;
        if (localIdOfChangedEntity == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localIdOfChangedEntity);
        }
        final List<EWidgetType> _result = new ArrayList<EWidgetType>();
        while (_stmt.step()) {
          final EWidgetType _item;
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _item = EnumConverter.toWidgetTypeEnum(_tmp);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void delete(final Integer filterWidgetId) {
    final String _sql = "DELETE FROM filterwidget WHERE id = ?";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (filterWidgetId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, filterWidgetId);
        }
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
