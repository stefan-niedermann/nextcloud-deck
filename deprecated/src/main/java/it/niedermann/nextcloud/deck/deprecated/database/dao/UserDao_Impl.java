package it.niedermann.nextcloud.deck.database.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.database.converter.DateTypeConverter;
import it.niedermann.nextcloud.deck.model.User;
import java.lang.Class;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class UserDao_Impl implements UserDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<User> __insertAdapterOfUser;

  private final EntityDeleteOrUpdateAdapter<User> __deleteAdapterOfUser;

  private final EntityDeleteOrUpdateAdapter<User> __updateAdapterOfUser;

  public UserDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfUser = new EntityInsertAdapter<User>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `User` (`primaryKey`,`uid`,`displayname`,`type`,`localId`,`accountId`,`id`,`status`,`lastModified`,`lastModifiedLocal`,`etag`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final User entity) {
        if (entity.getPrimaryKey() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getPrimaryKey());
        }
        if (entity.getUid() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getUid());
        }
        if (entity.getDisplayname() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getDisplayname());
        }
        statement.bindLong(4, entity.getType());
        if (entity.getLocalId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getLocalId());
        }
        statement.bindLong(6, entity.getAccountId());
        if (entity.getId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getId());
        }
        statement.bindLong(8, entity.getStatus());
        final Long _tmp = DateTypeConverter.fromInstant(entity.getLastModified());
        if (_tmp == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, _tmp);
        }
        final Long _tmp_1 = DateTypeConverter.fromInstant(entity.getLastModifiedLocal());
        if (_tmp_1 == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, _tmp_1);
        }
        if (entity.getEtag() == null) {
          statement.bindNull(11);
        } else {
          statement.bindText(11, entity.getEtag());
        }
      }
    };
    this.__deleteAdapterOfUser = new EntityDeleteOrUpdateAdapter<User>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `User` WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final User entity) {
        if (entity.getLocalId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getLocalId());
        }
      }
    };
    this.__updateAdapterOfUser = new EntityDeleteOrUpdateAdapter<User>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `User` SET `primaryKey` = ?,`uid` = ?,`displayname` = ?,`type` = ?,`localId` = ?,`accountId` = ?,`id` = ?,`status` = ?,`lastModified` = ?,`lastModifiedLocal` = ?,`etag` = ? WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final User entity) {
        if (entity.getPrimaryKey() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getPrimaryKey());
        }
        if (entity.getUid() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getUid());
        }
        if (entity.getDisplayname() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getDisplayname());
        }
        statement.bindLong(4, entity.getType());
        if (entity.getLocalId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getLocalId());
        }
        statement.bindLong(6, entity.getAccountId());
        if (entity.getId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getId());
        }
        statement.bindLong(8, entity.getStatus());
        final Long _tmp = DateTypeConverter.fromInstant(entity.getLastModified());
        if (_tmp == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, _tmp);
        }
        final Long _tmp_1 = DateTypeConverter.fromInstant(entity.getLastModifiedLocal());
        if (_tmp_1 == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, _tmp_1);
        }
        if (entity.getEtag() == null) {
          statement.bindNull(11);
        } else {
          statement.bindText(11, entity.getEtag());
        }
        if (entity.getLocalId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getLocalId());
        }
      }
    };
  }

  @Override
  public long insert(final User entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfUser.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final User... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfUser.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final User... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfUser.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final User... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfUser.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public LiveData<List<User>> getUsersForAccount(final long accountId) {
    final String _sql = "SELECT * FROM user WHERE accountId = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"user"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        final int _columnIndexOfPrimaryKey = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "primaryKey");
        final int _columnIndexOfUid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uid");
        final int _columnIndexOfDisplayname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayname");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<User> _result = new ArrayList<User>();
        while (_stmt.step()) {
          final User _item;
          _item = new User();
          final String _tmpPrimaryKey;
          if (_stmt.isNull(_columnIndexOfPrimaryKey)) {
            _tmpPrimaryKey = null;
          } else {
            _tmpPrimaryKey = _stmt.getText(_columnIndexOfPrimaryKey);
          }
          _item.setPrimaryKey(_tmpPrimaryKey);
          final String _tmpUid;
          if (_stmt.isNull(_columnIndexOfUid)) {
            _tmpUid = null;
          } else {
            _tmpUid = _stmt.getText(_columnIndexOfUid);
          }
          _item.setUid(_tmpUid);
          final String _tmpDisplayname;
          if (_stmt.isNull(_columnIndexOfDisplayname)) {
            _tmpDisplayname = null;
          } else {
            _tmpDisplayname = _stmt.getText(_columnIndexOfDisplayname);
          }
          _item.setDisplayname(_tmpDisplayname);
          final long _tmpType;
          _tmpType = _stmt.getLong(_columnIndexOfType);
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
  public List<User> getAllUsersDirectly() {
    final String _sql = "SELECT * FROM user";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfPrimaryKey = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "primaryKey");
        final int _columnIndexOfUid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uid");
        final int _columnIndexOfDisplayname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayname");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<User> _result = new ArrayList<User>();
        while (_stmt.step()) {
          final User _item;
          _item = new User();
          final String _tmpPrimaryKey;
          if (_stmt.isNull(_columnIndexOfPrimaryKey)) {
            _tmpPrimaryKey = null;
          } else {
            _tmpPrimaryKey = _stmt.getText(_columnIndexOfPrimaryKey);
          }
          _item.setPrimaryKey(_tmpPrimaryKey);
          final String _tmpUid;
          if (_stmt.isNull(_columnIndexOfUid)) {
            _tmpUid = null;
          } else {
            _tmpUid = _stmt.getText(_columnIndexOfUid);
          }
          _item.setUid(_tmpUid);
          final String _tmpDisplayname;
          if (_stmt.isNull(_columnIndexOfDisplayname)) {
            _tmpDisplayname = null;
          } else {
            _tmpDisplayname = _stmt.getText(_columnIndexOfDisplayname);
          }
          _item.setDisplayname(_tmpDisplayname);
          final long _tmpType;
          _tmpType = _stmt.getLong(_columnIndexOfType);
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
  public LiveData<User> getUserByLocalId(final long accountId, final long localId) {
    final String _sql = "SELECT * FROM user WHERE accountId = ? and localId = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"user"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localId);
        final int _columnIndexOfPrimaryKey = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "primaryKey");
        final int _columnIndexOfUid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uid");
        final int _columnIndexOfDisplayname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayname");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final User _result;
        if (_stmt.step()) {
          _result = new User();
          final String _tmpPrimaryKey;
          if (_stmt.isNull(_columnIndexOfPrimaryKey)) {
            _tmpPrimaryKey = null;
          } else {
            _tmpPrimaryKey = _stmt.getText(_columnIndexOfPrimaryKey);
          }
          _result.setPrimaryKey(_tmpPrimaryKey);
          final String _tmpUid;
          if (_stmt.isNull(_columnIndexOfUid)) {
            _tmpUid = null;
          } else {
            _tmpUid = _stmt.getText(_columnIndexOfUid);
          }
          _result.setUid(_tmpUid);
          final String _tmpDisplayname;
          if (_stmt.isNull(_columnIndexOfDisplayname)) {
            _tmpDisplayname = null;
          } else {
            _tmpDisplayname = _stmt.getText(_columnIndexOfDisplayname);
          }
          _result.setDisplayname(_tmpDisplayname);
          final long _tmpType;
          _tmpType = _stmt.getLong(_columnIndexOfType);
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

  @Override
  public LiveData<User> getUserByUid(final long accountId, final String uid) {
    final String _sql = "SELECT * FROM user WHERE accountId = ? and uid = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"user"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        if (uid == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, uid);
        }
        final int _columnIndexOfPrimaryKey = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "primaryKey");
        final int _columnIndexOfUid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uid");
        final int _columnIndexOfDisplayname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayname");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final User _result;
        if (_stmt.step()) {
          _result = new User();
          final String _tmpPrimaryKey;
          if (_stmt.isNull(_columnIndexOfPrimaryKey)) {
            _tmpPrimaryKey = null;
          } else {
            _tmpPrimaryKey = _stmt.getText(_columnIndexOfPrimaryKey);
          }
          _result.setPrimaryKey(_tmpPrimaryKey);
          final String _tmpUid;
          if (_stmt.isNull(_columnIndexOfUid)) {
            _tmpUid = null;
          } else {
            _tmpUid = _stmt.getText(_columnIndexOfUid);
          }
          _result.setUid(_tmpUid);
          final String _tmpDisplayname;
          if (_stmt.isNull(_columnIndexOfDisplayname)) {
            _tmpDisplayname = null;
          } else {
            _tmpDisplayname = _stmt.getText(_columnIndexOfDisplayname);
          }
          _result.setDisplayname(_tmpDisplayname);
          final long _tmpType;
          _tmpType = _stmt.getLong(_columnIndexOfType);
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

  @Override
  public String getUserNameByUidDirectly(final long accountId, final String uid) {
    final String _sql = "SELECT u.displayname FROM user u WHERE accountId = ? and uid = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        if (uid == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, uid);
        }
        final String _result;
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null;
          } else {
            _result = _stmt.getText(0);
          }
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
  public LiveData<List<User>> searchUserByUidOrDisplayName(final long accountId, final long boardId,
      final long notYetAssignedToLocalCardId, final String searchTerm) {
    final String _sql = "SELECT u.* FROM user u WHERE accountId = ?     AND NOT EXISTS (            select 1 from joincardwithuser ju            where ju.userId = u.localId            and ju.cardId = ? AND status <> 3    )  AND (     EXISTS (            select 1 from userinboard where boardId = ? AND userId = u.localId    )    OR    EXISTS (       select 1 from accesscontrol       where (userId = u.localId OR (type = 1 and exists(select 1 from UserInGroup uig where uig.memberId = u.localId and uig.groupId = userId)))            and boardId = ? and status <> 3    )    OR    EXISTS (            select 1 from board where localId = ? AND ownerId = u.localId    ))and ( uid LIKE ? or displayname LIKE ? or primaryKey LIKE ? )";
    return __db.getInvalidationTracker().createLiveData(new String[] {"user", "joincardwithuser",
        "userinboard", "accesscontrol", "UserInGroup", "board"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, notYetAssignedToLocalCardId);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, boardId);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, boardId);
        _argIndex = 5;
        _stmt.bindLong(_argIndex, boardId);
        _argIndex = 6;
        if (searchTerm == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, searchTerm);
        }
        _argIndex = 7;
        if (searchTerm == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, searchTerm);
        }
        _argIndex = 8;
        if (searchTerm == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, searchTerm);
        }
        final int _columnIndexOfPrimaryKey = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "primaryKey");
        final int _columnIndexOfUid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uid");
        final int _columnIndexOfDisplayname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayname");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<User> _result = new ArrayList<User>();
        while (_stmt.step()) {
          final User _item;
          _item = new User();
          final String _tmpPrimaryKey;
          if (_stmt.isNull(_columnIndexOfPrimaryKey)) {
            _tmpPrimaryKey = null;
          } else {
            _tmpPrimaryKey = _stmt.getText(_columnIndexOfPrimaryKey);
          }
          _item.setPrimaryKey(_tmpPrimaryKey);
          final String _tmpUid;
          if (_stmt.isNull(_columnIndexOfUid)) {
            _tmpUid = null;
          } else {
            _tmpUid = _stmt.getText(_columnIndexOfUid);
          }
          _item.setUid(_tmpUid);
          final String _tmpDisplayname;
          if (_stmt.isNull(_columnIndexOfDisplayname)) {
            _tmpDisplayname = null;
          } else {
            _tmpDisplayname = _stmt.getText(_columnIndexOfDisplayname);
          }
          _item.setDisplayname(_tmpDisplayname);
          final long _tmpType;
          _tmpType = _stmt.getLong(_columnIndexOfType);
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
  public LiveData<List<User>> searchUserByUidOrDisplayNameForACL(final long accountId,
      final long boardId, final String searchTerm) {
    final String _sql = "SELECT u.* FROM user u WHERE accountId = ?     AND NOT EXISTS (            select 1 from accesscontrol ju            where ju.userId = u.localId and ju.boardId = ? and status <> 3    ) and ( uid LIKE ? or displayname LIKE ? or primaryKey LIKE ? ) and u.localId <> (select b.ownerId from board b where localId = ?)ORDER BY u.displayname";
    return __db.getInvalidationTracker().createLiveData(new String[] {"user", "accesscontrol",
        "board"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, boardId);
        _argIndex = 3;
        if (searchTerm == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, searchTerm);
        }
        _argIndex = 4;
        if (searchTerm == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, searchTerm);
        }
        _argIndex = 5;
        if (searchTerm == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, searchTerm);
        }
        _argIndex = 6;
        _stmt.bindLong(_argIndex, boardId);
        final int _columnIndexOfPrimaryKey = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "primaryKey");
        final int _columnIndexOfUid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uid");
        final int _columnIndexOfDisplayname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayname");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<User> _result = new ArrayList<User>();
        while (_stmt.step()) {
          final User _item;
          _item = new User();
          final String _tmpPrimaryKey;
          if (_stmt.isNull(_columnIndexOfPrimaryKey)) {
            _tmpPrimaryKey = null;
          } else {
            _tmpPrimaryKey = _stmt.getText(_columnIndexOfPrimaryKey);
          }
          _item.setPrimaryKey(_tmpPrimaryKey);
          final String _tmpUid;
          if (_stmt.isNull(_columnIndexOfUid)) {
            _tmpUid = null;
          } else {
            _tmpUid = _stmt.getText(_columnIndexOfUid);
          }
          _item.setUid(_tmpUid);
          final String _tmpDisplayname;
          if (_stmt.isNull(_columnIndexOfDisplayname)) {
            _tmpDisplayname = null;
          } else {
            _tmpDisplayname = _stmt.getText(_columnIndexOfDisplayname);
          }
          _item.setDisplayname(_tmpDisplayname);
          final long _tmpType;
          _tmpType = _stmt.getLong(_columnIndexOfType);
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
  public User getUserByUidDirectly(final long accountId, final String uid) {
    final String _sql = "SELECT * FROM user WHERE accountId = ? and uid = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        if (uid == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, uid);
        }
        final int _columnIndexOfPrimaryKey = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "primaryKey");
        final int _columnIndexOfUid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uid");
        final int _columnIndexOfDisplayname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayname");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final User _result;
        if (_stmt.step()) {
          _result = new User();
          final String _tmpPrimaryKey;
          if (_stmt.isNull(_columnIndexOfPrimaryKey)) {
            _tmpPrimaryKey = null;
          } else {
            _tmpPrimaryKey = _stmt.getText(_columnIndexOfPrimaryKey);
          }
          _result.setPrimaryKey(_tmpPrimaryKey);
          final String _tmpUid;
          if (_stmt.isNull(_columnIndexOfUid)) {
            _tmpUid = null;
          } else {
            _tmpUid = _stmt.getText(_columnIndexOfUid);
          }
          _result.setUid(_tmpUid);
          final String _tmpDisplayname;
          if (_stmt.isNull(_columnIndexOfDisplayname)) {
            _tmpDisplayname = null;
          } else {
            _tmpDisplayname = _stmt.getText(_columnIndexOfDisplayname);
          }
          _result.setDisplayname(_tmpDisplayname);
          final long _tmpType;
          _tmpType = _stmt.getLong(_columnIndexOfType);
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

  @Override
  public List<User> getUsersByIdDirectly(final List<Long> assignedUserIDs) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT * FROM user WHERE localId IN (");
    final int _inputSize = assignedUserIDs == null ? 1 : assignedUserIDs.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(") and status <> 3");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
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
        final int _columnIndexOfPrimaryKey = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "primaryKey");
        final int _columnIndexOfUid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uid");
        final int _columnIndexOfDisplayname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayname");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<User> _result = new ArrayList<User>();
        while (_stmt.step()) {
          final User _item_1;
          _item_1 = new User();
          final String _tmpPrimaryKey;
          if (_stmt.isNull(_columnIndexOfPrimaryKey)) {
            _tmpPrimaryKey = null;
          } else {
            _tmpPrimaryKey = _stmt.getText(_columnIndexOfPrimaryKey);
          }
          _item_1.setPrimaryKey(_tmpPrimaryKey);
          final String _tmpUid;
          if (_stmt.isNull(_columnIndexOfUid)) {
            _tmpUid = null;
          } else {
            _tmpUid = _stmt.getText(_columnIndexOfUid);
          }
          _item_1.setUid(_tmpUid);
          final String _tmpDisplayname;
          if (_stmt.isNull(_columnIndexOfDisplayname)) {
            _tmpDisplayname = null;
          } else {
            _tmpDisplayname = _stmt.getText(_columnIndexOfDisplayname);
          }
          _item_1.setDisplayname(_tmpDisplayname);
          final long _tmpType;
          _tmpType = _stmt.getLong(_columnIndexOfType);
          _item_1.setType(_tmpType);
          final Long _tmpLocalId;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpLocalId = null;
          } else {
            _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
          }
          _item_1.setLocalId(_tmpLocalId);
          final long _tmpAccountId;
          _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
          _item_1.setAccountId(_tmpAccountId);
          final Long _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId);
          }
          _item_1.setId(_tmpId);
          final int _tmpStatus;
          _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
          _item_1.setStatus(_tmpStatus);
          final Instant _tmpLastModified;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp);
          _item_1.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_1);
          _item_1.setLastModifiedLocal(_tmpLastModifiedLocal);
          final String _tmpEtag;
          if (_stmt.isNull(_columnIndexOfEtag)) {
            _tmpEtag = null;
          } else {
            _tmpEtag = _stmt.getText(_columnIndexOfEtag);
          }
          _item_1.setEtag(_tmpEtag);
          _result.add(_item_1);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public User getUserByLocalIdDirectly(final long localUserId) {
    final String _sql = "SELECT * FROM user WHERE localId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localUserId);
        final int _columnIndexOfPrimaryKey = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "primaryKey");
        final int _columnIndexOfUid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uid");
        final int _columnIndexOfDisplayname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayname");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final User _result;
        if (_stmt.step()) {
          _result = new User();
          final String _tmpPrimaryKey;
          if (_stmt.isNull(_columnIndexOfPrimaryKey)) {
            _tmpPrimaryKey = null;
          } else {
            _tmpPrimaryKey = _stmt.getText(_columnIndexOfPrimaryKey);
          }
          _result.setPrimaryKey(_tmpPrimaryKey);
          final String _tmpUid;
          if (_stmt.isNull(_columnIndexOfUid)) {
            _tmpUid = null;
          } else {
            _tmpUid = _stmt.getText(_columnIndexOfUid);
          }
          _result.setUid(_tmpUid);
          final String _tmpDisplayname;
          if (_stmt.isNull(_columnIndexOfDisplayname)) {
            _tmpDisplayname = null;
          } else {
            _tmpDisplayname = _stmt.getText(_columnIndexOfDisplayname);
          }
          _result.setDisplayname(_tmpDisplayname);
          final long _tmpType;
          _tmpType = _stmt.getLong(_columnIndexOfType);
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

  @Override
  public LiveData<List<User>> findProposalsForUsersToAssign(final long accountId,
      final long boardId, final long notAssignedToLocalCardId, final int topX) {
    final String _sql = "    SELECT u.* FROM user u    WHERE u.accountId = ?    AND NOT EXISTS (            select 1 from joincardwithuser ju            where ju.userId = u.localId            and ju.cardId = ? AND status <> 3    )  AND (     EXISTS (            select 1 from userinboard where boardId = ? AND userId = u.localId    )    OR    EXISTS (       select 1 from accesscontrol       where (userId = u.localId OR (type = 1 and exists(select 1 from UserInGroup uig where uig.memberId = u.localId and uig.groupId = userId)))            and boardId = ? and status <> 3    )    OR    EXISTS (            select 1 from board where localId = ? AND ownerId = u.localId    ))    ORDER BY (            select count(*) from joincardwithuser j    where userId = u.localId and cardId in (select c.localId from card c inner join stack s on s.localId = c.stackId where s.boardId = ?)) DESC    LIMIT ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"user", "joincardwithuser",
        "userinboard", "accesscontrol", "UserInGroup", "board", "card",
        "stack"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, notAssignedToLocalCardId);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, boardId);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, boardId);
        _argIndex = 5;
        _stmt.bindLong(_argIndex, boardId);
        _argIndex = 6;
        _stmt.bindLong(_argIndex, boardId);
        _argIndex = 7;
        _stmt.bindLong(_argIndex, topX);
        final int _columnIndexOfPrimaryKey = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "primaryKey");
        final int _columnIndexOfUid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uid");
        final int _columnIndexOfDisplayname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayname");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<User> _result = new ArrayList<User>();
        while (_stmt.step()) {
          final User _item;
          _item = new User();
          final String _tmpPrimaryKey;
          if (_stmt.isNull(_columnIndexOfPrimaryKey)) {
            _tmpPrimaryKey = null;
          } else {
            _tmpPrimaryKey = _stmt.getText(_columnIndexOfPrimaryKey);
          }
          _item.setPrimaryKey(_tmpPrimaryKey);
          final String _tmpUid;
          if (_stmt.isNull(_columnIndexOfUid)) {
            _tmpUid = null;
          } else {
            _tmpUid = _stmt.getText(_columnIndexOfUid);
          }
          _item.setUid(_tmpUid);
          final String _tmpDisplayname;
          if (_stmt.isNull(_columnIndexOfDisplayname)) {
            _tmpDisplayname = null;
          } else {
            _tmpDisplayname = _stmt.getText(_columnIndexOfDisplayname);
          }
          _item.setDisplayname(_tmpDisplayname);
          final long _tmpType;
          _tmpType = _stmt.getLong(_columnIndexOfType);
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
  public LiveData<List<User>> findProposalsForUsersToAssignForACL(final long accountId,
      final long boardId, final int topX) {
    final String _sql = "SELECT u.* FROM user u WHERE accountId = ?     AND NOT EXISTS (            select 1 from accesscontrol ju            where ju.userId = u.localId and ju.boardId = ? and status <> 3    ) and u.localId <> (select b.ownerId from board b where localId = ?)ORDER BY u.displayname LIMIT ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"user", "accesscontrol",
        "board"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, boardId);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, boardId);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, topX);
        final int _columnIndexOfPrimaryKey = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "primaryKey");
        final int _columnIndexOfUid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uid");
        final int _columnIndexOfDisplayname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayname");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<User> _result = new ArrayList<User>();
        while (_stmt.step()) {
          final User _item;
          _item = new User();
          final String _tmpPrimaryKey;
          if (_stmt.isNull(_columnIndexOfPrimaryKey)) {
            _tmpPrimaryKey = null;
          } else {
            _tmpPrimaryKey = _stmt.getText(_columnIndexOfPrimaryKey);
          }
          _item.setPrimaryKey(_tmpPrimaryKey);
          final String _tmpUid;
          if (_stmt.isNull(_columnIndexOfUid)) {
            _tmpUid = null;
          } else {
            _tmpUid = _stmt.getText(_columnIndexOfUid);
          }
          _item.setUid(_tmpUid);
          final String _tmpDisplayname;
          if (_stmt.isNull(_columnIndexOfDisplayname)) {
            _tmpDisplayname = null;
          } else {
            _tmpDisplayname = _stmt.getText(_columnIndexOfDisplayname);
          }
          _item.setDisplayname(_tmpDisplayname);
          final long _tmpType;
          _tmpType = _stmt.getLong(_columnIndexOfType);
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
  public List<User> getUsersByIdsDirectly(final List<Long> userIDs) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT * FROM user WHERE localId IN (");
    final int _inputSize = userIDs == null ? 1 : userIDs.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(") and status <> 3");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (userIDs == null) {
          _stmt.bindNull(_argIndex);
        } else {
          for (Long _item : userIDs) {
            if (_item == null) {
              _stmt.bindNull(_argIndex);
            } else {
              _stmt.bindLong(_argIndex, _item);
            }
            _argIndex++;
          }
        }
        final int _columnIndexOfPrimaryKey = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "primaryKey");
        final int _columnIndexOfUid = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "uid");
        final int _columnIndexOfDisplayname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "displayname");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<User> _result = new ArrayList<User>();
        while (_stmt.step()) {
          final User _item_1;
          _item_1 = new User();
          final String _tmpPrimaryKey;
          if (_stmt.isNull(_columnIndexOfPrimaryKey)) {
            _tmpPrimaryKey = null;
          } else {
            _tmpPrimaryKey = _stmt.getText(_columnIndexOfPrimaryKey);
          }
          _item_1.setPrimaryKey(_tmpPrimaryKey);
          final String _tmpUid;
          if (_stmt.isNull(_columnIndexOfUid)) {
            _tmpUid = null;
          } else {
            _tmpUid = _stmt.getText(_columnIndexOfUid);
          }
          _item_1.setUid(_tmpUid);
          final String _tmpDisplayname;
          if (_stmt.isNull(_columnIndexOfDisplayname)) {
            _tmpDisplayname = null;
          } else {
            _tmpDisplayname = _stmt.getText(_columnIndexOfDisplayname);
          }
          _item_1.setDisplayname(_tmpDisplayname);
          final long _tmpType;
          _tmpType = _stmt.getLong(_columnIndexOfType);
          _item_1.setType(_tmpType);
          final Long _tmpLocalId;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpLocalId = null;
          } else {
            _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
          }
          _item_1.setLocalId(_tmpLocalId);
          final long _tmpAccountId;
          _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
          _item_1.setAccountId(_tmpAccountId);
          final Long _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId);
          }
          _item_1.setId(_tmpId);
          final int _tmpStatus;
          _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
          _item_1.setStatus(_tmpStatus);
          final Instant _tmpLastModified;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp);
          _item_1.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_1);
          _item_1.setLastModifiedLocal(_tmpLastModifiedLocal);
          final String _tmpEtag;
          if (_stmt.isNull(_columnIndexOfEtag)) {
            _tmpEtag = null;
          } else {
            _tmpEtag = _stmt.getText(_columnIndexOfEtag);
          }
          _item_1.setEtag(_tmpEtag);
          _result.add(_item_1);
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
