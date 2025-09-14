package it.niedermann.nextcloud.deck.database.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.model.JoinBoardWithLabel;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class JoinBoardWithLabelDao_Impl implements JoinBoardWithLabelDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<JoinBoardWithLabel> __insertAdapterOfJoinBoardWithLabel;

  private final EntityDeleteOrUpdateAdapter<JoinBoardWithLabel> __deleteAdapterOfJoinBoardWithLabel;

  private final EntityDeleteOrUpdateAdapter<JoinBoardWithLabel> __updateAdapterOfJoinBoardWithLabel;

  public JoinBoardWithLabelDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfJoinBoardWithLabel = new EntityInsertAdapter<JoinBoardWithLabel>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `JoinBoardWithLabel` (`boardId`,`labelId`,`status`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final JoinBoardWithLabel entity) {
        if (entity.getBoardId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getBoardId());
        }
        if (entity.getLabelId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getLabelId());
        }
        statement.bindLong(3, entity.getStatus());
      }
    };
    this.__deleteAdapterOfJoinBoardWithLabel = new EntityDeleteOrUpdateAdapter<JoinBoardWithLabel>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `JoinBoardWithLabel` WHERE `labelId` = ? AND `boardId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final JoinBoardWithLabel entity) {
        if (entity.getLabelId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getLabelId());
        }
        if (entity.getBoardId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getBoardId());
        }
      }
    };
    this.__updateAdapterOfJoinBoardWithLabel = new EntityDeleteOrUpdateAdapter<JoinBoardWithLabel>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `JoinBoardWithLabel` SET `boardId` = ?,`labelId` = ?,`status` = ? WHERE `labelId` = ? AND `boardId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final JoinBoardWithLabel entity) {
        if (entity.getBoardId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getBoardId());
        }
        if (entity.getLabelId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getLabelId());
        }
        statement.bindLong(3, entity.getStatus());
        if (entity.getLabelId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getLabelId());
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
  public long insert(final JoinBoardWithLabel entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfJoinBoardWithLabel.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final JoinBoardWithLabel... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfJoinBoardWithLabel.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final JoinBoardWithLabel... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfJoinBoardWithLabel.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final JoinBoardWithLabel... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfJoinBoardWithLabel.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void deleteByBoardId(final long localId) {
    final String _sql = "DELETE FROM joinboardwithlabel WHERE boardId = ?";
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
