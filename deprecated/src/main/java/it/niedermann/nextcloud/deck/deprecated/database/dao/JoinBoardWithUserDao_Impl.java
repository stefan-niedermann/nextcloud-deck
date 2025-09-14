package it.niedermann.nextcloud.deck.database.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.model.JoinBoardWithUser;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class JoinBoardWithUserDao_Impl implements JoinBoardWithUserDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<JoinBoardWithUser> __insertAdapterOfJoinBoardWithUser;

  private final EntityDeleteOrUpdateAdapter<JoinBoardWithUser> __deleteAdapterOfJoinBoardWithUser;

  private final EntityDeleteOrUpdateAdapter<JoinBoardWithUser> __updateAdapterOfJoinBoardWithUser;

  public JoinBoardWithUserDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfJoinBoardWithUser = new EntityInsertAdapter<JoinBoardWithUser>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `JoinBoardWithUser` (`userId`,`boardId`,`status`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final JoinBoardWithUser entity) {
        if (entity.getUserId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getUserId());
        }
        if (entity.getBoardId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getBoardId());
        }
        statement.bindLong(3, entity.getStatus());
      }
    };
    this.__deleteAdapterOfJoinBoardWithUser = new EntityDeleteOrUpdateAdapter<JoinBoardWithUser>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `JoinBoardWithUser` WHERE `userId` = ? AND `boardId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final JoinBoardWithUser entity) {
        if (entity.getUserId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getUserId());
        }
        if (entity.getBoardId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getBoardId());
        }
      }
    };
    this.__updateAdapterOfJoinBoardWithUser = new EntityDeleteOrUpdateAdapter<JoinBoardWithUser>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `JoinBoardWithUser` SET `userId` = ?,`boardId` = ?,`status` = ? WHERE `userId` = ? AND `boardId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final JoinBoardWithUser entity) {
        if (entity.getUserId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getUserId());
        }
        if (entity.getBoardId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getBoardId());
        }
        statement.bindLong(3, entity.getStatus());
        if (entity.getUserId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getUserId());
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
  public long insert(final JoinBoardWithUser entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfJoinBoardWithUser.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final JoinBoardWithUser... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfJoinBoardWithUser.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final JoinBoardWithUser... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfJoinBoardWithUser.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final JoinBoardWithUser... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfJoinBoardWithUser.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void deleteByBoardId(final long localId) {
    final String _sql = "DELETE FROM joinboardwithuser WHERE boardId = ?";
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
