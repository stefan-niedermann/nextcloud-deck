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
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.full.FullDeckComment;
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
import kotlin.Unit;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class CommentDao_Impl implements CommentDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<DeckComment> __insertAdapterOfDeckComment;

  private final EntityDeleteOrUpdateAdapter<DeckComment> __deleteAdapterOfDeckComment;

  private final EntityDeleteOrUpdateAdapter<DeckComment> __updateAdapterOfDeckComment;

  public CommentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfDeckComment = new EntityInsertAdapter<DeckComment>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `DeckComment` (`objectId`,`actorType`,`creationDateTime`,`actorId`,`actorDisplayName`,`message`,`parentId`,`localId`,`accountId`,`id`,`status`,`lastModified`,`lastModifiedLocal`,`etag`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final DeckComment entity) {
        if (entity.getObjectId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getObjectId());
        }
        if (entity.getActorType() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getActorType());
        }
        final Long _tmp = DateTypeConverter.fromInstant(entity.getCreationDateTime());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, _tmp);
        }
        if (entity.getActorId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getActorId());
        }
        if (entity.getActorDisplayName() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getActorDisplayName());
        }
        if (entity.getMessage() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getMessage());
        }
        if (entity.getParentId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getParentId());
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
        final Long _tmp_1 = DateTypeConverter.fromInstant(entity.getLastModified());
        if (_tmp_1 == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, _tmp_1);
        }
        final Long _tmp_2 = DateTypeConverter.fromInstant(entity.getLastModifiedLocal());
        if (_tmp_2 == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, _tmp_2);
        }
        if (entity.getEtag() == null) {
          statement.bindNull(14);
        } else {
          statement.bindText(14, entity.getEtag());
        }
      }
    };
    this.__deleteAdapterOfDeckComment = new EntityDeleteOrUpdateAdapter<DeckComment>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `DeckComment` WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final DeckComment entity) {
        if (entity.getLocalId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getLocalId());
        }
      }
    };
    this.__updateAdapterOfDeckComment = new EntityDeleteOrUpdateAdapter<DeckComment>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `DeckComment` SET `objectId` = ?,`actorType` = ?,`creationDateTime` = ?,`actorId` = ?,`actorDisplayName` = ?,`message` = ?,`parentId` = ?,`localId` = ?,`accountId` = ?,`id` = ?,`status` = ?,`lastModified` = ?,`lastModifiedLocal` = ?,`etag` = ? WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final DeckComment entity) {
        if (entity.getObjectId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getObjectId());
        }
        if (entity.getActorType() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getActorType());
        }
        final Long _tmp = DateTypeConverter.fromInstant(entity.getCreationDateTime());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, _tmp);
        }
        if (entity.getActorId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getActorId());
        }
        if (entity.getActorDisplayName() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getActorDisplayName());
        }
        if (entity.getMessage() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getMessage());
        }
        if (entity.getParentId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getParentId());
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
        final Long _tmp_1 = DateTypeConverter.fromInstant(entity.getLastModified());
        if (_tmp_1 == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, _tmp_1);
        }
        final Long _tmp_2 = DateTypeConverter.fromInstant(entity.getLastModifiedLocal());
        if (_tmp_2 == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, _tmp_2);
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
  public long insert(final DeckComment entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfDeckComment.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final DeckComment... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfDeckComment.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final DeckComment... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfDeckComment.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final DeckComment... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfDeckComment.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public DeckComment getCommentByRemoteIdDirectly(final long accountId, final Long remoteId) {
    final String _sql = "SELECT * FROM DeckComment where accountId = ? and id = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        if (remoteId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, remoteId);
        }
        final int _columnIndexOfObjectId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "objectId");
        final int _columnIndexOfActorType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorType");
        final int _columnIndexOfCreationDateTime = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "creationDateTime");
        final int _columnIndexOfActorId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorId");
        final int _columnIndexOfActorDisplayName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorDisplayName");
        final int _columnIndexOfMessage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "message");
        final int _columnIndexOfParentId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "parentId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final DeckComment _result;
        if (_stmt.step()) {
          _result = new DeckComment();
          final Long _tmpObjectId;
          if (_stmt.isNull(_columnIndexOfObjectId)) {
            _tmpObjectId = null;
          } else {
            _tmpObjectId = _stmt.getLong(_columnIndexOfObjectId);
          }
          _result.setObjectId(_tmpObjectId);
          final String _tmpActorType;
          if (_stmt.isNull(_columnIndexOfActorType)) {
            _tmpActorType = null;
          } else {
            _tmpActorType = _stmt.getText(_columnIndexOfActorType);
          }
          _result.setActorType(_tmpActorType);
          final Instant _tmpCreationDateTime;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfCreationDateTime)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfCreationDateTime);
          }
          _tmpCreationDateTime = DateTypeConverter.toInstant(_tmp);
          _result.setCreationDateTime(_tmpCreationDateTime);
          final String _tmpActorId;
          if (_stmt.isNull(_columnIndexOfActorId)) {
            _tmpActorId = null;
          } else {
            _tmpActorId = _stmt.getText(_columnIndexOfActorId);
          }
          _result.setActorId(_tmpActorId);
          final String _tmpActorDisplayName;
          if (_stmt.isNull(_columnIndexOfActorDisplayName)) {
            _tmpActorDisplayName = null;
          } else {
            _tmpActorDisplayName = _stmt.getText(_columnIndexOfActorDisplayName);
          }
          _result.setActorDisplayName(_tmpActorDisplayName);
          final String _tmpMessage;
          if (_stmt.isNull(_columnIndexOfMessage)) {
            _tmpMessage = null;
          } else {
            _tmpMessage = _stmt.getText(_columnIndexOfMessage);
          }
          _result.setMessage(_tmpMessage);
          final Long _tmpParentId;
          if (_stmt.isNull(_columnIndexOfParentId)) {
            _tmpParentId = null;
          } else {
            _tmpParentId = _stmt.getLong(_columnIndexOfParentId);
          }
          _result.setParentId(_tmpParentId);
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
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_1);
          _result.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_2);
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
  public DeckComment getCommentByLocalIdDirectly(final long accountId, final Long id) {
    final String _sql = "SELECT * FROM DeckComment where accountId = ? and localId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        if (id == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, id);
        }
        final int _columnIndexOfObjectId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "objectId");
        final int _columnIndexOfActorType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorType");
        final int _columnIndexOfCreationDateTime = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "creationDateTime");
        final int _columnIndexOfActorId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorId");
        final int _columnIndexOfActorDisplayName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorDisplayName");
        final int _columnIndexOfMessage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "message");
        final int _columnIndexOfParentId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "parentId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final DeckComment _result;
        if (_stmt.step()) {
          _result = new DeckComment();
          final Long _tmpObjectId;
          if (_stmt.isNull(_columnIndexOfObjectId)) {
            _tmpObjectId = null;
          } else {
            _tmpObjectId = _stmt.getLong(_columnIndexOfObjectId);
          }
          _result.setObjectId(_tmpObjectId);
          final String _tmpActorType;
          if (_stmt.isNull(_columnIndexOfActorType)) {
            _tmpActorType = null;
          } else {
            _tmpActorType = _stmt.getText(_columnIndexOfActorType);
          }
          _result.setActorType(_tmpActorType);
          final Instant _tmpCreationDateTime;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfCreationDateTime)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfCreationDateTime);
          }
          _tmpCreationDateTime = DateTypeConverter.toInstant(_tmp);
          _result.setCreationDateTime(_tmpCreationDateTime);
          final String _tmpActorId;
          if (_stmt.isNull(_columnIndexOfActorId)) {
            _tmpActorId = null;
          } else {
            _tmpActorId = _stmt.getText(_columnIndexOfActorId);
          }
          _result.setActorId(_tmpActorId);
          final String _tmpActorDisplayName;
          if (_stmt.isNull(_columnIndexOfActorDisplayName)) {
            _tmpActorDisplayName = null;
          } else {
            _tmpActorDisplayName = _stmt.getText(_columnIndexOfActorDisplayName);
          }
          _result.setActorDisplayName(_tmpActorDisplayName);
          final String _tmpMessage;
          if (_stmt.isNull(_columnIndexOfMessage)) {
            _tmpMessage = null;
          } else {
            _tmpMessage = _stmt.getText(_columnIndexOfMessage);
          }
          _result.setMessage(_tmpMessage);
          final Long _tmpParentId;
          if (_stmt.isNull(_columnIndexOfParentId)) {
            _tmpParentId = null;
          } else {
            _tmpParentId = _stmt.getLong(_columnIndexOfParentId);
          }
          _result.setParentId(_tmpParentId);
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
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_1);
          _result.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_2);
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
  public List<DeckComment> getLocallyChangedCommentsByLocalCardIdDirectly(final long accountId,
      final long localCardId) {
    final String _sql = "SELECT * FROM DeckComment WHERE accountId = ? and objectId = ? and (status<>1 or id is null or lastModified <> lastModifiedLocal) order by localId asc";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localCardId);
        final int _columnIndexOfObjectId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "objectId");
        final int _columnIndexOfActorType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorType");
        final int _columnIndexOfCreationDateTime = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "creationDateTime");
        final int _columnIndexOfActorId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorId");
        final int _columnIndexOfActorDisplayName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorDisplayName");
        final int _columnIndexOfMessage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "message");
        final int _columnIndexOfParentId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "parentId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<DeckComment> _result = new ArrayList<DeckComment>();
        while (_stmt.step()) {
          final DeckComment _item;
          _item = new DeckComment();
          final Long _tmpObjectId;
          if (_stmt.isNull(_columnIndexOfObjectId)) {
            _tmpObjectId = null;
          } else {
            _tmpObjectId = _stmt.getLong(_columnIndexOfObjectId);
          }
          _item.setObjectId(_tmpObjectId);
          final String _tmpActorType;
          if (_stmt.isNull(_columnIndexOfActorType)) {
            _tmpActorType = null;
          } else {
            _tmpActorType = _stmt.getText(_columnIndexOfActorType);
          }
          _item.setActorType(_tmpActorType);
          final Instant _tmpCreationDateTime;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfCreationDateTime)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfCreationDateTime);
          }
          _tmpCreationDateTime = DateTypeConverter.toInstant(_tmp);
          _item.setCreationDateTime(_tmpCreationDateTime);
          final String _tmpActorId;
          if (_stmt.isNull(_columnIndexOfActorId)) {
            _tmpActorId = null;
          } else {
            _tmpActorId = _stmt.getText(_columnIndexOfActorId);
          }
          _item.setActorId(_tmpActorId);
          final String _tmpActorDisplayName;
          if (_stmt.isNull(_columnIndexOfActorDisplayName)) {
            _tmpActorDisplayName = null;
          } else {
            _tmpActorDisplayName = _stmt.getText(_columnIndexOfActorDisplayName);
          }
          _item.setActorDisplayName(_tmpActorDisplayName);
          final String _tmpMessage;
          if (_stmt.isNull(_columnIndexOfMessage)) {
            _tmpMessage = null;
          } else {
            _tmpMessage = _stmt.getText(_columnIndexOfMessage);
          }
          _item.setMessage(_tmpMessage);
          final Long _tmpParentId;
          if (_stmt.isNull(_columnIndexOfParentId)) {
            _tmpParentId = null;
          } else {
            _tmpParentId = _stmt.getLong(_columnIndexOfParentId);
          }
          _item.setParentId(_tmpParentId);
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
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_1);
          _item.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_2);
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
  public List<DeckComment> getLocallyChangedCommentsDirectly(final long accountId) {
    final String _sql = "SELECT * FROM DeckComment WHERE accountId = ? and (status<>1 or id is null or lastModified <> lastModifiedLocal)";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        final int _columnIndexOfObjectId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "objectId");
        final int _columnIndexOfActorType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorType");
        final int _columnIndexOfCreationDateTime = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "creationDateTime");
        final int _columnIndexOfActorId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorId");
        final int _columnIndexOfActorDisplayName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorDisplayName");
        final int _columnIndexOfMessage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "message");
        final int _columnIndexOfParentId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "parentId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<DeckComment> _result = new ArrayList<DeckComment>();
        while (_stmt.step()) {
          final DeckComment _item;
          _item = new DeckComment();
          final Long _tmpObjectId;
          if (_stmt.isNull(_columnIndexOfObjectId)) {
            _tmpObjectId = null;
          } else {
            _tmpObjectId = _stmt.getLong(_columnIndexOfObjectId);
          }
          _item.setObjectId(_tmpObjectId);
          final String _tmpActorType;
          if (_stmt.isNull(_columnIndexOfActorType)) {
            _tmpActorType = null;
          } else {
            _tmpActorType = _stmt.getText(_columnIndexOfActorType);
          }
          _item.setActorType(_tmpActorType);
          final Instant _tmpCreationDateTime;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfCreationDateTime)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfCreationDateTime);
          }
          _tmpCreationDateTime = DateTypeConverter.toInstant(_tmp);
          _item.setCreationDateTime(_tmpCreationDateTime);
          final String _tmpActorId;
          if (_stmt.isNull(_columnIndexOfActorId)) {
            _tmpActorId = null;
          } else {
            _tmpActorId = _stmt.getText(_columnIndexOfActorId);
          }
          _item.setActorId(_tmpActorId);
          final String _tmpActorDisplayName;
          if (_stmt.isNull(_columnIndexOfActorDisplayName)) {
            _tmpActorDisplayName = null;
          } else {
            _tmpActorDisplayName = _stmt.getText(_columnIndexOfActorDisplayName);
          }
          _item.setActorDisplayName(_tmpActorDisplayName);
          final String _tmpMessage;
          if (_stmt.isNull(_columnIndexOfMessage)) {
            _tmpMessage = null;
          } else {
            _tmpMessage = _stmt.getText(_columnIndexOfMessage);
          }
          _item.setMessage(_tmpMessage);
          final Long _tmpParentId;
          if (_stmt.isNull(_columnIndexOfParentId)) {
            _tmpParentId = null;
          } else {
            _tmpParentId = _stmt.getLong(_columnIndexOfParentId);
          }
          _item.setParentId(_tmpParentId);
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
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_1);
          _item.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_2);
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
  public List<DeckComment> getCommentsForLocalCardIdDirectly(final long accountId,
      final Long localCardId) {
    final String _sql = "SELECT * FROM DeckComment WHERE accountId = ? and objectId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        if (localCardId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localCardId);
        }
        final int _columnIndexOfObjectId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "objectId");
        final int _columnIndexOfActorType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorType");
        final int _columnIndexOfCreationDateTime = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "creationDateTime");
        final int _columnIndexOfActorId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorId");
        final int _columnIndexOfActorDisplayName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorDisplayName");
        final int _columnIndexOfMessage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "message");
        final int _columnIndexOfParentId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "parentId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<DeckComment> _result = new ArrayList<DeckComment>();
        while (_stmt.step()) {
          final DeckComment _item;
          _item = new DeckComment();
          final Long _tmpObjectId;
          if (_stmt.isNull(_columnIndexOfObjectId)) {
            _tmpObjectId = null;
          } else {
            _tmpObjectId = _stmt.getLong(_columnIndexOfObjectId);
          }
          _item.setObjectId(_tmpObjectId);
          final String _tmpActorType;
          if (_stmt.isNull(_columnIndexOfActorType)) {
            _tmpActorType = null;
          } else {
            _tmpActorType = _stmt.getText(_columnIndexOfActorType);
          }
          _item.setActorType(_tmpActorType);
          final Instant _tmpCreationDateTime;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfCreationDateTime)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfCreationDateTime);
          }
          _tmpCreationDateTime = DateTypeConverter.toInstant(_tmp);
          _item.setCreationDateTime(_tmpCreationDateTime);
          final String _tmpActorId;
          if (_stmt.isNull(_columnIndexOfActorId)) {
            _tmpActorId = null;
          } else {
            _tmpActorId = _stmt.getText(_columnIndexOfActorId);
          }
          _item.setActorId(_tmpActorId);
          final String _tmpActorDisplayName;
          if (_stmt.isNull(_columnIndexOfActorDisplayName)) {
            _tmpActorDisplayName = null;
          } else {
            _tmpActorDisplayName = _stmt.getText(_columnIndexOfActorDisplayName);
          }
          _item.setActorDisplayName(_tmpActorDisplayName);
          final String _tmpMessage;
          if (_stmt.isNull(_columnIndexOfMessage)) {
            _tmpMessage = null;
          } else {
            _tmpMessage = _stmt.getText(_columnIndexOfMessage);
          }
          _item.setMessage(_tmpMessage);
          final Long _tmpParentId;
          if (_stmt.isNull(_columnIndexOfParentId)) {
            _tmpParentId = null;
          } else {
            _tmpParentId = _stmt.getLong(_columnIndexOfParentId);
          }
          _item.setParentId(_tmpParentId);
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
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_1);
          _item.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_2);
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
  public List<DeckComment> getCommentByLocalCardIdDirectly(final Long localCardId) {
    final String _sql = "SELECT * FROM DeckComment where objectId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (localCardId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localCardId);
        }
        final int _columnIndexOfObjectId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "objectId");
        final int _columnIndexOfActorType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorType");
        final int _columnIndexOfCreationDateTime = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "creationDateTime");
        final int _columnIndexOfActorId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorId");
        final int _columnIndexOfActorDisplayName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorDisplayName");
        final int _columnIndexOfMessage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "message");
        final int _columnIndexOfParentId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "parentId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<DeckComment> _result = new ArrayList<DeckComment>();
        while (_stmt.step()) {
          final DeckComment _item;
          _item = new DeckComment();
          final Long _tmpObjectId;
          if (_stmt.isNull(_columnIndexOfObjectId)) {
            _tmpObjectId = null;
          } else {
            _tmpObjectId = _stmt.getLong(_columnIndexOfObjectId);
          }
          _item.setObjectId(_tmpObjectId);
          final String _tmpActorType;
          if (_stmt.isNull(_columnIndexOfActorType)) {
            _tmpActorType = null;
          } else {
            _tmpActorType = _stmt.getText(_columnIndexOfActorType);
          }
          _item.setActorType(_tmpActorType);
          final Instant _tmpCreationDateTime;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfCreationDateTime)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfCreationDateTime);
          }
          _tmpCreationDateTime = DateTypeConverter.toInstant(_tmp);
          _item.setCreationDateTime(_tmpCreationDateTime);
          final String _tmpActorId;
          if (_stmt.isNull(_columnIndexOfActorId)) {
            _tmpActorId = null;
          } else {
            _tmpActorId = _stmt.getText(_columnIndexOfActorId);
          }
          _item.setActorId(_tmpActorId);
          final String _tmpActorDisplayName;
          if (_stmt.isNull(_columnIndexOfActorDisplayName)) {
            _tmpActorDisplayName = null;
          } else {
            _tmpActorDisplayName = _stmt.getText(_columnIndexOfActorDisplayName);
          }
          _item.setActorDisplayName(_tmpActorDisplayName);
          final String _tmpMessage;
          if (_stmt.isNull(_columnIndexOfMessage)) {
            _tmpMessage = null;
          } else {
            _tmpMessage = _stmt.getText(_columnIndexOfMessage);
          }
          _item.setMessage(_tmpMessage);
          final Long _tmpParentId;
          if (_stmt.isNull(_columnIndexOfParentId)) {
            _tmpParentId = null;
          } else {
            _tmpParentId = _stmt.getLong(_columnIndexOfParentId);
          }
          _item.setParentId(_tmpParentId);
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
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_1);
          _item.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_2);
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
  public LiveData<List<DeckComment>> getCommentByLocalCardId(final Long localCardId) {
    final String _sql = "SELECT * FROM DeckComment where objectId = ? order by creationDateTime desc";
    return __db.getInvalidationTracker().createLiveData(new String[] {"DeckComment"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (localCardId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localCardId);
        }
        final int _columnIndexOfObjectId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "objectId");
        final int _columnIndexOfActorType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorType");
        final int _columnIndexOfCreationDateTime = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "creationDateTime");
        final int _columnIndexOfActorId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorId");
        final int _columnIndexOfActorDisplayName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorDisplayName");
        final int _columnIndexOfMessage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "message");
        final int _columnIndexOfParentId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "parentId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<DeckComment> _result = new ArrayList<DeckComment>();
        while (_stmt.step()) {
          final DeckComment _item;
          _item = new DeckComment();
          final Long _tmpObjectId;
          if (_stmt.isNull(_columnIndexOfObjectId)) {
            _tmpObjectId = null;
          } else {
            _tmpObjectId = _stmt.getLong(_columnIndexOfObjectId);
          }
          _item.setObjectId(_tmpObjectId);
          final String _tmpActorType;
          if (_stmt.isNull(_columnIndexOfActorType)) {
            _tmpActorType = null;
          } else {
            _tmpActorType = _stmt.getText(_columnIndexOfActorType);
          }
          _item.setActorType(_tmpActorType);
          final Instant _tmpCreationDateTime;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfCreationDateTime)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfCreationDateTime);
          }
          _tmpCreationDateTime = DateTypeConverter.toInstant(_tmp);
          _item.setCreationDateTime(_tmpCreationDateTime);
          final String _tmpActorId;
          if (_stmt.isNull(_columnIndexOfActorId)) {
            _tmpActorId = null;
          } else {
            _tmpActorId = _stmt.getText(_columnIndexOfActorId);
          }
          _item.setActorId(_tmpActorId);
          final String _tmpActorDisplayName;
          if (_stmt.isNull(_columnIndexOfActorDisplayName)) {
            _tmpActorDisplayName = null;
          } else {
            _tmpActorDisplayName = _stmt.getText(_columnIndexOfActorDisplayName);
          }
          _item.setActorDisplayName(_tmpActorDisplayName);
          final String _tmpMessage;
          if (_stmt.isNull(_columnIndexOfMessage)) {
            _tmpMessage = null;
          } else {
            _tmpMessage = _stmt.getText(_columnIndexOfMessage);
          }
          _item.setMessage(_tmpMessage);
          final Long _tmpParentId;
          if (_stmt.isNull(_columnIndexOfParentId)) {
            _tmpParentId = null;
          } else {
            _tmpParentId = _stmt.getLong(_columnIndexOfParentId);
          }
          _item.setParentId(_tmpParentId);
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
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_1);
          _item.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_2);
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
  public LiveData<List<FullDeckComment>> getFullCommentByLocalCardId(final Long localCardId) {
    final String _sql = "SELECT * FROM DeckComment where objectId = ? order by creationDateTime desc, localId desc";
    return __db.getInvalidationTracker().createLiveData(new String[] {"DeckComment"}, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (localCardId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localCardId);
        }
        final int _columnIndexOfObjectId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "objectId");
        final int _columnIndexOfActorType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorType");
        final int _columnIndexOfCreationDateTime = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "creationDateTime");
        final int _columnIndexOfActorId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorId");
        final int _columnIndexOfActorDisplayName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "actorDisplayName");
        final int _columnIndexOfMessage = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "message");
        final int _columnIndexOfParentId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "parentId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<DeckComment> _collectionParent = new LongSparseArray<DeckComment>();
        while (_stmt.step()) {
          final Long _tmpKey;
          if (_stmt.isNull(_columnIndexOfParentId)) {
            _tmpKey = null;
          } else {
            _tmpKey = _stmt.getLong(_columnIndexOfParentId);
          }
          if (_tmpKey != null) {
            _collectionParent.put(_tmpKey, null);
          }
        }
        _stmt.reset();
        __fetchRelationshipDeckCommentAsitNiedermannNextcloudDeckModelOcsCommentDeckComment(_connection, _collectionParent);
        final List<FullDeckComment> _result = new ArrayList<FullDeckComment>();
        while (_stmt.step()) {
          final FullDeckComment _item;
          final DeckComment _tmpComment;
          if (!(_stmt.isNull(_columnIndexOfObjectId) && _stmt.isNull(_columnIndexOfActorType) && _stmt.isNull(_columnIndexOfCreationDateTime) && _stmt.isNull(_columnIndexOfActorId) && _stmt.isNull(_columnIndexOfActorDisplayName) && _stmt.isNull(_columnIndexOfMessage) && _stmt.isNull(_columnIndexOfParentId) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpComment = new DeckComment();
            final Long _tmpObjectId;
            if (_stmt.isNull(_columnIndexOfObjectId)) {
              _tmpObjectId = null;
            } else {
              _tmpObjectId = _stmt.getLong(_columnIndexOfObjectId);
            }
            _tmpComment.setObjectId(_tmpObjectId);
            final String _tmpActorType;
            if (_stmt.isNull(_columnIndexOfActorType)) {
              _tmpActorType = null;
            } else {
              _tmpActorType = _stmt.getText(_columnIndexOfActorType);
            }
            _tmpComment.setActorType(_tmpActorType);
            final Instant _tmpCreationDateTime;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfCreationDateTime)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfCreationDateTime);
            }
            _tmpCreationDateTime = DateTypeConverter.toInstant(_tmp);
            _tmpComment.setCreationDateTime(_tmpCreationDateTime);
            final String _tmpActorId;
            if (_stmt.isNull(_columnIndexOfActorId)) {
              _tmpActorId = null;
            } else {
              _tmpActorId = _stmt.getText(_columnIndexOfActorId);
            }
            _tmpComment.setActorId(_tmpActorId);
            final String _tmpActorDisplayName;
            if (_stmt.isNull(_columnIndexOfActorDisplayName)) {
              _tmpActorDisplayName = null;
            } else {
              _tmpActorDisplayName = _stmt.getText(_columnIndexOfActorDisplayName);
            }
            _tmpComment.setActorDisplayName(_tmpActorDisplayName);
            final String _tmpMessage;
            if (_stmt.isNull(_columnIndexOfMessage)) {
              _tmpMessage = null;
            } else {
              _tmpMessage = _stmt.getText(_columnIndexOfMessage);
            }
            _tmpComment.setMessage(_tmpMessage);
            final Long _tmpParentId;
            if (_stmt.isNull(_columnIndexOfParentId)) {
              _tmpParentId = null;
            } else {
              _tmpParentId = _stmt.getLong(_columnIndexOfParentId);
            }
            _tmpComment.setParentId(_tmpParentId);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpComment.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpComment.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpComment.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpComment.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_1);
            _tmpComment.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_2);
            _tmpComment.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpComment.setEtag(_tmpEtag);
          } else {
            _tmpComment = null;
          }
          final DeckComment _tmpParent;
          final Long _tmpKey_1;
          if (_stmt.isNull(_columnIndexOfParentId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfParentId);
          }
          if (_tmpKey_1 != null) {
            _tmpParent = _collectionParent.get(_tmpKey_1);
          } else {
            _tmpParent = null;
          }
          _item = new FullDeckComment();
          _item.setComment(_tmpComment);
          _item.parent = _tmpParent;
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Long getRemoteCommentIdForLocalIdDirectly(final Long localId) {
    final String _sql = "SELECT id FROM DeckComment where localId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (localId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localId);
        }
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
  public Long getLocalCommentIdForRemoteIdDirectly(final long accountId, final Long remoteId) {
    final String _sql = "SELECT localId FROM DeckComment where id = ? and accountId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (remoteId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, remoteId);
        }
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipDeckCommentAsitNiedermannNextcloudDeckModelOcsCommentDeckComment(
      @NonNull final SQLiteConnection _connection,
      @NonNull final LongSparseArray<DeckComment> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > 999) {
      RelationUtil.recursiveFetchLongSparseArray(_map, false, (_tmpMap) -> {
        __fetchRelationshipDeckCommentAsitNiedermannNextcloudDeckModelOcsCommentDeckComment(_connection, _tmpMap);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT `objectId`,`actorType`,`creationDateTime`,`actorId`,`actorDisplayName`,`message`,`parentId`,`localId`,`accountId`,`id`,`status`,`lastModified`,`lastModifiedLocal`,`etag` FROM `DeckComment` WHERE `localId` IN (");
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
      final int _columnIndexOfObjectId = 0;
      final int _columnIndexOfActorType = 1;
      final int _columnIndexOfCreationDateTime = 2;
      final int _columnIndexOfActorId = 3;
      final int _columnIndexOfActorDisplayName = 4;
      final int _columnIndexOfMessage = 5;
      final int _columnIndexOfParentId = 6;
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
          if (_map.containsKey(_tmpKey)) {
            final DeckComment _item_1;
            _item_1 = new DeckComment();
            final Long _tmpObjectId;
            if (_stmt.isNull(_columnIndexOfObjectId)) {
              _tmpObjectId = null;
            } else {
              _tmpObjectId = _stmt.getLong(_columnIndexOfObjectId);
            }
            _item_1.setObjectId(_tmpObjectId);
            final String _tmpActorType;
            if (_stmt.isNull(_columnIndexOfActorType)) {
              _tmpActorType = null;
            } else {
              _tmpActorType = _stmt.getText(_columnIndexOfActorType);
            }
            _item_1.setActorType(_tmpActorType);
            final Instant _tmpCreationDateTime;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfCreationDateTime)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfCreationDateTime);
            }
            _tmpCreationDateTime = DateTypeConverter.toInstant(_tmp);
            _item_1.setCreationDateTime(_tmpCreationDateTime);
            final String _tmpActorId;
            if (_stmt.isNull(_columnIndexOfActorId)) {
              _tmpActorId = null;
            } else {
              _tmpActorId = _stmt.getText(_columnIndexOfActorId);
            }
            _item_1.setActorId(_tmpActorId);
            final String _tmpActorDisplayName;
            if (_stmt.isNull(_columnIndexOfActorDisplayName)) {
              _tmpActorDisplayName = null;
            } else {
              _tmpActorDisplayName = _stmt.getText(_columnIndexOfActorDisplayName);
            }
            _item_1.setActorDisplayName(_tmpActorDisplayName);
            final String _tmpMessage;
            if (_stmt.isNull(_columnIndexOfMessage)) {
              _tmpMessage = null;
            } else {
              _tmpMessage = _stmt.getText(_columnIndexOfMessage);
            }
            _item_1.setMessage(_tmpMessage);
            final Long _tmpParentId;
            if (_stmt.isNull(_columnIndexOfParentId)) {
              _tmpParentId = null;
            } else {
              _tmpParentId = _stmt.getLong(_columnIndexOfParentId);
            }
            _item_1.setParentId(_tmpParentId);
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
            _map.put(_tmpKey, _item_1);
          }
        }
      }
    } finally {
      _stmt.close();
    }
  }
}
