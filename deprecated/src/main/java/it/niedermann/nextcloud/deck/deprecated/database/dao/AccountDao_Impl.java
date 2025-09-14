package it.niedermann.nextcloud.deck.database.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.processing.Generated;

import it.niedermann.nextcloud.deck.model.Account;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class AccountDao_Impl implements AccountDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Account> __insertAdapterOfAccount;

  private final EntityDeleteOrUpdateAdapter<Account> __deleteAdapterOfAccount;

  private final EntityDeleteOrUpdateAdapter<Account> __updateAdapterOfAccount;

  public AccountDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfAccount = new EntityInsertAdapter<Account>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `Account` (`id`,`name`,`userName`,`url`,`color`,`textColor`,`serverDeckVersion`,`maintenanceEnabled`,`etag`,`boardsEtag`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Account entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getName());
        }
        if (entity.getUserName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getUserName());
        }
        if (entity.getUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getUrl());
        }
        if (entity.getColor() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getColor());
        }
        if (entity.getTextColor() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getTextColor());
        }
        if (entity.getServerDeckVersion() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getServerDeckVersion());
        }
        final int _tmp = entity.isMaintenanceEnabled() ? 1 : 0;
        statement.bindLong(8, _tmp);
        if (entity.getEtag() == null) {
          statement.bindNull(9);
        } else {
          statement.bindText(9, entity.getEtag());
        }
        if (entity.getBoardsEtag() == null) {
          statement.bindNull(10);
        } else {
          statement.bindText(10, entity.getBoardsEtag());
        }
      }
    };
    this.__deleteAdapterOfAccount = new EntityDeleteOrUpdateAdapter<Account>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `Account` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Account entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfAccount = new EntityDeleteOrUpdateAdapter<Account>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `Account` SET `id` = ?,`name` = ?,`userName` = ?,`url` = ?,`color` = ?,`textColor` = ?,`serverDeckVersion` = ?,`maintenanceEnabled` = ?,`etag` = ?,`boardsEtag` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Account entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getName());
        }
        if (entity.getUserName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getUserName());
        }
        if (entity.getUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getUrl());
        }
        if (entity.getColor() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getColor());
        }
        if (entity.getTextColor() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getTextColor());
        }
        if (entity.getServerDeckVersion() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getServerDeckVersion());
        }
        final int _tmp = entity.isMaintenanceEnabled() ? 1 : 0;
        statement.bindLong(8, _tmp);
        if (entity.getEtag() == null) {
          statement.bindNull(9);
        } else {
          statement.bindText(9, entity.getEtag());
        }
        if (entity.getBoardsEtag() == null) {
          statement.bindNull(10);
        } else {
          statement.bindText(10, entity.getBoardsEtag());
        }
        if (entity.getId() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getId());
        }
      }
    };
  }

  @Override
  public long insert(final Account entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfAccount.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final Account... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfAccount.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final Account... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfAccount.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final Account... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfAccount.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public int countAccountsDirectly() {
    final String _sql = "SELECT count(*) FROM account";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
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
  public LiveData<Integer> countAccounts() {
    final String _sql = "SELECT count(*) FROM account";
    return __db.getInvalidationTracker().createLiveData(new String[] {"account"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
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
  public Account getAccountByIdDirectly(final long id) {
    final String _sql = "SELECT * from account where id = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfUserName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userName");
        final int _columnIndexOfUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "url");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfTextColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "textColor");
        final int _columnIndexOfServerDeckVersion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "serverDeckVersion");
        final int _columnIndexOfMaintenanceEnabled = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "maintenanceEnabled");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final int _columnIndexOfBoardsEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardsEtag");
        final Account _result;
        if (_stmt.step()) {
          _result = new Account();
          final Long _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId);
          }
          _result.setId(_tmpId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          _result.setName(_tmpName);
          final String _tmpUserName;
          if (_stmt.isNull(_columnIndexOfUserName)) {
            _tmpUserName = null;
          } else {
            _tmpUserName = _stmt.getText(_columnIndexOfUserName);
          }
          _result.setUserName(_tmpUserName);
          final String _tmpUrl;
          if (_stmt.isNull(_columnIndexOfUrl)) {
            _tmpUrl = null;
          } else {
            _tmpUrl = _stmt.getText(_columnIndexOfUrl);
          }
          _result.setUrl(_tmpUrl);
          final Integer _tmpColor;
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
          }
          _result.setColor(_tmpColor);
          final Integer _tmpTextColor;
          if (_stmt.isNull(_columnIndexOfTextColor)) {
            _tmpTextColor = null;
          } else {
            _tmpTextColor = (int) (_stmt.getLong(_columnIndexOfTextColor));
          }
          _result.setTextColor(_tmpTextColor);
          final String _tmpServerDeckVersion;
          if (_stmt.isNull(_columnIndexOfServerDeckVersion)) {
            _tmpServerDeckVersion = null;
          } else {
            _tmpServerDeckVersion = _stmt.getText(_columnIndexOfServerDeckVersion);
          }
          _result.setServerDeckVersion(_tmpServerDeckVersion);
          final boolean _tmpMaintenanceEnabled;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfMaintenanceEnabled));
          _tmpMaintenanceEnabled = _tmp != 0;
          _result.setMaintenanceEnabled(_tmpMaintenanceEnabled);
          final String _tmpEtag;
          if (_stmt.isNull(_columnIndexOfEtag)) {
            _tmpEtag = null;
          } else {
            _tmpEtag = _stmt.getText(_columnIndexOfEtag);
          }
          _result.setEtag(_tmpEtag);
          final String _tmpBoardsEtag;
          if (_stmt.isNull(_columnIndexOfBoardsEtag)) {
            _tmpBoardsEtag = null;
          } else {
            _tmpBoardsEtag = _stmt.getText(_columnIndexOfBoardsEtag);
          }
          _result.setBoardsEtag(_tmpBoardsEtag);
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
  public LiveData<Account> getAccountById(final long id) {
    final String _sql = "SELECT * from account where id = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"account"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfUserName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userName");
        final int _columnIndexOfUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "url");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfTextColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "textColor");
        final int _columnIndexOfServerDeckVersion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "serverDeckVersion");
        final int _columnIndexOfMaintenanceEnabled = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "maintenanceEnabled");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final int _columnIndexOfBoardsEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardsEtag");
        final Account _result;
        if (_stmt.step()) {
          _result = new Account();
          final Long _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId);
          }
          _result.setId(_tmpId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          _result.setName(_tmpName);
          final String _tmpUserName;
          if (_stmt.isNull(_columnIndexOfUserName)) {
            _tmpUserName = null;
          } else {
            _tmpUserName = _stmt.getText(_columnIndexOfUserName);
          }
          _result.setUserName(_tmpUserName);
          final String _tmpUrl;
          if (_stmt.isNull(_columnIndexOfUrl)) {
            _tmpUrl = null;
          } else {
            _tmpUrl = _stmt.getText(_columnIndexOfUrl);
          }
          _result.setUrl(_tmpUrl);
          final Integer _tmpColor;
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
          }
          _result.setColor(_tmpColor);
          final Integer _tmpTextColor;
          if (_stmt.isNull(_columnIndexOfTextColor)) {
            _tmpTextColor = null;
          } else {
            _tmpTextColor = (int) (_stmt.getLong(_columnIndexOfTextColor));
          }
          _result.setTextColor(_tmpTextColor);
          final String _tmpServerDeckVersion;
          if (_stmt.isNull(_columnIndexOfServerDeckVersion)) {
            _tmpServerDeckVersion = null;
          } else {
            _tmpServerDeckVersion = _stmt.getText(_columnIndexOfServerDeckVersion);
          }
          _result.setServerDeckVersion(_tmpServerDeckVersion);
          final boolean _tmpMaintenanceEnabled;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfMaintenanceEnabled));
          _tmpMaintenanceEnabled = _tmp != 0;
          _result.setMaintenanceEnabled(_tmpMaintenanceEnabled);
          final String _tmpEtag;
          if (_stmt.isNull(_columnIndexOfEtag)) {
            _tmpEtag = null;
          } else {
            _tmpEtag = _stmt.getText(_columnIndexOfEtag);
          }
          _result.setEtag(_tmpEtag);
          final String _tmpBoardsEtag;
          if (_stmt.isNull(_columnIndexOfBoardsEtag)) {
            _tmpBoardsEtag = null;
          } else {
            _tmpBoardsEtag = _stmt.getText(_columnIndexOfBoardsEtag);
          }
          _result.setBoardsEtag(_tmpBoardsEtag);
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
  public LiveData<Account> getAccountByName(final String name) {
    final String _sql = "SELECT * from account where name = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"account"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (name == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, name);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfUserName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userName");
        final int _columnIndexOfUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "url");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfTextColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "textColor");
        final int _columnIndexOfServerDeckVersion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "serverDeckVersion");
        final int _columnIndexOfMaintenanceEnabled = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "maintenanceEnabled");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final int _columnIndexOfBoardsEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardsEtag");
        final Account _result;
        if (_stmt.step()) {
          _result = new Account();
          final Long _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId);
          }
          _result.setId(_tmpId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          _result.setName(_tmpName);
          final String _tmpUserName;
          if (_stmt.isNull(_columnIndexOfUserName)) {
            _tmpUserName = null;
          } else {
            _tmpUserName = _stmt.getText(_columnIndexOfUserName);
          }
          _result.setUserName(_tmpUserName);
          final String _tmpUrl;
          if (_stmt.isNull(_columnIndexOfUrl)) {
            _tmpUrl = null;
          } else {
            _tmpUrl = _stmt.getText(_columnIndexOfUrl);
          }
          _result.setUrl(_tmpUrl);
          final Integer _tmpColor;
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
          }
          _result.setColor(_tmpColor);
          final Integer _tmpTextColor;
          if (_stmt.isNull(_columnIndexOfTextColor)) {
            _tmpTextColor = null;
          } else {
            _tmpTextColor = (int) (_stmt.getLong(_columnIndexOfTextColor));
          }
          _result.setTextColor(_tmpTextColor);
          final String _tmpServerDeckVersion;
          if (_stmt.isNull(_columnIndexOfServerDeckVersion)) {
            _tmpServerDeckVersion = null;
          } else {
            _tmpServerDeckVersion = _stmt.getText(_columnIndexOfServerDeckVersion);
          }
          _result.setServerDeckVersion(_tmpServerDeckVersion);
          final boolean _tmpMaintenanceEnabled;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfMaintenanceEnabled));
          _tmpMaintenanceEnabled = _tmp != 0;
          _result.setMaintenanceEnabled(_tmpMaintenanceEnabled);
          final String _tmpEtag;
          if (_stmt.isNull(_columnIndexOfEtag)) {
            _tmpEtag = null;
          } else {
            _tmpEtag = _stmt.getText(_columnIndexOfEtag);
          }
          _result.setEtag(_tmpEtag);
          final String _tmpBoardsEtag;
          if (_stmt.isNull(_columnIndexOfBoardsEtag)) {
            _tmpBoardsEtag = null;
          } else {
            _tmpBoardsEtag = _stmt.getText(_columnIndexOfBoardsEtag);
          }
          _result.setBoardsEtag(_tmpBoardsEtag);
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
  public Account getAccountByNameDirectly(final String name) {
    final String _sql = "SELECT * from account where name = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (name == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, name);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfUserName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userName");
        final int _columnIndexOfUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "url");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfTextColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "textColor");
        final int _columnIndexOfServerDeckVersion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "serverDeckVersion");
        final int _columnIndexOfMaintenanceEnabled = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "maintenanceEnabled");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final int _columnIndexOfBoardsEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardsEtag");
        final Account _result;
        if (_stmt.step()) {
          _result = new Account();
          final Long _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId);
          }
          _result.setId(_tmpId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          _result.setName(_tmpName);
          final String _tmpUserName;
          if (_stmt.isNull(_columnIndexOfUserName)) {
            _tmpUserName = null;
          } else {
            _tmpUserName = _stmt.getText(_columnIndexOfUserName);
          }
          _result.setUserName(_tmpUserName);
          final String _tmpUrl;
          if (_stmt.isNull(_columnIndexOfUrl)) {
            _tmpUrl = null;
          } else {
            _tmpUrl = _stmt.getText(_columnIndexOfUrl);
          }
          _result.setUrl(_tmpUrl);
          final Integer _tmpColor;
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
          }
          _result.setColor(_tmpColor);
          final Integer _tmpTextColor;
          if (_stmt.isNull(_columnIndexOfTextColor)) {
            _tmpTextColor = null;
          } else {
            _tmpTextColor = (int) (_stmt.getLong(_columnIndexOfTextColor));
          }
          _result.setTextColor(_tmpTextColor);
          final String _tmpServerDeckVersion;
          if (_stmt.isNull(_columnIndexOfServerDeckVersion)) {
            _tmpServerDeckVersion = null;
          } else {
            _tmpServerDeckVersion = _stmt.getText(_columnIndexOfServerDeckVersion);
          }
          _result.setServerDeckVersion(_tmpServerDeckVersion);
          final boolean _tmpMaintenanceEnabled;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfMaintenanceEnabled));
          _tmpMaintenanceEnabled = _tmp != 0;
          _result.setMaintenanceEnabled(_tmpMaintenanceEnabled);
          final String _tmpEtag;
          if (_stmt.isNull(_columnIndexOfEtag)) {
            _tmpEtag = null;
          } else {
            _tmpEtag = _stmt.getText(_columnIndexOfEtag);
          }
          _result.setEtag(_tmpEtag);
          final String _tmpBoardsEtag;
          if (_stmt.isNull(_columnIndexOfBoardsEtag)) {
            _tmpBoardsEtag = null;
          } else {
            _tmpBoardsEtag = _stmt.getText(_columnIndexOfBoardsEtag);
          }
          _result.setBoardsEtag(_tmpBoardsEtag);
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
  public LiveData<List<Account>> getAllAccounts() {
    final String _sql = "SELECT * from account";
    return __db.getInvalidationTracker().createLiveData(new String[] {"account"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfUserName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userName");
        final int _columnIndexOfUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "url");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfTextColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "textColor");
        final int _columnIndexOfServerDeckVersion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "serverDeckVersion");
        final int _columnIndexOfMaintenanceEnabled = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "maintenanceEnabled");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final int _columnIndexOfBoardsEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardsEtag");
        final List<Account> _result = new ArrayList<Account>();
        while (_stmt.step()) {
          final Account _item;
          _item = new Account();
          final Long _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId);
          }
          _item.setId(_tmpId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          _item.setName(_tmpName);
          final String _tmpUserName;
          if (_stmt.isNull(_columnIndexOfUserName)) {
            _tmpUserName = null;
          } else {
            _tmpUserName = _stmt.getText(_columnIndexOfUserName);
          }
          _item.setUserName(_tmpUserName);
          final String _tmpUrl;
          if (_stmt.isNull(_columnIndexOfUrl)) {
            _tmpUrl = null;
          } else {
            _tmpUrl = _stmt.getText(_columnIndexOfUrl);
          }
          _item.setUrl(_tmpUrl);
          final Integer _tmpColor;
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
          }
          _item.setColor(_tmpColor);
          final Integer _tmpTextColor;
          if (_stmt.isNull(_columnIndexOfTextColor)) {
            _tmpTextColor = null;
          } else {
            _tmpTextColor = (int) (_stmt.getLong(_columnIndexOfTextColor));
          }
          _item.setTextColor(_tmpTextColor);
          final String _tmpServerDeckVersion;
          if (_stmt.isNull(_columnIndexOfServerDeckVersion)) {
            _tmpServerDeckVersion = null;
          } else {
            _tmpServerDeckVersion = _stmt.getText(_columnIndexOfServerDeckVersion);
          }
          _item.setServerDeckVersion(_tmpServerDeckVersion);
          final boolean _tmpMaintenanceEnabled;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfMaintenanceEnabled));
          _tmpMaintenanceEnabled = _tmp != 0;
          _item.setMaintenanceEnabled(_tmpMaintenanceEnabled);
          final String _tmpEtag;
          if (_stmt.isNull(_columnIndexOfEtag)) {
            _tmpEtag = null;
          } else {
            _tmpEtag = _stmt.getText(_columnIndexOfEtag);
          }
          _item.setEtag(_tmpEtag);
          final String _tmpBoardsEtag;
          if (_stmt.isNull(_columnIndexOfBoardsEtag)) {
            _tmpBoardsEtag = null;
          } else {
            _tmpBoardsEtag = _stmt.getText(_columnIndexOfBoardsEtag);
          }
          _item.setBoardsEtag(_tmpBoardsEtag);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<Account> getAllAccountsDirectly() {
    final String _sql = "SELECT * from account";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfUserName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userName");
        final int _columnIndexOfUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "url");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfTextColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "textColor");
        final int _columnIndexOfServerDeckVersion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "serverDeckVersion");
        final int _columnIndexOfMaintenanceEnabled = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "maintenanceEnabled");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final int _columnIndexOfBoardsEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardsEtag");
        final List<Account> _result = new ArrayList<Account>();
        while (_stmt.step()) {
          final Account _item;
          _item = new Account();
          final Long _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId);
          }
          _item.setId(_tmpId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          _item.setName(_tmpName);
          final String _tmpUserName;
          if (_stmt.isNull(_columnIndexOfUserName)) {
            _tmpUserName = null;
          } else {
            _tmpUserName = _stmt.getText(_columnIndexOfUserName);
          }
          _item.setUserName(_tmpUserName);
          final String _tmpUrl;
          if (_stmt.isNull(_columnIndexOfUrl)) {
            _tmpUrl = null;
          } else {
            _tmpUrl = _stmt.getText(_columnIndexOfUrl);
          }
          _item.setUrl(_tmpUrl);
          final Integer _tmpColor;
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
          }
          _item.setColor(_tmpColor);
          final Integer _tmpTextColor;
          if (_stmt.isNull(_columnIndexOfTextColor)) {
            _tmpTextColor = null;
          } else {
            _tmpTextColor = (int) (_stmt.getLong(_columnIndexOfTextColor));
          }
          _item.setTextColor(_tmpTextColor);
          final String _tmpServerDeckVersion;
          if (_stmt.isNull(_columnIndexOfServerDeckVersion)) {
            _tmpServerDeckVersion = null;
          } else {
            _tmpServerDeckVersion = _stmt.getText(_columnIndexOfServerDeckVersion);
          }
          _item.setServerDeckVersion(_tmpServerDeckVersion);
          final boolean _tmpMaintenanceEnabled;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfMaintenanceEnabled));
          _tmpMaintenanceEnabled = _tmp != 0;
          _item.setMaintenanceEnabled(_tmpMaintenanceEnabled);
          final String _tmpEtag;
          if (_stmt.isNull(_columnIndexOfEtag)) {
            _tmpEtag = null;
          } else {
            _tmpEtag = _stmt.getText(_columnIndexOfEtag);
          }
          _item.setEtag(_tmpEtag);
          final String _tmpBoardsEtag;
          if (_stmt.isNull(_columnIndexOfBoardsEtag)) {
            _tmpBoardsEtag = null;
          } else {
            _tmpBoardsEtag = _stmt.getText(_columnIndexOfBoardsEtag);
          }
          _item.setBoardsEtag(_tmpBoardsEtag);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<Account>> readAccountsForHostWithReadAccessToBoard(final String hostLike,
      final long boardRemoteId) {
    final String _sql = "SELECT * from account a where a.url like ? and exists (select 1 from board b where b.id = ? and a.id = b.accountId)";
    return __db.getInvalidationTracker().createLiveData(new String[] {"account",
        "board"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (hostLike == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, hostLike);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, boardRemoteId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfUserName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userName");
        final int _columnIndexOfUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "url");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfTextColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "textColor");
        final int _columnIndexOfServerDeckVersion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "serverDeckVersion");
        final int _columnIndexOfMaintenanceEnabled = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "maintenanceEnabled");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final int _columnIndexOfBoardsEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardsEtag");
        final List<Account> _result = new ArrayList<Account>();
        while (_stmt.step()) {
          final Account _item;
          _item = new Account();
          final Long _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId);
          }
          _item.setId(_tmpId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          _item.setName(_tmpName);
          final String _tmpUserName;
          if (_stmt.isNull(_columnIndexOfUserName)) {
            _tmpUserName = null;
          } else {
            _tmpUserName = _stmt.getText(_columnIndexOfUserName);
          }
          _item.setUserName(_tmpUserName);
          final String _tmpUrl;
          if (_stmt.isNull(_columnIndexOfUrl)) {
            _tmpUrl = null;
          } else {
            _tmpUrl = _stmt.getText(_columnIndexOfUrl);
          }
          _item.setUrl(_tmpUrl);
          final Integer _tmpColor;
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
          }
          _item.setColor(_tmpColor);
          final Integer _tmpTextColor;
          if (_stmt.isNull(_columnIndexOfTextColor)) {
            _tmpTextColor = null;
          } else {
            _tmpTextColor = (int) (_stmt.getLong(_columnIndexOfTextColor));
          }
          _item.setTextColor(_tmpTextColor);
          final String _tmpServerDeckVersion;
          if (_stmt.isNull(_columnIndexOfServerDeckVersion)) {
            _tmpServerDeckVersion = null;
          } else {
            _tmpServerDeckVersion = _stmt.getText(_columnIndexOfServerDeckVersion);
          }
          _item.setServerDeckVersion(_tmpServerDeckVersion);
          final boolean _tmpMaintenanceEnabled;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfMaintenanceEnabled));
          _tmpMaintenanceEnabled = _tmp != 0;
          _item.setMaintenanceEnabled(_tmpMaintenanceEnabled);
          final String _tmpEtag;
          if (_stmt.isNull(_columnIndexOfEtag)) {
            _tmpEtag = null;
          } else {
            _tmpEtag = _stmt.getText(_columnIndexOfEtag);
          }
          _item.setEtag(_tmpEtag);
          final String _tmpBoardsEtag;
          if (_stmt.isNull(_columnIndexOfBoardsEtag)) {
            _tmpBoardsEtag = null;
          } else {
            _tmpBoardsEtag = _stmt.getText(_columnIndexOfBoardsEtag);
          }
          _item.setBoardsEtag(_tmpBoardsEtag);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<Account> readAccountsForHostWithReadAccessToBoardDirectly(final String hostLike,
      final long boardRemoteId) {
    final String _sql = "SELECT * from account a where a.url like ? and exists (select 1 from board b where b.id = ? and a.id = b.accountId)";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (hostLike == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, hostLike);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, boardRemoteId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfUserName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userName");
        final int _columnIndexOfUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "url");
        final int _columnIndexOfColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "color");
        final int _columnIndexOfTextColor = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "textColor");
        final int _columnIndexOfServerDeckVersion = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "serverDeckVersion");
        final int _columnIndexOfMaintenanceEnabled = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "maintenanceEnabled");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final int _columnIndexOfBoardsEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "boardsEtag");
        final List<Account> _result = new ArrayList<Account>();
        while (_stmt.step()) {
          final Account _item;
          _item = new Account();
          final Long _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId);
          }
          _item.setId(_tmpId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          _item.setName(_tmpName);
          final String _tmpUserName;
          if (_stmt.isNull(_columnIndexOfUserName)) {
            _tmpUserName = null;
          } else {
            _tmpUserName = _stmt.getText(_columnIndexOfUserName);
          }
          _item.setUserName(_tmpUserName);
          final String _tmpUrl;
          if (_stmt.isNull(_columnIndexOfUrl)) {
            _tmpUrl = null;
          } else {
            _tmpUrl = _stmt.getText(_columnIndexOfUrl);
          }
          _item.setUrl(_tmpUrl);
          final Integer _tmpColor;
          if (_stmt.isNull(_columnIndexOfColor)) {
            _tmpColor = null;
          } else {
            _tmpColor = (int) (_stmt.getLong(_columnIndexOfColor));
          }
          _item.setColor(_tmpColor);
          final Integer _tmpTextColor;
          if (_stmt.isNull(_columnIndexOfTextColor)) {
            _tmpTextColor = null;
          } else {
            _tmpTextColor = (int) (_stmt.getLong(_columnIndexOfTextColor));
          }
          _item.setTextColor(_tmpTextColor);
          final String _tmpServerDeckVersion;
          if (_stmt.isNull(_columnIndexOfServerDeckVersion)) {
            _tmpServerDeckVersion = null;
          } else {
            _tmpServerDeckVersion = _stmt.getText(_columnIndexOfServerDeckVersion);
          }
          _item.setServerDeckVersion(_tmpServerDeckVersion);
          final boolean _tmpMaintenanceEnabled;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfMaintenanceEnabled));
          _tmpMaintenanceEnabled = _tmp != 0;
          _item.setMaintenanceEnabled(_tmpMaintenanceEnabled);
          final String _tmpEtag;
          if (_stmt.isNull(_columnIndexOfEtag)) {
            _tmpEtag = null;
          } else {
            _tmpEtag = _stmt.getText(_columnIndexOfEtag);
          }
          _item.setEtag(_tmpEtag);
          final String _tmpBoardsEtag;
          if (_stmt.isNull(_columnIndexOfBoardsEtag)) {
            _tmpBoardsEtag = null;
          } else {
            _tmpBoardsEtag = _stmt.getText(_columnIndexOfBoardsEtag);
          }
          _item.setBoardsEtag(_tmpBoardsEtag);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<Integer> getAccountColor(final long accountId) {
    final String _sql = "SELECT a.color FROM account a where a.id = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"account"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
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
  public Integer getAccountColorDirectly(final long accountId) {
    final String _sql = "SELECT a.color FROM account a where a.id = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
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
  public void deleteById(final long id) {
    final String _sql = "DELETE from account where id = ?";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
