package it.niedermann.nextcloud.deck.database.dao.widgets.filter;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetLabel;
import java.lang.Class;
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
public final class FilterWidgetLabelDao_Impl implements FilterWidgetLabelDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<FilterWidgetLabel> __insertAdapterOfFilterWidgetLabel;

  private final EntityDeleteOrUpdateAdapter<FilterWidgetLabel> __deleteAdapterOfFilterWidgetLabel;

  private final EntityDeleteOrUpdateAdapter<FilterWidgetLabel> __updateAdapterOfFilterWidgetLabel;

  public FilterWidgetLabelDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfFilterWidgetLabel = new EntityInsertAdapter<FilterWidgetLabel>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `FilterWidgetLabel` (`id`,`filterBoardId`,`labelId`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final FilterWidgetLabel entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getFilterBoardId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getFilterBoardId());
        }
        if (entity.getLabelId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getLabelId());
        }
      }
    };
    this.__deleteAdapterOfFilterWidgetLabel = new EntityDeleteOrUpdateAdapter<FilterWidgetLabel>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `FilterWidgetLabel` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final FilterWidgetLabel entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfFilterWidgetLabel = new EntityDeleteOrUpdateAdapter<FilterWidgetLabel>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `FilterWidgetLabel` SET `id` = ?,`filterBoardId` = ?,`labelId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final FilterWidgetLabel entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getFilterBoardId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getFilterBoardId());
        }
        if (entity.getLabelId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getLabelId());
        }
        if (entity.getId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getId());
        }
      }
    };
  }

  @Override
  public long insert(final FilterWidgetLabel entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfFilterWidgetLabel.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final FilterWidgetLabel... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfFilterWidgetLabel.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final FilterWidgetLabel... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfFilterWidgetLabel.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final FilterWidgetLabel... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfFilterWidgetLabel.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public List<FilterWidgetLabel> getFilterWidgetLabelsByFilterWidgetBoardIdDirectly(
      final Long filterWidgetBoardId) {
    final String _sql = "SELECT * FROM FilterWidgetLabel where filterBoardId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (filterWidgetBoardId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, filterWidgetBoardId);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfFilterBoardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "filterBoardId");
        final int _columnIndexOfLabelId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "labelId");
        final List<FilterWidgetLabel> _result = new ArrayList<FilterWidgetLabel>();
        while (_stmt.step()) {
          final FilterWidgetLabel _item;
          _item = new FilterWidgetLabel();
          final Long _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId);
          }
          _item.setId(_tmpId);
          final Long _tmpFilterBoardId;
          if (_stmt.isNull(_columnIndexOfFilterBoardId)) {
            _tmpFilterBoardId = null;
          } else {
            _tmpFilterBoardId = _stmt.getLong(_columnIndexOfFilterBoardId);
          }
          _item.setFilterBoardId(_tmpFilterBoardId);
          final Long _tmpLabelId;
          if (_stmt.isNull(_columnIndexOfLabelId)) {
            _tmpLabelId = null;
          } else {
            _tmpLabelId = _stmt.getLong(_columnIndexOfLabelId);
          }
          _item.setLabelId(_tmpLabelId);
          _result.add(_item);
        }
        return _result;
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
