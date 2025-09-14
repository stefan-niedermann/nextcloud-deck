package it.niedermann.nextcloud.deck.database.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.model.JoinBoardWithPermission;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class JoinBoardWithPermissionDao_Impl implements JoinBoardWithPermissionDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<JoinBoardWithPermission> __insertAdapterOfJoinBoardWithPermission;

  private final EntityDeleteOrUpdateAdapter<JoinBoardWithPermission> __deleteAdapterOfJoinBoardWithPermission;

  private final EntityDeleteOrUpdateAdapter<JoinBoardWithPermission> __updateAdapterOfJoinBoardWithPermission;

  public JoinBoardWithPermissionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfJoinBoardWithPermission = new EntityInsertAdapter<JoinBoardWithPermission>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `JoinBoardWithPermission` (`permissionId`,`boardId`,`status`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final JoinBoardWithPermission entity) {
        if (entity.getPermissionId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getPermissionId());
        }
        if (entity.getBoardId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getBoardId());
        }
        statement.bindLong(3, entity.getStatus());
      }
    };
    this.__deleteAdapterOfJoinBoardWithPermission = new EntityDeleteOrUpdateAdapter<JoinBoardWithPermission>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `JoinBoardWithPermission` WHERE `permissionId` = ? AND `boardId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final JoinBoardWithPermission entity) {
        if (entity.getPermissionId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getPermissionId());
        }
        if (entity.getBoardId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getBoardId());
        }
      }
    };
    this.__updateAdapterOfJoinBoardWithPermission = new EntityDeleteOrUpdateAdapter<JoinBoardWithPermission>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `JoinBoardWithPermission` SET `permissionId` = ?,`boardId` = ?,`status` = ? WHERE `permissionId` = ? AND `boardId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final JoinBoardWithPermission entity) {
        if (entity.getPermissionId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getPermissionId());
        }
        if (entity.getBoardId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getBoardId());
        }
        statement.bindLong(3, entity.getStatus());
        if (entity.getPermissionId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getPermissionId());
        }
        if (entity.getBoardId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getBoardId());
        }
      }
    };
  }

  @Override
  public long insert(final JoinBoardWithPermission entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfJoinBoardWithPermission.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final JoinBoardWithPermission... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfJoinBoardWithPermission.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final JoinBoardWithPermission... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfJoinBoardWithPermission.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final JoinBoardWithPermission... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfJoinBoardWithPermission.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void deleteByBoardId(final long localId) {
    final String _sql = "DELETE FROM joinboardwithpermission WHERE boardId = ?";
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
