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
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullStack;
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
public final class StackDao_Impl implements StackDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Stack> __insertAdapterOfStack;

  private final EntityDeleteOrUpdateAdapter<Stack> __deleteAdapterOfStack;

  private final EntityDeleteOrUpdateAdapter<Stack> __updateAdapterOfStack;

  public StackDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfStack = new EntityInsertAdapter<Stack>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `Stack` (`title`,`boardId`,`deletedAt`,`order`,`localId`,`accountId`,`id`,`status`,`lastModified`,`lastModifiedLocal`,`etag`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Stack entity) {
        if (entity.getTitle() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getTitle());
        }
        statement.bindLong(2, entity.getBoardId());
        final Long _tmp = DateTypeConverter.fromInstant(entity.getDeletedAt());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, _tmp);
        }
        statement.bindLong(4, entity.getOrder());
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
        final Long _tmp_1 = DateTypeConverter.fromInstant(entity.getLastModified());
        if (_tmp_1 == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, _tmp_1);
        }
        final Long _tmp_2 = DateTypeConverter.fromInstant(entity.getLastModifiedLocal());
        if (_tmp_2 == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, _tmp_2);
        }
        if (entity.getEtag() == null) {
          statement.bindNull(11);
        } else {
          statement.bindText(11, entity.getEtag());
        }
      }
    };
    this.__deleteAdapterOfStack = new EntityDeleteOrUpdateAdapter<Stack>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `Stack` WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Stack entity) {
        if (entity.getLocalId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getLocalId());
        }
      }
    };
    this.__updateAdapterOfStack = new EntityDeleteOrUpdateAdapter<Stack>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `Stack` SET `title` = ?,`boardId` = ?,`deletedAt` = ?,`order` = ?,`localId` = ?,`accountId` = ?,`id` = ?,`status` = ?,`lastModified` = ?,`lastModifiedLocal` = ?,`etag` = ? WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Stack entity) {
        if (entity.getTitle() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getTitle());
        }
        statement.bindLong(2, entity.getBoardId());
        final Long _tmp = DateTypeConverter.fromInstant(entity.getDeletedAt());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, _tmp);
        }
        statement.bindLong(4, entity.getOrder());
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
        final Long _tmp_1 = DateTypeConverter.fromInstant(entity.getLastModified());
        if (_tmp_1 == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, _tmp_1);
        }
        final Long _tmp_2 = DateTypeConverter.fromInstant(entity.getLastModifiedLocal());
        if (_tmp_2 == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, _tmp_2);
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
  public long insert(final Stack entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfStack.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final Stack... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfStack.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final Stack... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfStack.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final Stack... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfStack.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public LiveData<List<Stack>> getStacksForBoard(final long accountId, final long localBoardId) {
    final String _sql = "SELECT * FROM stack WHERE accountId = ? AND boardId = ? and status<>3 and (deletedAt is null or deletedAt = 0) order by `order` asc";
    return __db.getInvalidationTracker().createLiveData(new String[] {"stack"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localBoardId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfBoardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardId");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<Stack> _result = new ArrayList<Stack>();
        while (_stmt.step()) {
          final Stack _item;
          _item = new Stack();
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _item.setTitle(_tmpTitle);
          final long _tmpBoardId;
          _tmpBoardId = _stmt.getLong(_columnIndexOfBoardId);
          _item.setBoardId(_tmpBoardId);
          final Instant _tmpDeletedAt;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp);
          _item.setDeletedAt(_tmpDeletedAt);
          final int _tmpOrder;
          _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
          _item.setOrder(_tmpOrder);
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
  public LiveData<Stack> getStackByRemoteId(final long accountId, final long localBoardId,
      final long remoteId) {
    final String _sql = "SELECT * FROM stack WHERE accountId = ? and boardId = ? and id = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"stack"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localBoardId);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, remoteId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfBoardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardId");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final Stack _result;
        if (_stmt.step()) {
          _result = new Stack();
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _result.setTitle(_tmpTitle);
          final long _tmpBoardId;
          _tmpBoardId = _stmt.getLong(_columnIndexOfBoardId);
          _result.setBoardId(_tmpBoardId);
          final Instant _tmpDeletedAt;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp);
          _result.setDeletedAt(_tmpDeletedAt);
          final int _tmpOrder;
          _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
          _result.setOrder(_tmpOrder);
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
  public Stack getStackByLocalIdDirectly(final long localStackId) {
    final String _sql = "SELECT * FROM stack WHERE localId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localStackId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfBoardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardId");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final Stack _result;
        if (_stmt.step()) {
          _result = new Stack();
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _result.setTitle(_tmpTitle);
          final long _tmpBoardId;
          _tmpBoardId = _stmt.getLong(_columnIndexOfBoardId);
          _result.setBoardId(_tmpBoardId);
          final Instant _tmpDeletedAt;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp);
          _result.setDeletedAt(_tmpDeletedAt);
          final int _tmpOrder;
          _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
          _result.setOrder(_tmpOrder);
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
  public FullStack getFullStackByLocalIdDirectly(final long localStackId) {
    final String _sql = "SELECT * FROM stack WHERE localId = ?";
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localStackId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfBoardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardId");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Card>> _collectionCards = new LongSparseArray<ArrayList<Card>>();
        while (_stmt.step()) {
          final Long _tmpKey;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey = null;
          } else {
            _tmpKey = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey != null) {
            if (!_collectionCards.containsKey(_tmpKey)) {
              _collectionCards.put(_tmpKey, new ArrayList<Card>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipCardAsitNiedermannNextcloudDeckModelCard(_connection, _collectionCards);
        final FullStack _result;
        if (_stmt.step()) {
          final Stack _tmpStack;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfBoardId) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfOrder) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpStack = new Stack();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpStack.setTitle(_tmpTitle);
            final long _tmpBoardId;
            _tmpBoardId = _stmt.getLong(_columnIndexOfBoardId);
            _tmpStack.setBoardId(_tmpBoardId);
            final Instant _tmpDeletedAt;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp);
            _tmpStack.setDeletedAt(_tmpDeletedAt);
            final int _tmpOrder;
            _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
            _tmpStack.setOrder(_tmpOrder);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpStack.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpStack.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpStack.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpStack.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_1);
            _tmpStack.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_2);
            _tmpStack.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpStack.setEtag(_tmpEtag);
          } else {
            _tmpStack = null;
          }
          final ArrayList<Card> _tmpCardsCollection;
          final Long _tmpKey_1;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            _tmpCardsCollection = _collectionCards.get(_tmpKey_1);
          } else {
            _tmpCardsCollection = new ArrayList<Card>();
          }
          _result = new FullStack();
          _result.stack = _tmpStack;
          _result.cards = _tmpCardsCollection;
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
  public FullStack getFullStackByRemoteIdDirectly(final long accountId, final long localBoardId,
      final long remoteId) {
    final String _sql = "SELECT * FROM stack WHERE accountId = ? and boardId = ? and id = ?";
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localBoardId);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, remoteId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfBoardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardId");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Card>> _collectionCards = new LongSparseArray<ArrayList<Card>>();
        while (_stmt.step()) {
          final Long _tmpKey;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey = null;
          } else {
            _tmpKey = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey != null) {
            if (!_collectionCards.containsKey(_tmpKey)) {
              _collectionCards.put(_tmpKey, new ArrayList<Card>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipCardAsitNiedermannNextcloudDeckModelCard(_connection, _collectionCards);
        final FullStack _result;
        if (_stmt.step()) {
          final Stack _tmpStack;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfBoardId) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfOrder) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpStack = new Stack();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpStack.setTitle(_tmpTitle);
            final long _tmpBoardId;
            _tmpBoardId = _stmt.getLong(_columnIndexOfBoardId);
            _tmpStack.setBoardId(_tmpBoardId);
            final Instant _tmpDeletedAt;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp);
            _tmpStack.setDeletedAt(_tmpDeletedAt);
            final int _tmpOrder;
            _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
            _tmpStack.setOrder(_tmpOrder);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpStack.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpStack.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpStack.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpStack.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_1);
            _tmpStack.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_2);
            _tmpStack.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpStack.setEtag(_tmpEtag);
          } else {
            _tmpStack = null;
          }
          final ArrayList<Card> _tmpCardsCollection;
          final Long _tmpKey_1;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            _tmpCardsCollection = _collectionCards.get(_tmpKey_1);
          } else {
            _tmpCardsCollection = new ArrayList<Card>();
          }
          _result = new FullStack();
          _result.stack = _tmpStack;
          _result.cards = _tmpCardsCollection;
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
  public LiveData<FullStack> getFullStackByRemoteId(final long accountId, final long localBoardId,
      final long remoteId) {
    final String _sql = "SELECT * FROM stack WHERE accountId = ? and boardId = ? and id = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"Card",
        "stack"}, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localBoardId);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, remoteId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfBoardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardId");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Card>> _collectionCards = new LongSparseArray<ArrayList<Card>>();
        while (_stmt.step()) {
          final Long _tmpKey;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey = null;
          } else {
            _tmpKey = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey != null) {
            if (!_collectionCards.containsKey(_tmpKey)) {
              _collectionCards.put(_tmpKey, new ArrayList<Card>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipCardAsitNiedermannNextcloudDeckModelCard(_connection, _collectionCards);
        final FullStack _result;
        if (_stmt.step()) {
          final Stack _tmpStack;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfBoardId) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfOrder) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpStack = new Stack();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpStack.setTitle(_tmpTitle);
            final long _tmpBoardId;
            _tmpBoardId = _stmt.getLong(_columnIndexOfBoardId);
            _tmpStack.setBoardId(_tmpBoardId);
            final Instant _tmpDeletedAt;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp);
            _tmpStack.setDeletedAt(_tmpDeletedAt);
            final int _tmpOrder;
            _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
            _tmpStack.setOrder(_tmpOrder);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpStack.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpStack.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpStack.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpStack.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_1);
            _tmpStack.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_2);
            _tmpStack.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpStack.setEtag(_tmpEtag);
          } else {
            _tmpStack = null;
          }
          final ArrayList<Card> _tmpCardsCollection;
          final Long _tmpKey_1;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            _tmpCardsCollection = _collectionCards.get(_tmpKey_1);
          } else {
            _tmpCardsCollection = new ArrayList<Card>();
          }
          _result = new FullStack();
          _result.stack = _tmpStack;
          _result.cards = _tmpCardsCollection;
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
  public LiveData<FullStack> getFullStack(final long accountId, final long localId) {
    final String _sql = "SELECT * FROM stack WHERE accountId = ? and localId = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"Card",
        "stack"}, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfBoardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardId");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Card>> _collectionCards = new LongSparseArray<ArrayList<Card>>();
        while (_stmt.step()) {
          final Long _tmpKey;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey = null;
          } else {
            _tmpKey = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey != null) {
            if (!_collectionCards.containsKey(_tmpKey)) {
              _collectionCards.put(_tmpKey, new ArrayList<Card>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipCardAsitNiedermannNextcloudDeckModelCard(_connection, _collectionCards);
        final FullStack _result;
        if (_stmt.step()) {
          final Stack _tmpStack;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfBoardId) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfOrder) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpStack = new Stack();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpStack.setTitle(_tmpTitle);
            final long _tmpBoardId;
            _tmpBoardId = _stmt.getLong(_columnIndexOfBoardId);
            _tmpStack.setBoardId(_tmpBoardId);
            final Instant _tmpDeletedAt;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp);
            _tmpStack.setDeletedAt(_tmpDeletedAt);
            final int _tmpOrder;
            _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
            _tmpStack.setOrder(_tmpOrder);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpStack.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpStack.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpStack.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpStack.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_1);
            _tmpStack.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_2);
            _tmpStack.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpStack.setEtag(_tmpEtag);
          } else {
            _tmpStack = null;
          }
          final ArrayList<Card> _tmpCardsCollection;
          final Long _tmpKey_1;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            _tmpCardsCollection = _collectionCards.get(_tmpKey_1);
          } else {
            _tmpCardsCollection = new ArrayList<Card>();
          }
          _result = new FullStack();
          _result.stack = _tmpStack;
          _result.cards = _tmpCardsCollection;
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
  public List<Long> getLocalStackIdsByAccountIdDirectly(final long accountId) {
    final String _sql = "SELECT localId FROM stack WHERE accountId = ?";
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
  public List<Long> getLocalStackIdsByLocalBoardIdDirectly(final long localBoardId) {
    final String _sql = "SELECT localId FROM stack WHERE boardId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localBoardId);
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
  public List<FullStack> getLocallyChangedStacksForBoardDirectly(final long accountId,
      final long localBoardId) {
    final String _sql = "SELECT * FROM stack WHERE accountId = ? and boardId = ? and (status<>1 or id is null or lastModified <> lastModifiedLocal)";
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localBoardId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfBoardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardId");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Card>> _collectionCards = new LongSparseArray<ArrayList<Card>>();
        while (_stmt.step()) {
          final Long _tmpKey;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey = null;
          } else {
            _tmpKey = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey != null) {
            if (!_collectionCards.containsKey(_tmpKey)) {
              _collectionCards.put(_tmpKey, new ArrayList<Card>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipCardAsitNiedermannNextcloudDeckModelCard(_connection, _collectionCards);
        final List<FullStack> _result = new ArrayList<FullStack>();
        while (_stmt.step()) {
          final FullStack _item;
          final Stack _tmpStack;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfBoardId) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfOrder) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpStack = new Stack();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpStack.setTitle(_tmpTitle);
            final long _tmpBoardId;
            _tmpBoardId = _stmt.getLong(_columnIndexOfBoardId);
            _tmpStack.setBoardId(_tmpBoardId);
            final Instant _tmpDeletedAt;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp);
            _tmpStack.setDeletedAt(_tmpDeletedAt);
            final int _tmpOrder;
            _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
            _tmpStack.setOrder(_tmpOrder);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpStack.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpStack.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpStack.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpStack.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_1);
            _tmpStack.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_2);
            _tmpStack.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpStack.setEtag(_tmpEtag);
          } else {
            _tmpStack = null;
          }
          final ArrayList<Card> _tmpCardsCollection;
          final Long _tmpKey_1;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            _tmpCardsCollection = _collectionCards.get(_tmpKey_1);
          } else {
            _tmpCardsCollection = new ArrayList<Card>();
          }
          _item = new FullStack();
          _item.stack = _tmpStack;
          _item.cards = _tmpCardsCollection;
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<FullStack> getLocallyChangedStacksDirectly(final long accountId) {
    final String _sql = "SELECT * FROM stack WHERE accountId = ? and (status<>1 or id is null or lastModified <> lastModifiedLocal)";
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfBoardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardId");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Card>> _collectionCards = new LongSparseArray<ArrayList<Card>>();
        while (_stmt.step()) {
          final Long _tmpKey;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey = null;
          } else {
            _tmpKey = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey != null) {
            if (!_collectionCards.containsKey(_tmpKey)) {
              _collectionCards.put(_tmpKey, new ArrayList<Card>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipCardAsitNiedermannNextcloudDeckModelCard(_connection, _collectionCards);
        final List<FullStack> _result = new ArrayList<FullStack>();
        while (_stmt.step()) {
          final FullStack _item;
          final Stack _tmpStack;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfBoardId) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfOrder) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpStack = new Stack();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpStack.setTitle(_tmpTitle);
            final long _tmpBoardId;
            _tmpBoardId = _stmt.getLong(_columnIndexOfBoardId);
            _tmpStack.setBoardId(_tmpBoardId);
            final Instant _tmpDeletedAt;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp);
            _tmpStack.setDeletedAt(_tmpDeletedAt);
            final int _tmpOrder;
            _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
            _tmpStack.setOrder(_tmpOrder);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpStack.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpStack.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpStack.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpStack.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_1);
            _tmpStack.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_2);
            _tmpStack.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpStack.setEtag(_tmpEtag);
          } else {
            _tmpStack = null;
          }
          final ArrayList<Card> _tmpCardsCollection;
          final Long _tmpKey_1;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            _tmpCardsCollection = _collectionCards.get(_tmpKey_1);
          } else {
            _tmpCardsCollection = new ArrayList<Card>();
          }
          _item = new FullStack();
          _item.stack = _tmpStack;
          _item.cards = _tmpCardsCollection;
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<FullStack> getFullStacksForBoardDirectly(final long accountId,
      final long localBoardId) {
    final String _sql = "SELECT * FROM stack WHERE accountId = ? AND boardId = ? and status<>3 and (deletedAt is null or deletedAt = 0) order by `order` asc";
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localBoardId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfBoardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardId");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Card>> _collectionCards = new LongSparseArray<ArrayList<Card>>();
        while (_stmt.step()) {
          final Long _tmpKey;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey = null;
          } else {
            _tmpKey = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey != null) {
            if (!_collectionCards.containsKey(_tmpKey)) {
              _collectionCards.put(_tmpKey, new ArrayList<Card>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipCardAsitNiedermannNextcloudDeckModelCard(_connection, _collectionCards);
        final List<FullStack> _result = new ArrayList<FullStack>();
        while (_stmt.step()) {
          final FullStack _item;
          final Stack _tmpStack;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfBoardId) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfOrder) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpStack = new Stack();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpStack.setTitle(_tmpTitle);
            final long _tmpBoardId;
            _tmpBoardId = _stmt.getLong(_columnIndexOfBoardId);
            _tmpStack.setBoardId(_tmpBoardId);
            final Instant _tmpDeletedAt;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp);
            _tmpStack.setDeletedAt(_tmpDeletedAt);
            final int _tmpOrder;
            _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
            _tmpStack.setOrder(_tmpOrder);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpStack.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpStack.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpStack.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpStack.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_1);
            _tmpStack.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_2);
            _tmpStack.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpStack.setEtag(_tmpEtag);
          } else {
            _tmpStack = null;
          }
          final ArrayList<Card> _tmpCardsCollection;
          final Long _tmpKey_1;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            _tmpCardsCollection = _collectionCards.get(_tmpKey_1);
          } else {
            _tmpCardsCollection = new ArrayList<Card>();
          }
          _item = new FullStack();
          _item.stack = _tmpStack;
          _item.cards = _tmpCardsCollection;
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Long getLocalStackIdByRemoteStackIdDirectly(final long accountId, final Long stackId) {
    final String _sql = "SELECT localId FROM stack s WHERE accountId = ? and id = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        if (stackId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, stackId);
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
  public Integer getHighestStackOrderInBoard(final long localBoardId) {
    final String _sql = "SELECT coalesce(MAX(`order`), -1) FROM stack s WHERE boardId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localBoardId);
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
  public boolean isStackOnSharedBoardDirectly(final Long localStackId) {
    final String _sql = "SELECT exists(select 1 from Stack s join Board b on s.boardId = b.localId where s.localId = ? and exists(select 1 from AccessControl ac where ac.boardId = b.localId and status <> 3))";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (localStackId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localStackId);
        }
        final boolean _result;
        if (_stmt.step()) {
          final int _tmp;
          _tmp = (int) (_stmt.getLong(0));
          _result = _tmp != 0;
        } else {
          _result = false;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<Long> getLocalStackIdsInArchivedBoardsByAccountIdsDirectly(
      final List<Long> accountIds) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT s.localId FROM stack s join Board b on s.boardId = b.localId where b.archived <> 0 and b.accountId in (");
    final int _inputSize = accountIds == null ? 1 : accountIds.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (accountIds == null) {
          _stmt.bindNull(_argIndex);
        } else {
          for (Long _item : accountIds) {
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
  public List<Long> getAllIDs() {
    final String _sql = "SELECT s.localId FROM stack s";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipCardAsitNiedermannNextcloudDeckModelCard(
      @NonNull final SQLiteConnection _connection,
      @NonNull final LongSparseArray<ArrayList<Card>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > 999) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (_tmpMap) -> {
        __fetchRelationshipCardAsitNiedermannNextcloudDeckModelCard(_connection, _tmpMap);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT `title`,`description`,`stackId`,`type`,`createdAt`,`deletedAt`,`done`,`attachmentCount`,`userId`,`order`,`archived`,`dueDate`,`notified`,`overdue`,`commentsUnread`,`localId`,`accountId`,`id`,`status`,`lastModified`,`lastModifiedLocal`,`etag` FROM `Card` WHERE `stackId` IN (");
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
      final int _itemKeyIndex = SQLiteStatementUtil.getColumnIndex(_stmt, "stackId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _columnIndexOfTitle = 0;
      final int _columnIndexOfDescription = 1;
      final int _columnIndexOfStackId = 2;
      final int _columnIndexOfType = 3;
      final int _columnIndexOfCreatedAt = 4;
      final int _columnIndexOfDeletedAt = 5;
      final int _columnIndexOfDone = 6;
      final int _columnIndexOfAttachmentCount = 7;
      final int _columnIndexOfUserId = 8;
      final int _columnIndexOfOrder = 9;
      final int _columnIndexOfArchived = 10;
      final int _columnIndexOfDueDate = 11;
      final int _columnIndexOfNotified = 12;
      final int _columnIndexOfOverdue = 13;
      final int _columnIndexOfCommentsUnread = 14;
      final int _columnIndexOfLocalId = 15;
      final int _columnIndexOfAccountId = 16;
      final int _columnIndexOfId = 17;
      final int _columnIndexOfStatus = 18;
      final int _columnIndexOfLastModified = 19;
      final int _columnIndexOfLastModifiedLocal = 20;
      final int _columnIndexOfEtag = 21;
      while (_stmt.step()) {
        final long _tmpKey;
        _tmpKey = _stmt.getLong(_itemKeyIndex);
        final ArrayList<Card> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final Card _item_1;
          _item_1 = new Card();
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _item_1.setTitle(_tmpTitle);
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          _item_1.setDescription(_tmpDescription);
          final Long _tmpStackId;
          if (_stmt.isNull(_columnIndexOfStackId)) {
            _tmpStackId = null;
          } else {
            _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
          }
          _item_1.setStackId(_tmpStackId);
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          _item_1.setType(_tmpType);
          final Instant _tmpCreatedAt;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
          _item_1.setCreatedAt(_tmpCreatedAt);
          final Instant _tmpDeletedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
          _item_1.setDeletedAt(_tmpDeletedAt);
          final Instant _tmpDone;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfDone)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfDone);
          }
          _tmpDone = DateTypeConverter.toInstant(_tmp_2);
          _item_1.setDone(_tmpDone);
          final int _tmpAttachmentCount;
          _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
          _item_1.setAttachmentCount(_tmpAttachmentCount);
          final Long _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
          }
          _item_1.setUserId(_tmpUserId);
          final int _tmpOrder;
          _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
          _item_1.setOrder(_tmpOrder);
          final boolean _tmpArchived;
          final int _tmp_3;
          _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
          _tmpArchived = _tmp_3 != 0;
          _item_1.setArchived(_tmpArchived);
          final Instant _tmpDueDate;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfDueDate)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
          }
          _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
          _item_1.setDueDate(_tmpDueDate);
          final boolean _tmpNotified;
          final int _tmp_5;
          _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
          _tmpNotified = _tmp_5 != 0;
          _item_1.setNotified(_tmpNotified);
          final int _tmpOverdue;
          _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
          _item_1.setOverdue(_tmpOverdue);
          final int _tmpCommentsUnread;
          _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
          _item_1.setCommentsUnread(_tmpCommentsUnread);
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
          final Long _tmp_6;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_6 = null;
          } else {
            _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
          _item_1.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_7;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_7 = null;
          } else {
            _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
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
