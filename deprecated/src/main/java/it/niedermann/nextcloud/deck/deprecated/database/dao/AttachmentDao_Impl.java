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
import it.niedermann.nextcloud.deck.database.converter.EnumConverter;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.enums.EAttachmentType;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class AttachmentDao_Impl implements AttachmentDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Attachment> __insertAdapterOfAttachment;

  private final EntityDeleteOrUpdateAdapter<Attachment> __deleteAdapterOfAttachment;

  private final EntityDeleteOrUpdateAdapter<Attachment> __updateAdapterOfAttachment;

  public AttachmentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfAttachment = new EntityInsertAdapter<Attachment>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `Attachment` (`cardId`,`type`,`data`,`createdAt`,`createdBy`,`deletedAt`,`filesize`,`mimetype`,`dirname`,`basename`,`extension`,`filename`,`localPath`,`fileId`,`localId`,`accountId`,`id`,`status`,`lastModified`,`lastModifiedLocal`,`etag`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Attachment entity) {
        statement.bindLong(1, entity.getCardId());
        final String _tmp = EnumConverter.fromEAttachmentType(entity.getType());
        if (_tmp == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, _tmp);
        }
        if (entity.getData() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getData());
        }
        final Long _tmp_1 = DateTypeConverter.fromInstant(entity.getCreatedAt());
        if (_tmp_1 == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, _tmp_1);
        }
        if (entity.getCreatedBy() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getCreatedBy());
        }
        final Long _tmp_2 = DateTypeConverter.fromInstant(entity.getDeletedAt());
        if (_tmp_2 == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, _tmp_2);
        }
        statement.bindLong(7, entity.getFilesize());
        if (entity.getMimetype() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getMimetype());
        }
        if (entity.getDirname() == null) {
          statement.bindNull(9);
        } else {
          statement.bindText(9, entity.getDirname());
        }
        if (entity.getBasename() == null) {
          statement.bindNull(10);
        } else {
          statement.bindText(10, entity.getBasename());
        }
        if (entity.getExtension() == null) {
          statement.bindNull(11);
        } else {
          statement.bindText(11, entity.getExtension());
        }
        if (entity.getFilename() == null) {
          statement.bindNull(12);
        } else {
          statement.bindText(12, entity.getFilename());
        }
        if (entity.getLocalPath() == null) {
          statement.bindNull(13);
        } else {
          statement.bindText(13, entity.getLocalPath());
        }
        if (entity.getFileId() == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, entity.getFileId());
        }
        if (entity.getLocalId() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getLocalId());
        }
        statement.bindLong(16, entity.getAccountId());
        if (entity.getId() == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, entity.getId());
        }
        statement.bindLong(18, entity.getStatus());
        final Long _tmp_3 = DateTypeConverter.fromInstant(entity.getLastModified());
        if (_tmp_3 == null) {
          statement.bindNull(19);
        } else {
          statement.bindLong(19, _tmp_3);
        }
        final Long _tmp_4 = DateTypeConverter.fromInstant(entity.getLastModifiedLocal());
        if (_tmp_4 == null) {
          statement.bindNull(20);
        } else {
          statement.bindLong(20, _tmp_4);
        }
        if (entity.getEtag() == null) {
          statement.bindNull(21);
        } else {
          statement.bindText(21, entity.getEtag());
        }
      }
    };
    this.__deleteAdapterOfAttachment = new EntityDeleteOrUpdateAdapter<Attachment>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `Attachment` WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Attachment entity) {
        if (entity.getLocalId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getLocalId());
        }
      }
    };
    this.__updateAdapterOfAttachment = new EntityDeleteOrUpdateAdapter<Attachment>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `Attachment` SET `cardId` = ?,`type` = ?,`data` = ?,`createdAt` = ?,`createdBy` = ?,`deletedAt` = ?,`filesize` = ?,`mimetype` = ?,`dirname` = ?,`basename` = ?,`extension` = ?,`filename` = ?,`localPath` = ?,`fileId` = ?,`localId` = ?,`accountId` = ?,`id` = ?,`status` = ?,`lastModified` = ?,`lastModifiedLocal` = ?,`etag` = ? WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Attachment entity) {
        statement.bindLong(1, entity.getCardId());
        final String _tmp = EnumConverter.fromEAttachmentType(entity.getType());
        if (_tmp == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, _tmp);
        }
        if (entity.getData() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getData());
        }
        final Long _tmp_1 = DateTypeConverter.fromInstant(entity.getCreatedAt());
        if (_tmp_1 == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, _tmp_1);
        }
        if (entity.getCreatedBy() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getCreatedBy());
        }
        final Long _tmp_2 = DateTypeConverter.fromInstant(entity.getDeletedAt());
        if (_tmp_2 == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, _tmp_2);
        }
        statement.bindLong(7, entity.getFilesize());
        if (entity.getMimetype() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getMimetype());
        }
        if (entity.getDirname() == null) {
          statement.bindNull(9);
        } else {
          statement.bindText(9, entity.getDirname());
        }
        if (entity.getBasename() == null) {
          statement.bindNull(10);
        } else {
          statement.bindText(10, entity.getBasename());
        }
        if (entity.getExtension() == null) {
          statement.bindNull(11);
        } else {
          statement.bindText(11, entity.getExtension());
        }
        if (entity.getFilename() == null) {
          statement.bindNull(12);
        } else {
          statement.bindText(12, entity.getFilename());
        }
        if (entity.getLocalPath() == null) {
          statement.bindNull(13);
        } else {
          statement.bindText(13, entity.getLocalPath());
        }
        if (entity.getFileId() == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, entity.getFileId());
        }
        if (entity.getLocalId() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getLocalId());
        }
        statement.bindLong(16, entity.getAccountId());
        if (entity.getId() == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, entity.getId());
        }
        statement.bindLong(18, entity.getStatus());
        final Long _tmp_3 = DateTypeConverter.fromInstant(entity.getLastModified());
        if (_tmp_3 == null) {
          statement.bindNull(19);
        } else {
          statement.bindLong(19, _tmp_3);
        }
        final Long _tmp_4 = DateTypeConverter.fromInstant(entity.getLastModifiedLocal());
        if (_tmp_4 == null) {
          statement.bindNull(20);
        } else {
          statement.bindLong(20, _tmp_4);
        }
        if (entity.getEtag() == null) {
          statement.bindNull(21);
        } else {
          statement.bindText(21, entity.getEtag());
        }
        if (entity.getLocalId() == null) {
          statement.bindNull(22);
        } else {
          statement.bindLong(22, entity.getLocalId());
        }
      }
    };
  }

  @Override
  public long insert(final Attachment entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfAttachment.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final Attachment... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfAttachment.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final Attachment... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfAttachment.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final Attachment... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfAttachment.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public LiveData<List<Attachment>> getAttachmentsForCard(final long cardId) {
    final String _sql = "SELECT * FROM attachment where cardId = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"attachment"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cardId);
        final int _columnIndexOfCardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cardId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfData = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "data");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfCreatedBy = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdBy");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfFilesize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "filesize");
        final int _columnIndexOfMimetype = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "mimetype");
        final int _columnIndexOfDirname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dirname");
        final int _columnIndexOfBasename = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "basename");
        final int _columnIndexOfExtension = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "extension");
        final int _columnIndexOfFilename = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "filename");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fileId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<Attachment> _result = new ArrayList<Attachment>();
        while (_stmt.step()) {
          final Attachment _item;
          _item = new Attachment();
          final long _tmpCardId;
          _tmpCardId = _stmt.getLong(_columnIndexOfCardId);
          _item.setCardId(_tmpCardId);
          final EAttachmentType _tmpType;
          final String _tmp;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getText(_columnIndexOfType);
          }
          _tmpType = EnumConverter.toEAttachmentType(_tmp);
          _item.setType(_tmpType);
          final String _tmpData;
          if (_stmt.isNull(_columnIndexOfData)) {
            _tmpData = null;
          } else {
            _tmpData = _stmt.getText(_columnIndexOfData);
          }
          _item.setData(_tmpData);
          final Instant _tmpCreatedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = DateTypeConverter.toInstant(_tmp_1);
          _item.setCreatedAt(_tmpCreatedAt);
          final String _tmpCreatedBy;
          if (_stmt.isNull(_columnIndexOfCreatedBy)) {
            _tmpCreatedBy = null;
          } else {
            _tmpCreatedBy = _stmt.getText(_columnIndexOfCreatedBy);
          }
          _item.setCreatedBy(_tmpCreatedBy);
          final Instant _tmpDeletedAt;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_2);
          _item.setDeletedAt(_tmpDeletedAt);
          final long _tmpFilesize;
          _tmpFilesize = _stmt.getLong(_columnIndexOfFilesize);
          _item.setFilesize(_tmpFilesize);
          final String _tmpMimetype;
          if (_stmt.isNull(_columnIndexOfMimetype)) {
            _tmpMimetype = null;
          } else {
            _tmpMimetype = _stmt.getText(_columnIndexOfMimetype);
          }
          _item.setMimetype(_tmpMimetype);
          final String _tmpDirname;
          if (_stmt.isNull(_columnIndexOfDirname)) {
            _tmpDirname = null;
          } else {
            _tmpDirname = _stmt.getText(_columnIndexOfDirname);
          }
          _item.setDirname(_tmpDirname);
          final String _tmpBasename;
          if (_stmt.isNull(_columnIndexOfBasename)) {
            _tmpBasename = null;
          } else {
            _tmpBasename = _stmt.getText(_columnIndexOfBasename);
          }
          _item.setBasename(_tmpBasename);
          final String _tmpExtension;
          if (_stmt.isNull(_columnIndexOfExtension)) {
            _tmpExtension = null;
          } else {
            _tmpExtension = _stmt.getText(_columnIndexOfExtension);
          }
          _item.setExtension(_tmpExtension);
          final String _tmpFilename;
          if (_stmt.isNull(_columnIndexOfFilename)) {
            _tmpFilename = null;
          } else {
            _tmpFilename = _stmt.getText(_columnIndexOfFilename);
          }
          _item.setFilename(_tmpFilename);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          _item.setLocalPath(_tmpLocalPath);
          final Long _tmpFileId;
          if (_stmt.isNull(_columnIndexOfFileId)) {
            _tmpFileId = null;
          } else {
            _tmpFileId = _stmt.getLong(_columnIndexOfFileId);
          }
          _item.setFileId(_tmpFileId);
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
          final Long _tmp_3;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_3);
          _item.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_4);
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
  public Attachment getAttachmentByRemoteIdDirectly(final long accountId, final Long remoteId) {
    final String _sql = "SELECT * FROM attachment where accountId = ? and id = ?";
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
        final int _columnIndexOfCardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cardId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfData = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "data");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfCreatedBy = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdBy");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfFilesize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "filesize");
        final int _columnIndexOfMimetype = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "mimetype");
        final int _columnIndexOfDirname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dirname");
        final int _columnIndexOfBasename = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "basename");
        final int _columnIndexOfExtension = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "extension");
        final int _columnIndexOfFilename = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "filename");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fileId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final Attachment _result;
        if (_stmt.step()) {
          _result = new Attachment();
          final long _tmpCardId;
          _tmpCardId = _stmt.getLong(_columnIndexOfCardId);
          _result.setCardId(_tmpCardId);
          final EAttachmentType _tmpType;
          final String _tmp;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getText(_columnIndexOfType);
          }
          _tmpType = EnumConverter.toEAttachmentType(_tmp);
          _result.setType(_tmpType);
          final String _tmpData;
          if (_stmt.isNull(_columnIndexOfData)) {
            _tmpData = null;
          } else {
            _tmpData = _stmt.getText(_columnIndexOfData);
          }
          _result.setData(_tmpData);
          final Instant _tmpCreatedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = DateTypeConverter.toInstant(_tmp_1);
          _result.setCreatedAt(_tmpCreatedAt);
          final String _tmpCreatedBy;
          if (_stmt.isNull(_columnIndexOfCreatedBy)) {
            _tmpCreatedBy = null;
          } else {
            _tmpCreatedBy = _stmt.getText(_columnIndexOfCreatedBy);
          }
          _result.setCreatedBy(_tmpCreatedBy);
          final Instant _tmpDeletedAt;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_2);
          _result.setDeletedAt(_tmpDeletedAt);
          final long _tmpFilesize;
          _tmpFilesize = _stmt.getLong(_columnIndexOfFilesize);
          _result.setFilesize(_tmpFilesize);
          final String _tmpMimetype;
          if (_stmt.isNull(_columnIndexOfMimetype)) {
            _tmpMimetype = null;
          } else {
            _tmpMimetype = _stmt.getText(_columnIndexOfMimetype);
          }
          _result.setMimetype(_tmpMimetype);
          final String _tmpDirname;
          if (_stmt.isNull(_columnIndexOfDirname)) {
            _tmpDirname = null;
          } else {
            _tmpDirname = _stmt.getText(_columnIndexOfDirname);
          }
          _result.setDirname(_tmpDirname);
          final String _tmpBasename;
          if (_stmt.isNull(_columnIndexOfBasename)) {
            _tmpBasename = null;
          } else {
            _tmpBasename = _stmt.getText(_columnIndexOfBasename);
          }
          _result.setBasename(_tmpBasename);
          final String _tmpExtension;
          if (_stmt.isNull(_columnIndexOfExtension)) {
            _tmpExtension = null;
          } else {
            _tmpExtension = _stmt.getText(_columnIndexOfExtension);
          }
          _result.setExtension(_tmpExtension);
          final String _tmpFilename;
          if (_stmt.isNull(_columnIndexOfFilename)) {
            _tmpFilename = null;
          } else {
            _tmpFilename = _stmt.getText(_columnIndexOfFilename);
          }
          _result.setFilename(_tmpFilename);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          _result.setLocalPath(_tmpLocalPath);
          final Long _tmpFileId;
          if (_stmt.isNull(_columnIndexOfFileId)) {
            _tmpFileId = null;
          } else {
            _tmpFileId = _stmt.getLong(_columnIndexOfFileId);
          }
          _result.setFileId(_tmpFileId);
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
          final Long _tmp_3;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_3);
          _result.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_4);
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
  public Attachment getAttachmentByLocalIdDirectly(final long accountId, final Long id) {
    final String _sql = "SELECT * FROM attachment where accountId = ? and localId = ?";
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
        final int _columnIndexOfCardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cardId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfData = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "data");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfCreatedBy = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdBy");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfFilesize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "filesize");
        final int _columnIndexOfMimetype = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "mimetype");
        final int _columnIndexOfDirname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dirname");
        final int _columnIndexOfBasename = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "basename");
        final int _columnIndexOfExtension = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "extension");
        final int _columnIndexOfFilename = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "filename");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fileId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final Attachment _result;
        if (_stmt.step()) {
          _result = new Attachment();
          final long _tmpCardId;
          _tmpCardId = _stmt.getLong(_columnIndexOfCardId);
          _result.setCardId(_tmpCardId);
          final EAttachmentType _tmpType;
          final String _tmp;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getText(_columnIndexOfType);
          }
          _tmpType = EnumConverter.toEAttachmentType(_tmp);
          _result.setType(_tmpType);
          final String _tmpData;
          if (_stmt.isNull(_columnIndexOfData)) {
            _tmpData = null;
          } else {
            _tmpData = _stmt.getText(_columnIndexOfData);
          }
          _result.setData(_tmpData);
          final Instant _tmpCreatedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = DateTypeConverter.toInstant(_tmp_1);
          _result.setCreatedAt(_tmpCreatedAt);
          final String _tmpCreatedBy;
          if (_stmt.isNull(_columnIndexOfCreatedBy)) {
            _tmpCreatedBy = null;
          } else {
            _tmpCreatedBy = _stmt.getText(_columnIndexOfCreatedBy);
          }
          _result.setCreatedBy(_tmpCreatedBy);
          final Instant _tmpDeletedAt;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_2);
          _result.setDeletedAt(_tmpDeletedAt);
          final long _tmpFilesize;
          _tmpFilesize = _stmt.getLong(_columnIndexOfFilesize);
          _result.setFilesize(_tmpFilesize);
          final String _tmpMimetype;
          if (_stmt.isNull(_columnIndexOfMimetype)) {
            _tmpMimetype = null;
          } else {
            _tmpMimetype = _stmt.getText(_columnIndexOfMimetype);
          }
          _result.setMimetype(_tmpMimetype);
          final String _tmpDirname;
          if (_stmt.isNull(_columnIndexOfDirname)) {
            _tmpDirname = null;
          } else {
            _tmpDirname = _stmt.getText(_columnIndexOfDirname);
          }
          _result.setDirname(_tmpDirname);
          final String _tmpBasename;
          if (_stmt.isNull(_columnIndexOfBasename)) {
            _tmpBasename = null;
          } else {
            _tmpBasename = _stmt.getText(_columnIndexOfBasename);
          }
          _result.setBasename(_tmpBasename);
          final String _tmpExtension;
          if (_stmt.isNull(_columnIndexOfExtension)) {
            _tmpExtension = null;
          } else {
            _tmpExtension = _stmt.getText(_columnIndexOfExtension);
          }
          _result.setExtension(_tmpExtension);
          final String _tmpFilename;
          if (_stmt.isNull(_columnIndexOfFilename)) {
            _tmpFilename = null;
          } else {
            _tmpFilename = _stmt.getText(_columnIndexOfFilename);
          }
          _result.setFilename(_tmpFilename);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          _result.setLocalPath(_tmpLocalPath);
          final Long _tmpFileId;
          if (_stmt.isNull(_columnIndexOfFileId)) {
            _tmpFileId = null;
          } else {
            _tmpFileId = _stmt.getLong(_columnIndexOfFileId);
          }
          _result.setFileId(_tmpFileId);
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
          final Long _tmp_3;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_3);
          _result.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_4);
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
  public List<Attachment> getLocallyChangedAttachmentsByLocalCardIdDirectly(final long accountId,
      final long localCardId) {
    final String _sql = "SELECT * FROM attachment WHERE accountId = ? and cardId = ? and (status<>1 or id is null or lastModified <> lastModifiedLocal)";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localCardId);
        final int _columnIndexOfCardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cardId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfData = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "data");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfCreatedBy = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdBy");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfFilesize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "filesize");
        final int _columnIndexOfMimetype = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "mimetype");
        final int _columnIndexOfDirname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dirname");
        final int _columnIndexOfBasename = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "basename");
        final int _columnIndexOfExtension = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "extension");
        final int _columnIndexOfFilename = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "filename");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fileId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<Attachment> _result = new ArrayList<Attachment>();
        while (_stmt.step()) {
          final Attachment _item;
          _item = new Attachment();
          final long _tmpCardId;
          _tmpCardId = _stmt.getLong(_columnIndexOfCardId);
          _item.setCardId(_tmpCardId);
          final EAttachmentType _tmpType;
          final String _tmp;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getText(_columnIndexOfType);
          }
          _tmpType = EnumConverter.toEAttachmentType(_tmp);
          _item.setType(_tmpType);
          final String _tmpData;
          if (_stmt.isNull(_columnIndexOfData)) {
            _tmpData = null;
          } else {
            _tmpData = _stmt.getText(_columnIndexOfData);
          }
          _item.setData(_tmpData);
          final Instant _tmpCreatedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = DateTypeConverter.toInstant(_tmp_1);
          _item.setCreatedAt(_tmpCreatedAt);
          final String _tmpCreatedBy;
          if (_stmt.isNull(_columnIndexOfCreatedBy)) {
            _tmpCreatedBy = null;
          } else {
            _tmpCreatedBy = _stmt.getText(_columnIndexOfCreatedBy);
          }
          _item.setCreatedBy(_tmpCreatedBy);
          final Instant _tmpDeletedAt;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_2);
          _item.setDeletedAt(_tmpDeletedAt);
          final long _tmpFilesize;
          _tmpFilesize = _stmt.getLong(_columnIndexOfFilesize);
          _item.setFilesize(_tmpFilesize);
          final String _tmpMimetype;
          if (_stmt.isNull(_columnIndexOfMimetype)) {
            _tmpMimetype = null;
          } else {
            _tmpMimetype = _stmt.getText(_columnIndexOfMimetype);
          }
          _item.setMimetype(_tmpMimetype);
          final String _tmpDirname;
          if (_stmt.isNull(_columnIndexOfDirname)) {
            _tmpDirname = null;
          } else {
            _tmpDirname = _stmt.getText(_columnIndexOfDirname);
          }
          _item.setDirname(_tmpDirname);
          final String _tmpBasename;
          if (_stmt.isNull(_columnIndexOfBasename)) {
            _tmpBasename = null;
          } else {
            _tmpBasename = _stmt.getText(_columnIndexOfBasename);
          }
          _item.setBasename(_tmpBasename);
          final String _tmpExtension;
          if (_stmt.isNull(_columnIndexOfExtension)) {
            _tmpExtension = null;
          } else {
            _tmpExtension = _stmt.getText(_columnIndexOfExtension);
          }
          _item.setExtension(_tmpExtension);
          final String _tmpFilename;
          if (_stmt.isNull(_columnIndexOfFilename)) {
            _tmpFilename = null;
          } else {
            _tmpFilename = _stmt.getText(_columnIndexOfFilename);
          }
          _item.setFilename(_tmpFilename);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          _item.setLocalPath(_tmpLocalPath);
          final Long _tmpFileId;
          if (_stmt.isNull(_columnIndexOfFileId)) {
            _tmpFileId = null;
          } else {
            _tmpFileId = _stmt.getLong(_columnIndexOfFileId);
          }
          _item.setFileId(_tmpFileId);
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
          final Long _tmp_3;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_3);
          _item.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_4);
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
  public List<Attachment> getLocallyChangedAttachmentsDirectly(final long accountId) {
    final String _sql = "SELECT * FROM attachment WHERE accountId = ? and (status<>1 or id is null or lastModified <> lastModifiedLocal)";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        final int _columnIndexOfCardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cardId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfData = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "data");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfCreatedBy = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdBy");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfFilesize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "filesize");
        final int _columnIndexOfMimetype = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "mimetype");
        final int _columnIndexOfDirname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dirname");
        final int _columnIndexOfBasename = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "basename");
        final int _columnIndexOfExtension = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "extension");
        final int _columnIndexOfFilename = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "filename");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fileId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<Attachment> _result = new ArrayList<Attachment>();
        while (_stmt.step()) {
          final Attachment _item;
          _item = new Attachment();
          final long _tmpCardId;
          _tmpCardId = _stmt.getLong(_columnIndexOfCardId);
          _item.setCardId(_tmpCardId);
          final EAttachmentType _tmpType;
          final String _tmp;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getText(_columnIndexOfType);
          }
          _tmpType = EnumConverter.toEAttachmentType(_tmp);
          _item.setType(_tmpType);
          final String _tmpData;
          if (_stmt.isNull(_columnIndexOfData)) {
            _tmpData = null;
          } else {
            _tmpData = _stmt.getText(_columnIndexOfData);
          }
          _item.setData(_tmpData);
          final Instant _tmpCreatedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = DateTypeConverter.toInstant(_tmp_1);
          _item.setCreatedAt(_tmpCreatedAt);
          final String _tmpCreatedBy;
          if (_stmt.isNull(_columnIndexOfCreatedBy)) {
            _tmpCreatedBy = null;
          } else {
            _tmpCreatedBy = _stmt.getText(_columnIndexOfCreatedBy);
          }
          _item.setCreatedBy(_tmpCreatedBy);
          final Instant _tmpDeletedAt;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_2);
          _item.setDeletedAt(_tmpDeletedAt);
          final long _tmpFilesize;
          _tmpFilesize = _stmt.getLong(_columnIndexOfFilesize);
          _item.setFilesize(_tmpFilesize);
          final String _tmpMimetype;
          if (_stmt.isNull(_columnIndexOfMimetype)) {
            _tmpMimetype = null;
          } else {
            _tmpMimetype = _stmt.getText(_columnIndexOfMimetype);
          }
          _item.setMimetype(_tmpMimetype);
          final String _tmpDirname;
          if (_stmt.isNull(_columnIndexOfDirname)) {
            _tmpDirname = null;
          } else {
            _tmpDirname = _stmt.getText(_columnIndexOfDirname);
          }
          _item.setDirname(_tmpDirname);
          final String _tmpBasename;
          if (_stmt.isNull(_columnIndexOfBasename)) {
            _tmpBasename = null;
          } else {
            _tmpBasename = _stmt.getText(_columnIndexOfBasename);
          }
          _item.setBasename(_tmpBasename);
          final String _tmpExtension;
          if (_stmt.isNull(_columnIndexOfExtension)) {
            _tmpExtension = null;
          } else {
            _tmpExtension = _stmt.getText(_columnIndexOfExtension);
          }
          _item.setExtension(_tmpExtension);
          final String _tmpFilename;
          if (_stmt.isNull(_columnIndexOfFilename)) {
            _tmpFilename = null;
          } else {
            _tmpFilename = _stmt.getText(_columnIndexOfFilename);
          }
          _item.setFilename(_tmpFilename);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          _item.setLocalPath(_tmpLocalPath);
          final Long _tmpFileId;
          if (_stmt.isNull(_columnIndexOfFileId)) {
            _tmpFileId = null;
          } else {
            _tmpFileId = _stmt.getLong(_columnIndexOfFileId);
          }
          _item.setFileId(_tmpFileId);
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
          final Long _tmp_3;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_3);
          _item.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_4);
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
  public List<Attachment> getLocallyChangedAttachmentsForStackDirectly(final long localStackId) {
    final String _sql = "SELECT a.* FROM attachment a inner join card c on c.localId = a.cardId WHERE c.stackId = ? and (a.status<>1 or a.id is null or a.lastModified <> a.lastModifiedLocal)";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localStackId);
        final int _columnIndexOfCardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cardId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfData = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "data");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfCreatedBy = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdBy");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfFilesize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "filesize");
        final int _columnIndexOfMimetype = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "mimetype");
        final int _columnIndexOfDirname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dirname");
        final int _columnIndexOfBasename = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "basename");
        final int _columnIndexOfExtension = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "extension");
        final int _columnIndexOfFilename = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "filename");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fileId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<Attachment> _result = new ArrayList<Attachment>();
        while (_stmt.step()) {
          final Attachment _item;
          _item = new Attachment();
          final long _tmpCardId;
          _tmpCardId = _stmt.getLong(_columnIndexOfCardId);
          _item.setCardId(_tmpCardId);
          final EAttachmentType _tmpType;
          final String _tmp;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getText(_columnIndexOfType);
          }
          _tmpType = EnumConverter.toEAttachmentType(_tmp);
          _item.setType(_tmpType);
          final String _tmpData;
          if (_stmt.isNull(_columnIndexOfData)) {
            _tmpData = null;
          } else {
            _tmpData = _stmt.getText(_columnIndexOfData);
          }
          _item.setData(_tmpData);
          final Instant _tmpCreatedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = DateTypeConverter.toInstant(_tmp_1);
          _item.setCreatedAt(_tmpCreatedAt);
          final String _tmpCreatedBy;
          if (_stmt.isNull(_columnIndexOfCreatedBy)) {
            _tmpCreatedBy = null;
          } else {
            _tmpCreatedBy = _stmt.getText(_columnIndexOfCreatedBy);
          }
          _item.setCreatedBy(_tmpCreatedBy);
          final Instant _tmpDeletedAt;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_2);
          _item.setDeletedAt(_tmpDeletedAt);
          final long _tmpFilesize;
          _tmpFilesize = _stmt.getLong(_columnIndexOfFilesize);
          _item.setFilesize(_tmpFilesize);
          final String _tmpMimetype;
          if (_stmt.isNull(_columnIndexOfMimetype)) {
            _tmpMimetype = null;
          } else {
            _tmpMimetype = _stmt.getText(_columnIndexOfMimetype);
          }
          _item.setMimetype(_tmpMimetype);
          final String _tmpDirname;
          if (_stmt.isNull(_columnIndexOfDirname)) {
            _tmpDirname = null;
          } else {
            _tmpDirname = _stmt.getText(_columnIndexOfDirname);
          }
          _item.setDirname(_tmpDirname);
          final String _tmpBasename;
          if (_stmt.isNull(_columnIndexOfBasename)) {
            _tmpBasename = null;
          } else {
            _tmpBasename = _stmt.getText(_columnIndexOfBasename);
          }
          _item.setBasename(_tmpBasename);
          final String _tmpExtension;
          if (_stmt.isNull(_columnIndexOfExtension)) {
            _tmpExtension = null;
          } else {
            _tmpExtension = _stmt.getText(_columnIndexOfExtension);
          }
          _item.setExtension(_tmpExtension);
          final String _tmpFilename;
          if (_stmt.isNull(_columnIndexOfFilename)) {
            _tmpFilename = null;
          } else {
            _tmpFilename = _stmt.getText(_columnIndexOfFilename);
          }
          _item.setFilename(_tmpFilename);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          _item.setLocalPath(_tmpLocalPath);
          final Long _tmpFileId;
          if (_stmt.isNull(_columnIndexOfFileId)) {
            _tmpFileId = null;
          } else {
            _tmpFileId = _stmt.getLong(_columnIndexOfFileId);
          }
          _item.setFileId(_tmpFileId);
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
          final Long _tmp_3;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_3);
          _item.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_4);
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
  public List<Attachment> getAttachmentsForLocalCardIdDirectly(final long accountId,
      final Long localCardId) {
    final String _sql = "SELECT * FROM attachment WHERE accountId = ? and cardId = ?";
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
        final int _columnIndexOfCardId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cardId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfData = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "data");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfCreatedBy = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdBy");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfFilesize = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "filesize");
        final int _columnIndexOfMimetype = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "mimetype");
        final int _columnIndexOfDirname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dirname");
        final int _columnIndexOfBasename = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "basename");
        final int _columnIndexOfExtension = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "extension");
        final int _columnIndexOfFilename = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "filename");
        final int _columnIndexOfLocalPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localPath");
        final int _columnIndexOfFileId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "fileId");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<Attachment> _result = new ArrayList<Attachment>();
        while (_stmt.step()) {
          final Attachment _item;
          _item = new Attachment();
          final long _tmpCardId;
          _tmpCardId = _stmt.getLong(_columnIndexOfCardId);
          _item.setCardId(_tmpCardId);
          final EAttachmentType _tmpType;
          final String _tmp;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getText(_columnIndexOfType);
          }
          _tmpType = EnumConverter.toEAttachmentType(_tmp);
          _item.setType(_tmpType);
          final String _tmpData;
          if (_stmt.isNull(_columnIndexOfData)) {
            _tmpData = null;
          } else {
            _tmpData = _stmt.getText(_columnIndexOfData);
          }
          _item.setData(_tmpData);
          final Instant _tmpCreatedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = DateTypeConverter.toInstant(_tmp_1);
          _item.setCreatedAt(_tmpCreatedAt);
          final String _tmpCreatedBy;
          if (_stmt.isNull(_columnIndexOfCreatedBy)) {
            _tmpCreatedBy = null;
          } else {
            _tmpCreatedBy = _stmt.getText(_columnIndexOfCreatedBy);
          }
          _item.setCreatedBy(_tmpCreatedBy);
          final Instant _tmpDeletedAt;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_2);
          _item.setDeletedAt(_tmpDeletedAt);
          final long _tmpFilesize;
          _tmpFilesize = _stmt.getLong(_columnIndexOfFilesize);
          _item.setFilesize(_tmpFilesize);
          final String _tmpMimetype;
          if (_stmt.isNull(_columnIndexOfMimetype)) {
            _tmpMimetype = null;
          } else {
            _tmpMimetype = _stmt.getText(_columnIndexOfMimetype);
          }
          _item.setMimetype(_tmpMimetype);
          final String _tmpDirname;
          if (_stmt.isNull(_columnIndexOfDirname)) {
            _tmpDirname = null;
          } else {
            _tmpDirname = _stmt.getText(_columnIndexOfDirname);
          }
          _item.setDirname(_tmpDirname);
          final String _tmpBasename;
          if (_stmt.isNull(_columnIndexOfBasename)) {
            _tmpBasename = null;
          } else {
            _tmpBasename = _stmt.getText(_columnIndexOfBasename);
          }
          _item.setBasename(_tmpBasename);
          final String _tmpExtension;
          if (_stmt.isNull(_columnIndexOfExtension)) {
            _tmpExtension = null;
          } else {
            _tmpExtension = _stmt.getText(_columnIndexOfExtension);
          }
          _item.setExtension(_tmpExtension);
          final String _tmpFilename;
          if (_stmt.isNull(_columnIndexOfFilename)) {
            _tmpFilename = null;
          } else {
            _tmpFilename = _stmt.getText(_columnIndexOfFilename);
          }
          _item.setFilename(_tmpFilename);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          _item.setLocalPath(_tmpLocalPath);
          final Long _tmpFileId;
          if (_stmt.isNull(_columnIndexOfFileId)) {
            _tmpFileId = null;
          } else {
            _tmpFileId = _stmt.getLong(_columnIndexOfFileId);
          }
          _item.setFileId(_tmpFileId);
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
          final Long _tmp_3;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_3);
          _item.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_4);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
