package it.niedermann.nextcloud.deck.database.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.model.JoinCardWithLabel;
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
public final class JoinCardWithLabelDao_Impl implements JoinCardWithLabelDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<JoinCardWithLabel> __insertAdapterOfJoinCardWithLabel;

  private final EntityDeleteOrUpdateAdapter<JoinCardWithLabel> __deleteAdapterOfJoinCardWithLabel;

  private final EntityDeleteOrUpdateAdapter<JoinCardWithLabel> __updateAdapterOfJoinCardWithLabel;

  public JoinCardWithLabelDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfJoinCardWithLabel = new EntityInsertAdapter<JoinCardWithLabel>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `JoinCardWithLabel` (`labelId`,`cardId`,`status`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final JoinCardWithLabel entity) {
        if (entity.getLabelId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getLabelId());
        }
        if (entity.getCardId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getCardId());
        }
        statement.bindLong(3, entity.getStatus());
      }
    };
    this.__deleteAdapterOfJoinCardWithLabel = new EntityDeleteOrUpdateAdapter<JoinCardWithLabel>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `JoinCardWithLabel` WHERE `labelId` = ? AND `cardId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final JoinCardWithLabel entity) {
        if (entity.getLabelId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getLabelId());
        }
        if (entity.getCardId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getCardId());
        }
      }
    };
    this.__updateAdapterOfJoinCardWithLabel = new EntityDeleteOrUpdateAdapter<JoinCardWithLabel>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `JoinCardWithLabel` SET `labelId` = ?,`cardId` = ?,`status` = ? WHERE `labelId` = ? AND `cardId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final JoinCardWithLabel entity) {
        if (entity.getLabelId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getLabelId());
        }
        if (entity.getCardId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getCardId());
        }
        statement.bindLong(3, entity.getStatus());
        if (entity.getLabelId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getLabelId());
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
  public long insert(final JoinCardWithLabel entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfJoinCardWithLabel.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final JoinCardWithLabel... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfJoinCardWithLabel.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final JoinCardWithLabel... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfJoinCardWithLabel.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final JoinCardWithLabel... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfJoinCardWithLabel.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public List<Long> filterDeleted(final long localCardId, final List<Long> localLabelIds) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("select labelId from joincardwithlabel WHERE cardId = ");
    _stringBuilder.append("?");
    _stringBuilder.append(" and labelId IN (");
    final int _inputSize = localLabelIds == null ? 1 : localLabelIds.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(") and status <> 3");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localCardId);
        _argIndex = 2;
        if (localLabelIds == null) {
          _stmt.bindNull(_argIndex);
        } else {
          for (Long _item : localLabelIds) {
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
  public JoinCardWithLabel getJoin(final Long localLabelId, final Long localCardId) {
    final String _sql = "select * from joincardwithlabel WHERE cardId = ? and labelId = ?";
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
        if (localLabelId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localLabelId);
        }
        final int _columnIndexOfLabelId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "labelId");
        final int _columnIndexOfCardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cardId");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final JoinCardWithLabel _result;
        if (_stmt.step()) {
          _result = new JoinCardWithLabel();
          final Long _tmpLabelId;
          if (_stmt.isNull(_columnIndexOfLabelId)) {
            _tmpLabelId = null;
          } else {
            _tmpLabelId = _stmt.getLong(_columnIndexOfLabelId);
          }
          _result.setLabelId(_tmpLabelId);
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
  public List<JoinCardWithLabel> getAllDeletedJoinsWithRemoteIDs() {
    final String _sql = "select l.id as labelId, c.id as cardId, j.status from joincardwithlabel j inner join card c on j.cardId = c.localId inner join label l on j.labelId = l.localId WHERE j.status <> 1";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfLabelId = 0;
        final int _columnIndexOfCardId = 1;
        final int _columnIndexOfStatus = 2;
        final List<JoinCardWithLabel> _result = new ArrayList<JoinCardWithLabel>();
        while (_stmt.step()) {
          final JoinCardWithLabel _item;
          _item = new JoinCardWithLabel();
          final Long _tmpLabelId;
          if (_stmt.isNull(_columnIndexOfLabelId)) {
            _tmpLabelId = null;
          } else {
            _tmpLabelId = _stmt.getLong(_columnIndexOfLabelId);
          }
          _item.setLabelId(_tmpLabelId);
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
  public JoinCardWithLabel getRemoteIdsForJoin(final long localCardId, final long localLabelId) {
    final String _sql = "select l.id as labelId, c.id as cardId, j.status from joincardwithlabel j inner join card c on j.cardId = c.localId inner join label l on j.labelId = l.localId WHERE j.cardId = ? and j.labelId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localCardId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localLabelId);
        final int _columnIndexOfLabelId = 0;
        final int _columnIndexOfCardId = 1;
        final int _columnIndexOfStatus = 2;
        final JoinCardWithLabel _result;
        if (_stmt.step()) {
          _result = new JoinCardWithLabel();
          final Long _tmpLabelId;
          if (_stmt.isNull(_columnIndexOfLabelId)) {
            _tmpLabelId = null;
          } else {
            _tmpLabelId = _stmt.getLong(_columnIndexOfLabelId);
          }
          _result.setLabelId(_tmpLabelId);
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
  public List<JoinCardWithLabel> getAllChangedJoins() {
    final String _sql = "select * from joincardwithlabel WHERE status <> 1";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfLabelId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "labelId");
        final int _columnIndexOfCardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cardId");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final List<JoinCardWithLabel> _result = new ArrayList<JoinCardWithLabel>();
        while (_stmt.step()) {
          final JoinCardWithLabel _item;
          _item = new JoinCardWithLabel();
          final Long _tmpLabelId;
          if (_stmt.isNull(_columnIndexOfLabelId)) {
            _tmpLabelId = null;
          } else {
            _tmpLabelId = _stmt.getLong(_columnIndexOfLabelId);
          }
          _item.setLabelId(_tmpLabelId);
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
  public List<JoinCardWithLabel> getAllChangedJoinsForStack(final Long localStackId) {
    final String _sql = "select j.* from joincardwithlabel j inner join card c on j.cardId = c.localId  WHERE c.stackId = ? and j.status <> 1";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (localStackId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localStackId);
        }
        final int _columnIndexOfLabelId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "labelId");
        final int _columnIndexOfCardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cardId");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final List<JoinCardWithLabel> _result = new ArrayList<JoinCardWithLabel>();
        while (_stmt.step()) {
          final JoinCardWithLabel _item;
          _item = new JoinCardWithLabel();
          final Long _tmpLabelId;
          if (_stmt.isNull(_columnIndexOfLabelId)) {
            _tmpLabelId = null;
          } else {
            _tmpLabelId = _stmt.getLong(_columnIndexOfLabelId);
          }
          _item.setLabelId(_tmpLabelId);
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
  public int countCardsWithLabelDirectly(final long localLabelId) {
    final String _sql = "select count(*) from joincardwithlabel WHERE labelId = ? and status <> 3";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localLabelId);
        final int _result;
        if (_stmt.step()) {
          _result = (int) (_stmt.getLong(0));
        } else {
          _result = 0;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void deleteByCardId(final long localCardId) {
    final String _sql = "DELETE FROM joincardwithlabel WHERE  cardId = ? and status == 1";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localCardId);
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void deleteByCardIdAndLabelId(final long localCardId, final long labelId) {
    final String _sql = "DELETE FROM joincardwithlabel WHERE cardId = ? and labelId = ?";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localCardId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, labelId);
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void setDbStatus(final long localCardId, final long localLabelId, final int status) {
    final String _sql = "Update joincardwithlabel set status = ? WHERE cardId = ? and labelId = ?";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localCardId);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, localLabelId);
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void deleteJoinedLabelForCardPhysicallyByRemoteIDs(final Long accountId,
      final Long remoteCardId, final Long remoteLabelId) {
    final String _sql = "delete from joincardwithlabel where cardId = (select c.localId from card c where c.accountId = ? and c.id = ?) and labelId = (select l.localId from label l where l.accountId = ? and l.id = ?)";
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
        if (remoteLabelId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, remoteLabelId);
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
