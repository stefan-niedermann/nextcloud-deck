package it.niedermann.nextcloud.deck.database.dao.projects;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.model.ocs.projects.JoinCardWithProject;
import java.lang.Class;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class JoinCardWithOcsProjectDao_Impl implements JoinCardWithOcsProjectDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<JoinCardWithProject> __insertAdapterOfJoinCardWithProject;

  private final EntityDeleteOrUpdateAdapter<JoinCardWithProject> __deleteAdapterOfJoinCardWithProject;

  private final EntityDeleteOrUpdateAdapter<JoinCardWithProject> __updateAdapterOfJoinCardWithProject;

  public JoinCardWithOcsProjectDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfJoinCardWithProject = new EntityInsertAdapter<JoinCardWithProject>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `JoinCardWithProject` (`projectId`,`cardId`,`status`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final JoinCardWithProject entity) {
        if (entity.getProjectId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getProjectId());
        }
        if (entity.getCardId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getCardId());
        }
        statement.bindLong(3, entity.getStatus());
      }
    };
    this.__deleteAdapterOfJoinCardWithProject = new EntityDeleteOrUpdateAdapter<JoinCardWithProject>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `JoinCardWithProject` WHERE `projectId` = ? AND `cardId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final JoinCardWithProject entity) {
        if (entity.getProjectId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getProjectId());
        }
        if (entity.getCardId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getCardId());
        }
      }
    };
    this.__updateAdapterOfJoinCardWithProject = new EntityDeleteOrUpdateAdapter<JoinCardWithProject>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `JoinCardWithProject` SET `projectId` = ?,`cardId` = ?,`status` = ? WHERE `projectId` = ? AND `cardId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final JoinCardWithProject entity) {
        if (entity.getProjectId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getProjectId());
        }
        if (entity.getCardId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getCardId());
        }
        statement.bindLong(3, entity.getStatus());
        if (entity.getProjectId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getProjectId());
        }
        if (entity.getCardId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getCardId());
        }
      }
    };
  }

  @Override
  public long insert(final JoinCardWithProject entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfJoinCardWithProject.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final JoinCardWithProject... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfJoinCardWithProject.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final JoinCardWithProject... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfJoinCardWithProject.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final JoinCardWithProject... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfJoinCardWithProject.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public JoinCardWithProject getAssignmentByCardIdAndProjectIdDirectly(final Long localCardId,
      final Long localProjectId) {
    final String _sql = "select * from JoinCardWithProject where projectId = ? and cardId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (localProjectId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localProjectId);
        }
        _argIndex = 2;
        if (localCardId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localCardId);
        }
        final int _columnIndexOfProjectId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "projectId");
        final int _columnIndexOfCardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cardId");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final JoinCardWithProject _result;
        if (_stmt.step()) {
          _result = new JoinCardWithProject();
          final Long _tmpProjectId;
          if (_stmt.isNull(_columnIndexOfProjectId)) {
            _tmpProjectId = null;
          } else {
            _tmpProjectId = _stmt.getLong(_columnIndexOfProjectId);
          }
          _result.setProjectId(_tmpProjectId);
          final Long _tmpCardId;
          if (_stmt.isNull(_columnIndexOfCardId)) {
            _tmpCardId = null;
          } else {
            _tmpCardId = _stmt.getLong(_columnIndexOfCardId);
          }
          _result.setCardId(_tmpCardId);
          final int _tmpStatus;
          _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
          _result.setStatus(_tmpStatus);
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
  public void deleteProjectResourcesByCardIdExceptGivenProjectIdsDirectly(final long accountId,
      final Long localCardId, final List<Long> remoteProjectIDs) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("delete from JoinCardWithProject where cardId = ");
    _stringBuilder.append("?");
    _stringBuilder.append(" and projectId NOT in (select p.localId from OcsProject p where p.accountId = ");
    _stringBuilder.append("?");
    _stringBuilder.append(" and p.id in (");
    final int _inputSize = remoteProjectIDs == null ? 1 : remoteProjectIDs.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append("))");
    final String _sql = _stringBuilder.toString();
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (localCardId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localCardId);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 3;
        if (remoteProjectIDs == null) {
          _stmt.bindNull(_argIndex);
        } else {
          for (Long _item : remoteProjectIDs) {
            if (_item == null) {
              _stmt.bindNull(_argIndex);
            } else {
              _stmt.bindLong(_argIndex, _item);
            }
            _argIndex++;
          }
        }
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void deleteProjectResourcesByCardIdDirectly(final Long localCardId) {
    final String _sql = "delete from JoinCardWithProject where cardId = ?";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (localCardId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localCardId);
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
