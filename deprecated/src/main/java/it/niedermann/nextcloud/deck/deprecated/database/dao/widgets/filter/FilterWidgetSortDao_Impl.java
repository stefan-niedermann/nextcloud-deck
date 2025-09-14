package it.niedermann.nextcloud.deck.database.dao.widgets.filter;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.database.converter.EnumConverter;
import it.niedermann.nextcloud.deck.model.enums.ESortCriteria;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetSort;
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
public final class FilterWidgetSortDao_Impl implements FilterWidgetSortDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<FilterWidgetSort> __insertAdapterOfFilterWidgetSort;

  private final EntityDeleteOrUpdateAdapter<FilterWidgetSort> __deleteAdapterOfFilterWidgetSort;

  private final EntityDeleteOrUpdateAdapter<FilterWidgetSort> __updateAdapterOfFilterWidgetSort;

  public FilterWidgetSortDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfFilterWidgetSort = new EntityInsertAdapter<FilterWidgetSort>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `FilterWidgetSort` (`id`,`filterWidgetId`,`direction`,`criteria`,`ruleOrder`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final FilterWidgetSort entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getFilterWidgetId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getFilterWidgetId());
        }
        final int _tmp = entity.isDirection() ? 1 : 0;
        statement.bindLong(3, _tmp);
        final Integer _tmp_1 = EnumConverter.fromSortCriteriaEnum(entity.getCriteria());
        if (_tmp_1 == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, _tmp_1);
        }
        statement.bindLong(5, entity.getRuleOrder());
      }
    };
    this.__deleteAdapterOfFilterWidgetSort = new EntityDeleteOrUpdateAdapter<FilterWidgetSort>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `FilterWidgetSort` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final FilterWidgetSort entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfFilterWidgetSort = new EntityDeleteOrUpdateAdapter<FilterWidgetSort>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `FilterWidgetSort` SET `id` = ?,`filterWidgetId` = ?,`direction` = ?,`criteria` = ?,`ruleOrder` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final FilterWidgetSort entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getFilterWidgetId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getFilterWidgetId());
        }
        final int _tmp = entity.isDirection() ? 1 : 0;
        statement.bindLong(3, _tmp);
        final Integer _tmp_1 = EnumConverter.fromSortCriteriaEnum(entity.getCriteria());
        if (_tmp_1 == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, _tmp_1);
        }
        statement.bindLong(5, entity.getRuleOrder());
        if (entity.getId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getId());
        }
      }
    };
  }

  @Override
  public long insert(final FilterWidgetSort entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfFilterWidgetSort.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final FilterWidgetSort... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfFilterWidgetSort.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final FilterWidgetSort... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfFilterWidgetSort.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final FilterWidgetSort... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfFilterWidgetSort.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public List<FilterWidgetSort> getFilterWidgetSortByFilterWidgetIdDirectly(
      final Integer filterWidgetId) {
    final String _sql = "select * FROM FilterWidgetSort WHERE filterWidgetId = ? order by ruleOrder asc";
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
        final int _columnIndexOfFilterWidgetId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "filterWidgetId");
        final int _columnIndexOfDirection = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "direction");
        final int _columnIndexOfCriteria = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "criteria");
        final int _columnIndexOfRuleOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ruleOrder");
        final List<FilterWidgetSort> _result = new ArrayList<FilterWidgetSort>();
        while (_stmt.step()) {
          final FilterWidgetSort _item;
          _item = new FilterWidgetSort();
          final Long _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId);
          }
          _item.setId(_tmpId);
          final Long _tmpFilterWidgetId;
          if (_stmt.isNull(_columnIndexOfFilterWidgetId)) {
            _tmpFilterWidgetId = null;
          } else {
            _tmpFilterWidgetId = _stmt.getLong(_columnIndexOfFilterWidgetId);
          }
          _item.setFilterWidgetId(_tmpFilterWidgetId);
          final boolean _tmpDirection;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfDirection));
          _tmpDirection = _tmp != 0;
          _item.setDirection(_tmpDirection);
          final ESortCriteria _tmpCriteria;
          final Integer _tmp_1;
          if (_stmt.isNull(_columnIndexOfCriteria)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = (int) (_stmt.getLong(_columnIndexOfCriteria));
          }
          _tmpCriteria = EnumConverter.toSortCriteriaEnum(_tmp_1);
          _item.setCriteria(_tmpCriteria);
          final int _tmpRuleOrder;
          _tmpRuleOrder = (int) (_stmt.getLong(_columnIndexOfRuleOrder));
          _item.setRuleOrder(_tmpRuleOrder);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void deleteByFilterWidgetId(final Integer filterWidgetId) {
    final String _sql = "DELETE FROM FilterWidgetSort WHERE filterWidgetId = ?";
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
