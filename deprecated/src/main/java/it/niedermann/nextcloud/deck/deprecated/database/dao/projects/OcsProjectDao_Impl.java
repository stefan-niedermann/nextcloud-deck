package it.niedermann.nextcloud.deck.database.dao.projects;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.database.converter.DateTypeConverter;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProject;
import java.lang.Class;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class OcsProjectDao_Impl implements OcsProjectDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<OcsProject> __insertAdapterOfOcsProject;

  private final EntityDeleteOrUpdateAdapter<OcsProject> __deleteAdapterOfOcsProject;

  private final EntityDeleteOrUpdateAdapter<OcsProject> __updateAdapterOfOcsProject;

  public OcsProjectDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfOcsProject = new EntityInsertAdapter<OcsProject>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `OcsProject` (`name`,`localId`,`accountId`,`id`,`status`,`lastModified`,`lastModifiedLocal`,`etag`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final OcsProject entity) {
        if (entity.getName() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getName());
        }
        if (entity.getLocalId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getLocalId());
        }
        statement.bindLong(3, entity.getAccountId());
        if (entity.getId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getId());
        }
        statement.bindLong(5, entity.getStatus());
        final Long _tmp = DateTypeConverter.fromInstant(entity.getLastModified());
        if (_tmp == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, _tmp);
        }
        final Long _tmp_1 = DateTypeConverter.fromInstant(entity.getLastModifiedLocal());
        if (_tmp_1 == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, _tmp_1);
        }
        if (entity.getEtag() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getEtag());
        }
      }
    };
    this.__deleteAdapterOfOcsProject = new EntityDeleteOrUpdateAdapter<OcsProject>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `OcsProject` WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final OcsProject entity) {
        if (entity.getLocalId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getLocalId());
        }
      }
    };
    this.__updateAdapterOfOcsProject = new EntityDeleteOrUpdateAdapter<OcsProject>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `OcsProject` SET `name` = ?,`localId` = ?,`accountId` = ?,`id` = ?,`status` = ?,`lastModified` = ?,`lastModifiedLocal` = ?,`etag` = ? WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final OcsProject entity) {
        if (entity.getName() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getName());
        }
        if (entity.getLocalId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getLocalId());
        }
        statement.bindLong(3, entity.getAccountId());
        if (entity.getId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getId());
        }
        statement.bindLong(5, entity.getStatus());
        final Long _tmp = DateTypeConverter.fromInstant(entity.getLastModified());
        if (_tmp == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, _tmp);
        }
        final Long _tmp_1 = DateTypeConverter.fromInstant(entity.getLastModifiedLocal());
        if (_tmp_1 == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, _tmp_1);
        }
        if (entity.getEtag() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getEtag());
        }
        if (entity.getLocalId() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getLocalId());
        }
      }
    };
  }

  @Override
  public long insert(final OcsProject entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfOcsProject.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final OcsProject... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfOcsProject.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final OcsProject... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfOcsProject.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final OcsProject... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfOcsProject.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public OcsProject getProjectByRemoteIdDirectly(final long accountId, final Long remoteId) {
    final String _sql = "select * from OcsProject where accountId = ? and id = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        if (remoteId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, remoteId);
        }
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final OcsProject _result;
        if (_stmt.step()) {
          _result = new OcsProject();
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          _result.setName(_tmpName);
          final Long _tmpLocalId;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpLocalId = null;
          } else {
            _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
          }
          _result.setLocalId(_tmpLocalId);
          final long _tmpAccountId;
          _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
          _result.setAccountId(_tmpAccountId);
          final Long _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId);
          }
          _result.setId(_tmpId);
          final int _tmpStatus;
          _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
          _result.setStatus(_tmpStatus);
          final Instant _tmpLastModified;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp);
          _result.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_1);
          _result.setLastModifiedLocal(_tmpLastModifiedLocal);
          final String _tmpEtag;
          if (_stmt.isNull(_columnIndexOfEtag)) {
            _tmpEtag = null;
          } else {
            _tmpEtag = _stmt.getText(_columnIndexOfEtag);
          }
          _result.setEtag(_tmpEtag);
        } else {
          _result = null;
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
