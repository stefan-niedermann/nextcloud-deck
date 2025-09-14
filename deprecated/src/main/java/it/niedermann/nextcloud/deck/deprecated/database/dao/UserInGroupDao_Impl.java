package it.niedermann.nextcloud.deck.database.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomRawQuery;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import androidx.sqlite.db.SupportSQLiteQuery;
import it.niedermann.nextcloud.deck.model.ocs.user.UserForAssignment;
import it.niedermann.nextcloud.deck.model.relations.UserInGroup;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class UserInGroupDao_Impl implements UserInGroupDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<UserInGroup> __insertAdapterOfUserInGroup;

  private final EntityDeleteOrUpdateAdapter<UserInGroup> __deleteAdapterOfUserInGroup;

  private final EntityDeleteOrUpdateAdapter<UserInGroup> __updateAdapterOfUserInGroup;

  public UserInGroupDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfUserInGroup = new EntityInsertAdapter<UserInGroup>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `UserInGroup` (`groupId`,`memberId`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final UserInGroup entity) {
        if (entity.getGroupId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getGroupId());
        }
        if (entity.getMemberId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getMemberId());
        }
      }
    };
    this.__deleteAdapterOfUserInGroup = new EntityDeleteOrUpdateAdapter<UserInGroup>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `UserInGroup` WHERE `groupId` = ? AND `memberId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final UserInGroup entity) {
        if (entity.getGroupId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getGroupId());
        }
        if (entity.getMemberId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getMemberId());
        }
      }
    };
    this.__updateAdapterOfUserInGroup = new EntityDeleteOrUpdateAdapter<UserInGroup>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `UserInGroup` SET `groupId` = ?,`memberId` = ? WHERE `groupId` = ? AND `memberId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final UserInGroup entity) {
        if (entity.getGroupId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getGroupId());
        }
        if (entity.getMemberId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getMemberId());
        }
        if (entity.getGroupId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getGroupId());
        }
        if (entity.getMemberId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getMemberId());
        }
      }
    };
  }

  @Override
  public long insert(final UserInGroup entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfUserInGroup.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final UserInGroup... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfUserInGroup.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final UserInGroup... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfUserInGroup.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final UserInGroup... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfUserInGroup.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void deleteByGroupId(final long localId) {
    final String _sql = "DELETE FROM useringroup WHERE groupId = ?";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localId);
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public UserForAssignment getUserForAssignment(final SupportSQLiteQuery query) {
    final RoomRawQuery _rawQuery = RoomSQLiteQuery.copyFrom(query).toRoomRawQuery();
    final String _sql = _rawQuery.getSql();
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _rawQuery.getBindingFunction().invoke(_stmt);
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndex(_stmt, "type");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndex(_stmt, "userId");
        final UserForAssignment _result;
        if (_stmt.step()) {
          final int _tmpType;
          if (_columnIndexOfType == -1) {
            _tmpType = 0;
          } else {
            _tmpType = (int) (_stmt.getLong(_columnIndexOfType));
          }
          final String _tmpUserId;
          if (_columnIndexOfUserId == -1) {
            _tmpUserId = null;
          } else {
            if (_stmt.isNull(_columnIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _stmt.getText(_columnIndexOfUserId);
            }
          }
          _result = new UserForAssignment(_tmpType,_tmpUserId);
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
