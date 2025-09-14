package it.niedermann.nextcloud.deck.database.dao.widgets;

import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.SQLiteConnection;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullSingleCardWidgetModel;
import it.niedermann.nextcloud.deck.model.widget.singlecard.SingleCardWidgetModel;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.Unit;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class SingleCardWidgetModelDao_Impl implements SingleCardWidgetModelDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<SingleCardWidgetModel> __insertAdapterOfSingleCardWidgetModel;

  private final EntityDeleteOrUpdateAdapter<SingleCardWidgetModel> __deleteAdapterOfSingleCardWidgetModel;

  private final EntityDeleteOrUpdateAdapter<SingleCardWidgetModel> __updateAdapterOfSingleCardWidgetModel;

  public SingleCardWidgetModelDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfSingleCardWidgetModel = new EntityInsertAdapter<SingleCardWidgetModel>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `SingleCardWidgetModel` (`widgetId`,`accountId`,`boardId`,`cardId`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final SingleCardWidgetModel entity) {
        if (entity.getWidgetId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getWidgetId());
        }
        if (entity.getAccountId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getAccountId());
        }
        if (entity.getBoardId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getBoardId());
        }
        if (entity.getCardId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getCardId());
        }
      }
    };
    this.__deleteAdapterOfSingleCardWidgetModel = new EntityDeleteOrUpdateAdapter<SingleCardWidgetModel>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `SingleCardWidgetModel` WHERE `widgetId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final SingleCardWidgetModel entity) {
        if (entity.getWidgetId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getWidgetId());
        }
      }
    };
    this.__updateAdapterOfSingleCardWidgetModel = new EntityDeleteOrUpdateAdapter<SingleCardWidgetModel>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `SingleCardWidgetModel` SET `widgetId` = ?,`accountId` = ?,`boardId` = ?,`cardId` = ? WHERE `widgetId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final SingleCardWidgetModel entity) {
        if (entity.getWidgetId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getWidgetId());
        }
        if (entity.getAccountId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getAccountId());
        }
        if (entity.getBoardId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getBoardId());
        }
        if (entity.getCardId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getCardId());
        }
        if (entity.getWidgetId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getWidgetId());
        }
      }
    };
  }

  @Override
  public long insert(final SingleCardWidgetModel entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfSingleCardWidgetModel.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final SingleCardWidgetModel... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfSingleCardWidgetModel.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final SingleCardWidgetModel... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfSingleCardWidgetModel.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final SingleCardWidgetModel... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfSingleCardWidgetModel.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public FullSingleCardWidgetModel getFullCardByRemoteIdDirectly(final int widgetId) {
    final String _sql = "SELECT * FROM singlecardwidgetmodel WHERE widgetId = ?";
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, widgetId);
        final int _columnIndexOfWidgetId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "widgetId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfBoardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardId");
        final int _columnIndexOfCardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cardId");
        final LongSparseArray<Account> _collectionAccount = new LongSparseArray<Account>();
        while (_stmt.step()) {
          final Long _tmpKey;
          if (_stmt.isNull(_columnIndexOfAccountId)) {
            _tmpKey = null;
          } else {
            _tmpKey = _stmt.getLong(_columnIndexOfAccountId);
          }
          if (_tmpKey != null) {
            _collectionAccount.put(_tmpKey, null);
          }
        }
        _stmt.reset();
        __fetchRelationshipAccountAsitNiedermannNextcloudDeckModelAccount(_connection, _collectionAccount);
        final FullSingleCardWidgetModel _result;
        if (_stmt.step()) {
          final SingleCardWidgetModel _tmpModel;
          if (!(_stmt.isNull(_columnIndexOfWidgetId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfBoardId) && _stmt.isNull(_columnIndexOfCardId))) {
            _tmpModel = new SingleCardWidgetModel();
            final Integer _tmpWidgetId;
            if (_stmt.isNull(_columnIndexOfWidgetId)) {
              _tmpWidgetId = null;
            } else {
              _tmpWidgetId = (int) (_stmt.getLong(_columnIndexOfWidgetId));
            }
            _tmpModel.setWidgetId(_tmpWidgetId);
            final Long _tmpAccountId;
            if (_stmt.isNull(_columnIndexOfAccountId)) {
              _tmpAccountId = null;
            } else {
              _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            }
            _tmpModel.setAccountId(_tmpAccountId);
            final Long _tmpBoardId;
            if (_stmt.isNull(_columnIndexOfBoardId)) {
              _tmpBoardId = null;
            } else {
              _tmpBoardId = _stmt.getLong(_columnIndexOfBoardId);
            }
            _tmpModel.setBoardId(_tmpBoardId);
            final Long _tmpCardId;
            if (_stmt.isNull(_columnIndexOfCardId)) {
              _tmpCardId = null;
            } else {
              _tmpCardId = _stmt.getLong(_columnIndexOfCardId);
            }
            _tmpModel.setCardId(_tmpCardId);
          } else {
            _tmpModel = null;
          }
          final Account _tmpAccount;
          final Long _tmpKey_1;
          if (_stmt.isNull(_columnIndexOfAccountId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfAccountId);
          }
          if (_tmpKey_1 != null) {
            _tmpAccount = _collectionAccount.get(_tmpKey_1);
          } else {
            _tmpAccount = null;
          }
          _result = new FullSingleCardWidgetModel();
          _result.setModel(_tmpModel);
          _result.setAccount(_tmpAccount);
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
  public boolean containsCardLocalId(final Long cardLocalId) {
    final String _sql = "SELECT EXISTS (SELECT 1 FROM singlecardwidgetmodel WHERE cardId = ?)";
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (cardLocalId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, cardLocalId);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipAccountAsitNiedermannNextcloudDeckModelAccount(
      @NonNull final SQLiteConnection _connection, @NonNull final LongSparseArray<Account> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > 999) {
      RelationUtil.recursiveFetchLongSparseArray(_map, false, (_tmpMap) -> {
        __fetchRelationshipAccountAsitNiedermannNextcloudDeckModelAccount(_connection, _tmpMap);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT `id`,`name`,`userName`,`url`,`color`,`textColor`,`serverDeckVersion`,`maintenanceEnabled`,`etag`,`boardsEtag` FROM `Account` WHERE `id` IN (");
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
      final int _itemKeyIndex = SQLiteStatementUtil.getColumnIndex(_stmt, "id");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _columnIndexOfId = 0;
      final int _columnIndexOfName = 1;
      final int _columnIndexOfUserName = 2;
      final int _columnIndexOfUrl = 3;
      final int _columnIndexOfColor = 4;
      final int _columnIndexOfTextColor = 5;
      final int _columnIndexOfServerDeckVersion = 6;
      final int _columnIndexOfMaintenanceEnabled = 7;
      final int _columnIndexOfEtag = 8;
      final int _columnIndexOfBoardsEtag = 9;
      while (_stmt.step()) {
        final Long _tmpKey;
        if (_stmt.isNull(_itemKeyIndex)) {
          _tmpKey = null;
        } else {
          _tmpKey = _stmt.getLong(_itemKeyIndex);
        }
        if (_tmpKey != null) {
          if (_map.containsKey(_tmpKey)) {
            final Account _item_1;
            _item_1 = new Account();
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _item_1.setId(_tmpId);
            final String _tmpName;
            if (_stmt.isNull(_columnIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _stmt.getText(_columnIndexOfName);
            }
            _item_1.setName(_tmpName);
            final String _tmpUserName;
            if (_stmt.isNull(_columnIndexOfUserName)) {
              _tmpUserName = null;
            } else {
              _tmpUserName = _stmt.getText(_columnIndexOfUserName);
            }
            _item_1.setUserName(_tmpUserName);
            final String _tmpUrl;
            if (_stmt.isNull(_columnIndexOfUrl)) {
              _tmpUrl = null;
            } else {
              _tmpUrl = _stmt.getText(_columnIndexOfUrl);
            }
            _item_1.setUrl(_tmpUrl);
            final Integer _tmpColor;
            if (_stmt.isNull(_columnIndexOfColor)) {
              _tmpColor = null;
            } else {
              _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
            }
            _item_1.setColor(_tmpColor);
            final Integer _tmpTextColor;
            if (_stmt.isNull(_columnIndexOfTextColor)) {
              _tmpTextColor = null;
            } else {
              _tmpTextColor = (int) (_stmt.getLong(_columnIndexOfTextColor));
            }
            _item_1.setTextColor(_tmpTextColor);
            final String _tmpServerDeckVersion;
            if (_stmt.isNull(_columnIndexOfServerDeckVersion)) {
              _tmpServerDeckVersion = null;
            } else {
              _tmpServerDeckVersion = _stmt.getText(_columnIndexOfServerDeckVersion);
            }
            _item_1.setServerDeckVersion(_tmpServerDeckVersion);
            final boolean _tmpMaintenanceEnabled;
            final int _tmp;
            _tmp = (int) (_stmt.getLong(_columnIndexOfMaintenanceEnabled));
            _tmpMaintenanceEnabled = _tmp != 0;
            _item_1.setMaintenanceEnabled(_tmpMaintenanceEnabled);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _item_1.setEtag(_tmpEtag);
            final String _tmpBoardsEtag;
            if (_stmt.isNull(_columnIndexOfBoardsEtag)) {
              _tmpBoardsEtag = null;
            } else {
              _tmpBoardsEtag = _stmt.getText(_columnIndexOfBoardsEtag);
            }
            _item_1.setBoardsEtag(_tmpBoardsEtag);
            _map.put(_tmpKey, _item_1);
          }
        }
      }
    } finally {
      _stmt.close();
    }
  }
}
