package it.niedermann.nextcloud.deck.database.dao.widgets.filter;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetProject;
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
public final class FilterWidgetProjectDao_Impl implements FilterWidgetProjectDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<FilterWidgetProject> __insertAdapterOfFilterWidgetProject;

  private final EntityDeleteOrUpdateAdapter<FilterWidgetProject> __deleteAdapterOfFilterWidgetProject;

  private final EntityDeleteOrUpdateAdapter<FilterWidgetProject> __updateAdapterOfFilterWidgetProject;

  public FilterWidgetProjectDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfFilterWidgetProject = new EntityInsertAdapter<FilterWidgetProject>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `FilterWidgetProject` (`id`,`filterAccountId`,`projectId`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final FilterWidgetProject entity) {
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
        if (entity.getProjectId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getProjectId());
        }
      }
    };
    this.__deleteAdapterOfFilterWidgetProject = new EntityDeleteOrUpdateAdapter<FilterWidgetProject>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `FilterWidgetProject` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final FilterWidgetProject entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfFilterWidgetProject = new EntityDeleteOrUpdateAdapter<FilterWidgetProject>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `FilterWidgetProject` SET `id` = ?,`filterAccountId` = ?,`projectId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final FilterWidgetProject entity) {
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
        if (entity.getProjectId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getProjectId());
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
  public long insert(final FilterWidgetProject entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfFilterWidgetProject.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final FilterWidgetProject... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfFilterWidgetProject.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final FilterWidgetProject... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfFilterWidgetProject.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final FilterWidgetProject... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfFilterWidgetProject.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public List<FilterWidgetProject> getFilterWidgetProjectsByFilterWidgetAccountIdDirectly(
      final Long filterWidgetAccountId) {
    final String _sql = "SELECT * FROM FilterWidgetProject where filterAccountId = ?";
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
        final int _columnIndexOfProjectId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "projectId");
        final List<FilterWidgetProject> _result = new ArrayList<FilterWidgetProject>();
        while (_stmt.step()) {
          final FilterWidgetProject _item;
          _item = new FilterWidgetProject();
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
          final Long _tmpProjectId;
          if (_stmt.isNull(_columnIndexOfProjectId)) {
            _tmpProjectId = null;
          } else {
            _tmpProjectId = _stmt.getLong(_columnIndexOfProjectId);
          }
          _item.setProjectId(_tmpProjectId);
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
