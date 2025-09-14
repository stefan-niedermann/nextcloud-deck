package it.niedermann.nextcloud.deck.database.dao.projects;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.database.converter.DateTypeConverter;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectResource;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class OcsProjectResourceDao_Impl implements OcsProjectResourceDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<OcsProjectResource> __insertAdapterOfOcsProjectResource;

  private final EntityDeleteOrUpdateAdapter<OcsProjectResource> __deleteAdapterOfOcsProjectResource;

  private final EntityDeleteOrUpdateAdapter<OcsProjectResource> __updateAdapterOfOcsProjectResource;

  public OcsProjectResourceDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfOcsProjectResource = new EntityInsertAdapter<OcsProjectResource>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `OcsProjectResource` (`type`,`name`,`link`,`path`,`iconUrl`,`mimetype`,`previewAvailable`,`idString`,`projectId`,`localId`,`accountId`,`id`,`status`,`lastModified`,`lastModifiedLocal`,`etag`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final OcsProjectResource entity) {
        if (entity.getType() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getType());
        }
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getName());
        }
        if (entity.getLink() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getLink());
        }
        if (entity.getPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getPath());
        }
        if (entity.getIconUrl() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getIconUrl());
        }
        if (entity.getMimetype() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getMimetype());
        }
        final Integer _tmp = entity.getPreviewAvailable() == null ? null : (entity.getPreviewAvailable() ? 1 : 0);
        if (_tmp == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, _tmp);
        }
        if (entity.getIdString() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getIdString());
        }
        if (entity.getProjectId() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getProjectId());
        }
        if (entity.getLocalId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getLocalId());
        }
        statement.bindLong(11, entity.getAccountId());
        if (entity.getId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getId());
        }
        statement.bindLong(13, entity.getStatus());
        final Long _tmp_1 = DateTypeConverter.fromInstant(entity.getLastModified());
        if (_tmp_1 == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, _tmp_1);
        }
        final Long _tmp_2 = DateTypeConverter.fromInstant(entity.getLastModifiedLocal());
        if (_tmp_2 == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, _tmp_2);
        }
        if (entity.getEtag() == null) {
          statement.bindNull(16);
        } else {
          statement.bindText(16, entity.getEtag());
        }
      }
    };
    this.__deleteAdapterOfOcsProjectResource = new EntityDeleteOrUpdateAdapter<OcsProjectResource>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `OcsProjectResource` WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final OcsProjectResource entity) {
        if (entity.getLocalId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getLocalId());
        }
      }
    };
    this.__updateAdapterOfOcsProjectResource = new EntityDeleteOrUpdateAdapter<OcsProjectResource>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `OcsProjectResource` SET `type` = ?,`name` = ?,`link` = ?,`path` = ?,`iconUrl` = ?,`mimetype` = ?,`previewAvailable` = ?,`idString` = ?,`projectId` = ?,`localId` = ?,`accountId` = ?,`id` = ?,`status` = ?,`lastModified` = ?,`lastModifiedLocal` = ?,`etag` = ? WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          final OcsProjectResource entity) {
        if (entity.getType() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getType());
        }
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getName());
        }
        if (entity.getLink() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getLink());
        }
        if (entity.getPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getPath());
        }
        if (entity.getIconUrl() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getIconUrl());
        }
        if (entity.getMimetype() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getMimetype());
        }
        final Integer _tmp = entity.getPreviewAvailable() == null ? null : (entity.getPreviewAvailable() ? 1 : 0);
        if (_tmp == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, _tmp);
        }
        if (entity.getIdString() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getIdString());
        }
        if (entity.getProjectId() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getProjectId());
        }
        if (entity.getLocalId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getLocalId());
        }
        statement.bindLong(11, entity.getAccountId());
        if (entity.getId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getId());
        }
        statement.bindLong(13, entity.getStatus());
        final Long _tmp_1 = DateTypeConverter.fromInstant(entity.getLastModified());
        if (_tmp_1 == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, _tmp_1);
        }
        final Long _tmp_2 = DateTypeConverter.fromInstant(entity.getLastModifiedLocal());
        if (_tmp_2 == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, _tmp_2);
        }
        if (entity.getEtag() == null) {
          statement.bindNull(16);
        } else {
          statement.bindText(16, entity.getEtag());
        }
        if (entity.getLocalId() == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, entity.getLocalId());
        }
      }
    };
  }

  @Override
  public long insert(final OcsProjectResource entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfOcsProjectResource.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final OcsProjectResource... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfOcsProjectResource.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final OcsProjectResource... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfOcsProjectResource.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final OcsProjectResource... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfOcsProjectResource.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public LiveData<List<OcsProjectResource>> getResourcesByLocalProjectId(
      final Long localProjectId) {
    final String _sql = "select * from OcsProjectResource where projectId = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"OcsProjectResource"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (localProjectId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localProjectId);
        }
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfLink = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "link");
        final int _columnIndexOfPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "path");
        final int _columnIndexOfIconUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "iconUrl");
        final int _columnIndexOfMimetype = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "mimetype");
        final int _columnIndexOfPreviewAvailable = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "previewAvailable");
        final int _columnIndexOfIdString = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "idString");
        final int _columnIndexOfProjectId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "projectId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<OcsProjectResource> _result = new ArrayList<OcsProjectResource>();
        while (_stmt.step()) {
          final OcsProjectResource _item;
          _item = new OcsProjectResource();
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          _item.setType(_tmpType);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          _item.setName(_tmpName);
          final String _tmpLink;
          if (_stmt.isNull(_columnIndexOfLink)) {
            _tmpLink = null;
          } else {
            _tmpLink = _stmt.getText(_columnIndexOfLink);
          }
          _item.setLink(_tmpLink);
          final String _tmpPath;
          if (_stmt.isNull(_columnIndexOfPath)) {
            _tmpPath = null;
          } else {
            _tmpPath = _stmt.getText(_columnIndexOfPath);
          }
          _item.setPath(_tmpPath);
          final String _tmpIconUrl;
          if (_stmt.isNull(_columnIndexOfIconUrl)) {
            _tmpIconUrl = null;
          } else {
            _tmpIconUrl = _stmt.getText(_columnIndexOfIconUrl);
          }
          _item.setIconUrl(_tmpIconUrl);
          final String _tmpMimetype;
          if (_stmt.isNull(_columnIndexOfMimetype)) {
            _tmpMimetype = null;
          } else {
            _tmpMimetype = _stmt.getText(_columnIndexOfMimetype);
          }
          _item.setMimetype(_tmpMimetype);
          final Boolean _tmpPreviewAvailable;
          final Integer _tmp;
          if (_stmt.isNull(_columnIndexOfPreviewAvailable)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(_columnIndexOfPreviewAvailable));
          }
          _tmpPreviewAvailable = _tmp == null ? null : _tmp != 0;
          _item.setPreviewAvailable(_tmpPreviewAvailable);
          final String _tmpIdString;
          if (_stmt.isNull(_columnIndexOfIdString)) {
            _tmpIdString = null;
          } else {
            _tmpIdString = _stmt.getText(_columnIndexOfIdString);
          }
          _item.setIdString(_tmpIdString);
          final Long _tmpProjectId;
          if (_stmt.isNull(_columnIndexOfProjectId)) {
            _tmpProjectId = null;
          } else {
            _tmpProjectId = _stmt.getLong(_columnIndexOfProjectId);
          }
          _item.setProjectId(_tmpProjectId);
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
  public int countProjectResourcesInProjectDirectly(final Long localProjectId) {
    final String _sql = "select count(id) from OcsProjectResource where projectId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (localProjectId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localProjectId);
        }
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
  public LiveData<Integer> countProjectResourcesInProject(final Long localProjectId) {
    final String _sql = "select count(id) from OcsProjectResource where projectId = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"OcsProjectResource"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (localProjectId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localProjectId);
        }
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
  public void deleteByProjectId(final Long localProjectId) {
    final String _sql = "delete from OcsProjectResource where projectId = ?";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (localProjectId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localProjectId);
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
