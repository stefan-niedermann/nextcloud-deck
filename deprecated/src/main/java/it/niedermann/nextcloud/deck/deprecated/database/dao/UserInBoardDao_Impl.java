package it.niedermann.nextcloud.deck.database.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.model.relations.UserInBoard;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class UserInBoardDao_Impl implements UserInBoardDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<UserInBoard> __insertAdapterOfUserInBoard;

  private final EntityDeleteOrUpdateAdapter<UserInBoard> __deleteAdapterOfUserInBoard;

  private final EntityDeleteOrUpdateAdapter<UserInBoard> __updateAdapterOfUserInBoard;

  public UserInBoardDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfUserInBoard = new EntityInsertAdapter<UserInBoard>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `UserInBoard` (`userId`,`boardId`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final UserInBoard entity) {
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
    this.__deleteAdapterOfUserInBoard = new EntityDeleteOrUpdateAdapter<UserInBoard>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `UserInBoard` WHERE `userId` = ? AND `boardId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final UserInBoard entity) {
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
    this.__updateAdapterOfUserInBoard = new EntityDeleteOrUpdateAdapter<UserInBoard>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `UserInBoard` SET `userId` = ?,`boardId` = ? WHERE `userId` = ? AND `boardId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final UserInBoard entity) {
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
        if (entity.getUserId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getUserId());
        }
        if (entity.getBoardId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getBoardId());
        }
      }
    };
  }

  @Override
  public long insert(final UserInBoard entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfUserInBoard.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final UserInBoard... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfUserInBoard.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final UserInBoard... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfUserInBoard.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final UserInBoard... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfUserInBoard.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void deleteByBoardId(final long localId) {
    final String _sql = "DELETE FROM userinboard WHERE boardId = ?";
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
