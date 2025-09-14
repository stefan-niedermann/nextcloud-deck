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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Generated;

import it.niedermann.nextcloud.deck.database.converter.DateTypeConverter;
import it.niedermann.nextcloud.deck.model.AccessControl;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class AccessControlDao_Impl implements AccessControlDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<AccessControl> __insertAdapterOfAccessControl;

  private final EntityDeleteOrUpdateAdapter<AccessControl> __deleteAdapterOfAccessControl;

  private final EntityDeleteOrUpdateAdapter<AccessControl> __updateAdapterOfAccessControl;

  public AccessControlDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfAccessControl = new EntityInsertAdapter<AccessControl>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `AccessControl` (`type`,`boardId`,`owner`,`permissionEdit`,`permissionShare`,`permissionManage`,`userId`,`localId`,`accountId`,`id`,`status`,`lastModified`,`lastModifiedLocal`,`etag`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final AccessControl entity) {
        if (entity.getType() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getType());
        }
        if (entity.getBoardId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getBoardId());
        }
        final int _tmp = entity.isOwner() ? 1 : 0;
        statement.bindLong(3, _tmp);
        final int _tmp_1 = entity.isPermissionEdit() ? 1 : 0;
        statement.bindLong(4, _tmp_1);
        final int _tmp_2 = entity.isPermissionShare() ? 1 : 0;
        statement.bindLong(5, _tmp_2);
        final int _tmp_3 = entity.isPermissionManage() ? 1 : 0;
        statement.bindLong(6, _tmp_3);
        if (entity.getUserId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getUserId());
        }
        if (entity.getLocalId() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getLocalId());
        }
        statement.bindLong(9, entity.getAccountId());
        if (entity.getId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getId());
        }
        statement.bindLong(11, entity.getStatus());
        final Long _tmp_4 = DateTypeConverter.fromInstant(entity.getLastModified());
        if (_tmp_4 == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, _tmp_4);
        }
        final Long _tmp_5 = DateTypeConverter.fromInstant(entity.getLastModifiedLocal());
        if (_tmp_5 == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, _tmp_5);
        }
        if (entity.getEtag() == null) {
          statement.bindNull(14);
        } else {
          statement.bindText(14, entity.getEtag());
        }
      }
    };
    this.__deleteAdapterOfAccessControl = new EntityDeleteOrUpdateAdapter<AccessControl>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `AccessControl` WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final AccessControl entity) {
        if (entity.getLocalId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getLocalId());
        }
      }
    };
    this.__updateAdapterOfAccessControl = new EntityDeleteOrUpdateAdapter<AccessControl>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `AccessControl` SET `type` = ?,`boardId` = ?,`owner` = ?,`permissionEdit` = ?,`permissionShare` = ?,`permissionManage` = ?,`userId` = ?,`localId` = ?,`accountId` = ?,`id` = ?,`status` = ?,`lastModified` = ?,`lastModifiedLocal` = ?,`etag` = ? WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final AccessControl entity) {
        if (entity.getType() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getType());
        }
        if (entity.getBoardId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getBoardId());
        }
        final int _tmp = entity.isOwner() ? 1 : 0;
        statement.bindLong(3, _tmp);
        final int _tmp_1 = entity.isPermissionEdit() ? 1 : 0;
        statement.bindLong(4, _tmp_1);
        final int _tmp_2 = entity.isPermissionShare() ? 1 : 0;
        statement.bindLong(5, _tmp_2);
        final int _tmp_3 = entity.isPermissionManage() ? 1 : 0;
        statement.bindLong(6, _tmp_3);
        if (entity.getUserId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getUserId());
        }
        if (entity.getLocalId() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getLocalId());
        }
        statement.bindLong(9, entity.getAccountId());
        if (entity.getId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getId());
        }
        statement.bindLong(11, entity.getStatus());
        final Long _tmp_4 = DateTypeConverter.fromInstant(entity.getLastModified());
        if (_tmp_4 == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, _tmp_4);
        }
        final Long _tmp_5 = DateTypeConverter.fromInstant(entity.getLastModifiedLocal());
        if (_tmp_5 == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, _tmp_5);
        }
        if (entity.getEtag() == null) {
          statement.bindNull(14);
        } else {
          statement.bindText(14, entity.getEtag());
        }
        if (entity.getLocalId() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getLocalId());
        }
      }
    };
  }

  @Override
  public long insert(final AccessControl entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfAccessControl.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final AccessControl... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfAccessControl.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final AccessControl... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfAccessControl.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final AccessControl... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfAccessControl.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public LiveData<AccessControl> getAccessControlByRemoteId(final long accountId,
      final long remoteId) {
    final String _sql = "SELECT * FROM AccessControl WHERE accountId = ? and id = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"AccessControl"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, remoteId);
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfBoardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardId");
        final int _columnIndexOfOwner = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "owner");
        final int _columnIndexOfPermissionEdit = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionEdit");
        final int _columnIndexOfPermissionShare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionShare");
        final int _columnIndexOfPermissionManage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionManage");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final AccessControl _result;
        if (_stmt.step()) {
          _result = new AccessControl();
          final Long _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getLong(_columnIndexOfType);
          }
          _result.setType(_tmpType);
          final Long _tmpBoardId;
          if (_stmt.isNull(_columnIndexOfBoardId)) {
            _tmpBoardId = null;
          } else {
            _tmpBoardId = _stmt.getLong(_columnIndexOfBoardId);
          }
          _result.setBoardId(_tmpBoardId);
          final boolean _tmpOwner;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfOwner));
          _tmpOwner = _tmp != 0;
          _result.setOwner(_tmpOwner);
          final boolean _tmpPermissionEdit;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
          _tmpPermissionEdit = _tmp_1 != 0;
          _result.setPermissionEdit(_tmpPermissionEdit);
          final boolean _tmpPermissionShare;
          final int _tmp_2;
          _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
          _tmpPermissionShare = _tmp_2 != 0;
          _result.setPermissionShare(_tmpPermissionShare);
          final boolean _tmpPermissionManage;
          final int _tmp_3;
          _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
          _tmpPermissionManage = _tmp_3 != 0;
          _result.setPermissionManage(_tmpPermissionManage);
          final Long _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
          }
          _result.setUserId(_tmpUserId);
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
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_4);
          _result.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_5;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_5 = null;
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_5);
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
  public LiveData<List<AccessControl>> getAccessControlByLocalBoardId(final long accountId,
      final long localBoardId) {
    final String _sql = "SELECT * FROM AccessControl WHERE accountId = ? and boardId = ? and status <> 3";
    return __db.getInvalidationTracker().createLiveData(new String[] {"AccessControl"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localBoardId);
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfBoardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardId");
        final int _columnIndexOfOwner = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "owner");
        final int _columnIndexOfPermissionEdit = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionEdit");
        final int _columnIndexOfPermissionShare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionShare");
        final int _columnIndexOfPermissionManage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionManage");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<AccessControl> _result = new ArrayList<AccessControl>();
        while (_stmt.step()) {
          final AccessControl _item;
          _item = new AccessControl();
          final Long _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getLong(_columnIndexOfType);
          }
          _item.setType(_tmpType);
          final Long _tmpBoardId;
          if (_stmt.isNull(_columnIndexOfBoardId)) {
            _tmpBoardId = null;
          } else {
            _tmpBoardId = _stmt.getLong(_columnIndexOfBoardId);
          }
          _item.setBoardId(_tmpBoardId);
          final boolean _tmpOwner;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfOwner));
          _tmpOwner = _tmp != 0;
          _item.setOwner(_tmpOwner);
          final boolean _tmpPermissionEdit;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
          _tmpPermissionEdit = _tmp_1 != 0;
          _item.setPermissionEdit(_tmpPermissionEdit);
          final boolean _tmpPermissionShare;
          final int _tmp_2;
          _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
          _tmpPermissionShare = _tmp_2 != 0;
          _item.setPermissionShare(_tmpPermissionShare);
          final boolean _tmpPermissionManage;
          final int _tmp_3;
          _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
          _tmpPermissionManage = _tmp_3 != 0;
          _item.setPermissionManage(_tmpPermissionManage);
          final Long _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
          }
          _item.setUserId(_tmpUserId);
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
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_4);
          _item.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_5;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_5 = null;
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_5);
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
  public List<AccessControl> getAccessControlByLocalBoardIdDirectly(final long accountId,
      final long localBoardId) {
    final String _sql = "SELECT * FROM AccessControl WHERE accountId = ? and boardId = ? and status <> 3";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localBoardId);
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfBoardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardId");
        final int _columnIndexOfOwner = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "owner");
        final int _columnIndexOfPermissionEdit = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionEdit");
        final int _columnIndexOfPermissionShare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionShare");
        final int _columnIndexOfPermissionManage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionManage");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<AccessControl> _result = new ArrayList<AccessControl>();
        while (_stmt.step()) {
          final AccessControl _item;
          _item = new AccessControl();
          final Long _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getLong(_columnIndexOfType);
          }
          _item.setType(_tmpType);
          final Long _tmpBoardId;
          if (_stmt.isNull(_columnIndexOfBoardId)) {
            _tmpBoardId = null;
          } else {
            _tmpBoardId = _stmt.getLong(_columnIndexOfBoardId);
          }
          _item.setBoardId(_tmpBoardId);
          final boolean _tmpOwner;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfOwner));
          _tmpOwner = _tmp != 0;
          _item.setOwner(_tmpOwner);
          final boolean _tmpPermissionEdit;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
          _tmpPermissionEdit = _tmp_1 != 0;
          _item.setPermissionEdit(_tmpPermissionEdit);
          final boolean _tmpPermissionShare;
          final int _tmp_2;
          _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
          _tmpPermissionShare = _tmp_2 != 0;
          _item.setPermissionShare(_tmpPermissionShare);
          final boolean _tmpPermissionManage;
          final int _tmp_3;
          _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
          _tmpPermissionManage = _tmp_3 != 0;
          _item.setPermissionManage(_tmpPermissionManage);
          final Long _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
          }
          _item.setUserId(_tmpUserId);
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
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_4);
          _item.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_5;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_5 = null;
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_5);
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
  public AccessControl getAccessControlByRemoteIdDirectly(final long accountId,
      final long remoteId) {
    final String _sql = "SELECT * FROM AccessControl WHERE accountId = ? and id = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, remoteId);
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfBoardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardId");
        final int _columnIndexOfOwner = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "owner");
        final int _columnIndexOfPermissionEdit = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionEdit");
        final int _columnIndexOfPermissionShare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionShare");
        final int _columnIndexOfPermissionManage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionManage");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final AccessControl _result;
        if (_stmt.step()) {
          _result = new AccessControl();
          final Long _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getLong(_columnIndexOfType);
          }
          _result.setType(_tmpType);
          final Long _tmpBoardId;
          if (_stmt.isNull(_columnIndexOfBoardId)) {
            _tmpBoardId = null;
          } else {
            _tmpBoardId = _stmt.getLong(_columnIndexOfBoardId);
          }
          _result.setBoardId(_tmpBoardId);
          final boolean _tmpOwner;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfOwner));
          _tmpOwner = _tmp != 0;
          _result.setOwner(_tmpOwner);
          final boolean _tmpPermissionEdit;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
          _tmpPermissionEdit = _tmp_1 != 0;
          _result.setPermissionEdit(_tmpPermissionEdit);
          final boolean _tmpPermissionShare;
          final int _tmp_2;
          _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
          _tmpPermissionShare = _tmp_2 != 0;
          _result.setPermissionShare(_tmpPermissionShare);
          final boolean _tmpPermissionManage;
          final int _tmp_3;
          _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
          _tmpPermissionManage = _tmp_3 != 0;
          _result.setPermissionManage(_tmpPermissionManage);
          final Long _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
          }
          _result.setUserId(_tmpUserId);
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
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_4);
          _result.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_5;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_5 = null;
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_5);
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
  public List<AccessControl> getLocallyChangedAccessControl(final long accountId,
      final long boardId) {
    final String _sql = "SELECT * FROM AccessControl WHERE accountId = ? and boardId = ? and (status<>1 or id is null or lastModified <> lastModifiedLocal)";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, boardId);
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfBoardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardId");
        final int _columnIndexOfOwner = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "owner");
        final int _columnIndexOfPermissionEdit = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionEdit");
        final int _columnIndexOfPermissionShare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionShare");
        final int _columnIndexOfPermissionManage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionManage");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<AccessControl> _result = new ArrayList<AccessControl>();
        while (_stmt.step()) {
          final AccessControl _item;
          _item = new AccessControl();
          final Long _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getLong(_columnIndexOfType);
          }
          _item.setType(_tmpType);
          final Long _tmpBoardId;
          if (_stmt.isNull(_columnIndexOfBoardId)) {
            _tmpBoardId = null;
          } else {
            _tmpBoardId = _stmt.getLong(_columnIndexOfBoardId);
          }
          _item.setBoardId(_tmpBoardId);
          final boolean _tmpOwner;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfOwner));
          _tmpOwner = _tmp != 0;
          _item.setOwner(_tmpOwner);
          final boolean _tmpPermissionEdit;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
          _tmpPermissionEdit = _tmp_1 != 0;
          _item.setPermissionEdit(_tmpPermissionEdit);
          final boolean _tmpPermissionShare;
          final int _tmp_2;
          _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
          _tmpPermissionShare = _tmp_2 != 0;
          _item.setPermissionShare(_tmpPermissionShare);
          final boolean _tmpPermissionManage;
          final int _tmp_3;
          _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
          _tmpPermissionManage = _tmp_3 != 0;
          _item.setPermissionManage(_tmpPermissionManage);
          final Long _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
          }
          _item.setUserId(_tmpUserId);
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
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_4);
          _item.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_5;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_5 = null;
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_5);
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
  public List<Long> getBoardIDsOfLocallyChangedAccessControl(final long accountId) {
    final String _sql = "SELECT distinct boardId FROM AccessControl WHERE accountId = ? and (status<>1 or id is null or lastModified <> lastModifiedLocal)";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        final List<Long> _result = new ArrayList<Long>();
        while (_stmt.step()) {
          final Long _item;
          if (_stmt.isNull(0)) {
            _item = null;
          } else {
            _item = _stmt.getLong(0);
          }
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void deleteAccessControlsForBoardWhereLocalIdsNotInDirectly(final long localBoardId,
      final Set<Long> idsToKeep) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("DELETE FROM AccessControl WHERE boardId = ");
    _stringBuilder.append("?");
    _stringBuilder.append(" and localId not in (");
    final int _inputSize = idsToKeep == null ? 1 : idsToKeep.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localBoardId);
        _argIndex = 2;
        if (idsToKeep == null) {
          _stmt.bindNull(_argIndex);
        } else {
          for (Long _item : idsToKeep) {
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
