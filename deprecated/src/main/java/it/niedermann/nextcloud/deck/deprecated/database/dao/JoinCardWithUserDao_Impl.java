package it.niedermann.nextcloud.deck.database.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.model.JoinCardWithUser;
import java.lang.Class;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class JoinCardWithUserDao_Impl implements JoinCardWithUserDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<JoinCardWithUser> __insertAdapterOfJoinCardWithUser;

  private final EntityDeleteOrUpdateAdapter<JoinCardWithUser> __deleteAdapterOfJoinCardWithUser;

  private final EntityDeleteOrUpdateAdapter<JoinCardWithUser> __updateAdapterOfJoinCardWithUser;

  public JoinCardWithUserDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfJoinCardWithUser = new EntityInsertAdapter<JoinCardWithUser>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `JoinCardWithUser` (`userId`,`cardId`,`status`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final JoinCardWithUser entity) {
        if (entity.getUserId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getUserId());
        }
        if (entity.getCardId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getCardId());
        }
        statement.bindLong(3, entity.getStatus());
      }
    };
    this.__deleteAdapterOfJoinCardWithUser = new EntityDeleteOrUpdateAdapter<JoinCardWithUser>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `JoinCardWithUser` WHERE `userId` = ? AND `cardId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final JoinCardWithUser entity) {
        if (entity.getUserId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getUserId());
        }
        if (entity.getCardId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getCardId());
        }
      }
    };
    this.__updateAdapterOfJoinCardWithUser = new EntityDeleteOrUpdateAdapter<JoinCardWithUser>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `JoinCardWithUser` SET `userId` = ?,`cardId` = ?,`status` = ? WHERE `userId` = ? AND `cardId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final JoinCardWithUser entity) {
        if (entity.getUserId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getUserId());
        }
        if (entity.getCardId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getCardId());
        }
        statement.bindLong(3, entity.getStatus());
        if (entity.getUserId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getUserId());
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
  public long insert(final JoinCardWithUser entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfJoinCardWithUser.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final JoinCardWithUser... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfJoinCardWithUser.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final JoinCardWithUser... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfJoinCardWithUser.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final JoinCardWithUser... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfJoinCardWithUser.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public JoinCardWithUser getJoin(final Long localUserId, final Long localCardId) {
    final String _sql = "select * FROM joincardwithuser WHERE cardId = ? and userId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (localCardId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localCardId);
        }
        _argIndex = 2;
        if (localUserId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localUserId);
        }
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfCardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cardId");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final JoinCardWithUser _result;
        if (_stmt.step()) {
          _result = new JoinCardWithUser();
          final Long _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
          }
          _result.setUserId(_tmpUserId);
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
  public List<JoinCardWithUser> getChangedJoinsWithRemoteIDs() {
    final String _sql = "select u.localId as userId, c.id as cardId, j.status from joincardwithuser j inner join card c on j.cardId = c.localId inner join user u on j.userId = u.localId WHERE j.status <> 1";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfUserId = 0;
        final int _columnIndexOfCardId = 1;
        final int _columnIndexOfStatus = 2;
        final List<JoinCardWithUser> _result = new ArrayList<JoinCardWithUser>();
        while (_stmt.step()) {
          final JoinCardWithUser _item;
          _item = new JoinCardWithUser();
          final Long _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
          }
          _item.setUserId(_tmpUserId);
          final Long _tmpCardId;
          if (_stmt.isNull(_columnIndexOfCardId)) {
            _tmpCardId = null;
          } else {
            _tmpCardId = _stmt.getLong(_columnIndexOfCardId);
          }
          _item.setCardId(_tmpCardId);
          final int _tmpStatus;
          _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
          _item.setStatus(_tmpStatus);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<JoinCardWithUser> getChangedJoinsWithRemoteIDsForStack(final Long localStackId) {
    final String _sql = "select u.localId as userId, c.id as cardId, j.status from joincardwithuser j inner join card c on j.cardId = c.localId inner join user u on j.userId = u.localId WHERE c.stackId = ? AND j.status <> 1";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (localStackId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localStackId);
        }
        final int _columnIndexOfUserId = 0;
        final int _columnIndexOfCardId = 1;
        final int _columnIndexOfStatus = 2;
        final List<JoinCardWithUser> _result = new ArrayList<JoinCardWithUser>();
        while (_stmt.step()) {
          final JoinCardWithUser _item;
          _item = new JoinCardWithUser();
          final Long _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
          }
          _item.setUserId(_tmpUserId);
          final Long _tmpCardId;
          if (_stmt.isNull(_columnIndexOfCardId)) {
            _tmpCardId = null;
          } else {
            _tmpCardId = _stmt.getLong(_columnIndexOfCardId);
          }
          _item.setCardId(_tmpCardId);
          final int _tmpStatus;
          _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
          _item.setStatus(_tmpStatus);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<Long> filterDeleted(final long localCardId, final List<Long> assignedUserIDs) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("select userId from joincardwithuser WHERE cardId = ");
    _stringBuilder.append("?");
    _stringBuilder.append(" and userId IN (");
    final int _inputSize = assignedUserIDs == null ? 1 : assignedUserIDs.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(") and status <> 3");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localCardId);
        _argIndex = 2;
        if (assignedUserIDs == null) {
          _stmt.bindNull(_argIndex);
        } else {
          for (Long _item : assignedUserIDs) {
            if (_item == null) {
              _stmt.bindNull(_argIndex);
            } else {
              _stmt.bindLong(_argIndex, _item);
            }
            _argIndex++;
          }
        }
        final List<Long> _result = new ArrayList<Long>();
        while (_stmt.step()) {
          final Long _item_1;
          if (_stmt.isNull(0)) {
            _item_1 = null;
          } else {
            _item_1 = _stmt.getLong(0);
          }
          _result.add(_item_1);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void deleteByCardId(final long localId) {
    final String _sql = "DELETE FROM joincardwithuser WHERE cardId = ? and status=1";
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
  public void setDbStatus(final long localCardId, final long localUserId, final int status) {
    final String _sql = "Update joincardwithuser set status = ? WHERE cardId = ? and userId = ?";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localCardId);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, localUserId);
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void deleteByCardIdAndUserIdPhysically(final long localCardId, final long localUserId) {
    final String _sql = "DELETE FROM joincardwithuser WHERE cardId = ? and userId = ?";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localCardId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localUserId);
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void deleteJoinedUsersForCardsInBoardWithoutPermissionPhysically(final long localBoardId) {
    final String _sql = "DELETE FROM joincardwithuser WHERE cardid in (select c.localId from Card c join Stack s on c.stackId = s.localId and s.boardId = ?) and userId not in (select userId from UserInBoard where boardId = ?)";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localBoardId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localBoardId);
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void deleteJoinedUserForCardPhysicallyByRemoteIDs(final Long accountId,
      final Long remoteCardId, final String userUid) {
    final String _sql = "delete from joincardwithuser where cardId = (select c.localId from card c where c.accountId = ? and c.id = ?) and userId = (select u.localId from user u where u.accountId = ? and u.uid = ?)";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (accountId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, accountId);
        }
        _argIndex = 2;
        if (remoteCardId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, remoteCardId);
        }
        _argIndex = 3;
        if (accountId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, accountId);
        }
        _argIndex = 4;
        if (userUid == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, userUid);
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
