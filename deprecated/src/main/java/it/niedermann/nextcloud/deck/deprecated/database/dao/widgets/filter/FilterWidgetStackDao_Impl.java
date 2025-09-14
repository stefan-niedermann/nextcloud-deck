package it.niedermann.nextcloud.deck.database.dao.widgets.filter;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetStack;
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
public final class FilterWidgetStackDao_Impl implements FilterWidgetStackDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<FilterWidgetStack> __insertAdapterOfFilterWidgetStack;

  private final EntityDeleteOrUpdateAdapter<FilterWidgetStack> __deleteAdapterOfFilterWidgetStack;

  private final EntityDeleteOrUpdateAdapter<FilterWidgetStack> __updateAdapterOfFilterWidgetStack;

  public FilterWidgetStackDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfFilterWidgetStack = new EntityInsertAdapter<FilterWidgetStack>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `FilterWidgetStack` (`id`,`filterBoardId`,`stackId`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final FilterWidgetStack entity) {
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
        if (entity.getStackId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getStackId());
        }
      }
    };
    this.__deleteAdapterOfFilterWidgetStack = new EntityDeleteOrUpdateAdapter<FilterWidgetStack>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `FilterWidgetStack` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final FilterWidgetStack entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfFilterWidgetStack = new EntityDeleteOrUpdateAdapter<FilterWidgetStack>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `FilterWidgetStack` SET `id` = ?,`filterBoardId` = ?,`stackId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final FilterWidgetStack entity) {
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
        if (entity.getStackId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getStackId());
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
  public long insert(final FilterWidgetStack entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfFilterWidgetStack.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final FilterWidgetStack... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfFilterWidgetStack.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final FilterWidgetStack... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfFilterWidgetStack.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final FilterWidgetStack... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfFilterWidgetStack.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public List<FilterWidgetStack> getFilterWidgetStacksByFilterWidgetBoardIdDirectly(
      final Long filterWidgetBoardId) {
    final String _sql = "SELECT * FROM FilterWidgetStack where filterBoardId = ?";
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
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "stackId");
        final List<FilterWidgetStack> _result = new ArrayList<FilterWidgetStack>();
        while (_stmt.step()) {
          final FilterWidgetStack _item;
          _item = new FilterWidgetStack();
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
          final Long _tmpStackId;
          if (_stmt.isNull(_columnIndexOfStackId)) {
            _tmpStackId = null;
          } else {
            _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
          }
          _item.setStackId(_tmpStackId);
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
