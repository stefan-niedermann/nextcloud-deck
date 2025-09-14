package it.niedermann.nextcloud.deck.database.dao;

import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.SQLiteConnection;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.database.converter.DateTypeConverter;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import java.lang.Class;
import java.lang.Integer;
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
import kotlin.Unit;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class BoardDao_Impl implements BoardDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Board> __insertAdapterOfBoard;

  private final EntityDeleteOrUpdateAdapter<Board> __deleteAdapterOfBoard;

  private final EntityDeleteOrUpdateAdapter<Board> __updateAdapterOfBoard;

  public BoardDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfBoard = new EntityInsertAdapter<Board>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `Board` (`title`,`ownerId`,`color`,`archived`,`shared`,`deletedAt`,`permissionRead`,`permissionEdit`,`permissionManage`,`permissionShare`,`localId`,`accountId`,`id`,`status`,`lastModified`,`lastModifiedLocal`,`etag`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Board entity) {
        if (entity.getTitle() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getTitle());
        }
        statement.bindLong(2, entity.getOwnerId());
        if (entity.getColor() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getColor());
        }
        final int _tmp = entity.isArchived() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindLong(5, entity.getShared());
        final Long _tmp_1 = DateTypeConverter.fromInstant(entity.getDeletedAt());
        if (_tmp_1 == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, _tmp_1);
        }
        final int _tmp_2 = entity.isPermissionRead() ? 1 : 0;
        statement.bindLong(7, _tmp_2);
        final int _tmp_3 = entity.isPermissionEdit() ? 1 : 0;
        statement.bindLong(8, _tmp_3);
        final int _tmp_4 = entity.isPermissionManage() ? 1 : 0;
        statement.bindLong(9, _tmp_4);
        final int _tmp_5 = entity.isPermissionShare() ? 1 : 0;
        statement.bindLong(10, _tmp_5);
        if (entity.getLocalId() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getLocalId());
        }
        statement.bindLong(12, entity.getAccountId());
        if (entity.getId() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getId());
        }
        statement.bindLong(14, entity.getStatus());
        final Long _tmp_6 = DateTypeConverter.fromInstant(entity.getLastModified());
        if (_tmp_6 == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, _tmp_6);
        }
        final Long _tmp_7 = DateTypeConverter.fromInstant(entity.getLastModifiedLocal());
        if (_tmp_7 == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, _tmp_7);
        }
        if (entity.getEtag() == null) {
          statement.bindNull(17);
        } else {
          statement.bindText(17, entity.getEtag());
        }
      }
    };
    this.__deleteAdapterOfBoard = new EntityDeleteOrUpdateAdapter<Board>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `Board` WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Board entity) {
        if (entity.getLocalId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getLocalId());
        }
      }
    };
    this.__updateAdapterOfBoard = new EntityDeleteOrUpdateAdapter<Board>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `Board` SET `title` = ?,`ownerId` = ?,`color` = ?,`archived` = ?,`shared` = ?,`deletedAt` = ?,`permissionRead` = ?,`permissionEdit` = ?,`permissionManage` = ?,`permissionShare` = ?,`localId` = ?,`accountId` = ?,`id` = ?,`status` = ?,`lastModified` = ?,`lastModifiedLocal` = ?,`etag` = ? WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Board entity) {
        if (entity.getTitle() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getTitle());
        }
        statement.bindLong(2, entity.getOwnerId());
        if (entity.getColor() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getColor());
        }
        final int _tmp = entity.isArchived() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindLong(5, entity.getShared());
        final Long _tmp_1 = DateTypeConverter.fromInstant(entity.getDeletedAt());
        if (_tmp_1 == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, _tmp_1);
        }
        final int _tmp_2 = entity.isPermissionRead() ? 1 : 0;
        statement.bindLong(7, _tmp_2);
        final int _tmp_3 = entity.isPermissionEdit() ? 1 : 0;
        statement.bindLong(8, _tmp_3);
        final int _tmp_4 = entity.isPermissionManage() ? 1 : 0;
        statement.bindLong(9, _tmp_4);
        final int _tmp_5 = entity.isPermissionShare() ? 1 : 0;
        statement.bindLong(10, _tmp_5);
        if (entity.getLocalId() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getLocalId());
        }
        statement.bindLong(12, entity.getAccountId());
        if (entity.getId() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getId());
        }
        statement.bindLong(14, entity.getStatus());
        final Long _tmp_6 = DateTypeConverter.fromInstant(entity.getLastModified());
        if (_tmp_6 == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, _tmp_6);
        }
        final Long _tmp_7 = DateTypeConverter.fromInstant(entity.getLastModifiedLocal());
        if (_tmp_7 == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, _tmp_7);
        }
        if (entity.getEtag() == null) {
          statement.bindNull(17);
        } else {
          statement.bindText(17, entity.getEtag());
        }
        if (entity.getLocalId() == null) {
          statement.bindNull(18);
        } else {
          statement.bindLong(18, entity.getLocalId());
        }
      }
    };
  }

  @Override
  public long insert(final Board entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfBoard.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final Board... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfBoard.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final Board... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfBoard.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final Board... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfBoard.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public LiveData<List<Board>> getNotDeletedBoards(final long accountId, final int archived) {
    final String _sql = "SELECT * FROM board WHERE accountId = ? and archived = ? and (deletedAt = 0 or deletedAt is null) and status <> 3 order by title asc";
    return __db.getInvalidationTracker().createLiveData(new String[] {"board"}, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, archived);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfOwnerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ownerId");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfShared = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "shared");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfPermissionRead = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionRead");
        final int _columnIndexOfPermissionEdit = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionEdit");
        final int _columnIndexOfPermissionManage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionManage");
        final int _columnIndexOfPermissionShare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionShare");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<Board> _result = new ArrayList<Board>();
        while (_stmt.step()) {
          final Board _item;
          _item = new Board();
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _item.setTitle(_tmpTitle);
          final long _tmpOwnerId;
          _tmpOwnerId = _stmt.getLong(_columnIndexOfOwnerId);
          _item.setOwnerId(_tmpOwnerId);
          final Integer _tmpColor;
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
          }
          _item.setColor(_tmpColor);
          final boolean _tmpArchived;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfArchived));
          _tmpArchived = _tmp != 0;
          _item.setArchived(_tmpArchived);
          final int _tmpShared;
          _tmpShared = (int) (_stmt.getLong(_columnIndexOfShared));
          _item.setShared(_tmpShared);
          final Instant _tmpDeletedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
          _item.setDeletedAt(_tmpDeletedAt);
          final boolean _tmpPermissionRead;
          final int _tmp_2;
          _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionRead));
          _tmpPermissionRead = _tmp_2 != 0;
          _item.setPermissionRead(_tmpPermissionRead);
          final boolean _tmpPermissionEdit;
          final int _tmp_3;
          _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
          _tmpPermissionEdit = _tmp_3 != 0;
          _item.setPermissionEdit(_tmpPermissionEdit);
          final boolean _tmpPermissionManage;
          final int _tmp_4;
          _tmp_4 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
          _tmpPermissionManage = _tmp_4 != 0;
          _item.setPermissionManage(_tmpPermissionManage);
          final boolean _tmpPermissionShare;
          final int _tmp_5;
          _tmp_5 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
          _tmpPermissionShare = _tmp_5 != 0;
          _item.setPermissionShare(_tmpPermissionShare);
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
          final Long _tmp_6;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_6 = null;
          } else {
            _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
          _item.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_7;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_7 = null;
          } else {
            _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
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
  public List<Board> getNotDeletedBoardsDirectly(final long accountId, final int archived) {
    final String _sql = "SELECT * FROM board WHERE accountId = ? and archived = ? and (deletedAt = 0 or deletedAt is null) and status <> 3 order by title asc";
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, archived);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfOwnerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ownerId");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfShared = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "shared");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfPermissionRead = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionRead");
        final int _columnIndexOfPermissionEdit = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionEdit");
        final int _columnIndexOfPermissionManage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionManage");
        final int _columnIndexOfPermissionShare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionShare");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<Board> _result = new ArrayList<Board>();
        while (_stmt.step()) {
          final Board _item;
          _item = new Board();
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _item.setTitle(_tmpTitle);
          final long _tmpOwnerId;
          _tmpOwnerId = _stmt.getLong(_columnIndexOfOwnerId);
          _item.setOwnerId(_tmpOwnerId);
          final Integer _tmpColor;
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
          }
          _item.setColor(_tmpColor);
          final boolean _tmpArchived;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfArchived));
          _tmpArchived = _tmp != 0;
          _item.setArchived(_tmpArchived);
          final int _tmpShared;
          _tmpShared = (int) (_stmt.getLong(_columnIndexOfShared));
          _item.setShared(_tmpShared);
          final Instant _tmpDeletedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
          _item.setDeletedAt(_tmpDeletedAt);
          final boolean _tmpPermissionRead;
          final int _tmp_2;
          _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionRead));
          _tmpPermissionRead = _tmp_2 != 0;
          _item.setPermissionRead(_tmpPermissionRead);
          final boolean _tmpPermissionEdit;
          final int _tmp_3;
          _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
          _tmpPermissionEdit = _tmp_3 != 0;
          _item.setPermissionEdit(_tmpPermissionEdit);
          final boolean _tmpPermissionManage;
          final int _tmp_4;
          _tmp_4 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
          _tmpPermissionManage = _tmp_4 != 0;
          _item.setPermissionManage(_tmpPermissionManage);
          final boolean _tmpPermissionShare;
          final int _tmp_5;
          _tmp_5 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
          _tmpPermissionShare = _tmp_5 != 0;
          _item.setPermissionShare(_tmpPermissionShare);
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
          final Long _tmp_6;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_6 = null;
          } else {
            _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
          _item.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_7;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_7 = null;
          } else {
            _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
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
  public LiveData<List<FullBoard>> getNotDeletedFullBoards(final long accountId,
      final int archived) {
    final String _sql = "SELECT * FROM board WHERE accountId = ? and archived = ? and (deletedAt = 0 or deletedAt is null) and status <> 3 order by title asc";
    return __db.getInvalidationTracker().createLiveData(new String[] {"Label", "User",
        "AccessControl", "Stack", "board"}, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, archived);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfOwnerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ownerId");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfShared = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "shared");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfPermissionRead = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionRead");
        final int _columnIndexOfPermissionEdit = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionEdit");
        final int _columnIndexOfPermissionManage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionManage");
        final int _columnIndexOfPermissionShare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionShare");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<User> _collectionOwner = new LongSparseArray<User>();
        final LongSparseArray<ArrayList<AccessControl>> _collectionParticipants = new LongSparseArray<ArrayList<AccessControl>>();
        final LongSparseArray<ArrayList<Stack>> _collectionStacks = new LongSparseArray<ArrayList<Stack>>();
        while (_stmt.step()) {
          final Long _tmpKey;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey = null;
          } else {
            _tmpKey = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey != null) {
            if (!_collectionLabels.containsKey(_tmpKey)) {
              _collectionLabels.put(_tmpKey, new ArrayList<Label>());
            }
          }
          final Long _tmpKey_1;
          if (_stmt.isNull(_columnIndexOfOwnerId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfOwnerId);
          }
          if (_tmpKey_1 != null) {
            _collectionOwner.put(_tmpKey_1, null);
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionParticipants.containsKey(_tmpKey_2)) {
              _collectionParticipants.put(_tmpKey_2, new ArrayList<AccessControl>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionStacks.containsKey(_tmpKey_3)) {
              _collectionStacks.put(_tmpKey_3, new ArrayList<Stack>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionOwner);
        __fetchRelationshipAccessControlAsitNiedermannNextcloudDeckModelAccessControl(_connection, _collectionParticipants);
        __fetchRelationshipStackAsitNiedermannNextcloudDeckModelStack(_connection, _collectionStacks);
        final List<FullBoard> _result = new ArrayList<FullBoard>();
        while (_stmt.step()) {
          final FullBoard _item;
          final Board _tmpBoard;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfOwnerId) && _stmt.isNull(_columnIndexOfColor) && _stmt.isNull(_columnIndexOfArchived) && _stmt.isNull(_columnIndexOfShared) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfPermissionRead) && _stmt.isNull(_columnIndexOfPermissionEdit) && _stmt.isNull(_columnIndexOfPermissionManage) && _stmt.isNull(_columnIndexOfPermissionShare) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpBoard = new Board();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpBoard.setTitle(_tmpTitle);
            final long _tmpOwnerId;
            _tmpOwnerId = _stmt.getLong(_columnIndexOfOwnerId);
            _tmpBoard.setOwnerId(_tmpOwnerId);
            final Integer _tmpColor;
            if (_stmt.isNull(_columnIndexOfColor)) {
              _tmpColor = null;
            } else {
              _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
            }
            _tmpBoard.setColor(_tmpColor);
            final boolean _tmpArchived;
            final int _tmp;
            _tmp = (int) (_stmt.getLong(_columnIndexOfArchived));
            _tmpArchived = _tmp != 0;
            _tmpBoard.setArchived(_tmpArchived);
            final int _tmpShared;
            _tmpShared = (int) (_stmt.getLong(_columnIndexOfShared));
            _tmpBoard.setShared(_tmpShared);
            final Instant _tmpDeletedAt;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
            _tmpBoard.setDeletedAt(_tmpDeletedAt);
            final boolean _tmpPermissionRead;
            final int _tmp_2;
            _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionRead));
            _tmpPermissionRead = _tmp_2 != 0;
            _tmpBoard.setPermissionRead(_tmpPermissionRead);
            final boolean _tmpPermissionEdit;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
            _tmpPermissionEdit = _tmp_3 != 0;
            _tmpBoard.setPermissionEdit(_tmpPermissionEdit);
            final boolean _tmpPermissionManage;
            final int _tmp_4;
            _tmp_4 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
            _tmpPermissionManage = _tmp_4 != 0;
            _tmpBoard.setPermissionManage(_tmpPermissionManage);
            final boolean _tmpPermissionShare;
            final int _tmp_5;
            _tmp_5 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
            _tmpPermissionShare = _tmp_5 != 0;
            _tmpBoard.setPermissionShare(_tmpPermissionShare);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpBoard.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpBoard.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpBoard.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpBoard.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_6;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
            _tmpBoard.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_7;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
            _tmpBoard.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpBoard.setEtag(_tmpEtag);
          } else {
            _tmpBoard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_4);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final User _tmpOwner;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfOwnerId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfOwnerId);
          }
          if (_tmpKey_5 != null) {
            _tmpOwner = _collectionOwner.get(_tmpKey_5);
          } else {
            _tmpOwner = null;
          }
          final ArrayList<AccessControl> _tmpParticipantsCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpParticipantsCollection = _collectionParticipants.get(_tmpKey_6);
          } else {
            _tmpParticipantsCollection = new ArrayList<AccessControl>();
          }
          final ArrayList<Stack> _tmpStacksCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_7 != null) {
            _tmpStacksCollection = _collectionStacks.get(_tmpKey_7);
          } else {
            _tmpStacksCollection = new ArrayList<Stack>();
          }
          _item = new FullBoard();
          _item.board = _tmpBoard;
          _item.labels = _tmpLabelsCollection;
          _item.owner = _tmpOwner;
          _item.participants = _tmpParticipantsCollection;
          _item.stacks = _tmpStacksCollection;
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<Board> getBoardByRemoteId(final long accountId, final long remoteId) {
    final String _sql = "SELECT * FROM board WHERE accountId = ? and id = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"board"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, remoteId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfOwnerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ownerId");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfShared = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "shared");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfPermissionRead = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionRead");
        final int _columnIndexOfPermissionEdit = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionEdit");
        final int _columnIndexOfPermissionManage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionManage");
        final int _columnIndexOfPermissionShare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionShare");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final Board _result;
        if (_stmt.step()) {
          _result = new Board();
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _result.setTitle(_tmpTitle);
          final long _tmpOwnerId;
          _tmpOwnerId = _stmt.getLong(_columnIndexOfOwnerId);
          _result.setOwnerId(_tmpOwnerId);
          final Integer _tmpColor;
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
          }
          _result.setColor(_tmpColor);
          final boolean _tmpArchived;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfArchived));
          _tmpArchived = _tmp != 0;
          _result.setArchived(_tmpArchived);
          final int _tmpShared;
          _tmpShared = (int) (_stmt.getLong(_columnIndexOfShared));
          _result.setShared(_tmpShared);
          final Instant _tmpDeletedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
          _result.setDeletedAt(_tmpDeletedAt);
          final boolean _tmpPermissionRead;
          final int _tmp_2;
          _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionRead));
          _tmpPermissionRead = _tmp_2 != 0;
          _result.setPermissionRead(_tmpPermissionRead);
          final boolean _tmpPermissionEdit;
          final int _tmp_3;
          _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
          _tmpPermissionEdit = _tmp_3 != 0;
          _result.setPermissionEdit(_tmpPermissionEdit);
          final boolean _tmpPermissionManage;
          final int _tmp_4;
          _tmp_4 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
          _tmpPermissionManage = _tmp_4 != 0;
          _result.setPermissionManage(_tmpPermissionManage);
          final boolean _tmpPermissionShare;
          final int _tmp_5;
          _tmp_5 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
          _tmpPermissionShare = _tmp_5 != 0;
          _result.setPermissionShare(_tmpPermissionShare);
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
          final Long _tmp_6;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_6 = null;
          } else {
            _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
          _result.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_7;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_7 = null;
          } else {
            _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
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
  public Board getBoardByRemoteIdDirectly(final long accountId, final long remoteId) {
    final String _sql = "SELECT * FROM board WHERE accountId = ? and id = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, remoteId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfOwnerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ownerId");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfShared = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "shared");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfPermissionRead = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionRead");
        final int _columnIndexOfPermissionEdit = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionEdit");
        final int _columnIndexOfPermissionManage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionManage");
        final int _columnIndexOfPermissionShare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionShare");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final Board _result;
        if (_stmt.step()) {
          _result = new Board();
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _result.setTitle(_tmpTitle);
          final long _tmpOwnerId;
          _tmpOwnerId = _stmt.getLong(_columnIndexOfOwnerId);
          _result.setOwnerId(_tmpOwnerId);
          final Integer _tmpColor;
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
          }
          _result.setColor(_tmpColor);
          final boolean _tmpArchived;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfArchived));
          _tmpArchived = _tmp != 0;
          _result.setArchived(_tmpArchived);
          final int _tmpShared;
          _tmpShared = (int) (_stmt.getLong(_columnIndexOfShared));
          _result.setShared(_tmpShared);
          final Instant _tmpDeletedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
          _result.setDeletedAt(_tmpDeletedAt);
          final boolean _tmpPermissionRead;
          final int _tmp_2;
          _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionRead));
          _tmpPermissionRead = _tmp_2 != 0;
          _result.setPermissionRead(_tmpPermissionRead);
          final boolean _tmpPermissionEdit;
          final int _tmp_3;
          _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
          _tmpPermissionEdit = _tmp_3 != 0;
          _result.setPermissionEdit(_tmpPermissionEdit);
          final boolean _tmpPermissionManage;
          final int _tmp_4;
          _tmp_4 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
          _tmpPermissionManage = _tmp_4 != 0;
          _result.setPermissionManage(_tmpPermissionManage);
          final boolean _tmpPermissionShare;
          final int _tmp_5;
          _tmp_5 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
          _tmpPermissionShare = _tmp_5 != 0;
          _result.setPermissionShare(_tmpPermissionShare);
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
          final Long _tmp_6;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_6 = null;
          } else {
            _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
          _result.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_7;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_7 = null;
          } else {
            _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
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
  public Board getBoardByLocalIdDirectly(final long localId) {
    final String _sql = "SELECT * FROM board WHERE localId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfOwnerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ownerId");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfShared = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "shared");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfPermissionRead = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionRead");
        final int _columnIndexOfPermissionEdit = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionEdit");
        final int _columnIndexOfPermissionManage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionManage");
        final int _columnIndexOfPermissionShare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionShare");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final Board _result;
        if (_stmt.step()) {
          _result = new Board();
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _result.setTitle(_tmpTitle);
          final long _tmpOwnerId;
          _tmpOwnerId = _stmt.getLong(_columnIndexOfOwnerId);
          _result.setOwnerId(_tmpOwnerId);
          final Integer _tmpColor;
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
          }
          _result.setColor(_tmpColor);
          final boolean _tmpArchived;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfArchived));
          _tmpArchived = _tmp != 0;
          _result.setArchived(_tmpArchived);
          final int _tmpShared;
          _tmpShared = (int) (_stmt.getLong(_columnIndexOfShared));
          _result.setShared(_tmpShared);
          final Instant _tmpDeletedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
          _result.setDeletedAt(_tmpDeletedAt);
          final boolean _tmpPermissionRead;
          final int _tmp_2;
          _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionRead));
          _tmpPermissionRead = _tmp_2 != 0;
          _result.setPermissionRead(_tmpPermissionRead);
          final boolean _tmpPermissionEdit;
          final int _tmp_3;
          _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
          _tmpPermissionEdit = _tmp_3 != 0;
          _result.setPermissionEdit(_tmpPermissionEdit);
          final boolean _tmpPermissionManage;
          final int _tmp_4;
          _tmp_4 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
          _tmpPermissionManage = _tmp_4 != 0;
          _result.setPermissionManage(_tmpPermissionManage);
          final boolean _tmpPermissionShare;
          final int _tmp_5;
          _tmp_5 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
          _tmpPermissionShare = _tmp_5 != 0;
          _result.setPermissionShare(_tmpPermissionShare);
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
          final Long _tmp_6;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_6 = null;
          } else {
            _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
          _result.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_7;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_7 = null;
          } else {
            _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
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
  public FullBoard getFullBoardByRemoteIdDirectly(final long accountId, final long remoteId) {
    final String _sql = "SELECT * FROM board WHERE accountId = ? and id = ?";
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, remoteId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfOwnerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ownerId");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfShared = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "shared");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfPermissionRead = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionRead");
        final int _columnIndexOfPermissionEdit = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionEdit");
        final int _columnIndexOfPermissionManage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionManage");
        final int _columnIndexOfPermissionShare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionShare");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<User> _collectionOwner = new LongSparseArray<User>();
        final LongSparseArray<ArrayList<AccessControl>> _collectionParticipants = new LongSparseArray<ArrayList<AccessControl>>();
        final LongSparseArray<ArrayList<Stack>> _collectionStacks = new LongSparseArray<ArrayList<Stack>>();
        while (_stmt.step()) {
          final Long _tmpKey;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey = null;
          } else {
            _tmpKey = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey != null) {
            if (!_collectionLabels.containsKey(_tmpKey)) {
              _collectionLabels.put(_tmpKey, new ArrayList<Label>());
            }
          }
          final Long _tmpKey_1;
          if (_stmt.isNull(_columnIndexOfOwnerId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfOwnerId);
          }
          if (_tmpKey_1 != null) {
            _collectionOwner.put(_tmpKey_1, null);
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionParticipants.containsKey(_tmpKey_2)) {
              _collectionParticipants.put(_tmpKey_2, new ArrayList<AccessControl>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionStacks.containsKey(_tmpKey_3)) {
              _collectionStacks.put(_tmpKey_3, new ArrayList<Stack>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionOwner);
        __fetchRelationshipAccessControlAsitNiedermannNextcloudDeckModelAccessControl(_connection, _collectionParticipants);
        __fetchRelationshipStackAsitNiedermannNextcloudDeckModelStack(_connection, _collectionStacks);
        final FullBoard _result;
        if (_stmt.step()) {
          final Board _tmpBoard;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfOwnerId) && _stmt.isNull(_columnIndexOfColor) && _stmt.isNull(_columnIndexOfArchived) && _stmt.isNull(_columnIndexOfShared) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfPermissionRead) && _stmt.isNull(_columnIndexOfPermissionEdit) && _stmt.isNull(_columnIndexOfPermissionManage) && _stmt.isNull(_columnIndexOfPermissionShare) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpBoard = new Board();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpBoard.setTitle(_tmpTitle);
            final long _tmpOwnerId;
            _tmpOwnerId = _stmt.getLong(_columnIndexOfOwnerId);
            _tmpBoard.setOwnerId(_tmpOwnerId);
            final Integer _tmpColor;
            if (_stmt.isNull(_columnIndexOfColor)) {
              _tmpColor = null;
            } else {
              _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
            }
            _tmpBoard.setColor(_tmpColor);
            final boolean _tmpArchived;
            final int _tmp;
            _tmp = (int) (_stmt.getLong(_columnIndexOfArchived));
            _tmpArchived = _tmp != 0;
            _tmpBoard.setArchived(_tmpArchived);
            final int _tmpShared;
            _tmpShared = (int) (_stmt.getLong(_columnIndexOfShared));
            _tmpBoard.setShared(_tmpShared);
            final Instant _tmpDeletedAt;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
            _tmpBoard.setDeletedAt(_tmpDeletedAt);
            final boolean _tmpPermissionRead;
            final int _tmp_2;
            _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionRead));
            _tmpPermissionRead = _tmp_2 != 0;
            _tmpBoard.setPermissionRead(_tmpPermissionRead);
            final boolean _tmpPermissionEdit;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
            _tmpPermissionEdit = _tmp_3 != 0;
            _tmpBoard.setPermissionEdit(_tmpPermissionEdit);
            final boolean _tmpPermissionManage;
            final int _tmp_4;
            _tmp_4 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
            _tmpPermissionManage = _tmp_4 != 0;
            _tmpBoard.setPermissionManage(_tmpPermissionManage);
            final boolean _tmpPermissionShare;
            final int _tmp_5;
            _tmp_5 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
            _tmpPermissionShare = _tmp_5 != 0;
            _tmpBoard.setPermissionShare(_tmpPermissionShare);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpBoard.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpBoard.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpBoard.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpBoard.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_6;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
            _tmpBoard.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_7;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
            _tmpBoard.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpBoard.setEtag(_tmpEtag);
          } else {
            _tmpBoard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_4);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final User _tmpOwner;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfOwnerId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfOwnerId);
          }
          if (_tmpKey_5 != null) {
            _tmpOwner = _collectionOwner.get(_tmpKey_5);
          } else {
            _tmpOwner = null;
          }
          final ArrayList<AccessControl> _tmpParticipantsCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpParticipantsCollection = _collectionParticipants.get(_tmpKey_6);
          } else {
            _tmpParticipantsCollection = new ArrayList<AccessControl>();
          }
          final ArrayList<Stack> _tmpStacksCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_7 != null) {
            _tmpStacksCollection = _collectionStacks.get(_tmpKey_7);
          } else {
            _tmpStacksCollection = new ArrayList<Stack>();
          }
          _result = new FullBoard();
          _result.board = _tmpBoard;
          _result.labels = _tmpLabelsCollection;
          _result.owner = _tmpOwner;
          _result.participants = _tmpParticipantsCollection;
          _result.stacks = _tmpStacksCollection;
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
  public FullBoard getFullBoardByLocalIdDirectly(final long accountId, final long localId) {
    final String _sql = "SELECT * FROM board WHERE accountId = ? and localId = ?";
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfOwnerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ownerId");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfShared = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "shared");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfPermissionRead = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionRead");
        final int _columnIndexOfPermissionEdit = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionEdit");
        final int _columnIndexOfPermissionManage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionManage");
        final int _columnIndexOfPermissionShare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionShare");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<User> _collectionOwner = new LongSparseArray<User>();
        final LongSparseArray<ArrayList<AccessControl>> _collectionParticipants = new LongSparseArray<ArrayList<AccessControl>>();
        final LongSparseArray<ArrayList<Stack>> _collectionStacks = new LongSparseArray<ArrayList<Stack>>();
        while (_stmt.step()) {
          final Long _tmpKey;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey = null;
          } else {
            _tmpKey = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey != null) {
            if (!_collectionLabels.containsKey(_tmpKey)) {
              _collectionLabels.put(_tmpKey, new ArrayList<Label>());
            }
          }
          final Long _tmpKey_1;
          if (_stmt.isNull(_columnIndexOfOwnerId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfOwnerId);
          }
          if (_tmpKey_1 != null) {
            _collectionOwner.put(_tmpKey_1, null);
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionParticipants.containsKey(_tmpKey_2)) {
              _collectionParticipants.put(_tmpKey_2, new ArrayList<AccessControl>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionStacks.containsKey(_tmpKey_3)) {
              _collectionStacks.put(_tmpKey_3, new ArrayList<Stack>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionOwner);
        __fetchRelationshipAccessControlAsitNiedermannNextcloudDeckModelAccessControl(_connection, _collectionParticipants);
        __fetchRelationshipStackAsitNiedermannNextcloudDeckModelStack(_connection, _collectionStacks);
        final FullBoard _result;
        if (_stmt.step()) {
          final Board _tmpBoard;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfOwnerId) && _stmt.isNull(_columnIndexOfColor) && _stmt.isNull(_columnIndexOfArchived) && _stmt.isNull(_columnIndexOfShared) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfPermissionRead) && _stmt.isNull(_columnIndexOfPermissionEdit) && _stmt.isNull(_columnIndexOfPermissionManage) && _stmt.isNull(_columnIndexOfPermissionShare) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpBoard = new Board();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpBoard.setTitle(_tmpTitle);
            final long _tmpOwnerId;
            _tmpOwnerId = _stmt.getLong(_columnIndexOfOwnerId);
            _tmpBoard.setOwnerId(_tmpOwnerId);
            final Integer _tmpColor;
            if (_stmt.isNull(_columnIndexOfColor)) {
              _tmpColor = null;
            } else {
              _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
            }
            _tmpBoard.setColor(_tmpColor);
            final boolean _tmpArchived;
            final int _tmp;
            _tmp = (int) (_stmt.getLong(_columnIndexOfArchived));
            _tmpArchived = _tmp != 0;
            _tmpBoard.setArchived(_tmpArchived);
            final int _tmpShared;
            _tmpShared = (int) (_stmt.getLong(_columnIndexOfShared));
            _tmpBoard.setShared(_tmpShared);
            final Instant _tmpDeletedAt;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
            _tmpBoard.setDeletedAt(_tmpDeletedAt);
            final boolean _tmpPermissionRead;
            final int _tmp_2;
            _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionRead));
            _tmpPermissionRead = _tmp_2 != 0;
            _tmpBoard.setPermissionRead(_tmpPermissionRead);
            final boolean _tmpPermissionEdit;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
            _tmpPermissionEdit = _tmp_3 != 0;
            _tmpBoard.setPermissionEdit(_tmpPermissionEdit);
            final boolean _tmpPermissionManage;
            final int _tmp_4;
            _tmp_4 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
            _tmpPermissionManage = _tmp_4 != 0;
            _tmpBoard.setPermissionManage(_tmpPermissionManage);
            final boolean _tmpPermissionShare;
            final int _tmp_5;
            _tmp_5 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
            _tmpPermissionShare = _tmp_5 != 0;
            _tmpBoard.setPermissionShare(_tmpPermissionShare);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpBoard.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpBoard.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpBoard.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpBoard.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_6;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
            _tmpBoard.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_7;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
            _tmpBoard.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpBoard.setEtag(_tmpEtag);
          } else {
            _tmpBoard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_4);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final User _tmpOwner;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfOwnerId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfOwnerId);
          }
          if (_tmpKey_5 != null) {
            _tmpOwner = _collectionOwner.get(_tmpKey_5);
          } else {
            _tmpOwner = null;
          }
          final ArrayList<AccessControl> _tmpParticipantsCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpParticipantsCollection = _collectionParticipants.get(_tmpKey_6);
          } else {
            _tmpParticipantsCollection = new ArrayList<AccessControl>();
          }
          final ArrayList<Stack> _tmpStacksCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_7 != null) {
            _tmpStacksCollection = _collectionStacks.get(_tmpKey_7);
          } else {
            _tmpStacksCollection = new ArrayList<Stack>();
          }
          _result = new FullBoard();
          _result.board = _tmpBoard;
          _result.labels = _tmpLabelsCollection;
          _result.owner = _tmpOwner;
          _result.participants = _tmpParticipantsCollection;
          _result.stacks = _tmpStacksCollection;
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
  public List<FullBoard> getLocallyChangedBoardsDirectly(final long accountId) {
    final String _sql = "SELECT * FROM board WHERE accountId = ? and (status<>1 or id is null or lastModified <> lastModifiedLocal)";
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfOwnerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ownerId");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfShared = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "shared");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfPermissionRead = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionRead");
        final int _columnIndexOfPermissionEdit = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionEdit");
        final int _columnIndexOfPermissionManage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionManage");
        final int _columnIndexOfPermissionShare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionShare");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<User> _collectionOwner = new LongSparseArray<User>();
        final LongSparseArray<ArrayList<AccessControl>> _collectionParticipants = new LongSparseArray<ArrayList<AccessControl>>();
        final LongSparseArray<ArrayList<Stack>> _collectionStacks = new LongSparseArray<ArrayList<Stack>>();
        while (_stmt.step()) {
          final Long _tmpKey;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey = null;
          } else {
            _tmpKey = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey != null) {
            if (!_collectionLabels.containsKey(_tmpKey)) {
              _collectionLabels.put(_tmpKey, new ArrayList<Label>());
            }
          }
          final Long _tmpKey_1;
          if (_stmt.isNull(_columnIndexOfOwnerId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfOwnerId);
          }
          if (_tmpKey_1 != null) {
            _collectionOwner.put(_tmpKey_1, null);
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionParticipants.containsKey(_tmpKey_2)) {
              _collectionParticipants.put(_tmpKey_2, new ArrayList<AccessControl>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionStacks.containsKey(_tmpKey_3)) {
              _collectionStacks.put(_tmpKey_3, new ArrayList<Stack>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionOwner);
        __fetchRelationshipAccessControlAsitNiedermannNextcloudDeckModelAccessControl(_connection, _collectionParticipants);
        __fetchRelationshipStackAsitNiedermannNextcloudDeckModelStack(_connection, _collectionStacks);
        final List<FullBoard> _result = new ArrayList<FullBoard>();
        while (_stmt.step()) {
          final FullBoard _item;
          final Board _tmpBoard;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfOwnerId) && _stmt.isNull(_columnIndexOfColor) && _stmt.isNull(_columnIndexOfArchived) && _stmt.isNull(_columnIndexOfShared) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfPermissionRead) && _stmt.isNull(_columnIndexOfPermissionEdit) && _stmt.isNull(_columnIndexOfPermissionManage) && _stmt.isNull(_columnIndexOfPermissionShare) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpBoard = new Board();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpBoard.setTitle(_tmpTitle);
            final long _tmpOwnerId;
            _tmpOwnerId = _stmt.getLong(_columnIndexOfOwnerId);
            _tmpBoard.setOwnerId(_tmpOwnerId);
            final Integer _tmpColor;
            if (_stmt.isNull(_columnIndexOfColor)) {
              _tmpColor = null;
            } else {
              _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
            }
            _tmpBoard.setColor(_tmpColor);
            final boolean _tmpArchived;
            final int _tmp;
            _tmp = (int) (_stmt.getLong(_columnIndexOfArchived));
            _tmpArchived = _tmp != 0;
            _tmpBoard.setArchived(_tmpArchived);
            final int _tmpShared;
            _tmpShared = (int) (_stmt.getLong(_columnIndexOfShared));
            _tmpBoard.setShared(_tmpShared);
            final Instant _tmpDeletedAt;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
            _tmpBoard.setDeletedAt(_tmpDeletedAt);
            final boolean _tmpPermissionRead;
            final int _tmp_2;
            _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionRead));
            _tmpPermissionRead = _tmp_2 != 0;
            _tmpBoard.setPermissionRead(_tmpPermissionRead);
            final boolean _tmpPermissionEdit;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
            _tmpPermissionEdit = _tmp_3 != 0;
            _tmpBoard.setPermissionEdit(_tmpPermissionEdit);
            final boolean _tmpPermissionManage;
            final int _tmp_4;
            _tmp_4 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
            _tmpPermissionManage = _tmp_4 != 0;
            _tmpBoard.setPermissionManage(_tmpPermissionManage);
            final boolean _tmpPermissionShare;
            final int _tmp_5;
            _tmp_5 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
            _tmpPermissionShare = _tmp_5 != 0;
            _tmpBoard.setPermissionShare(_tmpPermissionShare);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpBoard.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpBoard.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpBoard.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpBoard.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_6;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
            _tmpBoard.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_7;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
            _tmpBoard.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpBoard.setEtag(_tmpEtag);
          } else {
            _tmpBoard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_4);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final User _tmpOwner;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfOwnerId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfOwnerId);
          }
          if (_tmpKey_5 != null) {
            _tmpOwner = _collectionOwner.get(_tmpKey_5);
          } else {
            _tmpOwner = null;
          }
          final ArrayList<AccessControl> _tmpParticipantsCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpParticipantsCollection = _collectionParticipants.get(_tmpKey_6);
          } else {
            _tmpParticipantsCollection = new ArrayList<AccessControl>();
          }
          final ArrayList<Stack> _tmpStacksCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_7 != null) {
            _tmpStacksCollection = _collectionStacks.get(_tmpKey_7);
          } else {
            _tmpStacksCollection = new ArrayList<Stack>();
          }
          _item = new FullBoard();
          _item.board = _tmpBoard;
          _item.labels = _tmpLabelsCollection;
          _item.owner = _tmpOwner;
          _item.participants = _tmpParticipantsCollection;
          _item.stacks = _tmpStacksCollection;
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<FullBoard> getFullBoardById(final long accountId, final long localId) {
    final String _sql = "SELECT * FROM board WHERE accountId = ? and localId = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"Label", "User",
        "AccessControl", "Stack", "board"}, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfOwnerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ownerId");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfShared = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "shared");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfPermissionRead = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionRead");
        final int _columnIndexOfPermissionEdit = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionEdit");
        final int _columnIndexOfPermissionManage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionManage");
        final int _columnIndexOfPermissionShare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionShare");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<User> _collectionOwner = new LongSparseArray<User>();
        final LongSparseArray<ArrayList<AccessControl>> _collectionParticipants = new LongSparseArray<ArrayList<AccessControl>>();
        final LongSparseArray<ArrayList<Stack>> _collectionStacks = new LongSparseArray<ArrayList<Stack>>();
        while (_stmt.step()) {
          final Long _tmpKey;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey = null;
          } else {
            _tmpKey = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey != null) {
            if (!_collectionLabels.containsKey(_tmpKey)) {
              _collectionLabels.put(_tmpKey, new ArrayList<Label>());
            }
          }
          final Long _tmpKey_1;
          if (_stmt.isNull(_columnIndexOfOwnerId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfOwnerId);
          }
          if (_tmpKey_1 != null) {
            _collectionOwner.put(_tmpKey_1, null);
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionParticipants.containsKey(_tmpKey_2)) {
              _collectionParticipants.put(_tmpKey_2, new ArrayList<AccessControl>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionStacks.containsKey(_tmpKey_3)) {
              _collectionStacks.put(_tmpKey_3, new ArrayList<Stack>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionOwner);
        __fetchRelationshipAccessControlAsitNiedermannNextcloudDeckModelAccessControl(_connection, _collectionParticipants);
        __fetchRelationshipStackAsitNiedermannNextcloudDeckModelStack(_connection, _collectionStacks);
        final FullBoard _result;
        if (_stmt.step()) {
          final Board _tmpBoard;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfOwnerId) && _stmt.isNull(_columnIndexOfColor) && _stmt.isNull(_columnIndexOfArchived) && _stmt.isNull(_columnIndexOfShared) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfPermissionRead) && _stmt.isNull(_columnIndexOfPermissionEdit) && _stmt.isNull(_columnIndexOfPermissionManage) && _stmt.isNull(_columnIndexOfPermissionShare) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpBoard = new Board();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpBoard.setTitle(_tmpTitle);
            final long _tmpOwnerId;
            _tmpOwnerId = _stmt.getLong(_columnIndexOfOwnerId);
            _tmpBoard.setOwnerId(_tmpOwnerId);
            final Integer _tmpColor;
            if (_stmt.isNull(_columnIndexOfColor)) {
              _tmpColor = null;
            } else {
              _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
            }
            _tmpBoard.setColor(_tmpColor);
            final boolean _tmpArchived;
            final int _tmp;
            _tmp = (int) (_stmt.getLong(_columnIndexOfArchived));
            _tmpArchived = _tmp != 0;
            _tmpBoard.setArchived(_tmpArchived);
            final int _tmpShared;
            _tmpShared = (int) (_stmt.getLong(_columnIndexOfShared));
            _tmpBoard.setShared(_tmpShared);
            final Instant _tmpDeletedAt;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
            _tmpBoard.setDeletedAt(_tmpDeletedAt);
            final boolean _tmpPermissionRead;
            final int _tmp_2;
            _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionRead));
            _tmpPermissionRead = _tmp_2 != 0;
            _tmpBoard.setPermissionRead(_tmpPermissionRead);
            final boolean _tmpPermissionEdit;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
            _tmpPermissionEdit = _tmp_3 != 0;
            _tmpBoard.setPermissionEdit(_tmpPermissionEdit);
            final boolean _tmpPermissionManage;
            final int _tmp_4;
            _tmp_4 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
            _tmpPermissionManage = _tmp_4 != 0;
            _tmpBoard.setPermissionManage(_tmpPermissionManage);
            final boolean _tmpPermissionShare;
            final int _tmp_5;
            _tmp_5 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
            _tmpPermissionShare = _tmp_5 != 0;
            _tmpBoard.setPermissionShare(_tmpPermissionShare);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpBoard.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpBoard.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpBoard.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpBoard.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_6;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
            _tmpBoard.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_7;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
            _tmpBoard.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpBoard.setEtag(_tmpEtag);
          } else {
            _tmpBoard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_4);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final User _tmpOwner;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfOwnerId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfOwnerId);
          }
          if (_tmpKey_5 != null) {
            _tmpOwner = _collectionOwner.get(_tmpKey_5);
          } else {
            _tmpOwner = null;
          }
          final ArrayList<AccessControl> _tmpParticipantsCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpParticipantsCollection = _collectionParticipants.get(_tmpKey_6);
          } else {
            _tmpParticipantsCollection = new ArrayList<AccessControl>();
          }
          final ArrayList<Stack> _tmpStacksCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_7 != null) {
            _tmpStacksCollection = _collectionStacks.get(_tmpKey_7);
          } else {
            _tmpStacksCollection = new ArrayList<Stack>();
          }
          _result = new FullBoard();
          _result.board = _tmpBoard;
          _result.labels = _tmpLabelsCollection;
          _result.owner = _tmpOwner;
          _result.participants = _tmpParticipantsCollection;
          _result.stacks = _tmpStacksCollection;
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
  public Board getBoardByLocalCardIdDirectly(final long localCardId) {
    final String _sql = "SELECT b.* FROM board b JOIN stack s ON s.boardId = b.localId JOIN card c ON s.localId = c.stackId where c.localId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localCardId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfOwnerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ownerId");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfShared = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "shared");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfPermissionRead = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionRead");
        final int _columnIndexOfPermissionEdit = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionEdit");
        final int _columnIndexOfPermissionManage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionManage");
        final int _columnIndexOfPermissionShare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionShare");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final Board _result;
        if (_stmt.step()) {
          _result = new Board();
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _result.setTitle(_tmpTitle);
          final long _tmpOwnerId;
          _tmpOwnerId = _stmt.getLong(_columnIndexOfOwnerId);
          _result.setOwnerId(_tmpOwnerId);
          final Integer _tmpColor;
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
          }
          _result.setColor(_tmpColor);
          final boolean _tmpArchived;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfArchived));
          _tmpArchived = _tmp != 0;
          _result.setArchived(_tmpArchived);
          final int _tmpShared;
          _tmpShared = (int) (_stmt.getLong(_columnIndexOfShared));
          _result.setShared(_tmpShared);
          final Instant _tmpDeletedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
          _result.setDeletedAt(_tmpDeletedAt);
          final boolean _tmpPermissionRead;
          final int _tmp_2;
          _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionRead));
          _tmpPermissionRead = _tmp_2 != 0;
          _result.setPermissionRead(_tmpPermissionRead);
          final boolean _tmpPermissionEdit;
          final int _tmp_3;
          _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
          _tmpPermissionEdit = _tmp_3 != 0;
          _result.setPermissionEdit(_tmpPermissionEdit);
          final boolean _tmpPermissionManage;
          final int _tmp_4;
          _tmp_4 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
          _tmpPermissionManage = _tmp_4 != 0;
          _result.setPermissionManage(_tmpPermissionManage);
          final boolean _tmpPermissionShare;
          final int _tmp_5;
          _tmp_5 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
          _tmpPermissionShare = _tmp_5 != 0;
          _result.setPermissionShare(_tmpPermissionShare);
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
          final Long _tmp_6;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_6 = null;
          } else {
            _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
          _result.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_7;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_7 = null;
          } else {
            _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
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
  public Long getBoardLocalIdByLocalCardIdDirectly(final long localCardId) {
    final String _sql = "SELECT b.localId FROM board b JOIN stack s ON s.boardId = b.localId JOIN card c ON s.localId = c.stackId where c.localId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localCardId);
        final Long _result;
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null;
          } else {
            _result = _stmt.getLong(0);
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
  public FullBoard getFullBoardByLocalCardIdDirectly(final long localCardId) {
    final String _sql = "SELECT b.* FROM board b JOIN stack s ON s.boardId = b.localId JOIN card c ON c.localId = ? and c.stackId = s.localId";
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localCardId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfOwnerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ownerId");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfShared = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "shared");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfPermissionRead = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionRead");
        final int _columnIndexOfPermissionEdit = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionEdit");
        final int _columnIndexOfPermissionManage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionManage");
        final int _columnIndexOfPermissionShare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionShare");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<User> _collectionOwner = new LongSparseArray<User>();
        final LongSparseArray<ArrayList<AccessControl>> _collectionParticipants = new LongSparseArray<ArrayList<AccessControl>>();
        final LongSparseArray<ArrayList<Stack>> _collectionStacks = new LongSparseArray<ArrayList<Stack>>();
        while (_stmt.step()) {
          final Long _tmpKey;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey = null;
          } else {
            _tmpKey = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey != null) {
            if (!_collectionLabels.containsKey(_tmpKey)) {
              _collectionLabels.put(_tmpKey, new ArrayList<Label>());
            }
          }
          final Long _tmpKey_1;
          if (_stmt.isNull(_columnIndexOfOwnerId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfOwnerId);
          }
          if (_tmpKey_1 != null) {
            _collectionOwner.put(_tmpKey_1, null);
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionParticipants.containsKey(_tmpKey_2)) {
              _collectionParticipants.put(_tmpKey_2, new ArrayList<AccessControl>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionStacks.containsKey(_tmpKey_3)) {
              _collectionStacks.put(_tmpKey_3, new ArrayList<Stack>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionOwner);
        __fetchRelationshipAccessControlAsitNiedermannNextcloudDeckModelAccessControl(_connection, _collectionParticipants);
        __fetchRelationshipStackAsitNiedermannNextcloudDeckModelStack(_connection, _collectionStacks);
        final FullBoard _result;
        if (_stmt.step()) {
          final Board _tmpBoard;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfOwnerId) && _stmt.isNull(_columnIndexOfColor) && _stmt.isNull(_columnIndexOfArchived) && _stmt.isNull(_columnIndexOfShared) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfPermissionRead) && _stmt.isNull(_columnIndexOfPermissionEdit) && _stmt.isNull(_columnIndexOfPermissionManage) && _stmt.isNull(_columnIndexOfPermissionShare) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpBoard = new Board();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpBoard.setTitle(_tmpTitle);
            final long _tmpOwnerId;
            _tmpOwnerId = _stmt.getLong(_columnIndexOfOwnerId);
            _tmpBoard.setOwnerId(_tmpOwnerId);
            final Integer _tmpColor;
            if (_stmt.isNull(_columnIndexOfColor)) {
              _tmpColor = null;
            } else {
              _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
            }
            _tmpBoard.setColor(_tmpColor);
            final boolean _tmpArchived;
            final int _tmp;
            _tmp = (int) (_stmt.getLong(_columnIndexOfArchived));
            _tmpArchived = _tmp != 0;
            _tmpBoard.setArchived(_tmpArchived);
            final int _tmpShared;
            _tmpShared = (int) (_stmt.getLong(_columnIndexOfShared));
            _tmpBoard.setShared(_tmpShared);
            final Instant _tmpDeletedAt;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
            _tmpBoard.setDeletedAt(_tmpDeletedAt);
            final boolean _tmpPermissionRead;
            final int _tmp_2;
            _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionRead));
            _tmpPermissionRead = _tmp_2 != 0;
            _tmpBoard.setPermissionRead(_tmpPermissionRead);
            final boolean _tmpPermissionEdit;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
            _tmpPermissionEdit = _tmp_3 != 0;
            _tmpBoard.setPermissionEdit(_tmpPermissionEdit);
            final boolean _tmpPermissionManage;
            final int _tmp_4;
            _tmp_4 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
            _tmpPermissionManage = _tmp_4 != 0;
            _tmpBoard.setPermissionManage(_tmpPermissionManage);
            final boolean _tmpPermissionShare;
            final int _tmp_5;
            _tmp_5 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
            _tmpPermissionShare = _tmp_5 != 0;
            _tmpBoard.setPermissionShare(_tmpPermissionShare);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpBoard.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpBoard.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpBoard.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpBoard.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_6;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
            _tmpBoard.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_7;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
            _tmpBoard.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpBoard.setEtag(_tmpEtag);
          } else {
            _tmpBoard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_4);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final User _tmpOwner;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfOwnerId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfOwnerId);
          }
          if (_tmpKey_5 != null) {
            _tmpOwner = _collectionOwner.get(_tmpKey_5);
          } else {
            _tmpOwner = null;
          }
          final ArrayList<AccessControl> _tmpParticipantsCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpParticipantsCollection = _collectionParticipants.get(_tmpKey_6);
          } else {
            _tmpParticipantsCollection = new ArrayList<AccessControl>();
          }
          final ArrayList<Stack> _tmpStacksCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_7 != null) {
            _tmpStacksCollection = _collectionStacks.get(_tmpKey_7);
          } else {
            _tmpStacksCollection = new ArrayList<Stack>();
          }
          _result = new FullBoard();
          _result.board = _tmpBoard;
          _result.labels = _tmpLabelsCollection;
          _result.owner = _tmpOwner;
          _result.participants = _tmpParticipantsCollection;
          _result.stacks = _tmpStacksCollection;
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
  public List<FullBoard> getAllFullBoards(final long accountId) {
    final String _sql = "SELECT * FROM board WHERE accountId = ?";
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfOwnerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ownerId");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfShared = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "shared");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfPermissionRead = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionRead");
        final int _columnIndexOfPermissionEdit = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionEdit");
        final int _columnIndexOfPermissionManage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionManage");
        final int _columnIndexOfPermissionShare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionShare");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<User> _collectionOwner = new LongSparseArray<User>();
        final LongSparseArray<ArrayList<AccessControl>> _collectionParticipants = new LongSparseArray<ArrayList<AccessControl>>();
        final LongSparseArray<ArrayList<Stack>> _collectionStacks = new LongSparseArray<ArrayList<Stack>>();
        while (_stmt.step()) {
          final Long _tmpKey;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey = null;
          } else {
            _tmpKey = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey != null) {
            if (!_collectionLabels.containsKey(_tmpKey)) {
              _collectionLabels.put(_tmpKey, new ArrayList<Label>());
            }
          }
          final Long _tmpKey_1;
          if (_stmt.isNull(_columnIndexOfOwnerId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfOwnerId);
          }
          if (_tmpKey_1 != null) {
            _collectionOwner.put(_tmpKey_1, null);
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionParticipants.containsKey(_tmpKey_2)) {
              _collectionParticipants.put(_tmpKey_2, new ArrayList<AccessControl>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionStacks.containsKey(_tmpKey_3)) {
              _collectionStacks.put(_tmpKey_3, new ArrayList<Stack>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionOwner);
        __fetchRelationshipAccessControlAsitNiedermannNextcloudDeckModelAccessControl(_connection, _collectionParticipants);
        __fetchRelationshipStackAsitNiedermannNextcloudDeckModelStack(_connection, _collectionStacks);
        final List<FullBoard> _result = new ArrayList<FullBoard>();
        while (_stmt.step()) {
          final FullBoard _item;
          final Board _tmpBoard;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfOwnerId) && _stmt.isNull(_columnIndexOfColor) && _stmt.isNull(_columnIndexOfArchived) && _stmt.isNull(_columnIndexOfShared) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfPermissionRead) && _stmt.isNull(_columnIndexOfPermissionEdit) && _stmt.isNull(_columnIndexOfPermissionManage) && _stmt.isNull(_columnIndexOfPermissionShare) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpBoard = new Board();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpBoard.setTitle(_tmpTitle);
            final long _tmpOwnerId;
            _tmpOwnerId = _stmt.getLong(_columnIndexOfOwnerId);
            _tmpBoard.setOwnerId(_tmpOwnerId);
            final Integer _tmpColor;
            if (_stmt.isNull(_columnIndexOfColor)) {
              _tmpColor = null;
            } else {
              _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
            }
            _tmpBoard.setColor(_tmpColor);
            final boolean _tmpArchived;
            final int _tmp;
            _tmp = (int) (_stmt.getLong(_columnIndexOfArchived));
            _tmpArchived = _tmp != 0;
            _tmpBoard.setArchived(_tmpArchived);
            final int _tmpShared;
            _tmpShared = (int) (_stmt.getLong(_columnIndexOfShared));
            _tmpBoard.setShared(_tmpShared);
            final Instant _tmpDeletedAt;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
            _tmpBoard.setDeletedAt(_tmpDeletedAt);
            final boolean _tmpPermissionRead;
            final int _tmp_2;
            _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionRead));
            _tmpPermissionRead = _tmp_2 != 0;
            _tmpBoard.setPermissionRead(_tmpPermissionRead);
            final boolean _tmpPermissionEdit;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
            _tmpPermissionEdit = _tmp_3 != 0;
            _tmpBoard.setPermissionEdit(_tmpPermissionEdit);
            final boolean _tmpPermissionManage;
            final int _tmp_4;
            _tmp_4 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
            _tmpPermissionManage = _tmp_4 != 0;
            _tmpBoard.setPermissionManage(_tmpPermissionManage);
            final boolean _tmpPermissionShare;
            final int _tmp_5;
            _tmp_5 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
            _tmpPermissionShare = _tmp_5 != 0;
            _tmpBoard.setPermissionShare(_tmpPermissionShare);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpBoard.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpBoard.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpBoard.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpBoard.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_6;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
            _tmpBoard.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_7;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
            _tmpBoard.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpBoard.setEtag(_tmpEtag);
          } else {
            _tmpBoard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_4);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final User _tmpOwner;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfOwnerId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfOwnerId);
          }
          if (_tmpKey_5 != null) {
            _tmpOwner = _collectionOwner.get(_tmpKey_5);
          } else {
            _tmpOwner = null;
          }
          final ArrayList<AccessControl> _tmpParticipantsCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpParticipantsCollection = _collectionParticipants.get(_tmpKey_6);
          } else {
            _tmpParticipantsCollection = new ArrayList<AccessControl>();
          }
          final ArrayList<Stack> _tmpStacksCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_7 != null) {
            _tmpStacksCollection = _collectionStacks.get(_tmpKey_7);
          } else {
            _tmpStacksCollection = new ArrayList<Stack>();
          }
          _item = new FullBoard();
          _item.board = _tmpBoard;
          _item.labels = _tmpLabelsCollection;
          _item.owner = _tmpOwner;
          _item.participants = _tmpParticipantsCollection;
          _item.stacks = _tmpStacksCollection;
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<Board>> getBoardsWithEditPermissionsForAccount(final long accountId) {
    final String _sql = "SELECT * FROM board WHERE accountId = ? and archived = 0 and permissionEdit = 1 and (deletedAt = 0 or deletedAt is null) and status <> 3 order by title asc";
    return __db.getInvalidationTracker().createLiveData(new String[] {"board"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfOwnerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ownerId");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfShared = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "shared");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfPermissionRead = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionRead");
        final int _columnIndexOfPermissionEdit = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionEdit");
        final int _columnIndexOfPermissionManage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionManage");
        final int _columnIndexOfPermissionShare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionShare");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<Board> _result = new ArrayList<Board>();
        while (_stmt.step()) {
          final Board _item;
          _item = new Board();
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _item.setTitle(_tmpTitle);
          final long _tmpOwnerId;
          _tmpOwnerId = _stmt.getLong(_columnIndexOfOwnerId);
          _item.setOwnerId(_tmpOwnerId);
          final Integer _tmpColor;
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
          }
          _item.setColor(_tmpColor);
          final boolean _tmpArchived;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfArchived));
          _tmpArchived = _tmp != 0;
          _item.setArchived(_tmpArchived);
          final int _tmpShared;
          _tmpShared = (int) (_stmt.getLong(_columnIndexOfShared));
          _item.setShared(_tmpShared);
          final Instant _tmpDeletedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
          _item.setDeletedAt(_tmpDeletedAt);
          final boolean _tmpPermissionRead;
          final int _tmp_2;
          _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionRead));
          _tmpPermissionRead = _tmp_2 != 0;
          _item.setPermissionRead(_tmpPermissionRead);
          final boolean _tmpPermissionEdit;
          final int _tmp_3;
          _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
          _tmpPermissionEdit = _tmp_3 != 0;
          _item.setPermissionEdit(_tmpPermissionEdit);
          final boolean _tmpPermissionManage;
          final int _tmp_4;
          _tmp_4 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
          _tmpPermissionManage = _tmp_4 != 0;
          _item.setPermissionManage(_tmpPermissionManage);
          final boolean _tmpPermissionShare;
          final int _tmp_5;
          _tmp_5 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
          _tmpPermissionShare = _tmp_5 != 0;
          _item.setPermissionShare(_tmpPermissionShare);
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
          final Long _tmp_6;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_6 = null;
          } else {
            _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
          _item.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_7;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_7 = null;
          } else {
            _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
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
  public LiveData<Long> getLocalBoardIdByCardRemoteIdAndAccountId(final long cardRemoteId,
      final long accountId) {
    final String _sql = "SELECT s.boardId FROM card c inner join stack s on s.localId = c.stackId WHERE c.id = ? and c.accountId =  ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"card",
        "stack"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cardRemoteId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, accountId);
        final Long _result;
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null;
          } else {
            _result = _stmt.getLong(0);
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
  public Long getBoardLocalIdByAccountAndCardRemoteIdDirectly(final long accountId,
      final long cardRemoteId) {
    final String _sql = "SELECT s.boardId FROM card c inner join stack s on s.localId = c.stackId WHERE c.id = ? and c.accountId =  ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cardRemoteId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, accountId);
        final Long _result;
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null;
          } else {
            _result = _stmt.getLong(0);
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
  public LiveData<Integer> countArchivedBoards(final long accountId) {
    final String _sql = "SELECT count(*) FROM board WHERE accountId = ? and archived = 1 and (deletedAt = 0 or deletedAt is null) and status <> 3";
    return __db.getInvalidationTracker().createLiveData(new String[] {"board"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        final Integer _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp;
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
  public Board getBoardForAccountByNameDirectly(final long accountId, final String title) {
    final String _sql = "SELECT * FROM board WHERE accountId = ? and title = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        if (title == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, title);
        }
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfOwnerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ownerId");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfShared = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "shared");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfPermissionRead = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionRead");
        final int _columnIndexOfPermissionEdit = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionEdit");
        final int _columnIndexOfPermissionManage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionManage");
        final int _columnIndexOfPermissionShare = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "permissionShare");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final Board _result;
        if (_stmt.step()) {
          _result = new Board();
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _result.setTitle(_tmpTitle);
          final long _tmpOwnerId;
          _tmpOwnerId = _stmt.getLong(_columnIndexOfOwnerId);
          _result.setOwnerId(_tmpOwnerId);
          final Integer _tmpColor;
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
          }
          _result.setColor(_tmpColor);
          final boolean _tmpArchived;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfArchived));
          _tmpArchived = _tmp != 0;
          _result.setArchived(_tmpArchived);
          final int _tmpShared;
          _tmpShared = (int) (_stmt.getLong(_columnIndexOfShared));
          _result.setShared(_tmpShared);
          final Instant _tmpDeletedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
          _result.setDeletedAt(_tmpDeletedAt);
          final boolean _tmpPermissionRead;
          final int _tmp_2;
          _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionRead));
          _tmpPermissionRead = _tmp_2 != 0;
          _result.setPermissionRead(_tmpPermissionRead);
          final boolean _tmpPermissionEdit;
          final int _tmp_3;
          _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
          _tmpPermissionEdit = _tmp_3 != 0;
          _result.setPermissionEdit(_tmpPermissionEdit);
          final boolean _tmpPermissionManage;
          final int _tmp_4;
          _tmp_4 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
          _tmpPermissionManage = _tmp_4 != 0;
          _result.setPermissionManage(_tmpPermissionManage);
          final boolean _tmpPermissionShare;
          final int _tmp_5;
          _tmp_5 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
          _tmpPermissionShare = _tmp_5 != 0;
          _result.setPermissionShare(_tmpPermissionShare);
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
          final Long _tmp_6;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_6 = null;
          } else {
            _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
          _result.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_7;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_7 = null;
          } else {
            _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
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
  public Integer getBoardColorByLocalIdDirectly(final long accountId, final long localBoardId) {
    final String _sql = "SELECT b.color FROM board b where b.localId = ? and b.accountId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localBoardId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, accountId);
        final Integer _result;
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null;
          } else {
            _result = (int) (_stmt.getLong(0));
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
  public LiveData<Integer> getBoardColor(final long accountId, final long localBoardId) {
    final String _sql = "SELECT b.color FROM board b where b.localId = ? and b.accountId = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"board"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localBoardId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, accountId);
        final Integer _result;
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null;
          } else {
            _result = (int) (_stmt.getLong(0));
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(
      @NonNull final SQLiteConnection _connection,
      @NonNull final LongSparseArray<ArrayList<Label>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > 999) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (_tmpMap) -> {
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _tmpMap);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT `title`,`color`,`boardId`,`localId`,`accountId`,`id`,`status`,`lastModified`,`lastModifiedLocal`,`etag` FROM `Label` WHERE `boardId` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final SQLiteStatement _stmt = _connection.prepare(_sql);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    try {
      final int _itemKeyIndex = SQLiteStatementUtil.getColumnIndex(_stmt, "boardId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _columnIndexOfTitle = 0;
      final int _columnIndexOfColor = 1;
      final int _columnIndexOfBoardId = 2;
      final int _columnIndexOfLocalId = 3;
      final int _columnIndexOfAccountId = 4;
      final int _columnIndexOfId = 5;
      final int _columnIndexOfStatus = 6;
      final int _columnIndexOfLastModified = 7;
      final int _columnIndexOfLastModifiedLocal = 8;
      final int _columnIndexOfEtag = 9;
      while (_stmt.step()) {
        final long _tmpKey;
        _tmpKey = _stmt.getLong(_itemKeyIndex);
        final ArrayList<Label> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final Label _item_1;
          _item_1 = new Label();
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _item_1.setTitle(_tmpTitle);
          final Integer _tmpColor;
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
          }
          _item_1.setColor(_tmpColor);
          final long _tmpBoardId;
          _tmpBoardId = _stmt.getLong(_columnIndexOfBoardId);
          _item_1.setBoardId(_tmpBoardId);
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
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _stmt.close();
    }
  }

  private void __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(
      @NonNull final SQLiteConnection _connection, @NonNull final LongSparseArray<User> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > 999) {
      RelationUtil.recursiveFetchLongSparseArray(_map, false, (_tmpMap) -> {
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _tmpMap);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT `primaryKey`,`uid`,`displayname`,`type`,`localId`,`accountId`,`id`,`status`,`lastModified`,`lastModifiedLocal`,`etag` FROM `User` WHERE `localId` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final SQLiteStatement _stmt = _connection.prepare(_sql);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    try {
      final int _itemKeyIndex = SQLiteStatementUtil.getColumnIndex(_stmt, "localId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _columnIndexOfPrimaryKey = 0;
      final int _columnIndexOfUid = 1;
      final int _columnIndexOfDisplayname = 2;
      final int _columnIndexOfType = 3;
      final int _columnIndexOfLocalId = 4;
      final int _columnIndexOfAccountId = 5;
      final int _columnIndexOfId = 6;
      final int _columnIndexOfStatus = 7;
      final int _columnIndexOfLastModified = 8;
      final int _columnIndexOfLastModifiedLocal = 9;
      final int _columnIndexOfEtag = 10;
      while (_stmt.step()) {
        final Long _tmpKey;
        if (_stmt.isNull(_itemKeyIndex)) {
          _tmpKey = null;
        } else {
          _tmpKey = _stmt.getLong(_itemKeyIndex);
        }
        if (_tmpKey != null) {
          if (_map.containsKey(_tmpKey)) {
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
            _map.put(_tmpKey, _item_1);
          }
        }
      }
    } finally {
      _stmt.close();
    }
  }

  private void __fetchRelationshipAccessControlAsitNiedermannNextcloudDeckModelAccessControl(
      @NonNull final SQLiteConnection _connection,
      @NonNull final LongSparseArray<ArrayList<AccessControl>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > 999) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (_tmpMap) -> {
        __fetchRelationshipAccessControlAsitNiedermannNextcloudDeckModelAccessControl(_connection, _tmpMap);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT `type`,`boardId`,`owner`,`permissionEdit`,`permissionShare`,`permissionManage`,`userId`,`localId`,`accountId`,`id`,`status`,`lastModified`,`lastModifiedLocal`,`etag` FROM `AccessControl` WHERE `boardId` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final SQLiteStatement _stmt = _connection.prepare(_sql);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    try {
      final int _itemKeyIndex = SQLiteStatementUtil.getColumnIndex(_stmt, "boardId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _columnIndexOfType = 0;
      final int _columnIndexOfBoardId = 1;
      final int _columnIndexOfOwner = 2;
      final int _columnIndexOfPermissionEdit = 3;
      final int _columnIndexOfPermissionShare = 4;
      final int _columnIndexOfPermissionManage = 5;
      final int _columnIndexOfUserId = 6;
      final int _columnIndexOfLocalId = 7;
      final int _columnIndexOfAccountId = 8;
      final int _columnIndexOfId = 9;
      final int _columnIndexOfStatus = 10;
      final int _columnIndexOfLastModified = 11;
      final int _columnIndexOfLastModifiedLocal = 12;
      final int _columnIndexOfEtag = 13;
      while (_stmt.step()) {
        final Long _tmpKey;
        if (_stmt.isNull(_itemKeyIndex)) {
          _tmpKey = null;
        } else {
          _tmpKey = _stmt.getLong(_itemKeyIndex);
        }
        if (_tmpKey != null) {
          final ArrayList<AccessControl> _tmpRelation = _map.get(_tmpKey);
          if (_tmpRelation != null) {
            final AccessControl _item_1;
            _item_1 = new AccessControl();
            final Long _tmpType;
            if (_stmt.isNull(_columnIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _stmt.getLong(_columnIndexOfType);
            }
            _item_1.setType(_tmpType);
            final Long _tmpBoardId;
            if (_stmt.isNull(_columnIndexOfBoardId)) {
              _tmpBoardId = null;
            } else {
              _tmpBoardId = _stmt.getLong(_columnIndexOfBoardId);
            }
            _item_1.setBoardId(_tmpBoardId);
            final boolean _tmpOwner;
            final int _tmp;
            _tmp = (int) (_stmt.getLong(_columnIndexOfOwner));
            _tmpOwner = _tmp != 0;
            _item_1.setOwner(_tmpOwner);
            final boolean _tmpPermissionEdit;
            final int _tmp_1;
            _tmp_1 = (int) (_stmt.getLong(_columnIndexOfPermissionEdit));
            _tmpPermissionEdit = _tmp_1 != 0;
            _item_1.setPermissionEdit(_tmpPermissionEdit);
            final boolean _tmpPermissionShare;
            final int _tmp_2;
            _tmp_2 = (int) (_stmt.getLong(_columnIndexOfPermissionShare));
            _tmpPermissionShare = _tmp_2 != 0;
            _item_1.setPermissionShare(_tmpPermissionShare);
            final boolean _tmpPermissionManage;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfPermissionManage));
            _tmpPermissionManage = _tmp_3 != 0;
            _item_1.setPermissionManage(_tmpPermissionManage);
            final Long _tmpUserId;
            if (_stmt.isNull(_columnIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
            }
            _item_1.setUserId(_tmpUserId);
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
            final Long _tmp_4;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_4);
            _item_1.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_5;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_5);
            _item_1.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _item_1.setEtag(_tmpEtag);
            _tmpRelation.add(_item_1);
          }
        }
      }
    } finally {
      _stmt.close();
    }
  }

  private void __fetchRelationshipStackAsitNiedermannNextcloudDeckModelStack(
      @NonNull final SQLiteConnection _connection,
      @NonNull final LongSparseArray<ArrayList<Stack>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > 999) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (_tmpMap) -> {
        __fetchRelationshipStackAsitNiedermannNextcloudDeckModelStack(_connection, _tmpMap);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT `title`,`boardId`,`deletedAt`,`order`,`localId`,`accountId`,`id`,`status`,`lastModified`,`lastModifiedLocal`,`etag` FROM `Stack` WHERE `boardId` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final SQLiteStatement _stmt = _connection.prepare(_sql);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    try {
      final int _itemKeyIndex = SQLiteStatementUtil.getColumnIndex(_stmt, "boardId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _columnIndexOfTitle = 0;
      final int _columnIndexOfBoardId = 1;
      final int _columnIndexOfDeletedAt = 2;
      final int _columnIndexOfOrder = 3;
      final int _columnIndexOfLocalId = 4;
      final int _columnIndexOfAccountId = 5;
      final int _columnIndexOfId = 6;
      final int _columnIndexOfStatus = 7;
      final int _columnIndexOfLastModified = 8;
      final int _columnIndexOfLastModifiedLocal = 9;
      final int _columnIndexOfEtag = 10;
      while (_stmt.step()) {
        final long _tmpKey;
        _tmpKey = _stmt.getLong(_itemKeyIndex);
        final ArrayList<Stack> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final Stack _item_1;
          _item_1 = new Stack();
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _item_1.setTitle(_tmpTitle);
          final long _tmpBoardId;
          _tmpBoardId = _stmt.getLong(_columnIndexOfBoardId);
          _item_1.setBoardId(_tmpBoardId);
          final Instant _tmpDeletedAt;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp);
          _item_1.setDeletedAt(_tmpDeletedAt);
          final int _tmpOrder;
          _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
          _item_1.setOrder(_tmpOrder);
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
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_1);
          _item_1.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_2);
          _item_1.setLastModifiedLocal(_tmpLastModifiedLocal);
          final String _tmpEtag;
          if (_stmt.isNull(_columnIndexOfEtag)) {
            _tmpEtag = null;
          } else {
            _tmpEtag = _stmt.getText(_columnIndexOfEtag);
          }
          _item_1.setEtag(_tmpEtag);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _stmt.close();
    }
  }
}
