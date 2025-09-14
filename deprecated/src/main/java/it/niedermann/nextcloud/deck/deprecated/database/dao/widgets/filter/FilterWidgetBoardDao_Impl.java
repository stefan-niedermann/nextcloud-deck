package it.niedermann.nextcloud.deck.database.dao.widgets.filter;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetBoard;
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
public final class FilterWidgetBoardDao_Impl implements FilterWidgetBoardDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<FilterWidgetBoard> __insertAdapterOfFilterWidgetBoard;

  private final EntityDeleteOrUpdateAdapter<FilterWidgetBoard> __deleteAdapterOfFilterWidgetBoard;

  private final EntityDeleteOrUpdateAdapter<FilterWidgetBoard> __updateAdapterOfFilterWidgetBoard;

  public FilterWidgetBoardDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfFilterWidgetBoard = new EntityInsertAdapter<FilterWidgetBoard>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `FilterWidgetBoard` (`id`,`filterAccountId`,`boardId`,`includeNoLabel`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final FilterWidgetBoard entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getFilterAccountId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getFilterAccountId());
        }
        if (entity.getBoardId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getBoardId());
        }
        final int _tmp = entity.isIncludeNoLabel() ? 1 : 0;
        statement.bindLong(4, _tmp);
      }
    };
    this.__deleteAdapterOfFilterWidgetBoard = new EntityDeleteOrUpdateAdapter<FilterWidgetBoard>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `FilterWidgetBoard` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final FilterWidgetBoard entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfFilterWidgetBoard = new EntityDeleteOrUpdateAdapter<FilterWidgetBoard>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `FilterWidgetBoard` SET `id` = ?,`filterAccountId` = ?,`boardId` = ?,`includeNoLabel` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final FilterWidgetBoard entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getFilterAccountId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getFilterAccountId());
        }
        if (entity.getBoardId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getBoardId());
        }
        final int _tmp = entity.isIncludeNoLabel() ? 1 : 0;
        statement.bindLong(4, _tmp);
        if (entity.getId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getId());
        }
      }
    };
  }

  @Override
  public long insert(final FilterWidgetBoard entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfFilterWidgetBoard.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final FilterWidgetBoard... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfFilterWidgetBoard.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final FilterWidgetBoard... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfFilterWidgetBoard.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final FilterWidgetBoard... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfFilterWidgetBoard.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public List<FilterWidgetBoard> getFilterWidgetBoardsByFilterWidgetAccountIdDirectly(
      final Long filterWidgetAccountId) {
    final String _sql = "SELECT * FROM FilterWidgetBoard where filterAccountId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (filterWidgetAccountId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, filterWidgetAccountId);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfFilterAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "filterAccountId");
        final int _columnIndexOfBoardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardId");
        final int _columnIndexOfIncludeNoLabel = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "includeNoLabel");
        final List<FilterWidgetBoard> _result = new ArrayList<FilterWidgetBoard>();
        while (_stmt.step()) {
          final FilterWidgetBoard _item;
          _item = new FilterWidgetBoard();
          final Long _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId);
          }
          _item.setId(_tmpId);
          final Long _tmpFilterAccountId;
          if (_stmt.isNull(_columnIndexOfFilterAccountId)) {
            _tmpFilterAccountId = null;
          } else {
            _tmpFilterAccountId = _stmt.getLong(_columnIndexOfFilterAccountId);
          }
          _item.setFilterAccountId(_tmpFilterAccountId);
          final Long _tmpBoardId;
          if (_stmt.isNull(_columnIndexOfBoardId)) {
            _tmpBoardId = null;
          } else {
            _tmpBoardId = _stmt.getLong(_columnIndexOfBoardId);
          }
          _item.setBoardId(_tmpBoardId);
          final boolean _tmpIncludeNoLabel;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIncludeNoLabel));
          _tmpIncludeNoLabel = _tmp != 0;
          _item.setIncludeNoLabel(_tmpIncludeNoLabel);
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
