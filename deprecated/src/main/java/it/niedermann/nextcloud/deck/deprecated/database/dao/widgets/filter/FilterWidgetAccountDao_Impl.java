package it.niedermann.nextcloud.deck.database.dao.widgets.filter;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetAccount;
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
public final class FilterWidgetAccountDao_Impl implements FilterWidgetAccountDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<FilterWidgetAccount> __insertAdapterOfFilterWidgetAccount;

  private final EntityDeleteOrUpdateAdapter<FilterWidgetAccount> __deleteAdapterOfFilterWidgetAccount;

  private final EntityDeleteOrUpdateAdapter<FilterWidgetAccount> __updateAdapterOfFilterWidgetAccount;

  public FilterWidgetAccountDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfFilterWidgetAccount = new EntityInsertAdapter<FilterWidgetAccount>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `FilterWidgetAccount` (`id`,`filterWidgetId`,`accountId`,`includeNoUser`,`includeNoProject`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final FilterWidgetAccount entity) {
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
        if (entity.getAccountId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getAccountId());
        }
        final int _tmp = entity.isIncludeNoUser() ? 1 : 0;
        statement.bindLong(4, _tmp);
        final int _tmp_1 = entity.isIncludeNoProject() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
      }
    };
    this.__deleteAdapterOfFilterWidgetAccount = new EntityDeleteOrUpdateAdapter<FilterWidgetAccount>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `FilterWidgetAccount` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final FilterWidgetAccount entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfFilterWidgetAccount = new EntityDeleteOrUpdateAdapter<FilterWidgetAccount>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `FilterWidgetAccount` SET `id` = ?,`filterWidgetId` = ?,`accountId` = ?,`includeNoUser` = ?,`includeNoProject` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final FilterWidgetAccount entity) {
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
        if (entity.getAccountId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getAccountId());
        }
        final int _tmp = entity.isIncludeNoUser() ? 1 : 0;
        statement.bindLong(4, _tmp);
        final int _tmp_1 = entity.isIncludeNoProject() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        if (entity.getId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getId());
        }
      }
    };
  }

  @Override
  public long insert(final FilterWidgetAccount entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfFilterWidgetAccount.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final FilterWidgetAccount... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfFilterWidgetAccount.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final FilterWidgetAccount... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfFilterWidgetAccount.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final FilterWidgetAccount... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfFilterWidgetAccount.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public List<FilterWidgetAccount> getFilterWidgetAccountsByFilterWidgetIdDirectly(
      final Integer filterWidgetId) {
    final String _sql = "select * FROM FilterWidgetAccount WHERE filterWidgetId = ?";
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
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfIncludeNoUser = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "includeNoUser");
        final int _columnIndexOfIncludeNoProject = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "includeNoProject");
        final List<FilterWidgetAccount> _result = new ArrayList<FilterWidgetAccount>();
        while (_stmt.step()) {
          final FilterWidgetAccount _item;
          _item = new FilterWidgetAccount();
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
          final Long _tmpAccountId;
          if (_stmt.isNull(_columnIndexOfAccountId)) {
            _tmpAccountId = null;
          } else {
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
          }
          _item.setAccountId(_tmpAccountId);
          final boolean _tmpIncludeNoUser;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIncludeNoUser));
          _tmpIncludeNoUser = _tmp != 0;
          _item.setIncludeNoUser(_tmpIncludeNoUser);
          final boolean _tmpIncludeNoProject;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfIncludeNoProject));
          _tmpIncludeNoProject = _tmp_1 != 0;
          _item.setIncludeNoProject(_tmpIncludeNoProject);
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
    final String _sql = "DELETE FROM FilterWidgetAccount WHERE filterWidgetId = ?";
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
