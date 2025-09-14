package it.niedermann.nextcloud.deck.database.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.processing.Generated;

import it.niedermann.nextcloud.deck.database.converter.DateTypeConverter;
import it.niedermann.nextcloud.deck.model.ocs.Activity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class ActivityDao_Impl implements ActivityDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Activity> __insertAdapterOfActivity;

  private final EntityDeleteOrUpdateAdapter<Activity> __deleteAdapterOfActivity;

  private final EntityDeleteOrUpdateAdapter<Activity> __updateAdapterOfActivity;

  public ActivityDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfActivity = new EntityInsertAdapter<Activity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `Activity` (`cardId`,`subject`,`type`,`localId`,`accountId`,`id`,`status`,`lastModified`,`lastModifiedLocal`,`etag`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Activity entity) {
        statement.bindLong(1, entity.getCardId());
        if (entity.getSubject() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getSubject());
        }
        statement.bindLong(3, entity.getType());
        if (entity.getLocalId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getLocalId());
        }
        statement.bindLong(5, entity.getAccountId());
        if (entity.getId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getId());
        }
        statement.bindLong(7, entity.getStatus());
        final Long _tmp = DateTypeConverter.fromInstant(entity.getLastModified());
        if (_tmp == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, _tmp);
        }
        final Long _tmp_1 = DateTypeConverter.fromInstant(entity.getLastModifiedLocal());
        if (_tmp_1 == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, _tmp_1);
        }
        if (entity.getEtag() == null) {
          statement.bindNull(10);
        } else {
          statement.bindText(10, entity.getEtag());
        }
      }
    };
    this.__deleteAdapterOfActivity = new EntityDeleteOrUpdateAdapter<Activity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `Activity` WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Activity entity) {
        if (entity.getLocalId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getLocalId());
        }
      }
    };
    this.__updateAdapterOfActivity = new EntityDeleteOrUpdateAdapter<Activity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `Activity` SET `cardId` = ?,`subject` = ?,`type` = ?,`localId` = ?,`accountId` = ?,`id` = ?,`status` = ?,`lastModified` = ?,`lastModifiedLocal` = ?,`etag` = ? WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Activity entity) {
        statement.bindLong(1, entity.getCardId());
        if (entity.getSubject() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getSubject());
        }
        statement.bindLong(3, entity.getType());
        if (entity.getLocalId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getLocalId());
        }
        statement.bindLong(5, entity.getAccountId());
        if (entity.getId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getId());
        }
        statement.bindLong(7, entity.getStatus());
        final Long _tmp = DateTypeConverter.fromInstant(entity.getLastModified());
        if (_tmp == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, _tmp);
        }
        final Long _tmp_1 = DateTypeConverter.fromInstant(entity.getLastModifiedLocal());
        if (_tmp_1 == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, _tmp_1);
        }
        if (entity.getEtag() == null) {
          statement.bindNull(10);
        } else {
          statement.bindText(10, entity.getEtag());
        }
        if (entity.getLocalId() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getLocalId());
        }
      }
    };
  }

  @Override
  public long insert(final Activity entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfActivity.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final Activity... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfActivity.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final Activity... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfActivity.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final Activity... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfActivity.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public LiveData<List<Activity>> getActivitiesForCard(final long localCardId) {
    final String _sql = "SELECT * FROM activity WHERE cardId = ? order by lastModified desc";
    return __db.getInvalidationTracker().createLiveData(new String[] {"activity"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localCardId);
        final int _columnIndexOfCardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cardId");
        final int _columnIndexOfSubject = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "subject");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<Activity> _result = new ArrayList<Activity>();
        while (_stmt.step()) {
          final Activity _item;
          _item = new Activity();
          final long _tmpCardId;
          _tmpCardId = _stmt.getLong(_columnIndexOfCardId);
          _item.setCardId(_tmpCardId);
          final String _tmpSubject;
          if (_stmt.isNull(_columnIndexOfSubject)) {
            _tmpSubject = null;
          } else {
            _tmpSubject = _stmt.getText(_columnIndexOfSubject);
          }
          _item.setSubject(_tmpSubject);
          final int _tmpType;
          _tmpType = (int) (_stmt.getLong(_columnIndexOfType));
          _item.setType(_tmpType);
          final Long _tmpLocalId;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpLocalId = null;
          } else {
            _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
          }
          _item.setLocalId(_tmpLocalId);
          final long _tmpAccountId;
          _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
          _item.setAccountId(_tmpAccountId);
          final Long _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId);
          }
          _item.setId(_tmpId);
          final int _tmpStatus;
          _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
          _item.setStatus(_tmpStatus);
          final Instant _tmpLastModified;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp);
          _item.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_1);
          _item.setLastModifiedLocal(_tmpLastModifiedLocal);
          final String _tmpEtag;
          if (_stmt.isNull(_columnIndexOfEtag)) {
            _tmpEtag = null;
          } else {
            _tmpEtag = _stmt.getText(_columnIndexOfEtag);
          }
          _item.setEtag(_tmpEtag);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Activity getActivityByRemoteIdDirectly(final long accountId, final long remoteActivityId) {
    final String _sql = "SELECT * FROM activity WHERE accountId = ? and id = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, remoteActivityId);
        final int _columnIndexOfCardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cardId");
        final int _columnIndexOfSubject = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "subject");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final Activity _result;
        if (_stmt.step()) {
          _result = new Activity();
          final long _tmpCardId;
          _tmpCardId = _stmt.getLong(_columnIndexOfCardId);
          _result.setCardId(_tmpCardId);
          final String _tmpSubject;
          if (_stmt.isNull(_columnIndexOfSubject)) {
            _tmpSubject = null;
          } else {
            _tmpSubject = _stmt.getText(_columnIndexOfSubject);
          }
          _result.setSubject(_tmpSubject);
          final int _tmpType;
          _tmpType = (int) (_stmt.getLong(_columnIndexOfType));
          _result.setType(_tmpType);
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
