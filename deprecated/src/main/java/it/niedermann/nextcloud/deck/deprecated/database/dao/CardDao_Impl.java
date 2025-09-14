package it.niedermann.nextcloud.deck.database.dao;

import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomRawQuery;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.SQLiteConnection;
import androidx.sqlite.SQLiteStatement;
import androidx.sqlite.db.SupportSQLiteQuery;
import it.niedermann.nextcloud.deck.database.converter.DateTypeConverter;
import it.niedermann.nextcloud.deck.database.converter.EnumConverter;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.EAttachmentType;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullCardWithProjects;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProject;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectResource;
import it.niedermann.nextcloud.deck.model.ocs.projects.full.OcsProjectWithResources;
import java.lang.Boolean;
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
public final class CardDao_Impl implements CardDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Card> __insertAdapterOfCard;

  private final EntityDeleteOrUpdateAdapter<Card> __deleteAdapterOfCard;

  private final EntityDeleteOrUpdateAdapter<Card> __updateAdapterOfCard;

  public CardDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfCard = new EntityInsertAdapter<Card>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `Card` (`title`,`description`,`stackId`,`type`,`createdAt`,`deletedAt`,`done`,`attachmentCount`,`userId`,`order`,`archived`,`dueDate`,`notified`,`overdue`,`commentsUnread`,`localId`,`accountId`,`id`,`status`,`lastModified`,`lastModifiedLocal`,`etag`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Card entity) {
        if (entity.getTitle() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getTitle());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getDescription());
        }
        if (entity.getStackId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getStackId());
        }
        if (entity.getType() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getType());
        }
        final Long _tmp = DateTypeConverter.fromInstant(entity.getCreatedAt());
        if (_tmp == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, _tmp);
        }
        final Long _tmp_1 = DateTypeConverter.fromInstant(entity.getDeletedAt());
        if (_tmp_1 == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, _tmp_1);
        }
        final Long _tmp_2 = DateTypeConverter.fromInstant(entity.getDone());
        if (_tmp_2 == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, _tmp_2);
        }
        statement.bindLong(8, entity.getAttachmentCount());
        if (entity.getUserId() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getUserId());
        }
        statement.bindLong(10, entity.getOrder());
        final int _tmp_3 = entity.isArchived() ? 1 : 0;
        statement.bindLong(11, _tmp_3);
        final Long _tmp_4 = DateTypeConverter.fromInstant(entity.getDueDate());
        if (_tmp_4 == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, _tmp_4);
        }
        final int _tmp_5 = entity.isNotified() ? 1 : 0;
        statement.bindLong(13, _tmp_5);
        statement.bindLong(14, entity.getOverdue());
        statement.bindLong(15, entity.getCommentsUnread());
        if (entity.getLocalId() == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, entity.getLocalId());
        }
        statement.bindLong(17, entity.getAccountId());
        if (entity.getId() == null) {
          statement.bindNull(18);
        } else {
          statement.bindLong(18, entity.getId());
        }
        statement.bindLong(19, entity.getStatus());
        final Long _tmp_6 = DateTypeConverter.fromInstant(entity.getLastModified());
        if (_tmp_6 == null) {
          statement.bindNull(20);
        } else {
          statement.bindLong(20, _tmp_6);
        }
        final Long _tmp_7 = DateTypeConverter.fromInstant(entity.getLastModifiedLocal());
        if (_tmp_7 == null) {
          statement.bindNull(21);
        } else {
          statement.bindLong(21, _tmp_7);
        }
        if (entity.getEtag() == null) {
          statement.bindNull(22);
        } else {
          statement.bindText(22, entity.getEtag());
        }
      }
    };
    this.__deleteAdapterOfCard = new EntityDeleteOrUpdateAdapter<Card>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `Card` WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Card entity) {
        if (entity.getLocalId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getLocalId());
        }
      }
    };
    this.__updateAdapterOfCard = new EntityDeleteOrUpdateAdapter<Card>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `Card` SET `title` = ?,`description` = ?,`stackId` = ?,`type` = ?,`createdAt` = ?,`deletedAt` = ?,`done` = ?,`attachmentCount` = ?,`userId` = ?,`order` = ?,`archived` = ?,`dueDate` = ?,`notified` = ?,`overdue` = ?,`commentsUnread` = ?,`localId` = ?,`accountId` = ?,`id` = ?,`status` = ?,`lastModified` = ?,`lastModifiedLocal` = ?,`etag` = ? WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Card entity) {
        if (entity.getTitle() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getTitle());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getDescription());
        }
        if (entity.getStackId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getStackId());
        }
        if (entity.getType() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getType());
        }
        final Long _tmp = DateTypeConverter.fromInstant(entity.getCreatedAt());
        if (_tmp == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, _tmp);
        }
        final Long _tmp_1 = DateTypeConverter.fromInstant(entity.getDeletedAt());
        if (_tmp_1 == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, _tmp_1);
        }
        final Long _tmp_2 = DateTypeConverter.fromInstant(entity.getDone());
        if (_tmp_2 == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, _tmp_2);
        }
        statement.bindLong(8, entity.getAttachmentCount());
        if (entity.getUserId() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getUserId());
        }
        statement.bindLong(10, entity.getOrder());
        final int _tmp_3 = entity.isArchived() ? 1 : 0;
        statement.bindLong(11, _tmp_3);
        final Long _tmp_4 = DateTypeConverter.fromInstant(entity.getDueDate());
        if (_tmp_4 == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, _tmp_4);
        }
        final int _tmp_5 = entity.isNotified() ? 1 : 0;
        statement.bindLong(13, _tmp_5);
        statement.bindLong(14, entity.getOverdue());
        statement.bindLong(15, entity.getCommentsUnread());
        if (entity.getLocalId() == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, entity.getLocalId());
        }
        statement.bindLong(17, entity.getAccountId());
        if (entity.getId() == null) {
          statement.bindNull(18);
        } else {
          statement.bindLong(18, entity.getId());
        }
        statement.bindLong(19, entity.getStatus());
        final Long _tmp_6 = DateTypeConverter.fromInstant(entity.getLastModified());
        if (_tmp_6 == null) {
          statement.bindNull(20);
        } else {
          statement.bindLong(20, _tmp_6);
        }
        final Long _tmp_7 = DateTypeConverter.fromInstant(entity.getLastModifiedLocal());
        if (_tmp_7 == null) {
          statement.bindNull(21);
        } else {
          statement.bindLong(21, _tmp_7);
        }
        if (entity.getEtag() == null) {
          statement.bindNull(22);
        } else {
          statement.bindText(22, entity.getEtag());
        }
        if (entity.getLocalId() == null) {
          statement.bindNull(23);
        } else {
          statement.bindLong(23, entity.getLocalId());
        }
      }
    };
  }

  @Override
  public long insert(final Card entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfCard.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final Card... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfCard.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final Card... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfCard.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final Card... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfCard.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public LiveData<List<Card>> getCardsForStack(final long localStackId) {
    final String _sql = "SELECT * FROM card WHERE stackId = ? order by `order`, createdAt asc";
    return __db.getInvalidationTracker().createLiveData(new String[] {"card"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localStackId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<Card> _result = new ArrayList<Card>();
        while (_stmt.step()) {
          final Card _item;
          _item = new Card();
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _item.setTitle(_tmpTitle);
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          _item.setDescription(_tmpDescription);
          final Long _tmpStackId;
          if (_stmt.isNull(_columnIndexOfStackId)) {
            _tmpStackId = null;
          } else {
            _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
          }
          _item.setStackId(_tmpStackId);
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          _item.setType(_tmpType);
          final Instant _tmpCreatedAt;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
          _item.setCreatedAt(_tmpCreatedAt);
          final Instant _tmpDeletedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
          _item.setDeletedAt(_tmpDeletedAt);
          final Instant _tmpDone;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfDone)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfDone);
          }
          _tmpDone = DateTypeConverter.toInstant(_tmp_2);
          _item.setDone(_tmpDone);
          final int _tmpAttachmentCount;
          _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
          _item.setAttachmentCount(_tmpAttachmentCount);
          final Long _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
          }
          _item.setUserId(_tmpUserId);
          final int _tmpOrder;
          _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
          _item.setOrder(_tmpOrder);
          final boolean _tmpArchived;
          final int _tmp_3;
          _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
          _tmpArchived = _tmp_3 != 0;
          _item.setArchived(_tmpArchived);
          final Instant _tmpDueDate;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfDueDate)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
          }
          _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
          _item.setDueDate(_tmpDueDate);
          final boolean _tmpNotified;
          final int _tmp_5;
          _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
          _tmpNotified = _tmp_5 != 0;
          _item.setNotified(_tmpNotified);
          final int _tmpOverdue;
          _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
          _item.setOverdue(_tmpOverdue);
          final int _tmpCommentsUnread;
          _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
          _item.setCommentsUnread(_tmpCommentsUnread);
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
  public LiveData<Card> getCardByRemoteId(final long accountId, final long remoteId) {
    final String _sql = "SELECT * FROM card WHERE accountId = ? and id = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"card"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, remoteId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final Card _result;
        if (_stmt.step()) {
          _result = new Card();
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _result.setTitle(_tmpTitle);
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          _result.setDescription(_tmpDescription);
          final Long _tmpStackId;
          if (_stmt.isNull(_columnIndexOfStackId)) {
            _tmpStackId = null;
          } else {
            _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
          }
          _result.setStackId(_tmpStackId);
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          _result.setType(_tmpType);
          final Instant _tmpCreatedAt;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
          _result.setCreatedAt(_tmpCreatedAt);
          final Instant _tmpDeletedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
          _result.setDeletedAt(_tmpDeletedAt);
          final Instant _tmpDone;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfDone)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfDone);
          }
          _tmpDone = DateTypeConverter.toInstant(_tmp_2);
          _result.setDone(_tmpDone);
          final int _tmpAttachmentCount;
          _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
          _result.setAttachmentCount(_tmpAttachmentCount);
          final Long _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
          }
          _result.setUserId(_tmpUserId);
          final int _tmpOrder;
          _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
          _result.setOrder(_tmpOrder);
          final boolean _tmpArchived;
          final int _tmp_3;
          _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
          _tmpArchived = _tmp_3 != 0;
          _result.setArchived(_tmpArchived);
          final Instant _tmpDueDate;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfDueDate)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
          }
          _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
          _result.setDueDate(_tmpDueDate);
          final boolean _tmpNotified;
          final int _tmp_5;
          _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
          _tmpNotified = _tmp_5 != 0;
          _result.setNotified(_tmpNotified);
          final int _tmpOverdue;
          _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
          _result.setOverdue(_tmpOverdue);
          final int _tmpCommentsUnread;
          _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
          _result.setCommentsUnread(_tmpCommentsUnread);
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
  public FullCard getFullCardByRemoteIdDirectly(final long accountId, final long remoteId) {
    final String _sql = "SELECT * FROM card WHERE accountId = ? and id = ?";
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, remoteId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<ArrayList<User>> _collectionAssignedUsers = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<User>> _collectionOwner = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<Attachment>> _collectionAttachments = new LongSparseArray<ArrayList<Attachment>>();
        final LongSparseArray<ArrayList<Long>> _collectionCommentIDs = new LongSparseArray<ArrayList<Long>>();
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
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            if (!_collectionAssignedUsers.containsKey(_tmpKey_1)) {
              _collectionAssignedUsers.put(_tmpKey_1, new ArrayList<User>());
            }
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionOwner.containsKey(_tmpKey_2)) {
              _collectionOwner.put(_tmpKey_2, new ArrayList<User>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionAttachments.containsKey(_tmpKey_3)) {
              _collectionAttachments.put(_tmpKey_3, new ArrayList<Attachment>());
            }
          }
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            if (!_collectionCommentIDs.containsKey(_tmpKey_4)) {
              _collectionCommentIDs.put(_tmpKey_4, new ArrayList<Long>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionAssignedUsers);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser_1(_connection, _collectionOwner);
        __fetchRelationshipAttachmentAsitNiedermannNextcloudDeckModelAttachment(_connection, _collectionAttachments);
        __fetchRelationshipDeckCommentAsjavaLangLong(_connection, _collectionCommentIDs);
        final FullCard _result;
        if (_stmt.step()) {
          final Card _tmpCard;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfDescription) && _stmt.isNull(_columnIndexOfStackId) && _stmt.isNull(_columnIndexOfType) && _stmt.isNull(_columnIndexOfCreatedAt) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfDone) && _stmt.isNull(_columnIndexOfAttachmentCount) && _stmt.isNull(_columnIndexOfUserId) && _stmt.isNull(_columnIndexOfOrder) && _stmt.isNull(_columnIndexOfArchived) && _stmt.isNull(_columnIndexOfDueDate) && _stmt.isNull(_columnIndexOfNotified) && _stmt.isNull(_columnIndexOfOverdue) && _stmt.isNull(_columnIndexOfCommentsUnread) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpCard = new Card();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpCard.setTitle(_tmpTitle);
            final String _tmpDescription;
            if (_stmt.isNull(_columnIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _stmt.getText(_columnIndexOfDescription);
            }
            _tmpCard.setDescription(_tmpDescription);
            final Long _tmpStackId;
            if (_stmt.isNull(_columnIndexOfStackId)) {
              _tmpStackId = null;
            } else {
              _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
            }
            _tmpCard.setStackId(_tmpStackId);
            final String _tmpType;
            if (_stmt.isNull(_columnIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _stmt.getText(_columnIndexOfType);
            }
            _tmpCard.setType(_tmpType);
            final Instant _tmpCreatedAt;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfCreatedAt)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
            _tmpCard.setCreatedAt(_tmpCreatedAt);
            final Instant _tmpDeletedAt;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
            _tmpCard.setDeletedAt(_tmpDeletedAt);
            final Instant _tmpDone;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfDone)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfDone);
            }
            _tmpDone = DateTypeConverter.toInstant(_tmp_2);
            _tmpCard.setDone(_tmpDone);
            final int _tmpAttachmentCount;
            _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
            _tmpCard.setAttachmentCount(_tmpAttachmentCount);
            final Long _tmpUserId;
            if (_stmt.isNull(_columnIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
            }
            _tmpCard.setUserId(_tmpUserId);
            final int _tmpOrder;
            _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
            _tmpCard.setOrder(_tmpOrder);
            final boolean _tmpArchived;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
            _tmpArchived = _tmp_3 != 0;
            _tmpCard.setArchived(_tmpArchived);
            final Instant _tmpDueDate;
            final Long _tmp_4;
            if (_stmt.isNull(_columnIndexOfDueDate)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
            }
            _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
            _tmpCard.setDueDate(_tmpDueDate);
            final boolean _tmpNotified;
            final int _tmp_5;
            _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
            _tmpNotified = _tmp_5 != 0;
            _tmpCard.setNotified(_tmpNotified);
            final int _tmpOverdue;
            _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
            _tmpCard.setOverdue(_tmpOverdue);
            final int _tmpCommentsUnread;
            _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
            _tmpCard.setCommentsUnread(_tmpCommentsUnread);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpCard.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpCard.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpCard.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpCard.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_6;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
            _tmpCard.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_7;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
            _tmpCard.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpCard.setEtag(_tmpEtag);
          } else {
            _tmpCard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_5 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_5);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final ArrayList<User> _tmpAssignedUsersCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpAssignedUsersCollection = _collectionAssignedUsers.get(_tmpKey_6);
          } else {
            _tmpAssignedUsersCollection = new ArrayList<User>();
          }
          final ArrayList<User> _tmpOwnerCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_7 != null) {
            _tmpOwnerCollection = _collectionOwner.get(_tmpKey_7);
          } else {
            _tmpOwnerCollection = new ArrayList<User>();
          }
          final ArrayList<Attachment> _tmpAttachmentsCollection;
          final Long _tmpKey_8;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_8 = null;
          } else {
            _tmpKey_8 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_8 != null) {
            _tmpAttachmentsCollection = _collectionAttachments.get(_tmpKey_8);
          } else {
            _tmpAttachmentsCollection = new ArrayList<Attachment>();
          }
          final ArrayList<Long> _tmpCommentIDsCollection;
          final Long _tmpKey_9;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_9 = null;
          } else {
            _tmpKey_9 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_9 != null) {
            _tmpCommentIDsCollection = _collectionCommentIDs.get(_tmpKey_9);
          } else {
            _tmpCommentIDsCollection = new ArrayList<Long>();
          }
          _result = new FullCard();
          _result.card = _tmpCard;
          _result.labels = _tmpLabelsCollection;
          _result.assignedUsers = _tmpAssignedUsersCollection;
          _result.owner = _tmpOwnerCollection;
          _result.attachments = _tmpAttachmentsCollection;
          _result.commentIDs = _tmpCommentIDsCollection;
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
  public Card getCardByLocalIdDirectly(final long accountId, final long localId) {
    final String _sql = "SELECT * FROM card WHERE accountId = ? and localId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final Card _result;
        if (_stmt.step()) {
          _result = new Card();
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _result.setTitle(_tmpTitle);
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          _result.setDescription(_tmpDescription);
          final Long _tmpStackId;
          if (_stmt.isNull(_columnIndexOfStackId)) {
            _tmpStackId = null;
          } else {
            _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
          }
          _result.setStackId(_tmpStackId);
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          _result.setType(_tmpType);
          final Instant _tmpCreatedAt;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
          _result.setCreatedAt(_tmpCreatedAt);
          final Instant _tmpDeletedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
          _result.setDeletedAt(_tmpDeletedAt);
          final Instant _tmpDone;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfDone)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfDone);
          }
          _tmpDone = DateTypeConverter.toInstant(_tmp_2);
          _result.setDone(_tmpDone);
          final int _tmpAttachmentCount;
          _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
          _result.setAttachmentCount(_tmpAttachmentCount);
          final Long _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
          }
          _result.setUserId(_tmpUserId);
          final int _tmpOrder;
          _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
          _result.setOrder(_tmpOrder);
          final boolean _tmpArchived;
          final int _tmp_3;
          _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
          _tmpArchived = _tmp_3 != 0;
          _result.setArchived(_tmpArchived);
          final Instant _tmpDueDate;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfDueDate)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
          }
          _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
          _result.setDueDate(_tmpDueDate);
          final boolean _tmpNotified;
          final int _tmp_5;
          _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
          _tmpNotified = _tmp_5 != 0;
          _result.setNotified(_tmpNotified);
          final int _tmpOverdue;
          _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
          _result.setOverdue(_tmpOverdue);
          final int _tmpCommentsUnread;
          _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
          _result.setCommentsUnread(_tmpCommentsUnread);
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
  public FullCard getFullCardByLocalIdDirectly(final long accountId, final long localId) {
    final String _sql = "SELECT * FROM card WHERE accountId = ? and localId = ?";
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<ArrayList<User>> _collectionAssignedUsers = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<User>> _collectionOwner = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<Attachment>> _collectionAttachments = new LongSparseArray<ArrayList<Attachment>>();
        final LongSparseArray<ArrayList<Long>> _collectionCommentIDs = new LongSparseArray<ArrayList<Long>>();
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
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            if (!_collectionAssignedUsers.containsKey(_tmpKey_1)) {
              _collectionAssignedUsers.put(_tmpKey_1, new ArrayList<User>());
            }
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionOwner.containsKey(_tmpKey_2)) {
              _collectionOwner.put(_tmpKey_2, new ArrayList<User>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionAttachments.containsKey(_tmpKey_3)) {
              _collectionAttachments.put(_tmpKey_3, new ArrayList<Attachment>());
            }
          }
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            if (!_collectionCommentIDs.containsKey(_tmpKey_4)) {
              _collectionCommentIDs.put(_tmpKey_4, new ArrayList<Long>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionAssignedUsers);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser_1(_connection, _collectionOwner);
        __fetchRelationshipAttachmentAsitNiedermannNextcloudDeckModelAttachment(_connection, _collectionAttachments);
        __fetchRelationshipDeckCommentAsjavaLangLong(_connection, _collectionCommentIDs);
        final FullCard _result;
        if (_stmt.step()) {
          final Card _tmpCard;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfDescription) && _stmt.isNull(_columnIndexOfStackId) && _stmt.isNull(_columnIndexOfType) && _stmt.isNull(_columnIndexOfCreatedAt) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfDone) && _stmt.isNull(_columnIndexOfAttachmentCount) && _stmt.isNull(_columnIndexOfUserId) && _stmt.isNull(_columnIndexOfOrder) && _stmt.isNull(_columnIndexOfArchived) && _stmt.isNull(_columnIndexOfDueDate) && _stmt.isNull(_columnIndexOfNotified) && _stmt.isNull(_columnIndexOfOverdue) && _stmt.isNull(_columnIndexOfCommentsUnread) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpCard = new Card();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpCard.setTitle(_tmpTitle);
            final String _tmpDescription;
            if (_stmt.isNull(_columnIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _stmt.getText(_columnIndexOfDescription);
            }
            _tmpCard.setDescription(_tmpDescription);
            final Long _tmpStackId;
            if (_stmt.isNull(_columnIndexOfStackId)) {
              _tmpStackId = null;
            } else {
              _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
            }
            _tmpCard.setStackId(_tmpStackId);
            final String _tmpType;
            if (_stmt.isNull(_columnIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _stmt.getText(_columnIndexOfType);
            }
            _tmpCard.setType(_tmpType);
            final Instant _tmpCreatedAt;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfCreatedAt)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
            _tmpCard.setCreatedAt(_tmpCreatedAt);
            final Instant _tmpDeletedAt;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
            _tmpCard.setDeletedAt(_tmpDeletedAt);
            final Instant _tmpDone;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfDone)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfDone);
            }
            _tmpDone = DateTypeConverter.toInstant(_tmp_2);
            _tmpCard.setDone(_tmpDone);
            final int _tmpAttachmentCount;
            _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
            _tmpCard.setAttachmentCount(_tmpAttachmentCount);
            final Long _tmpUserId;
            if (_stmt.isNull(_columnIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
            }
            _tmpCard.setUserId(_tmpUserId);
            final int _tmpOrder;
            _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
            _tmpCard.setOrder(_tmpOrder);
            final boolean _tmpArchived;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
            _tmpArchived = _tmp_3 != 0;
            _tmpCard.setArchived(_tmpArchived);
            final Instant _tmpDueDate;
            final Long _tmp_4;
            if (_stmt.isNull(_columnIndexOfDueDate)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
            }
            _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
            _tmpCard.setDueDate(_tmpDueDate);
            final boolean _tmpNotified;
            final int _tmp_5;
            _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
            _tmpNotified = _tmp_5 != 0;
            _tmpCard.setNotified(_tmpNotified);
            final int _tmpOverdue;
            _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
            _tmpCard.setOverdue(_tmpOverdue);
            final int _tmpCommentsUnread;
            _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
            _tmpCard.setCommentsUnread(_tmpCommentsUnread);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpCard.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpCard.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpCard.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpCard.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_6;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
            _tmpCard.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_7;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
            _tmpCard.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpCard.setEtag(_tmpEtag);
          } else {
            _tmpCard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_5 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_5);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final ArrayList<User> _tmpAssignedUsersCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpAssignedUsersCollection = _collectionAssignedUsers.get(_tmpKey_6);
          } else {
            _tmpAssignedUsersCollection = new ArrayList<User>();
          }
          final ArrayList<User> _tmpOwnerCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_7 != null) {
            _tmpOwnerCollection = _collectionOwner.get(_tmpKey_7);
          } else {
            _tmpOwnerCollection = new ArrayList<User>();
          }
          final ArrayList<Attachment> _tmpAttachmentsCollection;
          final Long _tmpKey_8;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_8 = null;
          } else {
            _tmpKey_8 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_8 != null) {
            _tmpAttachmentsCollection = _collectionAttachments.get(_tmpKey_8);
          } else {
            _tmpAttachmentsCollection = new ArrayList<Attachment>();
          }
          final ArrayList<Long> _tmpCommentIDsCollection;
          final Long _tmpKey_9;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_9 = null;
          } else {
            _tmpKey_9 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_9 != null) {
            _tmpCommentIDsCollection = _collectionCommentIDs.get(_tmpKey_9);
          } else {
            _tmpCommentIDsCollection = new ArrayList<Long>();
          }
          _result = new FullCard();
          _result.card = _tmpCard;
          _result.labels = _tmpLabelsCollection;
          _result.assignedUsers = _tmpAssignedUsersCollection;
          _result.owner = _tmpOwnerCollection;
          _result.attachments = _tmpAttachmentsCollection;
          _result.commentIDs = _tmpCommentIDsCollection;
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
  public LiveData<List<FullCard>> getFullCardsForStack(final long accountId,
      final long localStackId) {
    final String _sql = "SELECT * FROM card WHERE accountId = ? AND archived = 0 AND stackId = ? and status<>3 order by `order`, createdAt asc";
    return __db.getInvalidationTracker().createLiveData(new String[] {"JoinCardWithLabel", "Label",
        "JoinCardWithUser", "User", "Attachment", "DeckComment", "card"}, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localStackId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<ArrayList<User>> _collectionAssignedUsers = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<User>> _collectionOwner = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<Attachment>> _collectionAttachments = new LongSparseArray<ArrayList<Attachment>>();
        final LongSparseArray<ArrayList<Long>> _collectionCommentIDs = new LongSparseArray<ArrayList<Long>>();
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
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            if (!_collectionAssignedUsers.containsKey(_tmpKey_1)) {
              _collectionAssignedUsers.put(_tmpKey_1, new ArrayList<User>());
            }
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionOwner.containsKey(_tmpKey_2)) {
              _collectionOwner.put(_tmpKey_2, new ArrayList<User>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionAttachments.containsKey(_tmpKey_3)) {
              _collectionAttachments.put(_tmpKey_3, new ArrayList<Attachment>());
            }
          }
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            if (!_collectionCommentIDs.containsKey(_tmpKey_4)) {
              _collectionCommentIDs.put(_tmpKey_4, new ArrayList<Long>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionAssignedUsers);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser_1(_connection, _collectionOwner);
        __fetchRelationshipAttachmentAsitNiedermannNextcloudDeckModelAttachment(_connection, _collectionAttachments);
        __fetchRelationshipDeckCommentAsjavaLangLong(_connection, _collectionCommentIDs);
        final List<FullCard> _result = new ArrayList<FullCard>();
        while (_stmt.step()) {
          final FullCard _item;
          final Card _tmpCard;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfDescription) && _stmt.isNull(_columnIndexOfStackId) && _stmt.isNull(_columnIndexOfType) && _stmt.isNull(_columnIndexOfCreatedAt) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfDone) && _stmt.isNull(_columnIndexOfAttachmentCount) && _stmt.isNull(_columnIndexOfUserId) && _stmt.isNull(_columnIndexOfOrder) && _stmt.isNull(_columnIndexOfArchived) && _stmt.isNull(_columnIndexOfDueDate) && _stmt.isNull(_columnIndexOfNotified) && _stmt.isNull(_columnIndexOfOverdue) && _stmt.isNull(_columnIndexOfCommentsUnread) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpCard = new Card();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpCard.setTitle(_tmpTitle);
            final String _tmpDescription;
            if (_stmt.isNull(_columnIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _stmt.getText(_columnIndexOfDescription);
            }
            _tmpCard.setDescription(_tmpDescription);
            final Long _tmpStackId;
            if (_stmt.isNull(_columnIndexOfStackId)) {
              _tmpStackId = null;
            } else {
              _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
            }
            _tmpCard.setStackId(_tmpStackId);
            final String _tmpType;
            if (_stmt.isNull(_columnIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _stmt.getText(_columnIndexOfType);
            }
            _tmpCard.setType(_tmpType);
            final Instant _tmpCreatedAt;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfCreatedAt)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
            _tmpCard.setCreatedAt(_tmpCreatedAt);
            final Instant _tmpDeletedAt;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
            _tmpCard.setDeletedAt(_tmpDeletedAt);
            final Instant _tmpDone;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfDone)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfDone);
            }
            _tmpDone = DateTypeConverter.toInstant(_tmp_2);
            _tmpCard.setDone(_tmpDone);
            final int _tmpAttachmentCount;
            _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
            _tmpCard.setAttachmentCount(_tmpAttachmentCount);
            final Long _tmpUserId;
            if (_stmt.isNull(_columnIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
            }
            _tmpCard.setUserId(_tmpUserId);
            final int _tmpOrder;
            _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
            _tmpCard.setOrder(_tmpOrder);
            final boolean _tmpArchived;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
            _tmpArchived = _tmp_3 != 0;
            _tmpCard.setArchived(_tmpArchived);
            final Instant _tmpDueDate;
            final Long _tmp_4;
            if (_stmt.isNull(_columnIndexOfDueDate)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
            }
            _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
            _tmpCard.setDueDate(_tmpDueDate);
            final boolean _tmpNotified;
            final int _tmp_5;
            _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
            _tmpNotified = _tmp_5 != 0;
            _tmpCard.setNotified(_tmpNotified);
            final int _tmpOverdue;
            _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
            _tmpCard.setOverdue(_tmpOverdue);
            final int _tmpCommentsUnread;
            _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
            _tmpCard.setCommentsUnread(_tmpCommentsUnread);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpCard.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpCard.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpCard.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpCard.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_6;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
            _tmpCard.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_7;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
            _tmpCard.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpCard.setEtag(_tmpEtag);
          } else {
            _tmpCard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_5 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_5);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final ArrayList<User> _tmpAssignedUsersCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpAssignedUsersCollection = _collectionAssignedUsers.get(_tmpKey_6);
          } else {
            _tmpAssignedUsersCollection = new ArrayList<User>();
          }
          final ArrayList<User> _tmpOwnerCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_7 != null) {
            _tmpOwnerCollection = _collectionOwner.get(_tmpKey_7);
          } else {
            _tmpOwnerCollection = new ArrayList<User>();
          }
          final ArrayList<Attachment> _tmpAttachmentsCollection;
          final Long _tmpKey_8;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_8 = null;
          } else {
            _tmpKey_8 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_8 != null) {
            _tmpAttachmentsCollection = _collectionAttachments.get(_tmpKey_8);
          } else {
            _tmpAttachmentsCollection = new ArrayList<Attachment>();
          }
          final ArrayList<Long> _tmpCommentIDsCollection;
          final Long _tmpKey_9;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_9 = null;
          } else {
            _tmpKey_9 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_9 != null) {
            _tmpCommentIDsCollection = _collectionCommentIDs.get(_tmpKey_9);
          } else {
            _tmpCommentIDsCollection = new ArrayList<Long>();
          }
          _item = new FullCard();
          _item.card = _tmpCard;
          _item.labels = _tmpLabelsCollection;
          _item.assignedUsers = _tmpAssignedUsersCollection;
          _item.owner = _tmpOwnerCollection;
          _item.attachments = _tmpAttachmentsCollection;
          _item.commentIDs = _tmpCommentIDsCollection;
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<FullCard> getFullCardsForStackDirectly(final long accountId,
      final long localStackId) {
    final String _sql = "SELECT * FROM card WHERE accountId = ? AND stackId = ? order by `order`, createdAt asc";
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localStackId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<ArrayList<User>> _collectionAssignedUsers = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<User>> _collectionOwner = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<Attachment>> _collectionAttachments = new LongSparseArray<ArrayList<Attachment>>();
        final LongSparseArray<ArrayList<Long>> _collectionCommentIDs = new LongSparseArray<ArrayList<Long>>();
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
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            if (!_collectionAssignedUsers.containsKey(_tmpKey_1)) {
              _collectionAssignedUsers.put(_tmpKey_1, new ArrayList<User>());
            }
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionOwner.containsKey(_tmpKey_2)) {
              _collectionOwner.put(_tmpKey_2, new ArrayList<User>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionAttachments.containsKey(_tmpKey_3)) {
              _collectionAttachments.put(_tmpKey_3, new ArrayList<Attachment>());
            }
          }
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            if (!_collectionCommentIDs.containsKey(_tmpKey_4)) {
              _collectionCommentIDs.put(_tmpKey_4, new ArrayList<Long>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionAssignedUsers);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser_1(_connection, _collectionOwner);
        __fetchRelationshipAttachmentAsitNiedermannNextcloudDeckModelAttachment(_connection, _collectionAttachments);
        __fetchRelationshipDeckCommentAsjavaLangLong(_connection, _collectionCommentIDs);
        final List<FullCard> _result = new ArrayList<FullCard>();
        while (_stmt.step()) {
          final FullCard _item;
          final Card _tmpCard;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfDescription) && _stmt.isNull(_columnIndexOfStackId) && _stmt.isNull(_columnIndexOfType) && _stmt.isNull(_columnIndexOfCreatedAt) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfDone) && _stmt.isNull(_columnIndexOfAttachmentCount) && _stmt.isNull(_columnIndexOfUserId) && _stmt.isNull(_columnIndexOfOrder) && _stmt.isNull(_columnIndexOfArchived) && _stmt.isNull(_columnIndexOfDueDate) && _stmt.isNull(_columnIndexOfNotified) && _stmt.isNull(_columnIndexOfOverdue) && _stmt.isNull(_columnIndexOfCommentsUnread) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpCard = new Card();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpCard.setTitle(_tmpTitle);
            final String _tmpDescription;
            if (_stmt.isNull(_columnIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _stmt.getText(_columnIndexOfDescription);
            }
            _tmpCard.setDescription(_tmpDescription);
            final Long _tmpStackId;
            if (_stmt.isNull(_columnIndexOfStackId)) {
              _tmpStackId = null;
            } else {
              _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
            }
            _tmpCard.setStackId(_tmpStackId);
            final String _tmpType;
            if (_stmt.isNull(_columnIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _stmt.getText(_columnIndexOfType);
            }
            _tmpCard.setType(_tmpType);
            final Instant _tmpCreatedAt;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfCreatedAt)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
            _tmpCard.setCreatedAt(_tmpCreatedAt);
            final Instant _tmpDeletedAt;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
            _tmpCard.setDeletedAt(_tmpDeletedAt);
            final Instant _tmpDone;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfDone)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfDone);
            }
            _tmpDone = DateTypeConverter.toInstant(_tmp_2);
            _tmpCard.setDone(_tmpDone);
            final int _tmpAttachmentCount;
            _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
            _tmpCard.setAttachmentCount(_tmpAttachmentCount);
            final Long _tmpUserId;
            if (_stmt.isNull(_columnIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
            }
            _tmpCard.setUserId(_tmpUserId);
            final int _tmpOrder;
            _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
            _tmpCard.setOrder(_tmpOrder);
            final boolean _tmpArchived;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
            _tmpArchived = _tmp_3 != 0;
            _tmpCard.setArchived(_tmpArchived);
            final Instant _tmpDueDate;
            final Long _tmp_4;
            if (_stmt.isNull(_columnIndexOfDueDate)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
            }
            _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
            _tmpCard.setDueDate(_tmpDueDate);
            final boolean _tmpNotified;
            final int _tmp_5;
            _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
            _tmpNotified = _tmp_5 != 0;
            _tmpCard.setNotified(_tmpNotified);
            final int _tmpOverdue;
            _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
            _tmpCard.setOverdue(_tmpOverdue);
            final int _tmpCommentsUnread;
            _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
            _tmpCard.setCommentsUnread(_tmpCommentsUnread);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpCard.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpCard.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpCard.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpCard.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_6;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
            _tmpCard.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_7;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
            _tmpCard.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpCard.setEtag(_tmpEtag);
          } else {
            _tmpCard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_5 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_5);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final ArrayList<User> _tmpAssignedUsersCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpAssignedUsersCollection = _collectionAssignedUsers.get(_tmpKey_6);
          } else {
            _tmpAssignedUsersCollection = new ArrayList<User>();
          }
          final ArrayList<User> _tmpOwnerCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_7 != null) {
            _tmpOwnerCollection = _collectionOwner.get(_tmpKey_7);
          } else {
            _tmpOwnerCollection = new ArrayList<User>();
          }
          final ArrayList<Attachment> _tmpAttachmentsCollection;
          final Long _tmpKey_8;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_8 = null;
          } else {
            _tmpKey_8 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_8 != null) {
            _tmpAttachmentsCollection = _collectionAttachments.get(_tmpKey_8);
          } else {
            _tmpAttachmentsCollection = new ArrayList<Attachment>();
          }
          final ArrayList<Long> _tmpCommentIDsCollection;
          final Long _tmpKey_9;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_9 = null;
          } else {
            _tmpKey_9 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_9 != null) {
            _tmpCommentIDsCollection = _collectionCommentIDs.get(_tmpKey_9);
          } else {
            _tmpCommentIDsCollection = new ArrayList<Long>();
          }
          _item = new FullCard();
          _item.card = _tmpCard;
          _item.labels = _tmpLabelsCollection;
          _item.assignedUsers = _tmpAssignedUsersCollection;
          _item.owner = _tmpOwnerCollection;
          _item.attachments = _tmpAttachmentsCollection;
          _item.commentIDs = _tmpCommentIDsCollection;
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<FullCard> getFullCardByLocalId(final long accountId, final long localCardId) {
    final String _sql = "SELECT * FROM card WHERE accountId = ? and localId = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"JoinCardWithLabel", "Label",
        "JoinCardWithUser", "User", "Attachment", "DeckComment", "card"}, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localCardId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<ArrayList<User>> _collectionAssignedUsers = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<User>> _collectionOwner = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<Attachment>> _collectionAttachments = new LongSparseArray<ArrayList<Attachment>>();
        final LongSparseArray<ArrayList<Long>> _collectionCommentIDs = new LongSparseArray<ArrayList<Long>>();
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
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            if (!_collectionAssignedUsers.containsKey(_tmpKey_1)) {
              _collectionAssignedUsers.put(_tmpKey_1, new ArrayList<User>());
            }
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionOwner.containsKey(_tmpKey_2)) {
              _collectionOwner.put(_tmpKey_2, new ArrayList<User>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionAttachments.containsKey(_tmpKey_3)) {
              _collectionAttachments.put(_tmpKey_3, new ArrayList<Attachment>());
            }
          }
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            if (!_collectionCommentIDs.containsKey(_tmpKey_4)) {
              _collectionCommentIDs.put(_tmpKey_4, new ArrayList<Long>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionAssignedUsers);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser_1(_connection, _collectionOwner);
        __fetchRelationshipAttachmentAsitNiedermannNextcloudDeckModelAttachment(_connection, _collectionAttachments);
        __fetchRelationshipDeckCommentAsjavaLangLong(_connection, _collectionCommentIDs);
        final FullCard _result;
        if (_stmt.step()) {
          final Card _tmpCard;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfDescription) && _stmt.isNull(_columnIndexOfStackId) && _stmt.isNull(_columnIndexOfType) && _stmt.isNull(_columnIndexOfCreatedAt) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfDone) && _stmt.isNull(_columnIndexOfAttachmentCount) && _stmt.isNull(_columnIndexOfUserId) && _stmt.isNull(_columnIndexOfOrder) && _stmt.isNull(_columnIndexOfArchived) && _stmt.isNull(_columnIndexOfDueDate) && _stmt.isNull(_columnIndexOfNotified) && _stmt.isNull(_columnIndexOfOverdue) && _stmt.isNull(_columnIndexOfCommentsUnread) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpCard = new Card();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpCard.setTitle(_tmpTitle);
            final String _tmpDescription;
            if (_stmt.isNull(_columnIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _stmt.getText(_columnIndexOfDescription);
            }
            _tmpCard.setDescription(_tmpDescription);
            final Long _tmpStackId;
            if (_stmt.isNull(_columnIndexOfStackId)) {
              _tmpStackId = null;
            } else {
              _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
            }
            _tmpCard.setStackId(_tmpStackId);
            final String _tmpType;
            if (_stmt.isNull(_columnIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _stmt.getText(_columnIndexOfType);
            }
            _tmpCard.setType(_tmpType);
            final Instant _tmpCreatedAt;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfCreatedAt)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
            _tmpCard.setCreatedAt(_tmpCreatedAt);
            final Instant _tmpDeletedAt;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
            _tmpCard.setDeletedAt(_tmpDeletedAt);
            final Instant _tmpDone;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfDone)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfDone);
            }
            _tmpDone = DateTypeConverter.toInstant(_tmp_2);
            _tmpCard.setDone(_tmpDone);
            final int _tmpAttachmentCount;
            _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
            _tmpCard.setAttachmentCount(_tmpAttachmentCount);
            final Long _tmpUserId;
            if (_stmt.isNull(_columnIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
            }
            _tmpCard.setUserId(_tmpUserId);
            final int _tmpOrder;
            _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
            _tmpCard.setOrder(_tmpOrder);
            final boolean _tmpArchived;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
            _tmpArchived = _tmp_3 != 0;
            _tmpCard.setArchived(_tmpArchived);
            final Instant _tmpDueDate;
            final Long _tmp_4;
            if (_stmt.isNull(_columnIndexOfDueDate)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
            }
            _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
            _tmpCard.setDueDate(_tmpDueDate);
            final boolean _tmpNotified;
            final int _tmp_5;
            _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
            _tmpNotified = _tmp_5 != 0;
            _tmpCard.setNotified(_tmpNotified);
            final int _tmpOverdue;
            _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
            _tmpCard.setOverdue(_tmpOverdue);
            final int _tmpCommentsUnread;
            _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
            _tmpCard.setCommentsUnread(_tmpCommentsUnread);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpCard.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpCard.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpCard.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpCard.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_6;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
            _tmpCard.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_7;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
            _tmpCard.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpCard.setEtag(_tmpEtag);
          } else {
            _tmpCard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_5 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_5);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final ArrayList<User> _tmpAssignedUsersCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpAssignedUsersCollection = _collectionAssignedUsers.get(_tmpKey_6);
          } else {
            _tmpAssignedUsersCollection = new ArrayList<User>();
          }
          final ArrayList<User> _tmpOwnerCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_7 != null) {
            _tmpOwnerCollection = _collectionOwner.get(_tmpKey_7);
          } else {
            _tmpOwnerCollection = new ArrayList<User>();
          }
          final ArrayList<Attachment> _tmpAttachmentsCollection;
          final Long _tmpKey_8;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_8 = null;
          } else {
            _tmpKey_8 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_8 != null) {
            _tmpAttachmentsCollection = _collectionAttachments.get(_tmpKey_8);
          } else {
            _tmpAttachmentsCollection = new ArrayList<Attachment>();
          }
          final ArrayList<Long> _tmpCommentIDsCollection;
          final Long _tmpKey_9;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_9 = null;
          } else {
            _tmpKey_9 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_9 != null) {
            _tmpCommentIDsCollection = _collectionCommentIDs.get(_tmpKey_9);
          } else {
            _tmpCommentIDsCollection = new ArrayList<Long>();
          }
          _result = new FullCard();
          _result.card = _tmpCard;
          _result.labels = _tmpLabelsCollection;
          _result.assignedUsers = _tmpAssignedUsersCollection;
          _result.owner = _tmpOwnerCollection;
          _result.attachments = _tmpAttachmentsCollection;
          _result.commentIDs = _tmpCommentIDsCollection;
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
  public LiveData<FullCardWithProjects> getFullCardWithProjectsByLocalId(final long accountId,
      final long localCardId) {
    final String _sql = "SELECT * FROM card WHERE accountId = ? and localId = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"OcsProjectResource",
        "JoinCardWithProject", "OcsProject", "JoinCardWithLabel", "Label", "JoinCardWithUser",
        "User", "Attachment", "DeckComment", "card"}, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localCardId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<OcsProjectWithResources>> _collectionProjects = new LongSparseArray<ArrayList<OcsProjectWithResources>>();
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<ArrayList<User>> _collectionAssignedUsers = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<User>> _collectionOwner = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<Attachment>> _collectionAttachments = new LongSparseArray<ArrayList<Attachment>>();
        final LongSparseArray<ArrayList<Long>> _collectionCommentIDs = new LongSparseArray<ArrayList<Long>>();
        while (_stmt.step()) {
          final Long _tmpKey;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey = null;
          } else {
            _tmpKey = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey != null) {
            if (!_collectionProjects.containsKey(_tmpKey)) {
              _collectionProjects.put(_tmpKey, new ArrayList<OcsProjectWithResources>());
            }
          }
          final Long _tmpKey_1;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            if (!_collectionLabels.containsKey(_tmpKey_1)) {
              _collectionLabels.put(_tmpKey_1, new ArrayList<Label>());
            }
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionAssignedUsers.containsKey(_tmpKey_2)) {
              _collectionAssignedUsers.put(_tmpKey_2, new ArrayList<User>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionOwner.containsKey(_tmpKey_3)) {
              _collectionOwner.put(_tmpKey_3, new ArrayList<User>());
            }
          }
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            if (!_collectionAttachments.containsKey(_tmpKey_4)) {
              _collectionAttachments.put(_tmpKey_4, new ArrayList<Attachment>());
            }
          }
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_5 != null) {
            if (!_collectionCommentIDs.containsKey(_tmpKey_5)) {
              _collectionCommentIDs.put(_tmpKey_5, new ArrayList<Long>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipOcsProjectAsitNiedermannNextcloudDeckModelOcsProjectsFullOcsProjectWithResources(_connection, _collectionProjects);
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionAssignedUsers);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser_1(_connection, _collectionOwner);
        __fetchRelationshipAttachmentAsitNiedermannNextcloudDeckModelAttachment(_connection, _collectionAttachments);
        __fetchRelationshipDeckCommentAsjavaLangLong(_connection, _collectionCommentIDs);
        final FullCardWithProjects _result;
        if (_stmt.step()) {
          final Card _tmpCard;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfDescription) && _stmt.isNull(_columnIndexOfStackId) && _stmt.isNull(_columnIndexOfType) && _stmt.isNull(_columnIndexOfCreatedAt) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfDone) && _stmt.isNull(_columnIndexOfAttachmentCount) && _stmt.isNull(_columnIndexOfUserId) && _stmt.isNull(_columnIndexOfOrder) && _stmt.isNull(_columnIndexOfArchived) && _stmt.isNull(_columnIndexOfDueDate) && _stmt.isNull(_columnIndexOfNotified) && _stmt.isNull(_columnIndexOfOverdue) && _stmt.isNull(_columnIndexOfCommentsUnread) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpCard = new Card();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpCard.setTitle(_tmpTitle);
            final String _tmpDescription;
            if (_stmt.isNull(_columnIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _stmt.getText(_columnIndexOfDescription);
            }
            _tmpCard.setDescription(_tmpDescription);
            final Long _tmpStackId;
            if (_stmt.isNull(_columnIndexOfStackId)) {
              _tmpStackId = null;
            } else {
              _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
            }
            _tmpCard.setStackId(_tmpStackId);
            final String _tmpType;
            if (_stmt.isNull(_columnIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _stmt.getText(_columnIndexOfType);
            }
            _tmpCard.setType(_tmpType);
            final Instant _tmpCreatedAt;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfCreatedAt)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
            _tmpCard.setCreatedAt(_tmpCreatedAt);
            final Instant _tmpDeletedAt;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
            _tmpCard.setDeletedAt(_tmpDeletedAt);
            final Instant _tmpDone;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfDone)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfDone);
            }
            _tmpDone = DateTypeConverter.toInstant(_tmp_2);
            _tmpCard.setDone(_tmpDone);
            final int _tmpAttachmentCount;
            _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
            _tmpCard.setAttachmentCount(_tmpAttachmentCount);
            final Long _tmpUserId;
            if (_stmt.isNull(_columnIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
            }
            _tmpCard.setUserId(_tmpUserId);
            final int _tmpOrder;
            _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
            _tmpCard.setOrder(_tmpOrder);
            final boolean _tmpArchived;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
            _tmpArchived = _tmp_3 != 0;
            _tmpCard.setArchived(_tmpArchived);
            final Instant _tmpDueDate;
            final Long _tmp_4;
            if (_stmt.isNull(_columnIndexOfDueDate)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
            }
            _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
            _tmpCard.setDueDate(_tmpDueDate);
            final boolean _tmpNotified;
            final int _tmp_5;
            _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
            _tmpNotified = _tmp_5 != 0;
            _tmpCard.setNotified(_tmpNotified);
            final int _tmpOverdue;
            _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
            _tmpCard.setOverdue(_tmpOverdue);
            final int _tmpCommentsUnread;
            _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
            _tmpCard.setCommentsUnread(_tmpCommentsUnread);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpCard.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpCard.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpCard.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpCard.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_6;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
            _tmpCard.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_7;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
            _tmpCard.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpCard.setEtag(_tmpEtag);
          } else {
            _tmpCard = null;
          }
          final ArrayList<OcsProjectWithResources> _tmpProjectsCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpProjectsCollection = _collectionProjects.get(_tmpKey_6);
          } else {
            _tmpProjectsCollection = new ArrayList<OcsProjectWithResources>();
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_7 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_7);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final ArrayList<User> _tmpAssignedUsersCollection;
          final Long _tmpKey_8;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_8 = null;
          } else {
            _tmpKey_8 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_8 != null) {
            _tmpAssignedUsersCollection = _collectionAssignedUsers.get(_tmpKey_8);
          } else {
            _tmpAssignedUsersCollection = new ArrayList<User>();
          }
          final ArrayList<User> _tmpOwnerCollection;
          final Long _tmpKey_9;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_9 = null;
          } else {
            _tmpKey_9 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_9 != null) {
            _tmpOwnerCollection = _collectionOwner.get(_tmpKey_9);
          } else {
            _tmpOwnerCollection = new ArrayList<User>();
          }
          final ArrayList<Attachment> _tmpAttachmentsCollection;
          final Long _tmpKey_10;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_10 = null;
          } else {
            _tmpKey_10 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_10 != null) {
            _tmpAttachmentsCollection = _collectionAttachments.get(_tmpKey_10);
          } else {
            _tmpAttachmentsCollection = new ArrayList<Attachment>();
          }
          final ArrayList<Long> _tmpCommentIDsCollection;
          final Long _tmpKey_11;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_11 = null;
          } else {
            _tmpKey_11 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_11 != null) {
            _tmpCommentIDsCollection = _collectionCommentIDs.get(_tmpKey_11);
          } else {
            _tmpCommentIDsCollection = new ArrayList<Long>();
          }
          _result = new FullCardWithProjects();
          _result.card = _tmpCard;
          _result.setProjects(_tmpProjectsCollection);
          _result.labels = _tmpLabelsCollection;
          _result.assignedUsers = _tmpAssignedUsersCollection;
          _result.owner = _tmpOwnerCollection;
          _result.attachments = _tmpAttachmentsCollection;
          _result.commentIDs = _tmpCommentIDsCollection;
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
  public LiveData<FullCard> getFullCardByRemoteId(final long accountId, final long remoteId) {
    final String _sql = "SELECT * FROM card WHERE accountId = ? and id = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"JoinCardWithLabel", "Label",
        "JoinCardWithUser", "User", "Attachment", "DeckComment", "card"}, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, remoteId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<ArrayList<User>> _collectionAssignedUsers = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<User>> _collectionOwner = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<Attachment>> _collectionAttachments = new LongSparseArray<ArrayList<Attachment>>();
        final LongSparseArray<ArrayList<Long>> _collectionCommentIDs = new LongSparseArray<ArrayList<Long>>();
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
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            if (!_collectionAssignedUsers.containsKey(_tmpKey_1)) {
              _collectionAssignedUsers.put(_tmpKey_1, new ArrayList<User>());
            }
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionOwner.containsKey(_tmpKey_2)) {
              _collectionOwner.put(_tmpKey_2, new ArrayList<User>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionAttachments.containsKey(_tmpKey_3)) {
              _collectionAttachments.put(_tmpKey_3, new ArrayList<Attachment>());
            }
          }
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            if (!_collectionCommentIDs.containsKey(_tmpKey_4)) {
              _collectionCommentIDs.put(_tmpKey_4, new ArrayList<Long>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionAssignedUsers);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser_1(_connection, _collectionOwner);
        __fetchRelationshipAttachmentAsitNiedermannNextcloudDeckModelAttachment(_connection, _collectionAttachments);
        __fetchRelationshipDeckCommentAsjavaLangLong(_connection, _collectionCommentIDs);
        final FullCard _result;
        if (_stmt.step()) {
          final Card _tmpCard;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfDescription) && _stmt.isNull(_columnIndexOfStackId) && _stmt.isNull(_columnIndexOfType) && _stmt.isNull(_columnIndexOfCreatedAt) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfDone) && _stmt.isNull(_columnIndexOfAttachmentCount) && _stmt.isNull(_columnIndexOfUserId) && _stmt.isNull(_columnIndexOfOrder) && _stmt.isNull(_columnIndexOfArchived) && _stmt.isNull(_columnIndexOfDueDate) && _stmt.isNull(_columnIndexOfNotified) && _stmt.isNull(_columnIndexOfOverdue) && _stmt.isNull(_columnIndexOfCommentsUnread) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpCard = new Card();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpCard.setTitle(_tmpTitle);
            final String _tmpDescription;
            if (_stmt.isNull(_columnIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _stmt.getText(_columnIndexOfDescription);
            }
            _tmpCard.setDescription(_tmpDescription);
            final Long _tmpStackId;
            if (_stmt.isNull(_columnIndexOfStackId)) {
              _tmpStackId = null;
            } else {
              _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
            }
            _tmpCard.setStackId(_tmpStackId);
            final String _tmpType;
            if (_stmt.isNull(_columnIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _stmt.getText(_columnIndexOfType);
            }
            _tmpCard.setType(_tmpType);
            final Instant _tmpCreatedAt;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfCreatedAt)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
            _tmpCard.setCreatedAt(_tmpCreatedAt);
            final Instant _tmpDeletedAt;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
            _tmpCard.setDeletedAt(_tmpDeletedAt);
            final Instant _tmpDone;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfDone)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfDone);
            }
            _tmpDone = DateTypeConverter.toInstant(_tmp_2);
            _tmpCard.setDone(_tmpDone);
            final int _tmpAttachmentCount;
            _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
            _tmpCard.setAttachmentCount(_tmpAttachmentCount);
            final Long _tmpUserId;
            if (_stmt.isNull(_columnIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
            }
            _tmpCard.setUserId(_tmpUserId);
            final int _tmpOrder;
            _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
            _tmpCard.setOrder(_tmpOrder);
            final boolean _tmpArchived;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
            _tmpArchived = _tmp_3 != 0;
            _tmpCard.setArchived(_tmpArchived);
            final Instant _tmpDueDate;
            final Long _tmp_4;
            if (_stmt.isNull(_columnIndexOfDueDate)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
            }
            _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
            _tmpCard.setDueDate(_tmpDueDate);
            final boolean _tmpNotified;
            final int _tmp_5;
            _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
            _tmpNotified = _tmp_5 != 0;
            _tmpCard.setNotified(_tmpNotified);
            final int _tmpOverdue;
            _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
            _tmpCard.setOverdue(_tmpOverdue);
            final int _tmpCommentsUnread;
            _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
            _tmpCard.setCommentsUnread(_tmpCommentsUnread);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpCard.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpCard.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpCard.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpCard.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_6;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
            _tmpCard.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_7;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
            _tmpCard.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpCard.setEtag(_tmpEtag);
          } else {
            _tmpCard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_5 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_5);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final ArrayList<User> _tmpAssignedUsersCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpAssignedUsersCollection = _collectionAssignedUsers.get(_tmpKey_6);
          } else {
            _tmpAssignedUsersCollection = new ArrayList<User>();
          }
          final ArrayList<User> _tmpOwnerCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_7 != null) {
            _tmpOwnerCollection = _collectionOwner.get(_tmpKey_7);
          } else {
            _tmpOwnerCollection = new ArrayList<User>();
          }
          final ArrayList<Attachment> _tmpAttachmentsCollection;
          final Long _tmpKey_8;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_8 = null;
          } else {
            _tmpKey_8 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_8 != null) {
            _tmpAttachmentsCollection = _collectionAttachments.get(_tmpKey_8);
          } else {
            _tmpAttachmentsCollection = new ArrayList<Attachment>();
          }
          final ArrayList<Long> _tmpCommentIDsCollection;
          final Long _tmpKey_9;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_9 = null;
          } else {
            _tmpKey_9 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_9 != null) {
            _tmpCommentIDsCollection = _collectionCommentIDs.get(_tmpKey_9);
          } else {
            _tmpCommentIDsCollection = new ArrayList<Long>();
          }
          _result = new FullCard();
          _result.card = _tmpCard;
          _result.labels = _tmpLabelsCollection;
          _result.assignedUsers = _tmpAssignedUsersCollection;
          _result.owner = _tmpOwnerCollection;
          _result.attachments = _tmpAttachmentsCollection;
          _result.commentIDs = _tmpCommentIDsCollection;
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
  public Card getCardByRemoteIdDirectly(final long accountId, final long remoteId) {
    final String _sql = "SELECT * FROM card WHERE accountId = ? and id = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, remoteId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final Card _result;
        if (_stmt.step()) {
          _result = new Card();
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _result.setTitle(_tmpTitle);
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          _result.setDescription(_tmpDescription);
          final Long _tmpStackId;
          if (_stmt.isNull(_columnIndexOfStackId)) {
            _tmpStackId = null;
          } else {
            _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
          }
          _result.setStackId(_tmpStackId);
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          _result.setType(_tmpType);
          final Instant _tmpCreatedAt;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
          _result.setCreatedAt(_tmpCreatedAt);
          final Instant _tmpDeletedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
          _result.setDeletedAt(_tmpDeletedAt);
          final Instant _tmpDone;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfDone)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfDone);
          }
          _tmpDone = DateTypeConverter.toInstant(_tmp_2);
          _result.setDone(_tmpDone);
          final int _tmpAttachmentCount;
          _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
          _result.setAttachmentCount(_tmpAttachmentCount);
          final Long _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
          }
          _result.setUserId(_tmpUserId);
          final int _tmpOrder;
          _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
          _result.setOrder(_tmpOrder);
          final boolean _tmpArchived;
          final int _tmp_3;
          _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
          _tmpArchived = _tmp_3 != 0;
          _result.setArchived(_tmpArchived);
          final Instant _tmpDueDate;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfDueDate)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
          }
          _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
          _result.setDueDate(_tmpDueDate);
          final boolean _tmpNotified;
          final int _tmp_5;
          _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
          _tmpNotified = _tmp_5 != 0;
          _result.setNotified(_tmpNotified);
          final int _tmpOverdue;
          _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
          _result.setOverdue(_tmpOverdue);
          final int _tmpCommentsUnread;
          _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
          _result.setCommentsUnread(_tmpCommentsUnread);
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
  public List<FullCard> getLocallyChangedCardsDirectly(final long accountId) {
    final String _sql = "SELECT * FROM card WHERE accountId = ? and (status<>1 or id is null or lastModified <> lastModifiedLocal)";
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<ArrayList<User>> _collectionAssignedUsers = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<User>> _collectionOwner = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<Attachment>> _collectionAttachments = new LongSparseArray<ArrayList<Attachment>>();
        final LongSparseArray<ArrayList<Long>> _collectionCommentIDs = new LongSparseArray<ArrayList<Long>>();
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
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            if (!_collectionAssignedUsers.containsKey(_tmpKey_1)) {
              _collectionAssignedUsers.put(_tmpKey_1, new ArrayList<User>());
            }
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionOwner.containsKey(_tmpKey_2)) {
              _collectionOwner.put(_tmpKey_2, new ArrayList<User>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionAttachments.containsKey(_tmpKey_3)) {
              _collectionAttachments.put(_tmpKey_3, new ArrayList<Attachment>());
            }
          }
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            if (!_collectionCommentIDs.containsKey(_tmpKey_4)) {
              _collectionCommentIDs.put(_tmpKey_4, new ArrayList<Long>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionAssignedUsers);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser_1(_connection, _collectionOwner);
        __fetchRelationshipAttachmentAsitNiedermannNextcloudDeckModelAttachment(_connection, _collectionAttachments);
        __fetchRelationshipDeckCommentAsjavaLangLong(_connection, _collectionCommentIDs);
        final List<FullCard> _result = new ArrayList<FullCard>();
        while (_stmt.step()) {
          final FullCard _item;
          final Card _tmpCard;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfDescription) && _stmt.isNull(_columnIndexOfStackId) && _stmt.isNull(_columnIndexOfType) && _stmt.isNull(_columnIndexOfCreatedAt) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfDone) && _stmt.isNull(_columnIndexOfAttachmentCount) && _stmt.isNull(_columnIndexOfUserId) && _stmt.isNull(_columnIndexOfOrder) && _stmt.isNull(_columnIndexOfArchived) && _stmt.isNull(_columnIndexOfDueDate) && _stmt.isNull(_columnIndexOfNotified) && _stmt.isNull(_columnIndexOfOverdue) && _stmt.isNull(_columnIndexOfCommentsUnread) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpCard = new Card();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpCard.setTitle(_tmpTitle);
            final String _tmpDescription;
            if (_stmt.isNull(_columnIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _stmt.getText(_columnIndexOfDescription);
            }
            _tmpCard.setDescription(_tmpDescription);
            final Long _tmpStackId;
            if (_stmt.isNull(_columnIndexOfStackId)) {
              _tmpStackId = null;
            } else {
              _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
            }
            _tmpCard.setStackId(_tmpStackId);
            final String _tmpType;
            if (_stmt.isNull(_columnIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _stmt.getText(_columnIndexOfType);
            }
            _tmpCard.setType(_tmpType);
            final Instant _tmpCreatedAt;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfCreatedAt)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
            _tmpCard.setCreatedAt(_tmpCreatedAt);
            final Instant _tmpDeletedAt;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
            _tmpCard.setDeletedAt(_tmpDeletedAt);
            final Instant _tmpDone;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfDone)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfDone);
            }
            _tmpDone = DateTypeConverter.toInstant(_tmp_2);
            _tmpCard.setDone(_tmpDone);
            final int _tmpAttachmentCount;
            _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
            _tmpCard.setAttachmentCount(_tmpAttachmentCount);
            final Long _tmpUserId;
            if (_stmt.isNull(_columnIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
            }
            _tmpCard.setUserId(_tmpUserId);
            final int _tmpOrder;
            _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
            _tmpCard.setOrder(_tmpOrder);
            final boolean _tmpArchived;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
            _tmpArchived = _tmp_3 != 0;
            _tmpCard.setArchived(_tmpArchived);
            final Instant _tmpDueDate;
            final Long _tmp_4;
            if (_stmt.isNull(_columnIndexOfDueDate)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
            }
            _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
            _tmpCard.setDueDate(_tmpDueDate);
            final boolean _tmpNotified;
            final int _tmp_5;
            _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
            _tmpNotified = _tmp_5 != 0;
            _tmpCard.setNotified(_tmpNotified);
            final int _tmpOverdue;
            _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
            _tmpCard.setOverdue(_tmpOverdue);
            final int _tmpCommentsUnread;
            _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
            _tmpCard.setCommentsUnread(_tmpCommentsUnread);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpCard.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpCard.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpCard.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpCard.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_6;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
            _tmpCard.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_7;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
            _tmpCard.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpCard.setEtag(_tmpEtag);
          } else {
            _tmpCard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_5 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_5);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final ArrayList<User> _tmpAssignedUsersCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpAssignedUsersCollection = _collectionAssignedUsers.get(_tmpKey_6);
          } else {
            _tmpAssignedUsersCollection = new ArrayList<User>();
          }
          final ArrayList<User> _tmpOwnerCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_7 != null) {
            _tmpOwnerCollection = _collectionOwner.get(_tmpKey_7);
          } else {
            _tmpOwnerCollection = new ArrayList<User>();
          }
          final ArrayList<Attachment> _tmpAttachmentsCollection;
          final Long _tmpKey_8;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_8 = null;
          } else {
            _tmpKey_8 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_8 != null) {
            _tmpAttachmentsCollection = _collectionAttachments.get(_tmpKey_8);
          } else {
            _tmpAttachmentsCollection = new ArrayList<Attachment>();
          }
          final ArrayList<Long> _tmpCommentIDsCollection;
          final Long _tmpKey_9;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_9 = null;
          } else {
            _tmpKey_9 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_9 != null) {
            _tmpCommentIDsCollection = _collectionCommentIDs.get(_tmpKey_9);
          } else {
            _tmpCommentIDsCollection = new ArrayList<Long>();
          }
          _item = new FullCard();
          _item.card = _tmpCard;
          _item.labels = _tmpLabelsCollection;
          _item.assignedUsers = _tmpAssignedUsersCollection;
          _item.owner = _tmpOwnerCollection;
          _item.attachments = _tmpAttachmentsCollection;
          _item.commentIDs = _tmpCommentIDsCollection;
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<FullCard> getLocallyChangedCardsByLocalStackIdDirectly(final long accountId,
      final long localStackId) {
    final String _sql = "SELECT * FROM card WHERE accountId = ? and stackId = ? and (status<>1 or id is null or lastModified <> lastModifiedLocal)";
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localStackId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<ArrayList<User>> _collectionAssignedUsers = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<User>> _collectionOwner = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<Attachment>> _collectionAttachments = new LongSparseArray<ArrayList<Attachment>>();
        final LongSparseArray<ArrayList<Long>> _collectionCommentIDs = new LongSparseArray<ArrayList<Long>>();
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
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            if (!_collectionAssignedUsers.containsKey(_tmpKey_1)) {
              _collectionAssignedUsers.put(_tmpKey_1, new ArrayList<User>());
            }
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionOwner.containsKey(_tmpKey_2)) {
              _collectionOwner.put(_tmpKey_2, new ArrayList<User>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionAttachments.containsKey(_tmpKey_3)) {
              _collectionAttachments.put(_tmpKey_3, new ArrayList<Attachment>());
            }
          }
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            if (!_collectionCommentIDs.containsKey(_tmpKey_4)) {
              _collectionCommentIDs.put(_tmpKey_4, new ArrayList<Long>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionAssignedUsers);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser_1(_connection, _collectionOwner);
        __fetchRelationshipAttachmentAsitNiedermannNextcloudDeckModelAttachment(_connection, _collectionAttachments);
        __fetchRelationshipDeckCommentAsjavaLangLong(_connection, _collectionCommentIDs);
        final List<FullCard> _result = new ArrayList<FullCard>();
        while (_stmt.step()) {
          final FullCard _item;
          final Card _tmpCard;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfDescription) && _stmt.isNull(_columnIndexOfStackId) && _stmt.isNull(_columnIndexOfType) && _stmt.isNull(_columnIndexOfCreatedAt) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfDone) && _stmt.isNull(_columnIndexOfAttachmentCount) && _stmt.isNull(_columnIndexOfUserId) && _stmt.isNull(_columnIndexOfOrder) && _stmt.isNull(_columnIndexOfArchived) && _stmt.isNull(_columnIndexOfDueDate) && _stmt.isNull(_columnIndexOfNotified) && _stmt.isNull(_columnIndexOfOverdue) && _stmt.isNull(_columnIndexOfCommentsUnread) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpCard = new Card();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpCard.setTitle(_tmpTitle);
            final String _tmpDescription;
            if (_stmt.isNull(_columnIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _stmt.getText(_columnIndexOfDescription);
            }
            _tmpCard.setDescription(_tmpDescription);
            final Long _tmpStackId;
            if (_stmt.isNull(_columnIndexOfStackId)) {
              _tmpStackId = null;
            } else {
              _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
            }
            _tmpCard.setStackId(_tmpStackId);
            final String _tmpType;
            if (_stmt.isNull(_columnIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _stmt.getText(_columnIndexOfType);
            }
            _tmpCard.setType(_tmpType);
            final Instant _tmpCreatedAt;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfCreatedAt)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
            _tmpCard.setCreatedAt(_tmpCreatedAt);
            final Instant _tmpDeletedAt;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
            _tmpCard.setDeletedAt(_tmpDeletedAt);
            final Instant _tmpDone;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfDone)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfDone);
            }
            _tmpDone = DateTypeConverter.toInstant(_tmp_2);
            _tmpCard.setDone(_tmpDone);
            final int _tmpAttachmentCount;
            _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
            _tmpCard.setAttachmentCount(_tmpAttachmentCount);
            final Long _tmpUserId;
            if (_stmt.isNull(_columnIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
            }
            _tmpCard.setUserId(_tmpUserId);
            final int _tmpOrder;
            _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
            _tmpCard.setOrder(_tmpOrder);
            final boolean _tmpArchived;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
            _tmpArchived = _tmp_3 != 0;
            _tmpCard.setArchived(_tmpArchived);
            final Instant _tmpDueDate;
            final Long _tmp_4;
            if (_stmt.isNull(_columnIndexOfDueDate)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
            }
            _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
            _tmpCard.setDueDate(_tmpDueDate);
            final boolean _tmpNotified;
            final int _tmp_5;
            _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
            _tmpNotified = _tmp_5 != 0;
            _tmpCard.setNotified(_tmpNotified);
            final int _tmpOverdue;
            _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
            _tmpCard.setOverdue(_tmpOverdue);
            final int _tmpCommentsUnread;
            _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
            _tmpCard.setCommentsUnread(_tmpCommentsUnread);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpCard.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpCard.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpCard.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpCard.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_6;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
            _tmpCard.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_7;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
            _tmpCard.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpCard.setEtag(_tmpEtag);
          } else {
            _tmpCard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_5 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_5);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final ArrayList<User> _tmpAssignedUsersCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpAssignedUsersCollection = _collectionAssignedUsers.get(_tmpKey_6);
          } else {
            _tmpAssignedUsersCollection = new ArrayList<User>();
          }
          final ArrayList<User> _tmpOwnerCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_7 != null) {
            _tmpOwnerCollection = _collectionOwner.get(_tmpKey_7);
          } else {
            _tmpOwnerCollection = new ArrayList<User>();
          }
          final ArrayList<Attachment> _tmpAttachmentsCollection;
          final Long _tmpKey_8;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_8 = null;
          } else {
            _tmpKey_8 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_8 != null) {
            _tmpAttachmentsCollection = _collectionAttachments.get(_tmpKey_8);
          } else {
            _tmpAttachmentsCollection = new ArrayList<Attachment>();
          }
          final ArrayList<Long> _tmpCommentIDsCollection;
          final Long _tmpKey_9;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_9 = null;
          } else {
            _tmpKey_9 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_9 != null) {
            _tmpCommentIDsCollection = _collectionCommentIDs.get(_tmpKey_9);
          } else {
            _tmpCommentIDsCollection = new ArrayList<Long>();
          }
          _item = new FullCard();
          _item.card = _tmpCard;
          _item.labels = _tmpLabelsCollection;
          _item.assignedUsers = _tmpAssignedUsersCollection;
          _item.owner = _tmpOwnerCollection;
          _item.attachments = _tmpAttachmentsCollection;
          _item.commentIDs = _tmpCommentIDsCollection;
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<Card> getCardsWithLocallyChangedCommentsDirectly(final Long accountId) {
    final String _sql = "SELECT * FROM card c WHERE accountId = ? and exists ( select 1 from DeckComment dc where dc.objectId = c.localId and dc.status<>1)";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (accountId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, accountId);
        }
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<Card> _result = new ArrayList<Card>();
        while (_stmt.step()) {
          final Card _item;
          _item = new Card();
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _item.setTitle(_tmpTitle);
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          _item.setDescription(_tmpDescription);
          final Long _tmpStackId;
          if (_stmt.isNull(_columnIndexOfStackId)) {
            _tmpStackId = null;
          } else {
            _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
          }
          _item.setStackId(_tmpStackId);
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          _item.setType(_tmpType);
          final Instant _tmpCreatedAt;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
          _item.setCreatedAt(_tmpCreatedAt);
          final Instant _tmpDeletedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
          _item.setDeletedAt(_tmpDeletedAt);
          final Instant _tmpDone;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfDone)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfDone);
          }
          _tmpDone = DateTypeConverter.toInstant(_tmp_2);
          _item.setDone(_tmpDone);
          final int _tmpAttachmentCount;
          _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
          _item.setAttachmentCount(_tmpAttachmentCount);
          final Long _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
          }
          _item.setUserId(_tmpUserId);
          final int _tmpOrder;
          _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
          _item.setOrder(_tmpOrder);
          final boolean _tmpArchived;
          final int _tmp_3;
          _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
          _tmpArchived = _tmp_3 != 0;
          _item.setArchived(_tmpArchived);
          final Instant _tmpDueDate;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfDueDate)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
          }
          _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
          _item.setDueDate(_tmpDueDate);
          final boolean _tmpNotified;
          final int _tmp_5;
          _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
          _tmpNotified = _tmp_5 != 0;
          _item.setNotified(_tmpNotified);
          final int _tmpOverdue;
          _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
          _item.setOverdue(_tmpOverdue);
          final int _tmpCommentsUnread;
          _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
          _item.setCommentsUnread(_tmpCommentsUnread);
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
  public List<Card> getCardsWithLocallyChangedCommentsForStackDirectly(final Long localStackId) {
    final String _sql = "SELECT * FROM card c WHERE stackId = ? and exists ( select 1 from DeckComment dc where dc.objectId = c.localId and dc.status<>1)";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (localStackId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localStackId);
        }
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final List<Card> _result = new ArrayList<Card>();
        while (_stmt.step()) {
          final Card _item;
          _item = new Card();
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          _item.setTitle(_tmpTitle);
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          _item.setDescription(_tmpDescription);
          final Long _tmpStackId;
          if (_stmt.isNull(_columnIndexOfStackId)) {
            _tmpStackId = null;
          } else {
            _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
          }
          _item.setStackId(_tmpStackId);
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          _item.setType(_tmpType);
          final Instant _tmpCreatedAt;
          final Long _tmp;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
          _item.setCreatedAt(_tmpCreatedAt);
          final Instant _tmpDeletedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
          _item.setDeletedAt(_tmpDeletedAt);
          final Instant _tmpDone;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfDone)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfDone);
          }
          _tmpDone = DateTypeConverter.toInstant(_tmp_2);
          _item.setDone(_tmpDone);
          final int _tmpAttachmentCount;
          _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
          _item.setAttachmentCount(_tmpAttachmentCount);
          final Long _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
          }
          _item.setUserId(_tmpUserId);
          final int _tmpOrder;
          _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
          _item.setOrder(_tmpOrder);
          final boolean _tmpArchived;
          final int _tmp_3;
          _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
          _tmpArchived = _tmp_3 != 0;
          _item.setArchived(_tmpArchived);
          final Instant _tmpDueDate;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfDueDate)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
          }
          _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
          _item.setDueDate(_tmpDueDate);
          final boolean _tmpNotified;
          final int _tmp_5;
          _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
          _tmpNotified = _tmp_5 != 0;
          _item.setNotified(_tmpNotified);
          final int _tmpOverdue;
          _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
          _item.setOverdue(_tmpOverdue);
          final int _tmpCommentsUnread;
          _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
          _item.setCommentsUnread(_tmpCommentsUnread);
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
  public int countCardsInStackDirectly(final long accountId, final long localStackId) {
    final String _sql = "SELECT count(*) FROM card c WHERE accountId = ? and stackId = ? and status <> 3";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, localStackId);
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
  public Integer getHighestOrderInStack(final Long localStackId) {
    final String _sql = "SELECT coalesce(MAX(`order`), -1) FROM card c WHERE  stackId = ? and status <> 3";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (localStackId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localStackId);
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
  public Long getLocalStackIdByLocalCardId(final Long localCardId) {
    final String _sql = "SELECT c.stackId FROM card c WHERE  localId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (localCardId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, localCardId);
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
  public List<FullCard> getFullCardsForNonSharedBoardsWithDueDateForUpcomingCardsWidgetDirectly(
      final List<Long> accountIds) {
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT * FROM card c WHERE exists(select 1 from Stack s join Board b on s.boardId = b.localId where s.localId = c.stackId and b.archived = 0 and not exists(select 1 from AccessControl ac where ac.boardId = b.localId and status <> 3)) and dueDate is not null and (coalesce(");
    final int _inputSize = accountIds == null ? 1 : accountIds.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(", null) is null or accountId in (");
    final int _inputSize_1 = accountIds == null ? 1 : accountIds.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize_1);
    _stringBuilder.append(")) and status <> 3 and archived = 0");
    final String _sql = _stringBuilder.toString();
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
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
        _argIndex = 1 + _inputSize;
        if (accountIds == null) {
          _stmt.bindNull(_argIndex);
        } else {
          for (Long _item_1 : accountIds) {
            if (_item_1 == null) {
              _stmt.bindNull(_argIndex);
            } else {
              _stmt.bindLong(_argIndex, _item_1);
            }
            _argIndex++;
          }
        }
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<ArrayList<User>> _collectionAssignedUsers = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<User>> _collectionOwner = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<Attachment>> _collectionAttachments = new LongSparseArray<ArrayList<Attachment>>();
        final LongSparseArray<ArrayList<Long>> _collectionCommentIDs = new LongSparseArray<ArrayList<Long>>();
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
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            if (!_collectionAssignedUsers.containsKey(_tmpKey_1)) {
              _collectionAssignedUsers.put(_tmpKey_1, new ArrayList<User>());
            }
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionOwner.containsKey(_tmpKey_2)) {
              _collectionOwner.put(_tmpKey_2, new ArrayList<User>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionAttachments.containsKey(_tmpKey_3)) {
              _collectionAttachments.put(_tmpKey_3, new ArrayList<Attachment>());
            }
          }
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            if (!_collectionCommentIDs.containsKey(_tmpKey_4)) {
              _collectionCommentIDs.put(_tmpKey_4, new ArrayList<Long>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionAssignedUsers);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser_1(_connection, _collectionOwner);
        __fetchRelationshipAttachmentAsitNiedermannNextcloudDeckModelAttachment(_connection, _collectionAttachments);
        __fetchRelationshipDeckCommentAsjavaLangLong(_connection, _collectionCommentIDs);
        final List<FullCard> _result = new ArrayList<FullCard>();
        while (_stmt.step()) {
          final FullCard _item_2;
          final Card _tmpCard;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfDescription) && _stmt.isNull(_columnIndexOfStackId) && _stmt.isNull(_columnIndexOfType) && _stmt.isNull(_columnIndexOfCreatedAt) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfDone) && _stmt.isNull(_columnIndexOfAttachmentCount) && _stmt.isNull(_columnIndexOfUserId) && _stmt.isNull(_columnIndexOfOrder) && _stmt.isNull(_columnIndexOfArchived) && _stmt.isNull(_columnIndexOfDueDate) && _stmt.isNull(_columnIndexOfNotified) && _stmt.isNull(_columnIndexOfOverdue) && _stmt.isNull(_columnIndexOfCommentsUnread) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpCard = new Card();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpCard.setTitle(_tmpTitle);
            final String _tmpDescription;
            if (_stmt.isNull(_columnIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _stmt.getText(_columnIndexOfDescription);
            }
            _tmpCard.setDescription(_tmpDescription);
            final Long _tmpStackId;
            if (_stmt.isNull(_columnIndexOfStackId)) {
              _tmpStackId = null;
            } else {
              _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
            }
            _tmpCard.setStackId(_tmpStackId);
            final String _tmpType;
            if (_stmt.isNull(_columnIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _stmt.getText(_columnIndexOfType);
            }
            _tmpCard.setType(_tmpType);
            final Instant _tmpCreatedAt;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfCreatedAt)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
            _tmpCard.setCreatedAt(_tmpCreatedAt);
            final Instant _tmpDeletedAt;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
            _tmpCard.setDeletedAt(_tmpDeletedAt);
            final Instant _tmpDone;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfDone)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfDone);
            }
            _tmpDone = DateTypeConverter.toInstant(_tmp_2);
            _tmpCard.setDone(_tmpDone);
            final int _tmpAttachmentCount;
            _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
            _tmpCard.setAttachmentCount(_tmpAttachmentCount);
            final Long _tmpUserId;
            if (_stmt.isNull(_columnIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
            }
            _tmpCard.setUserId(_tmpUserId);
            final int _tmpOrder;
            _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
            _tmpCard.setOrder(_tmpOrder);
            final boolean _tmpArchived;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
            _tmpArchived = _tmp_3 != 0;
            _tmpCard.setArchived(_tmpArchived);
            final Instant _tmpDueDate;
            final Long _tmp_4;
            if (_stmt.isNull(_columnIndexOfDueDate)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
            }
            _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
            _tmpCard.setDueDate(_tmpDueDate);
            final boolean _tmpNotified;
            final int _tmp_5;
            _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
            _tmpNotified = _tmp_5 != 0;
            _tmpCard.setNotified(_tmpNotified);
            final int _tmpOverdue;
            _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
            _tmpCard.setOverdue(_tmpOverdue);
            final int _tmpCommentsUnread;
            _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
            _tmpCard.setCommentsUnread(_tmpCommentsUnread);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpCard.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpCard.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpCard.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpCard.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_6;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
            _tmpCard.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_7;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
            _tmpCard.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpCard.setEtag(_tmpEtag);
          } else {
            _tmpCard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_5 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_5);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final ArrayList<User> _tmpAssignedUsersCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpAssignedUsersCollection = _collectionAssignedUsers.get(_tmpKey_6);
          } else {
            _tmpAssignedUsersCollection = new ArrayList<User>();
          }
          final ArrayList<User> _tmpOwnerCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_7 != null) {
            _tmpOwnerCollection = _collectionOwner.get(_tmpKey_7);
          } else {
            _tmpOwnerCollection = new ArrayList<User>();
          }
          final ArrayList<Attachment> _tmpAttachmentsCollection;
          final Long _tmpKey_8;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_8 = null;
          } else {
            _tmpKey_8 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_8 != null) {
            _tmpAttachmentsCollection = _collectionAttachments.get(_tmpKey_8);
          } else {
            _tmpAttachmentsCollection = new ArrayList<Attachment>();
          }
          final ArrayList<Long> _tmpCommentIDsCollection;
          final Long _tmpKey_9;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_9 = null;
          } else {
            _tmpKey_9 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_9 != null) {
            _tmpCommentIDsCollection = _collectionCommentIDs.get(_tmpKey_9);
          } else {
            _tmpCommentIDsCollection = new ArrayList<Long>();
          }
          _item_2 = new FullCard();
          _item_2.card = _tmpCard;
          _item_2.labels = _tmpLabelsCollection;
          _item_2.assignedUsers = _tmpAssignedUsersCollection;
          _item_2.owner = _tmpOwnerCollection;
          _item_2.attachments = _tmpAttachmentsCollection;
          _item_2.commentIDs = _tmpCommentIDsCollection;
          _result.add(_item_2);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<FullCard>> getUpcomingCards() {
    final String _sql = "SELECT c.* FROM card c join stack s on s.localId = c.stackId join board b on b.localId = s.boardId WHERE b.archived = 0 and c.archived = 0 and b.status <> 3 and s.status <> 3 and c.status <> 3 and (c.deletedAt is null or c.deletedAt = 0) and (s.deletedAt is null or s.deletedAt = 0) and (b.deletedAt is null or b.deletedAt = 0) and (c.done      is null or c.done      = 0) and ((c.dueDate is not null AND NOT exists(select 1 from AccessControl ac where ac.boardId = b.localId and ac.status <> 3))OR (exists(select 1 from AccessControl ac where ac.boardId = b.localId and ac.status <> 3) AND ((c.dueDate is not null AND not exists(select 1 from JoinCardWithUser j where j.cardId = c.localId)) OR exists(select 1 from JoinCardWithUser j where j.cardId = c.localId and j.userId in (select u.localId from user u where u.uid in (select a.userName from Account a))))))ORDER BY c.dueDate asc";
    return __db.getInvalidationTracker().createLiveData(new String[] {"JoinCardWithLabel", "Label",
        "JoinCardWithUser", "User", "Attachment", "DeckComment", "card", "stack", "board",
        "AccessControl", "user", "Account"}, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<ArrayList<User>> _collectionAssignedUsers = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<User>> _collectionOwner = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<Attachment>> _collectionAttachments = new LongSparseArray<ArrayList<Attachment>>();
        final LongSparseArray<ArrayList<Long>> _collectionCommentIDs = new LongSparseArray<ArrayList<Long>>();
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
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            if (!_collectionAssignedUsers.containsKey(_tmpKey_1)) {
              _collectionAssignedUsers.put(_tmpKey_1, new ArrayList<User>());
            }
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionOwner.containsKey(_tmpKey_2)) {
              _collectionOwner.put(_tmpKey_2, new ArrayList<User>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionAttachments.containsKey(_tmpKey_3)) {
              _collectionAttachments.put(_tmpKey_3, new ArrayList<Attachment>());
            }
          }
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            if (!_collectionCommentIDs.containsKey(_tmpKey_4)) {
              _collectionCommentIDs.put(_tmpKey_4, new ArrayList<Long>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionAssignedUsers);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser_1(_connection, _collectionOwner);
        __fetchRelationshipAttachmentAsitNiedermannNextcloudDeckModelAttachment(_connection, _collectionAttachments);
        __fetchRelationshipDeckCommentAsjavaLangLong(_connection, _collectionCommentIDs);
        final List<FullCard> _result = new ArrayList<FullCard>();
        while (_stmt.step()) {
          final FullCard _item;
          final Card _tmpCard;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfDescription) && _stmt.isNull(_columnIndexOfStackId) && _stmt.isNull(_columnIndexOfType) && _stmt.isNull(_columnIndexOfCreatedAt) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfDone) && _stmt.isNull(_columnIndexOfAttachmentCount) && _stmt.isNull(_columnIndexOfUserId) && _stmt.isNull(_columnIndexOfOrder) && _stmt.isNull(_columnIndexOfArchived) && _stmt.isNull(_columnIndexOfDueDate) && _stmt.isNull(_columnIndexOfNotified) && _stmt.isNull(_columnIndexOfOverdue) && _stmt.isNull(_columnIndexOfCommentsUnread) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpCard = new Card();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpCard.setTitle(_tmpTitle);
            final String _tmpDescription;
            if (_stmt.isNull(_columnIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _stmt.getText(_columnIndexOfDescription);
            }
            _tmpCard.setDescription(_tmpDescription);
            final Long _tmpStackId;
            if (_stmt.isNull(_columnIndexOfStackId)) {
              _tmpStackId = null;
            } else {
              _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
            }
            _tmpCard.setStackId(_tmpStackId);
            final String _tmpType;
            if (_stmt.isNull(_columnIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _stmt.getText(_columnIndexOfType);
            }
            _tmpCard.setType(_tmpType);
            final Instant _tmpCreatedAt;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfCreatedAt)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
            _tmpCard.setCreatedAt(_tmpCreatedAt);
            final Instant _tmpDeletedAt;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
            _tmpCard.setDeletedAt(_tmpDeletedAt);
            final Instant _tmpDone;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfDone)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfDone);
            }
            _tmpDone = DateTypeConverter.toInstant(_tmp_2);
            _tmpCard.setDone(_tmpDone);
            final int _tmpAttachmentCount;
            _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
            _tmpCard.setAttachmentCount(_tmpAttachmentCount);
            final Long _tmpUserId;
            if (_stmt.isNull(_columnIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
            }
            _tmpCard.setUserId(_tmpUserId);
            final int _tmpOrder;
            _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
            _tmpCard.setOrder(_tmpOrder);
            final boolean _tmpArchived;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
            _tmpArchived = _tmp_3 != 0;
            _tmpCard.setArchived(_tmpArchived);
            final Instant _tmpDueDate;
            final Long _tmp_4;
            if (_stmt.isNull(_columnIndexOfDueDate)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
            }
            _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
            _tmpCard.setDueDate(_tmpDueDate);
            final boolean _tmpNotified;
            final int _tmp_5;
            _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
            _tmpNotified = _tmp_5 != 0;
            _tmpCard.setNotified(_tmpNotified);
            final int _tmpOverdue;
            _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
            _tmpCard.setOverdue(_tmpOverdue);
            final int _tmpCommentsUnread;
            _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
            _tmpCard.setCommentsUnread(_tmpCommentsUnread);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpCard.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpCard.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpCard.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpCard.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_6;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
            _tmpCard.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_7;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
            _tmpCard.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpCard.setEtag(_tmpEtag);
          } else {
            _tmpCard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_5 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_5);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final ArrayList<User> _tmpAssignedUsersCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpAssignedUsersCollection = _collectionAssignedUsers.get(_tmpKey_6);
          } else {
            _tmpAssignedUsersCollection = new ArrayList<User>();
          }
          final ArrayList<User> _tmpOwnerCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_7 != null) {
            _tmpOwnerCollection = _collectionOwner.get(_tmpKey_7);
          } else {
            _tmpOwnerCollection = new ArrayList<User>();
          }
          final ArrayList<Attachment> _tmpAttachmentsCollection;
          final Long _tmpKey_8;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_8 = null;
          } else {
            _tmpKey_8 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_8 != null) {
            _tmpAttachmentsCollection = _collectionAttachments.get(_tmpKey_8);
          } else {
            _tmpAttachmentsCollection = new ArrayList<Attachment>();
          }
          final ArrayList<Long> _tmpCommentIDsCollection;
          final Long _tmpKey_9;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_9 = null;
          } else {
            _tmpKey_9 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_9 != null) {
            _tmpCommentIDsCollection = _collectionCommentIDs.get(_tmpKey_9);
          } else {
            _tmpCommentIDsCollection = new ArrayList<Long>();
          }
          _item = new FullCard();
          _item.card = _tmpCard;
          _item.labels = _tmpLabelsCollection;
          _item.assignedUsers = _tmpAssignedUsersCollection;
          _item.owner = _tmpOwnerCollection;
          _item.attachments = _tmpAttachmentsCollection;
          _item.commentIDs = _tmpCommentIDsCollection;
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<FullCard> getUpcomingCardsDirectly() {
    final String _sql = "SELECT c.* FROM card c join stack s on s.localId = c.stackId join board b on b.localId = s.boardId WHERE b.archived = 0 and c.archived = 0 and b.status <> 3 and s.status <> 3 and c.status <> 3 and (c.deletedAt is null or c.deletedAt = 0) and (s.deletedAt is null or s.deletedAt = 0) and (b.deletedAt is null or b.deletedAt = 0) and (c.done      is null or c.done      = 0) and ((c.dueDate is not null AND NOT exists(select 1 from AccessControl ac where ac.boardId = b.localId and ac.status <> 3))OR (exists(select 1 from AccessControl ac where ac.boardId = b.localId and ac.status <> 3) AND ((c.dueDate is not null AND not exists(select 1 from JoinCardWithUser j where j.cardId = c.localId)) OR exists(select 1 from JoinCardWithUser j where j.cardId = c.localId and j.userId in (select u.localId from user u where u.uid in (select a.userName from Account a))))))ORDER BY c.dueDate asc";
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<ArrayList<User>> _collectionAssignedUsers = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<User>> _collectionOwner = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<Attachment>> _collectionAttachments = new LongSparseArray<ArrayList<Attachment>>();
        final LongSparseArray<ArrayList<Long>> _collectionCommentIDs = new LongSparseArray<ArrayList<Long>>();
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
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            if (!_collectionAssignedUsers.containsKey(_tmpKey_1)) {
              _collectionAssignedUsers.put(_tmpKey_1, new ArrayList<User>());
            }
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionOwner.containsKey(_tmpKey_2)) {
              _collectionOwner.put(_tmpKey_2, new ArrayList<User>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionAttachments.containsKey(_tmpKey_3)) {
              _collectionAttachments.put(_tmpKey_3, new ArrayList<Attachment>());
            }
          }
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            if (!_collectionCommentIDs.containsKey(_tmpKey_4)) {
              _collectionCommentIDs.put(_tmpKey_4, new ArrayList<Long>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionAssignedUsers);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser_1(_connection, _collectionOwner);
        __fetchRelationshipAttachmentAsitNiedermannNextcloudDeckModelAttachment(_connection, _collectionAttachments);
        __fetchRelationshipDeckCommentAsjavaLangLong(_connection, _collectionCommentIDs);
        final List<FullCard> _result = new ArrayList<FullCard>();
        while (_stmt.step()) {
          final FullCard _item;
          final Card _tmpCard;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfDescription) && _stmt.isNull(_columnIndexOfStackId) && _stmt.isNull(_columnIndexOfType) && _stmt.isNull(_columnIndexOfCreatedAt) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfDone) && _stmt.isNull(_columnIndexOfAttachmentCount) && _stmt.isNull(_columnIndexOfUserId) && _stmt.isNull(_columnIndexOfOrder) && _stmt.isNull(_columnIndexOfArchived) && _stmt.isNull(_columnIndexOfDueDate) && _stmt.isNull(_columnIndexOfNotified) && _stmt.isNull(_columnIndexOfOverdue) && _stmt.isNull(_columnIndexOfCommentsUnread) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpCard = new Card();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpCard.setTitle(_tmpTitle);
            final String _tmpDescription;
            if (_stmt.isNull(_columnIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _stmt.getText(_columnIndexOfDescription);
            }
            _tmpCard.setDescription(_tmpDescription);
            final Long _tmpStackId;
            if (_stmt.isNull(_columnIndexOfStackId)) {
              _tmpStackId = null;
            } else {
              _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
            }
            _tmpCard.setStackId(_tmpStackId);
            final String _tmpType;
            if (_stmt.isNull(_columnIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _stmt.getText(_columnIndexOfType);
            }
            _tmpCard.setType(_tmpType);
            final Instant _tmpCreatedAt;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfCreatedAt)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
            _tmpCard.setCreatedAt(_tmpCreatedAt);
            final Instant _tmpDeletedAt;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
            _tmpCard.setDeletedAt(_tmpDeletedAt);
            final Instant _tmpDone;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfDone)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfDone);
            }
            _tmpDone = DateTypeConverter.toInstant(_tmp_2);
            _tmpCard.setDone(_tmpDone);
            final int _tmpAttachmentCount;
            _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
            _tmpCard.setAttachmentCount(_tmpAttachmentCount);
            final Long _tmpUserId;
            if (_stmt.isNull(_columnIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
            }
            _tmpCard.setUserId(_tmpUserId);
            final int _tmpOrder;
            _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
            _tmpCard.setOrder(_tmpOrder);
            final boolean _tmpArchived;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
            _tmpArchived = _tmp_3 != 0;
            _tmpCard.setArchived(_tmpArchived);
            final Instant _tmpDueDate;
            final Long _tmp_4;
            if (_stmt.isNull(_columnIndexOfDueDate)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
            }
            _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
            _tmpCard.setDueDate(_tmpDueDate);
            final boolean _tmpNotified;
            final int _tmp_5;
            _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
            _tmpNotified = _tmp_5 != 0;
            _tmpCard.setNotified(_tmpNotified);
            final int _tmpOverdue;
            _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
            _tmpCard.setOverdue(_tmpOverdue);
            final int _tmpCommentsUnread;
            _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
            _tmpCard.setCommentsUnread(_tmpCommentsUnread);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpCard.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpCard.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpCard.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpCard.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_6;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
            _tmpCard.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_7;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
            _tmpCard.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpCard.setEtag(_tmpEtag);
          } else {
            _tmpCard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_5 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_5);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final ArrayList<User> _tmpAssignedUsersCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpAssignedUsersCollection = _collectionAssignedUsers.get(_tmpKey_6);
          } else {
            _tmpAssignedUsersCollection = new ArrayList<User>();
          }
          final ArrayList<User> _tmpOwnerCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_7 != null) {
            _tmpOwnerCollection = _collectionOwner.get(_tmpKey_7);
          } else {
            _tmpOwnerCollection = new ArrayList<User>();
          }
          final ArrayList<Attachment> _tmpAttachmentsCollection;
          final Long _tmpKey_8;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_8 = null;
          } else {
            _tmpKey_8 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_8 != null) {
            _tmpAttachmentsCollection = _collectionAttachments.get(_tmpKey_8);
          } else {
            _tmpAttachmentsCollection = new ArrayList<Attachment>();
          }
          final ArrayList<Long> _tmpCommentIDsCollection;
          final Long _tmpKey_9;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_9 = null;
          } else {
            _tmpKey_9 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_9 != null) {
            _tmpCommentIDsCollection = _collectionCommentIDs.get(_tmpKey_9);
          } else {
            _tmpCommentIDsCollection = new ArrayList<Long>();
          }
          _item = new FullCard();
          _item.card = _tmpCard;
          _item.labels = _tmpLabelsCollection;
          _item.assignedUsers = _tmpAssignedUsersCollection;
          _item.owner = _tmpOwnerCollection;
          _item.attachments = _tmpAttachmentsCollection;
          _item.commentIDs = _tmpCommentIDsCollection;
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<FullCard>> searchCard(final long accountId, final long localBoardId,
      final String term) {
    final String _sql = "SELECT c.* FROM card c inner join Stack s on c.stackId = s.localId WHERE s.boardId = ? and (c.title like ? or c.description like ?) and c.accountId = ? and s.accountId = ? and c.status <> 3 and s.status <> 3 and c.archived = 0 order by s.`order`, c.`order`";
    return __db.getInvalidationTracker().createLiveData(new String[] {"JoinCardWithLabel", "Label",
        "JoinCardWithUser", "User", "Attachment", "DeckComment", "card",
        "Stack"}, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, localBoardId);
        _argIndex = 2;
        if (term == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, term);
        }
        _argIndex = 3;
        if (term == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, term);
        }
        _argIndex = 4;
        _stmt.bindLong(_argIndex, accountId);
        _argIndex = 5;
        _stmt.bindLong(_argIndex, accountId);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<ArrayList<User>> _collectionAssignedUsers = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<User>> _collectionOwner = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<Attachment>> _collectionAttachments = new LongSparseArray<ArrayList<Attachment>>();
        final LongSparseArray<ArrayList<Long>> _collectionCommentIDs = new LongSparseArray<ArrayList<Long>>();
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
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            if (!_collectionAssignedUsers.containsKey(_tmpKey_1)) {
              _collectionAssignedUsers.put(_tmpKey_1, new ArrayList<User>());
            }
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionOwner.containsKey(_tmpKey_2)) {
              _collectionOwner.put(_tmpKey_2, new ArrayList<User>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionAttachments.containsKey(_tmpKey_3)) {
              _collectionAttachments.put(_tmpKey_3, new ArrayList<Attachment>());
            }
          }
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            if (!_collectionCommentIDs.containsKey(_tmpKey_4)) {
              _collectionCommentIDs.put(_tmpKey_4, new ArrayList<Long>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionAssignedUsers);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser_1(_connection, _collectionOwner);
        __fetchRelationshipAttachmentAsitNiedermannNextcloudDeckModelAttachment(_connection, _collectionAttachments);
        __fetchRelationshipDeckCommentAsjavaLangLong(_connection, _collectionCommentIDs);
        final List<FullCard> _result = new ArrayList<FullCard>();
        while (_stmt.step()) {
          final FullCard _item;
          final Card _tmpCard;
          if (!(_stmt.isNull(_columnIndexOfTitle) && _stmt.isNull(_columnIndexOfDescription) && _stmt.isNull(_columnIndexOfStackId) && _stmt.isNull(_columnIndexOfType) && _stmt.isNull(_columnIndexOfCreatedAt) && _stmt.isNull(_columnIndexOfDeletedAt) && _stmt.isNull(_columnIndexOfDone) && _stmt.isNull(_columnIndexOfAttachmentCount) && _stmt.isNull(_columnIndexOfUserId) && _stmt.isNull(_columnIndexOfOrder) && _stmt.isNull(_columnIndexOfArchived) && _stmt.isNull(_columnIndexOfDueDate) && _stmt.isNull(_columnIndexOfNotified) && _stmt.isNull(_columnIndexOfOverdue) && _stmt.isNull(_columnIndexOfCommentsUnread) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
            _tmpCard = new Card();
            final String _tmpTitle;
            if (_stmt.isNull(_columnIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _stmt.getText(_columnIndexOfTitle);
            }
            _tmpCard.setTitle(_tmpTitle);
            final String _tmpDescription;
            if (_stmt.isNull(_columnIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _stmt.getText(_columnIndexOfDescription);
            }
            _tmpCard.setDescription(_tmpDescription);
            final Long _tmpStackId;
            if (_stmt.isNull(_columnIndexOfStackId)) {
              _tmpStackId = null;
            } else {
              _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
            }
            _tmpCard.setStackId(_tmpStackId);
            final String _tmpType;
            if (_stmt.isNull(_columnIndexOfType)) {
              _tmpType = null;
            } else {
              _tmpType = _stmt.getText(_columnIndexOfType);
            }
            _tmpCard.setType(_tmpType);
            final Instant _tmpCreatedAt;
            final Long _tmp;
            if (_stmt.isNull(_columnIndexOfCreatedAt)) {
              _tmp = null;
            } else {
              _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
            }
            _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
            _tmpCard.setCreatedAt(_tmpCreatedAt);
            final Instant _tmpDeletedAt;
            final Long _tmp_1;
            if (_stmt.isNull(_columnIndexOfDeletedAt)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
            }
            _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
            _tmpCard.setDeletedAt(_tmpDeletedAt);
            final Instant _tmpDone;
            final Long _tmp_2;
            if (_stmt.isNull(_columnIndexOfDone)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _stmt.getLong(_columnIndexOfDone);
            }
            _tmpDone = DateTypeConverter.toInstant(_tmp_2);
            _tmpCard.setDone(_tmpDone);
            final int _tmpAttachmentCount;
            _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
            _tmpCard.setAttachmentCount(_tmpAttachmentCount);
            final Long _tmpUserId;
            if (_stmt.isNull(_columnIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
            }
            _tmpCard.setUserId(_tmpUserId);
            final int _tmpOrder;
            _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
            _tmpCard.setOrder(_tmpOrder);
            final boolean _tmpArchived;
            final int _tmp_3;
            _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
            _tmpArchived = _tmp_3 != 0;
            _tmpCard.setArchived(_tmpArchived);
            final Instant _tmpDueDate;
            final Long _tmp_4;
            if (_stmt.isNull(_columnIndexOfDueDate)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
            }
            _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
            _tmpCard.setDueDate(_tmpDueDate);
            final boolean _tmpNotified;
            final int _tmp_5;
            _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
            _tmpNotified = _tmp_5 != 0;
            _tmpCard.setNotified(_tmpNotified);
            final int _tmpOverdue;
            _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
            _tmpCard.setOverdue(_tmpOverdue);
            final int _tmpCommentsUnread;
            _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
            _tmpCard.setCommentsUnread(_tmpCommentsUnread);
            final Long _tmpLocalId;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
            }
            _tmpCard.setLocalId(_tmpLocalId);
            final long _tmpAccountId;
            _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
            _tmpCard.setAccountId(_tmpAccountId);
            final Long _tmpId;
            if (_stmt.isNull(_columnIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _stmt.getLong(_columnIndexOfId);
            }
            _tmpCard.setId(_tmpId);
            final int _tmpStatus;
            _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
            _tmpCard.setStatus(_tmpStatus);
            final Instant _tmpLastModified;
            final Long _tmp_6;
            if (_stmt.isNull(_columnIndexOfLastModified)) {
              _tmp_6 = null;
            } else {
              _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
            }
            _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
            _tmpCard.setLastModified(_tmpLastModified);
            final Instant _tmpLastModifiedLocal;
            final Long _tmp_7;
            if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
              _tmp_7 = null;
            } else {
              _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
            }
            _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
            _tmpCard.setLastModifiedLocal(_tmpLastModifiedLocal);
            final String _tmpEtag;
            if (_stmt.isNull(_columnIndexOfEtag)) {
              _tmpEtag = null;
            } else {
              _tmpEtag = _stmt.getText(_columnIndexOfEtag);
            }
            _tmpCard.setEtag(_tmpEtag);
          } else {
            _tmpCard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_5 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_5);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final ArrayList<User> _tmpAssignedUsersCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpAssignedUsersCollection = _collectionAssignedUsers.get(_tmpKey_6);
          } else {
            _tmpAssignedUsersCollection = new ArrayList<User>();
          }
          final ArrayList<User> _tmpOwnerCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_7 != null) {
            _tmpOwnerCollection = _collectionOwner.get(_tmpKey_7);
          } else {
            _tmpOwnerCollection = new ArrayList<User>();
          }
          final ArrayList<Attachment> _tmpAttachmentsCollection;
          final Long _tmpKey_8;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_8 = null;
          } else {
            _tmpKey_8 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_8 != null) {
            _tmpAttachmentsCollection = _collectionAttachments.get(_tmpKey_8);
          } else {
            _tmpAttachmentsCollection = new ArrayList<Attachment>();
          }
          final ArrayList<Long> _tmpCommentIDsCollection;
          final Long _tmpKey_9;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_9 = null;
          } else {
            _tmpKey_9 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_9 != null) {
            _tmpCommentIDsCollection = _collectionCommentIDs.get(_tmpKey_9);
          } else {
            _tmpCommentIDsCollection = new ArrayList<Long>();
          }
          _item = new FullCard();
          _item.card = _tmpCard;
          _item.labels = _tmpLabelsCollection;
          _item.assignedUsers = _tmpAssignedUsersCollection;
          _item.owner = _tmpOwnerCollection;
          _item.attachments = _tmpAttachmentsCollection;
          _item.commentIDs = _tmpCommentIDsCollection;
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<Long> getAllIDs() {
    final String _sql = "SELECT s.localId FROM card s";
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

  @Override
  public LiveData<List<FullCard>> getFilteredFullCardsForStack(final SupportSQLiteQuery query) {
    final RoomRawQuery _rawQuery = RoomSQLiteQuery.copyFrom(query).toRoomRawQuery();
    final String _sql = _rawQuery.getSql();
    return __db.getInvalidationTracker().createLiveData(new String[] {"JoinCardWithLabel", "Label",
        "JoinCardWithUser", "User", "Attachment", "DeckComment", "Card"}, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _rawQuery.getBindingFunction().invoke(_stmt);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndex(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndex(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndex(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndex(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndex(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndex(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndex(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndex(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndex(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndex(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndex(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndex(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndex(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndex(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndex(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndex(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndex(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndex(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndex(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndex(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndex(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndex(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<ArrayList<User>> _collectionAssignedUsers = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<User>> _collectionOwner = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<Attachment>> _collectionAttachments = new LongSparseArray<ArrayList<Attachment>>();
        final LongSparseArray<ArrayList<Long>> _collectionCommentIDs = new LongSparseArray<ArrayList<Long>>();
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
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            if (!_collectionAssignedUsers.containsKey(_tmpKey_1)) {
              _collectionAssignedUsers.put(_tmpKey_1, new ArrayList<User>());
            }
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionOwner.containsKey(_tmpKey_2)) {
              _collectionOwner.put(_tmpKey_2, new ArrayList<User>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionAttachments.containsKey(_tmpKey_3)) {
              _collectionAttachments.put(_tmpKey_3, new ArrayList<Attachment>());
            }
          }
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            if (!_collectionCommentIDs.containsKey(_tmpKey_4)) {
              _collectionCommentIDs.put(_tmpKey_4, new ArrayList<Long>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionAssignedUsers);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser_1(_connection, _collectionOwner);
        __fetchRelationshipAttachmentAsitNiedermannNextcloudDeckModelAttachment(_connection, _collectionAttachments);
        __fetchRelationshipDeckCommentAsjavaLangLong(_connection, _collectionCommentIDs);
        final List<FullCard> _result = new ArrayList<FullCard>();
        while (_stmt.step()) {
          final FullCard _item;
          final Card _tmpCard;
          if (!((_columnIndexOfTitle == -1 || _stmt.isNull(_columnIndexOfTitle)) && (_columnIndexOfDescription == -1 || _stmt.isNull(_columnIndexOfDescription)) && (_columnIndexOfStackId == -1 || _stmt.isNull(_columnIndexOfStackId)) && (_columnIndexOfType == -1 || _stmt.isNull(_columnIndexOfType)) && (_columnIndexOfCreatedAt == -1 || _stmt.isNull(_columnIndexOfCreatedAt)) && (_columnIndexOfDeletedAt == -1 || _stmt.isNull(_columnIndexOfDeletedAt)) && (_columnIndexOfDone == -1 || _stmt.isNull(_columnIndexOfDone)) && (_columnIndexOfAttachmentCount == -1 || _stmt.isNull(_columnIndexOfAttachmentCount)) && (_columnIndexOfUserId == -1 || _stmt.isNull(_columnIndexOfUserId)) && (_columnIndexOfOrder == -1 || _stmt.isNull(_columnIndexOfOrder)) && (_columnIndexOfArchived == -1 || _stmt.isNull(_columnIndexOfArchived)) && (_columnIndexOfDueDate == -1 || _stmt.isNull(_columnIndexOfDueDate)) && (_columnIndexOfNotified == -1 || _stmt.isNull(_columnIndexOfNotified)) && (_columnIndexOfOverdue == -1 || _stmt.isNull(_columnIndexOfOverdue)) && (_columnIndexOfCommentsUnread == -1 || _stmt.isNull(_columnIndexOfCommentsUnread)) && (_columnIndexOfLocalId == -1 || _stmt.isNull(_columnIndexOfLocalId)) && (_columnIndexOfAccountId == -1 || _stmt.isNull(_columnIndexOfAccountId)) && (_columnIndexOfId == -1 || _stmt.isNull(_columnIndexOfId)) && (_columnIndexOfStatus == -1 || _stmt.isNull(_columnIndexOfStatus)) && (_columnIndexOfLastModified == -1 || _stmt.isNull(_columnIndexOfLastModified)) && (_columnIndexOfLastModifiedLocal == -1 || _stmt.isNull(_columnIndexOfLastModifiedLocal)) && (_columnIndexOfEtag == -1 || _stmt.isNull(_columnIndexOfEtag)))) {
            _tmpCard = new Card();
            if (_columnIndexOfTitle != -1) {
              final String _tmpTitle;
              if (_stmt.isNull(_columnIndexOfTitle)) {
                _tmpTitle = null;
              } else {
                _tmpTitle = _stmt.getText(_columnIndexOfTitle);
              }
              _tmpCard.setTitle(_tmpTitle);
            }
            if (_columnIndexOfDescription != -1) {
              final String _tmpDescription;
              if (_stmt.isNull(_columnIndexOfDescription)) {
                _tmpDescription = null;
              } else {
                _tmpDescription = _stmt.getText(_columnIndexOfDescription);
              }
              _tmpCard.setDescription(_tmpDescription);
            }
            if (_columnIndexOfStackId != -1) {
              final Long _tmpStackId;
              if (_stmt.isNull(_columnIndexOfStackId)) {
                _tmpStackId = null;
              } else {
                _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
              }
              _tmpCard.setStackId(_tmpStackId);
            }
            if (_columnIndexOfType != -1) {
              final String _tmpType;
              if (_stmt.isNull(_columnIndexOfType)) {
                _tmpType = null;
              } else {
                _tmpType = _stmt.getText(_columnIndexOfType);
              }
              _tmpCard.setType(_tmpType);
            }
            if (_columnIndexOfCreatedAt != -1) {
              final Instant _tmpCreatedAt;
              final Long _tmp;
              if (_stmt.isNull(_columnIndexOfCreatedAt)) {
                _tmp = null;
              } else {
                _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
              }
              _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
              _tmpCard.setCreatedAt(_tmpCreatedAt);
            }
            if (_columnIndexOfDeletedAt != -1) {
              final Instant _tmpDeletedAt;
              final Long _tmp_1;
              if (_stmt.isNull(_columnIndexOfDeletedAt)) {
                _tmp_1 = null;
              } else {
                _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
              }
              _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
              _tmpCard.setDeletedAt(_tmpDeletedAt);
            }
            if (_columnIndexOfDone != -1) {
              final Instant _tmpDone;
              final Long _tmp_2;
              if (_stmt.isNull(_columnIndexOfDone)) {
                _tmp_2 = null;
              } else {
                _tmp_2 = _stmt.getLong(_columnIndexOfDone);
              }
              _tmpDone = DateTypeConverter.toInstant(_tmp_2);
              _tmpCard.setDone(_tmpDone);
            }
            if (_columnIndexOfAttachmentCount != -1) {
              final int _tmpAttachmentCount;
              _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
              _tmpCard.setAttachmentCount(_tmpAttachmentCount);
            }
            if (_columnIndexOfUserId != -1) {
              final Long _tmpUserId;
              if (_stmt.isNull(_columnIndexOfUserId)) {
                _tmpUserId = null;
              } else {
                _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
              }
              _tmpCard.setUserId(_tmpUserId);
            }
            if (_columnIndexOfOrder != -1) {
              final int _tmpOrder;
              _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
              _tmpCard.setOrder(_tmpOrder);
            }
            if (_columnIndexOfArchived != -1) {
              final boolean _tmpArchived;
              final int _tmp_3;
              _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
              _tmpArchived = _tmp_3 != 0;
              _tmpCard.setArchived(_tmpArchived);
            }
            if (_columnIndexOfDueDate != -1) {
              final Instant _tmpDueDate;
              final Long _tmp_4;
              if (_stmt.isNull(_columnIndexOfDueDate)) {
                _tmp_4 = null;
              } else {
                _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
              }
              _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
              _tmpCard.setDueDate(_tmpDueDate);
            }
            if (_columnIndexOfNotified != -1) {
              final boolean _tmpNotified;
              final int _tmp_5;
              _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
              _tmpNotified = _tmp_5 != 0;
              _tmpCard.setNotified(_tmpNotified);
            }
            if (_columnIndexOfOverdue != -1) {
              final int _tmpOverdue;
              _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
              _tmpCard.setOverdue(_tmpOverdue);
            }
            if (_columnIndexOfCommentsUnread != -1) {
              final int _tmpCommentsUnread;
              _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
              _tmpCard.setCommentsUnread(_tmpCommentsUnread);
            }
            if (_columnIndexOfLocalId != -1) {
              final Long _tmpLocalId;
              if (_stmt.isNull(_columnIndexOfLocalId)) {
                _tmpLocalId = null;
              } else {
                _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
              }
              _tmpCard.setLocalId(_tmpLocalId);
            }
            if (_columnIndexOfAccountId != -1) {
              final long _tmpAccountId;
              _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
              _tmpCard.setAccountId(_tmpAccountId);
            }
            if (_columnIndexOfId != -1) {
              final Long _tmpId;
              if (_stmt.isNull(_columnIndexOfId)) {
                _tmpId = null;
              } else {
                _tmpId = _stmt.getLong(_columnIndexOfId);
              }
              _tmpCard.setId(_tmpId);
            }
            if (_columnIndexOfStatus != -1) {
              final int _tmpStatus;
              _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
              _tmpCard.setStatus(_tmpStatus);
            }
            if (_columnIndexOfLastModified != -1) {
              final Instant _tmpLastModified;
              final Long _tmp_6;
              if (_stmt.isNull(_columnIndexOfLastModified)) {
                _tmp_6 = null;
              } else {
                _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
              }
              _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
              _tmpCard.setLastModified(_tmpLastModified);
            }
            if (_columnIndexOfLastModifiedLocal != -1) {
              final Instant _tmpLastModifiedLocal;
              final Long _tmp_7;
              if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
                _tmp_7 = null;
              } else {
                _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
              }
              _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
              _tmpCard.setLastModifiedLocal(_tmpLastModifiedLocal);
            }
            if (_columnIndexOfEtag != -1) {
              final String _tmpEtag;
              if (_stmt.isNull(_columnIndexOfEtag)) {
                _tmpEtag = null;
              } else {
                _tmpEtag = _stmt.getText(_columnIndexOfEtag);
              }
              _tmpCard.setEtag(_tmpEtag);
            }
          } else {
            _tmpCard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_5 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_5);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final ArrayList<User> _tmpAssignedUsersCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpAssignedUsersCollection = _collectionAssignedUsers.get(_tmpKey_6);
          } else {
            _tmpAssignedUsersCollection = new ArrayList<User>();
          }
          final ArrayList<User> _tmpOwnerCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_7 != null) {
            _tmpOwnerCollection = _collectionOwner.get(_tmpKey_7);
          } else {
            _tmpOwnerCollection = new ArrayList<User>();
          }
          final ArrayList<Attachment> _tmpAttachmentsCollection;
          final Long _tmpKey_8;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_8 = null;
          } else {
            _tmpKey_8 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_8 != null) {
            _tmpAttachmentsCollection = _collectionAttachments.get(_tmpKey_8);
          } else {
            _tmpAttachmentsCollection = new ArrayList<Attachment>();
          }
          final ArrayList<Long> _tmpCommentIDsCollection;
          final Long _tmpKey_9;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_9 = null;
          } else {
            _tmpKey_9 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_9 != null) {
            _tmpCommentIDsCollection = _collectionCommentIDs.get(_tmpKey_9);
          } else {
            _tmpCommentIDsCollection = new ArrayList<Long>();
          }
          _item = new FullCard();
          _item.card = _tmpCard;
          _item.labels = _tmpLabelsCollection;
          _item.assignedUsers = _tmpAssignedUsersCollection;
          _item.owner = _tmpOwnerCollection;
          _item.attachments = _tmpAttachmentsCollection;
          _item.commentIDs = _tmpCommentIDsCollection;
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<FullCard> getFilteredFullCardsForStackDirectly(final SupportSQLiteQuery query) {
    final RoomRawQuery _rawQuery = RoomSQLiteQuery.copyFrom(query).toRoomRawQuery();
    final String _sql = _rawQuery.getSql();
    return DBUtil.performBlocking(__db, true, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _rawQuery.getBindingFunction().invoke(_stmt);
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndex(_stmt, "title");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndex(_stmt, "description");
        final int _columnIndexOfStackId = SQLiteStatementUtil.getColumnIndex(_stmt, "stackId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndex(_stmt, "type");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndex(_stmt, "createdAt");
        final int _columnIndexOfDeletedAt = SQLiteStatementUtil.getColumnIndex(_stmt, "deletedAt");
        final int _columnIndexOfDone = SQLiteStatementUtil.getColumnIndex(_stmt, "done");
        final int _columnIndexOfAttachmentCount = SQLiteStatementUtil.getColumnIndex(_stmt, "attachmentCount");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndex(_stmt, "userId");
        final int _columnIndexOfOrder = SQLiteStatementUtil.getColumnIndex(_stmt, "order");
        final int _columnIndexOfArchived = SQLiteStatementUtil.getColumnIndex(_stmt, "archived");
        final int _columnIndexOfDueDate = SQLiteStatementUtil.getColumnIndex(_stmt, "dueDate");
        final int _columnIndexOfNotified = SQLiteStatementUtil.getColumnIndex(_stmt, "notified");
        final int _columnIndexOfOverdue = SQLiteStatementUtil.getColumnIndex(_stmt, "overdue");
        final int _columnIndexOfCommentsUnread = SQLiteStatementUtil.getColumnIndex(_stmt, "commentsUnread");
        final int _columnIndexOfLocalId = SQLiteStatementUtil.getColumnIndex(_stmt, "localId");
        final int _columnIndexOfAccountId = SQLiteStatementUtil.getColumnIndex(_stmt, "accountId");
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndex(_stmt, "id");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndex(_stmt, "status");
        final int _columnIndexOfLastModified = SQLiteStatementUtil.getColumnIndex(_stmt, "lastModified");
        final int _columnIndexOfLastModifiedLocal = SQLiteStatementUtil.getColumnIndex(_stmt, "lastModifiedLocal");
        final int _columnIndexOfEtag = SQLiteStatementUtil.getColumnIndex(_stmt, "etag");
        final LongSparseArray<ArrayList<Label>> _collectionLabels = new LongSparseArray<ArrayList<Label>>();
        final LongSparseArray<ArrayList<User>> _collectionAssignedUsers = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<User>> _collectionOwner = new LongSparseArray<ArrayList<User>>();
        final LongSparseArray<ArrayList<Attachment>> _collectionAttachments = new LongSparseArray<ArrayList<Attachment>>();
        final LongSparseArray<ArrayList<Long>> _collectionCommentIDs = new LongSparseArray<ArrayList<Long>>();
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
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_1 = null;
          } else {
            _tmpKey_1 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_1 != null) {
            if (!_collectionAssignedUsers.containsKey(_tmpKey_1)) {
              _collectionAssignedUsers.put(_tmpKey_1, new ArrayList<User>());
            }
          }
          final Long _tmpKey_2;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_2 = null;
          } else {
            _tmpKey_2 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_2 != null) {
            if (!_collectionOwner.containsKey(_tmpKey_2)) {
              _collectionOwner.put(_tmpKey_2, new ArrayList<User>());
            }
          }
          final Long _tmpKey_3;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_3 = null;
          } else {
            _tmpKey_3 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_3 != null) {
            if (!_collectionAttachments.containsKey(_tmpKey_3)) {
              _collectionAttachments.put(_tmpKey_3, new ArrayList<Attachment>());
            }
          }
          final Long _tmpKey_4;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_4 = null;
          } else {
            _tmpKey_4 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_4 != null) {
            if (!_collectionCommentIDs.containsKey(_tmpKey_4)) {
              _collectionCommentIDs.put(_tmpKey_4, new ArrayList<Long>());
            }
          }
        }
        _stmt.reset();
        __fetchRelationshipLabelAsitNiedermannNextcloudDeckModelLabel(_connection, _collectionLabels);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _collectionAssignedUsers);
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser_1(_connection, _collectionOwner);
        __fetchRelationshipAttachmentAsitNiedermannNextcloudDeckModelAttachment(_connection, _collectionAttachments);
        __fetchRelationshipDeckCommentAsjavaLangLong(_connection, _collectionCommentIDs);
        final List<FullCard> _result = new ArrayList<FullCard>();
        while (_stmt.step()) {
          final FullCard _item;
          final Card _tmpCard;
          if (!((_columnIndexOfTitle == -1 || _stmt.isNull(_columnIndexOfTitle)) && (_columnIndexOfDescription == -1 || _stmt.isNull(_columnIndexOfDescription)) && (_columnIndexOfStackId == -1 || _stmt.isNull(_columnIndexOfStackId)) && (_columnIndexOfType == -1 || _stmt.isNull(_columnIndexOfType)) && (_columnIndexOfCreatedAt == -1 || _stmt.isNull(_columnIndexOfCreatedAt)) && (_columnIndexOfDeletedAt == -1 || _stmt.isNull(_columnIndexOfDeletedAt)) && (_columnIndexOfDone == -1 || _stmt.isNull(_columnIndexOfDone)) && (_columnIndexOfAttachmentCount == -1 || _stmt.isNull(_columnIndexOfAttachmentCount)) && (_columnIndexOfUserId == -1 || _stmt.isNull(_columnIndexOfUserId)) && (_columnIndexOfOrder == -1 || _stmt.isNull(_columnIndexOfOrder)) && (_columnIndexOfArchived == -1 || _stmt.isNull(_columnIndexOfArchived)) && (_columnIndexOfDueDate == -1 || _stmt.isNull(_columnIndexOfDueDate)) && (_columnIndexOfNotified == -1 || _stmt.isNull(_columnIndexOfNotified)) && (_columnIndexOfOverdue == -1 || _stmt.isNull(_columnIndexOfOverdue)) && (_columnIndexOfCommentsUnread == -1 || _stmt.isNull(_columnIndexOfCommentsUnread)) && (_columnIndexOfLocalId == -1 || _stmt.isNull(_columnIndexOfLocalId)) && (_columnIndexOfAccountId == -1 || _stmt.isNull(_columnIndexOfAccountId)) && (_columnIndexOfId == -1 || _stmt.isNull(_columnIndexOfId)) && (_columnIndexOfStatus == -1 || _stmt.isNull(_columnIndexOfStatus)) && (_columnIndexOfLastModified == -1 || _stmt.isNull(_columnIndexOfLastModified)) && (_columnIndexOfLastModifiedLocal == -1 || _stmt.isNull(_columnIndexOfLastModifiedLocal)) && (_columnIndexOfEtag == -1 || _stmt.isNull(_columnIndexOfEtag)))) {
            _tmpCard = new Card();
            if (_columnIndexOfTitle != -1) {
              final String _tmpTitle;
              if (_stmt.isNull(_columnIndexOfTitle)) {
                _tmpTitle = null;
              } else {
                _tmpTitle = _stmt.getText(_columnIndexOfTitle);
              }
              _tmpCard.setTitle(_tmpTitle);
            }
            if (_columnIndexOfDescription != -1) {
              final String _tmpDescription;
              if (_stmt.isNull(_columnIndexOfDescription)) {
                _tmpDescription = null;
              } else {
                _tmpDescription = _stmt.getText(_columnIndexOfDescription);
              }
              _tmpCard.setDescription(_tmpDescription);
            }
            if (_columnIndexOfStackId != -1) {
              final Long _tmpStackId;
              if (_stmt.isNull(_columnIndexOfStackId)) {
                _tmpStackId = null;
              } else {
                _tmpStackId = _stmt.getLong(_columnIndexOfStackId);
              }
              _tmpCard.setStackId(_tmpStackId);
            }
            if (_columnIndexOfType != -1) {
              final String _tmpType;
              if (_stmt.isNull(_columnIndexOfType)) {
                _tmpType = null;
              } else {
                _tmpType = _stmt.getText(_columnIndexOfType);
              }
              _tmpCard.setType(_tmpType);
            }
            if (_columnIndexOfCreatedAt != -1) {
              final Instant _tmpCreatedAt;
              final Long _tmp;
              if (_stmt.isNull(_columnIndexOfCreatedAt)) {
                _tmp = null;
              } else {
                _tmp = _stmt.getLong(_columnIndexOfCreatedAt);
              }
              _tmpCreatedAt = DateTypeConverter.toInstant(_tmp);
              _tmpCard.setCreatedAt(_tmpCreatedAt);
            }
            if (_columnIndexOfDeletedAt != -1) {
              final Instant _tmpDeletedAt;
              final Long _tmp_1;
              if (_stmt.isNull(_columnIndexOfDeletedAt)) {
                _tmp_1 = null;
              } else {
                _tmp_1 = _stmt.getLong(_columnIndexOfDeletedAt);
              }
              _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_1);
              _tmpCard.setDeletedAt(_tmpDeletedAt);
            }
            if (_columnIndexOfDone != -1) {
              final Instant _tmpDone;
              final Long _tmp_2;
              if (_stmt.isNull(_columnIndexOfDone)) {
                _tmp_2 = null;
              } else {
                _tmp_2 = _stmt.getLong(_columnIndexOfDone);
              }
              _tmpDone = DateTypeConverter.toInstant(_tmp_2);
              _tmpCard.setDone(_tmpDone);
            }
            if (_columnIndexOfAttachmentCount != -1) {
              final int _tmpAttachmentCount;
              _tmpAttachmentCount = (int) (_stmt.getLong(_columnIndexOfAttachmentCount));
              _tmpCard.setAttachmentCount(_tmpAttachmentCount);
            }
            if (_columnIndexOfUserId != -1) {
              final Long _tmpUserId;
              if (_stmt.isNull(_columnIndexOfUserId)) {
                _tmpUserId = null;
              } else {
                _tmpUserId = _stmt.getLong(_columnIndexOfUserId);
              }
              _tmpCard.setUserId(_tmpUserId);
            }
            if (_columnIndexOfOrder != -1) {
              final int _tmpOrder;
              _tmpOrder = (int) (_stmt.getLong(_columnIndexOfOrder));
              _tmpCard.setOrder(_tmpOrder);
            }
            if (_columnIndexOfArchived != -1) {
              final boolean _tmpArchived;
              final int _tmp_3;
              _tmp_3 = (int) (_stmt.getLong(_columnIndexOfArchived));
              _tmpArchived = _tmp_3 != 0;
              _tmpCard.setArchived(_tmpArchived);
            }
            if (_columnIndexOfDueDate != -1) {
              final Instant _tmpDueDate;
              final Long _tmp_4;
              if (_stmt.isNull(_columnIndexOfDueDate)) {
                _tmp_4 = null;
              } else {
                _tmp_4 = _stmt.getLong(_columnIndexOfDueDate);
              }
              _tmpDueDate = DateTypeConverter.toInstant(_tmp_4);
              _tmpCard.setDueDate(_tmpDueDate);
            }
            if (_columnIndexOfNotified != -1) {
              final boolean _tmpNotified;
              final int _tmp_5;
              _tmp_5 = (int) (_stmt.getLong(_columnIndexOfNotified));
              _tmpNotified = _tmp_5 != 0;
              _tmpCard.setNotified(_tmpNotified);
            }
            if (_columnIndexOfOverdue != -1) {
              final int _tmpOverdue;
              _tmpOverdue = (int) (_stmt.getLong(_columnIndexOfOverdue));
              _tmpCard.setOverdue(_tmpOverdue);
            }
            if (_columnIndexOfCommentsUnread != -1) {
              final int _tmpCommentsUnread;
              _tmpCommentsUnread = (int) (_stmt.getLong(_columnIndexOfCommentsUnread));
              _tmpCard.setCommentsUnread(_tmpCommentsUnread);
            }
            if (_columnIndexOfLocalId != -1) {
              final Long _tmpLocalId;
              if (_stmt.isNull(_columnIndexOfLocalId)) {
                _tmpLocalId = null;
              } else {
                _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
              }
              _tmpCard.setLocalId(_tmpLocalId);
            }
            if (_columnIndexOfAccountId != -1) {
              final long _tmpAccountId;
              _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
              _tmpCard.setAccountId(_tmpAccountId);
            }
            if (_columnIndexOfId != -1) {
              final Long _tmpId;
              if (_stmt.isNull(_columnIndexOfId)) {
                _tmpId = null;
              } else {
                _tmpId = _stmt.getLong(_columnIndexOfId);
              }
              _tmpCard.setId(_tmpId);
            }
            if (_columnIndexOfStatus != -1) {
              final int _tmpStatus;
              _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
              _tmpCard.setStatus(_tmpStatus);
            }
            if (_columnIndexOfLastModified != -1) {
              final Instant _tmpLastModified;
              final Long _tmp_6;
              if (_stmt.isNull(_columnIndexOfLastModified)) {
                _tmp_6 = null;
              } else {
                _tmp_6 = _stmt.getLong(_columnIndexOfLastModified);
              }
              _tmpLastModified = DateTypeConverter.toInstant(_tmp_6);
              _tmpCard.setLastModified(_tmpLastModified);
            }
            if (_columnIndexOfLastModifiedLocal != -1) {
              final Instant _tmpLastModifiedLocal;
              final Long _tmp_7;
              if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
                _tmp_7 = null;
              } else {
                _tmp_7 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
              }
              _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_7);
              _tmpCard.setLastModifiedLocal(_tmpLastModifiedLocal);
            }
            if (_columnIndexOfEtag != -1) {
              final String _tmpEtag;
              if (_stmt.isNull(_columnIndexOfEtag)) {
                _tmpEtag = null;
              } else {
                _tmpEtag = _stmt.getText(_columnIndexOfEtag);
              }
              _tmpCard.setEtag(_tmpEtag);
            }
          } else {
            _tmpCard = null;
          }
          final ArrayList<Label> _tmpLabelsCollection;
          final Long _tmpKey_5;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_5 = null;
          } else {
            _tmpKey_5 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_5 != null) {
            _tmpLabelsCollection = _collectionLabels.get(_tmpKey_5);
          } else {
            _tmpLabelsCollection = new ArrayList<Label>();
          }
          final ArrayList<User> _tmpAssignedUsersCollection;
          final Long _tmpKey_6;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_6 = null;
          } else {
            _tmpKey_6 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_6 != null) {
            _tmpAssignedUsersCollection = _collectionAssignedUsers.get(_tmpKey_6);
          } else {
            _tmpAssignedUsersCollection = new ArrayList<User>();
          }
          final ArrayList<User> _tmpOwnerCollection;
          final Long _tmpKey_7;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpKey_7 = null;
          } else {
            _tmpKey_7 = _stmt.getLong(_columnIndexOfUserId);
          }
          if (_tmpKey_7 != null) {
            _tmpOwnerCollection = _collectionOwner.get(_tmpKey_7);
          } else {
            _tmpOwnerCollection = new ArrayList<User>();
          }
          final ArrayList<Attachment> _tmpAttachmentsCollection;
          final Long _tmpKey_8;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_8 = null;
          } else {
            _tmpKey_8 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_8 != null) {
            _tmpAttachmentsCollection = _collectionAttachments.get(_tmpKey_8);
          } else {
            _tmpAttachmentsCollection = new ArrayList<Attachment>();
          }
          final ArrayList<Long> _tmpCommentIDsCollection;
          final Long _tmpKey_9;
          if (_stmt.isNull(_columnIndexOfLocalId)) {
            _tmpKey_9 = null;
          } else {
            _tmpKey_9 = _stmt.getLong(_columnIndexOfLocalId);
          }
          if (_tmpKey_9 != null) {
            _tmpCommentIDsCollection = _collectionCommentIDs.get(_tmpKey_9);
          } else {
            _tmpCommentIDsCollection = new ArrayList<Long>();
          }
          _item = new FullCard();
          _item.card = _tmpCard;
          _item.labels = _tmpLabelsCollection;
          _item.assignedUsers = _tmpAssignedUsersCollection;
          _item.owner = _tmpOwnerCollection;
          _item.attachments = _tmpAttachmentsCollection;
          _item.commentIDs = _tmpCommentIDsCollection;
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
    _stringBuilder.append("SELECT `Label`.`title` AS `title`,`Label`.`color` AS `color`,`Label`.`boardId` AS `boardId`,`Label`.`localId` AS `localId`,`Label`.`accountId` AS `accountId`,`Label`.`id` AS `id`,`Label`.`status` AS `status`,`Label`.`lastModified` AS `lastModified`,`Label`.`lastModifiedLocal` AS `lastModifiedLocal`,`Label`.`etag` AS `etag`,_junction.`cardId` FROM `JoinCardWithLabel` AS _junction INNER JOIN `Label` ON (_junction.`labelId` = `Label`.`localId`) WHERE _junction.`cardId` IN (");
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
      // _junction.cardId;
      final int _itemKeyIndex = 10;
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
        final Long _tmpKey;
        if (_stmt.isNull(_itemKeyIndex)) {
          _tmpKey = null;
        } else {
          _tmpKey = _stmt.getLong(_itemKeyIndex);
        }
        if (_tmpKey != null) {
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
      }
    } finally {
      _stmt.close();
    }
  }

  private void __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(
      @NonNull final SQLiteConnection _connection,
      @NonNull final LongSparseArray<ArrayList<User>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > 999) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (_tmpMap) -> {
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser(_connection, _tmpMap);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT `User`.`primaryKey` AS `primaryKey`,`User`.`uid` AS `uid`,`User`.`displayname` AS `displayname`,`User`.`type` AS `type`,`User`.`localId` AS `localId`,`User`.`accountId` AS `accountId`,`User`.`id` AS `id`,`User`.`status` AS `status`,`User`.`lastModified` AS `lastModified`,`User`.`lastModifiedLocal` AS `lastModifiedLocal`,`User`.`etag` AS `etag`,_junction.`cardId` FROM `JoinCardWithUser` AS _junction INNER JOIN `User` ON (_junction.`userId` = `User`.`localId`) WHERE _junction.`cardId` IN (");
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
      // _junction.cardId;
      final int _itemKeyIndex = 11;
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
          final ArrayList<User> _tmpRelation = _map.get(_tmpKey);
          if (_tmpRelation != null) {
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
            _tmpRelation.add(_item_1);
          }
        }
      }
    } finally {
      _stmt.close();
    }
  }

  private void __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser_1(
      @NonNull final SQLiteConnection _connection,
      @NonNull final LongSparseArray<ArrayList<User>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > 999) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (_tmpMap) -> {
        __fetchRelationshipUserAsitNiedermannNextcloudDeckModelUser_1(_connection, _tmpMap);
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
          final ArrayList<User> _tmpRelation = _map.get(_tmpKey);
          if (_tmpRelation != null) {
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
            _tmpRelation.add(_item_1);
          }
        }
      }
    } finally {
      _stmt.close();
    }
  }

  private void __fetchRelationshipAttachmentAsitNiedermannNextcloudDeckModelAttachment(
      @NonNull final SQLiteConnection _connection,
      @NonNull final LongSparseArray<ArrayList<Attachment>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > 999) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (_tmpMap) -> {
        __fetchRelationshipAttachmentAsitNiedermannNextcloudDeckModelAttachment(_connection, _tmpMap);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT `cardId`,`type`,`data`,`createdAt`,`createdBy`,`deletedAt`,`filesize`,`mimetype`,`dirname`,`basename`,`extension`,`filename`,`localPath`,`fileId`,`localId`,`accountId`,`id`,`status`,`lastModified`,`lastModifiedLocal`,`etag` FROM `Attachment` WHERE `cardId` IN (");
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
      final int _itemKeyIndex = SQLiteStatementUtil.getColumnIndex(_stmt, "cardId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _columnIndexOfCardId = 0;
      final int _columnIndexOfType = 1;
      final int _columnIndexOfData = 2;
      final int _columnIndexOfCreatedAt = 3;
      final int _columnIndexOfCreatedBy = 4;
      final int _columnIndexOfDeletedAt = 5;
      final int _columnIndexOfFilesize = 6;
      final int _columnIndexOfMimetype = 7;
      final int _columnIndexOfDirname = 8;
      final int _columnIndexOfBasename = 9;
      final int _columnIndexOfExtension = 10;
      final int _columnIndexOfFilename = 11;
      final int _columnIndexOfLocalPath = 12;
      final int _columnIndexOfFileId = 13;
      final int _columnIndexOfLocalId = 14;
      final int _columnIndexOfAccountId = 15;
      final int _columnIndexOfId = 16;
      final int _columnIndexOfStatus = 17;
      final int _columnIndexOfLastModified = 18;
      final int _columnIndexOfLastModifiedLocal = 19;
      final int _columnIndexOfEtag = 20;
      while (_stmt.step()) {
        final long _tmpKey;
        _tmpKey = _stmt.getLong(_itemKeyIndex);
        final ArrayList<Attachment> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final Attachment _item_1;
          _item_1 = new Attachment();
          final long _tmpCardId;
          _tmpCardId = _stmt.getLong(_columnIndexOfCardId);
          _item_1.setCardId(_tmpCardId);
          final EAttachmentType _tmpType;
          final String _tmp;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmp = null;
          } else {
            _tmp = _stmt.getText(_columnIndexOfType);
          }
          _tmpType = EnumConverter.toEAttachmentType(_tmp);
          _item_1.setType(_tmpType);
          final String _tmpData;
          if (_stmt.isNull(_columnIndexOfData)) {
            _tmpData = null;
          } else {
            _tmpData = _stmt.getText(_columnIndexOfData);
          }
          _item_1.setData(_tmpData);
          final Instant _tmpCreatedAt;
          final Long _tmp_1;
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_1 = null;
          } else {
            _tmp_1 = _stmt.getLong(_columnIndexOfCreatedAt);
          }
          _tmpCreatedAt = DateTypeConverter.toInstant(_tmp_1);
          _item_1.setCreatedAt(_tmpCreatedAt);
          final String _tmpCreatedBy;
          if (_stmt.isNull(_columnIndexOfCreatedBy)) {
            _tmpCreatedBy = null;
          } else {
            _tmpCreatedBy = _stmt.getText(_columnIndexOfCreatedBy);
          }
          _item_1.setCreatedBy(_tmpCreatedBy);
          final Instant _tmpDeletedAt;
          final Long _tmp_2;
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfDeletedAt);
          }
          _tmpDeletedAt = DateTypeConverter.toInstant(_tmp_2);
          _item_1.setDeletedAt(_tmpDeletedAt);
          final long _tmpFilesize;
          _tmpFilesize = _stmt.getLong(_columnIndexOfFilesize);
          _item_1.setFilesize(_tmpFilesize);
          final String _tmpMimetype;
          if (_stmt.isNull(_columnIndexOfMimetype)) {
            _tmpMimetype = null;
          } else {
            _tmpMimetype = _stmt.getText(_columnIndexOfMimetype);
          }
          _item_1.setMimetype(_tmpMimetype);
          final String _tmpDirname;
          if (_stmt.isNull(_columnIndexOfDirname)) {
            _tmpDirname = null;
          } else {
            _tmpDirname = _stmt.getText(_columnIndexOfDirname);
          }
          _item_1.setDirname(_tmpDirname);
          final String _tmpBasename;
          if (_stmt.isNull(_columnIndexOfBasename)) {
            _tmpBasename = null;
          } else {
            _tmpBasename = _stmt.getText(_columnIndexOfBasename);
          }
          _item_1.setBasename(_tmpBasename);
          final String _tmpExtension;
          if (_stmt.isNull(_columnIndexOfExtension)) {
            _tmpExtension = null;
          } else {
            _tmpExtension = _stmt.getText(_columnIndexOfExtension);
          }
          _item_1.setExtension(_tmpExtension);
          final String _tmpFilename;
          if (_stmt.isNull(_columnIndexOfFilename)) {
            _tmpFilename = null;
          } else {
            _tmpFilename = _stmt.getText(_columnIndexOfFilename);
          }
          _item_1.setFilename(_tmpFilename);
          final String _tmpLocalPath;
          if (_stmt.isNull(_columnIndexOfLocalPath)) {
            _tmpLocalPath = null;
          } else {
            _tmpLocalPath = _stmt.getText(_columnIndexOfLocalPath);
          }
          _item_1.setLocalPath(_tmpLocalPath);
          final Long _tmpFileId;
          if (_stmt.isNull(_columnIndexOfFileId)) {
            _tmpFileId = null;
          } else {
            _tmpFileId = _stmt.getLong(_columnIndexOfFileId);
          }
          _item_1.setFileId(_tmpFileId);
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
          final Long _tmp_3;
          if (_stmt.isNull(_columnIndexOfLastModified)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfLastModified);
          }
          _tmpLastModified = DateTypeConverter.toInstant(_tmp_3);
          _item_1.setLastModified(_tmpLastModified);
          final Instant _tmpLastModifiedLocal;
          final Long _tmp_4;
          if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
          }
          _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_4);
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

  private void __fetchRelationshipDeckCommentAsjavaLangLong(
      @NonNull final SQLiteConnection _connection,
      @NonNull final LongSparseArray<ArrayList<Long>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > 999) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (_tmpMap) -> {
        __fetchRelationshipDeckCommentAsjavaLangLong(_connection, _tmpMap);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT `localId`,`objectId` FROM `DeckComment` WHERE `objectId` IN (");
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
      final int _itemKeyIndex = SQLiteStatementUtil.getColumnIndex(_stmt, "objectId");
      if (_itemKeyIndex == -1) {
        return;
      }
      while (_stmt.step()) {
        final Long _tmpKey;
        if (_stmt.isNull(_itemKeyIndex)) {
          _tmpKey = null;
        } else {
          _tmpKey = _stmt.getLong(_itemKeyIndex);
        }
        if (_tmpKey != null) {
          final ArrayList<Long> _tmpRelation = _map.get(_tmpKey);
          if (_tmpRelation != null) {
            final Long _item_1;
            if (_stmt.isNull(0)) {
              _item_1 = null;
            } else {
              _item_1 = _stmt.getLong(0);
            }
            _tmpRelation.add(_item_1);
          }
        }
      }
    } finally {
      _stmt.close();
    }
  }

  private void __fetchRelationshipOcsProjectResourceAsitNiedermannNextcloudDeckModelOcsProjectsOcsProjectResource(
      @NonNull final SQLiteConnection _connection,
      @NonNull final LongSparseArray<ArrayList<OcsProjectResource>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > 999) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (_tmpMap) -> {
        __fetchRelationshipOcsProjectResourceAsitNiedermannNextcloudDeckModelOcsProjectsOcsProjectResource(_connection, _tmpMap);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT `type`,`name`,`link`,`path`,`iconUrl`,`mimetype`,`previewAvailable`,`idString`,`projectId`,`localId`,`accountId`,`id`,`status`,`lastModified`,`lastModifiedLocal`,`etag` FROM `OcsProjectResource` WHERE `projectId` IN (");
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
      final int _itemKeyIndex = SQLiteStatementUtil.getColumnIndex(_stmt, "projectId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _columnIndexOfType = 0;
      final int _columnIndexOfName = 1;
      final int _columnIndexOfLink = 2;
      final int _columnIndexOfPath = 3;
      final int _columnIndexOfIconUrl = 4;
      final int _columnIndexOfMimetype = 5;
      final int _columnIndexOfPreviewAvailable = 6;
      final int _columnIndexOfIdString = 7;
      final int _columnIndexOfProjectId = 8;
      final int _columnIndexOfLocalId = 9;
      final int _columnIndexOfAccountId = 10;
      final int _columnIndexOfId = 11;
      final int _columnIndexOfStatus = 12;
      final int _columnIndexOfLastModified = 13;
      final int _columnIndexOfLastModifiedLocal = 14;
      final int _columnIndexOfEtag = 15;
      while (_stmt.step()) {
        final long _tmpKey;
        _tmpKey = _stmt.getLong(_itemKeyIndex);
        final ArrayList<OcsProjectResource> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final OcsProjectResource _item_1;
          _item_1 = new OcsProjectResource();
          final String _tmpType;
          if (_stmt.isNull(_columnIndexOfType)) {
            _tmpType = null;
          } else {
            _tmpType = _stmt.getText(_columnIndexOfType);
          }
          _item_1.setType(_tmpType);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          _item_1.setName(_tmpName);
          final String _tmpLink;
          if (_stmt.isNull(_columnIndexOfLink)) {
            _tmpLink = null;
          } else {
            _tmpLink = _stmt.getText(_columnIndexOfLink);
          }
          _item_1.setLink(_tmpLink);
          final String _tmpPath;
          if (_stmt.isNull(_columnIndexOfPath)) {
            _tmpPath = null;
          } else {
            _tmpPath = _stmt.getText(_columnIndexOfPath);
          }
          _item_1.setPath(_tmpPath);
          final String _tmpIconUrl;
          if (_stmt.isNull(_columnIndexOfIconUrl)) {
            _tmpIconUrl = null;
          } else {
            _tmpIconUrl = _stmt.getText(_columnIndexOfIconUrl);
          }
          _item_1.setIconUrl(_tmpIconUrl);
          final String _tmpMimetype;
          if (_stmt.isNull(_columnIndexOfMimetype)) {
            _tmpMimetype = null;
          } else {
            _tmpMimetype = _stmt.getText(_columnIndexOfMimetype);
          }
          _item_1.setMimetype(_tmpMimetype);
          final Boolean _tmpPreviewAvailable;
          final Integer _tmp;
          if (_stmt.isNull(_columnIndexOfPreviewAvailable)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(_columnIndexOfPreviewAvailable));
          }
          _tmpPreviewAvailable = _tmp == null ? null : _tmp != 0;
          _item_1.setPreviewAvailable(_tmpPreviewAvailable);
          final String _tmpIdString;
          if (_stmt.isNull(_columnIndexOfIdString)) {
            _tmpIdString = null;
          } else {
            _tmpIdString = _stmt.getText(_columnIndexOfIdString);
          }
          _item_1.setIdString(_tmpIdString);
          final Long _tmpProjectId;
          if (_stmt.isNull(_columnIndexOfProjectId)) {
            _tmpProjectId = null;
          } else {
            _tmpProjectId = _stmt.getLong(_columnIndexOfProjectId);
          }
          _item_1.setProjectId(_tmpProjectId);
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

  private void __fetchRelationshipOcsProjectAsitNiedermannNextcloudDeckModelOcsProjectsFullOcsProjectWithResources(
      @NonNull final SQLiteConnection _connection,
      @NonNull final LongSparseArray<ArrayList<OcsProjectWithResources>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > 999) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (_tmpMap) -> {
        __fetchRelationshipOcsProjectAsitNiedermannNextcloudDeckModelOcsProjectsFullOcsProjectWithResources(_connection, _tmpMap);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = new StringBuilder();
    _stringBuilder.append("SELECT `OcsProject`.`name` AS `name`,`OcsProject`.`localId` AS `localId`,`OcsProject`.`accountId` AS `accountId`,`OcsProject`.`id` AS `id`,`OcsProject`.`status` AS `status`,`OcsProject`.`lastModified` AS `lastModified`,`OcsProject`.`lastModifiedLocal` AS `lastModifiedLocal`,`OcsProject`.`etag` AS `etag`,_junction.`cardId` FROM `JoinCardWithProject` AS _junction INNER JOIN `OcsProject` ON (_junction.`projectId` = `OcsProject`.`localId`) WHERE _junction.`cardId` IN (");
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
      // _junction.cardId;
      final int _itemKeyIndex = 8;
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _columnIndexOfName = 0;
      final int _columnIndexOfLocalId = 1;
      final int _columnIndexOfAccountId = 2;
      final int _columnIndexOfId = 3;
      final int _columnIndexOfStatus = 4;
      final int _columnIndexOfLastModified = 5;
      final int _columnIndexOfLastModifiedLocal = 6;
      final int _columnIndexOfEtag = 7;
      final LongSparseArray<ArrayList<OcsProjectResource>> _collectionResources = new LongSparseArray<ArrayList<OcsProjectResource>>();
      while (_stmt.step()) {
        final Long _tmpKey;
        if (_stmt.isNull(_columnIndexOfLocalId)) {
          _tmpKey = null;
        } else {
          _tmpKey = _stmt.getLong(_columnIndexOfLocalId);
        }
        if (_tmpKey != null) {
          if (!_collectionResources.containsKey(_tmpKey)) {
            _collectionResources.put(_tmpKey, new ArrayList<OcsProjectResource>());
          }
        }
      }
      _stmt.reset();
      __fetchRelationshipOcsProjectResourceAsitNiedermannNextcloudDeckModelOcsProjectsOcsProjectResource(_connection, _collectionResources);
      while (_stmt.step()) {
        final Long _tmpKey_1;
        if (_stmt.isNull(_itemKeyIndex)) {
          _tmpKey_1 = null;
        } else {
          _tmpKey_1 = _stmt.getLong(_itemKeyIndex);
        }
        if (_tmpKey_1 != null) {
          final ArrayList<OcsProjectWithResources> _tmpRelation = _map.get(_tmpKey_1);
          if (_tmpRelation != null) {
            final OcsProjectWithResources _item_1;
            final OcsProject _tmpProject;
            if (!(_stmt.isNull(_columnIndexOfName) && _stmt.isNull(_columnIndexOfLocalId) && _stmt.isNull(_columnIndexOfAccountId) && _stmt.isNull(_columnIndexOfId) && _stmt.isNull(_columnIndexOfStatus) && _stmt.isNull(_columnIndexOfLastModified) && _stmt.isNull(_columnIndexOfLastModifiedLocal) && _stmt.isNull(_columnIndexOfEtag))) {
              _tmpProject = new OcsProject();
              final String _tmpName;
              if (_stmt.isNull(_columnIndexOfName)) {
                _tmpName = null;
              } else {
                _tmpName = _stmt.getText(_columnIndexOfName);
              }
              _tmpProject.setName(_tmpName);
              final Long _tmpLocalId;
              if (_stmt.isNull(_columnIndexOfLocalId)) {
                _tmpLocalId = null;
              } else {
                _tmpLocalId = _stmt.getLong(_columnIndexOfLocalId);
              }
              _tmpProject.setLocalId(_tmpLocalId);
              final long _tmpAccountId;
              _tmpAccountId = _stmt.getLong(_columnIndexOfAccountId);
              _tmpProject.setAccountId(_tmpAccountId);
              final Long _tmpId;
              if (_stmt.isNull(_columnIndexOfId)) {
                _tmpId = null;
              } else {
                _tmpId = _stmt.getLong(_columnIndexOfId);
              }
              _tmpProject.setId(_tmpId);
              final int _tmpStatus;
              _tmpStatus = (int) (_stmt.getLong(_columnIndexOfStatus));
              _tmpProject.setStatus(_tmpStatus);
              final Instant _tmpLastModified;
              final Long _tmp;
              if (_stmt.isNull(_columnIndexOfLastModified)) {
                _tmp = null;
              } else {
                _tmp = _stmt.getLong(_columnIndexOfLastModified);
              }
              _tmpLastModified = DateTypeConverter.toInstant(_tmp);
              _tmpProject.setLastModified(_tmpLastModified);
              final Instant _tmpLastModifiedLocal;
              final Long _tmp_1;
              if (_stmt.isNull(_columnIndexOfLastModifiedLocal)) {
                _tmp_1 = null;
              } else {
                _tmp_1 = _stmt.getLong(_columnIndexOfLastModifiedLocal);
              }
              _tmpLastModifiedLocal = DateTypeConverter.toInstant(_tmp_1);
              _tmpProject.setLastModifiedLocal(_tmpLastModifiedLocal);
              final String _tmpEtag;
              if (_stmt.isNull(_columnIndexOfEtag)) {
                _tmpEtag = null;
              } else {
                _tmpEtag = _stmt.getText(_columnIndexOfEtag);
              }
              _tmpProject.setEtag(_tmpEtag);
            } else {
              _tmpProject = null;
            }
            final ArrayList<OcsProjectResource> _tmpResourcesCollection;
            final Long _tmpKey_2;
            if (_stmt.isNull(_columnIndexOfLocalId)) {
              _tmpKey_2 = null;
            } else {
              _tmpKey_2 = _stmt.getLong(_columnIndexOfLocalId);
            }
            if (_tmpKey_2 != null) {
              _tmpResourcesCollection = _collectionResources.get(_tmpKey_2);
            } else {
              _tmpResourcesCollection = new ArrayList<OcsProjectResource>();
            }
            _item_1 = new OcsProjectWithResources();
            _item_1.project = _tmpProject;
            _item_1.resources = _tmpResourcesCollection;
            _tmpRelation.add(_item_1);
          }
        }
      }
    } finally {
      _stmt.close();
    }
  }
}
