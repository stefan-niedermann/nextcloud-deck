package it.niedermann.nextcloud.deck.database.dao.widgets.filter;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetUser;
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
public final class FilterWidgetUserDao_Impl implements FilterWidgetUserDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<FilterWidgetUser> __insertAdapterOfFilterWidgetUser;

  private final EntityDeleteOrUpdateAdapter<FilterWidgetUser> __deleteAdapterOfFilterWidgetUser;

  private final EntityDeleteOrUpdateAdapter<FilterWidgetUser> __updateAdapterOfFilterWidgetUser;

  public FilterWidgetUserDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfFilterWidgetUser = new EntityInsertAdapter<FilterWidgetUser>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `FilterWidgetUser` (`id`,`filterAccountId`,`userId`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final FilterWidgetUser entity) {
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
        if (entity.getUserId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getUserId());
        }
      }
    };
    this.__deleteAdapterOfFilterWidgetUser = new EntityDeleteOrUpdateAdapter<FilterWidgetUser>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `FilterWidgetUser` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final FilterWidgetUser entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfFilterWidgetUser = new EntityDeleteOrUpdateAdapter<FilterWidgetUser>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `FilterWidgetUser` SET `id` = ?,`filterAccountId` = ?,`userId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final FilterWidgetUser entity) {
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
        if (entity.getUserId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getUserId());
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
  public long insert(final FilterWidgetUser entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfFilterWidgetUser.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final FilterWidgetUser... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfFilterWidgetUser.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final FilterWidgetUser... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfFilterWidgetUser.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final FilterWidgetUser... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfFilterWidgetUser.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public List<FilterWidgetUser> getFilterWidgetUsersByFilterWidgetAccountIdDirectly(
      final Long filterWidgetAccountId) {
    final String _sql = "SELECT * FROM FilterWidgetUser where filterAccountId = ?";
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
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final List<FilterWidgetUser> _result = new ArrayList<FilterWidgetUser>();
        while (_stmt.step()) {
          final FilterWidgetUser _item;
          _item = new FilterWidgetUser();
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
          final Long _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
          }
          _item.setUserId(_tmpUserId);
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
