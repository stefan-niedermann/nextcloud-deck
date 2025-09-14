package it.niedermann.nextcloud.deck.database.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.model.ocs.comment.Mention;
import java.lang.Class;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class MentionDao_Impl implements MentionDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Mention> __insertAdapterOfMention;

  private final EntityDeleteOrUpdateAdapter<Mention> __deleteAdapterOfMention;

  private final EntityDeleteOrUpdateAdapter<Mention> __updateAdapterOfMention;

  public MentionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfMention = new EntityInsertAdapter<Mention>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `Mention` (`id`,`commentId`,`mentionId`,`mentionType`,`mentionDisplayName`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Mention entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getCommentId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getCommentId());
        }
        if (entity.getMentionId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getMentionId());
        }
        if (entity.getMentionType() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getMentionType());
        }
        if (entity.getMentionDisplayName() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getMentionDisplayName());
        }
      }
    };
    this.__deleteAdapterOfMention = new EntityDeleteOrUpdateAdapter<Mention>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `Mention` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Mention entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
      }
    };
    this.__updateAdapterOfMention = new EntityDeleteOrUpdateAdapter<Mention>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `Mention` SET `id` = ?,`commentId` = ?,`mentionId` = ?,`mentionType` = ?,`mentionDisplayName` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Mention entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getCommentId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getCommentId());
        }
        if (entity.getMentionId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getMentionId());
        }
        if (entity.getMentionType() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getMentionType());
        }
        if (entity.getMentionDisplayName() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getMentionDisplayName());
        }
        if (entity.getId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getId());
        }
      }
    };
  }

  @Override
  public long insert(final Mention entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfMention.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final Mention... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfMention.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final Mention... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfMention.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final Mention... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfMention.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public List<Mention> getMentionsForCommentIdDirectly(final long commentID) {
    final String _sql = "select * from mention WHERE commentId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, commentID);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCommentId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "commentId");
        final int _columnIndexOfMentionId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "mentionId");
        final int _columnIndexOfMentionType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "mentionType");
        final int _columnIndexOfMentionDisplayName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "mentionDisplayName");
        final List<Mention> _result = new ArrayList<Mention>();
        while (_stmt.step()) {
          final Mention _item;
          _item = new Mention();
          final Long _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getLong(_columnIndexOfId);
          }
          _item.setId(_tmpId);
          final Long _tmpCommentId;
          if (_stmt.isNull(_columnIndexOfCommentId)) {
            _tmpCommentId = null;
          } else {
            _tmpCommentId = _stmt.getLong(_columnIndexOfCommentId);
          }
          _item.setCommentId(_tmpCommentId);
          final String _tmpMentionId;
          if (_stmt.isNull(_columnIndexOfMentionId)) {
            _tmpMentionId = null;
          } else {
            _tmpMentionId = _stmt.getText(_columnIndexOfMentionId);
          }
          _item.setMentionId(_tmpMentionId);
          final String _tmpMentionType;
          if (_stmt.isNull(_columnIndexOfMentionType)) {
            _tmpMentionType = null;
          } else {
            _tmpMentionType = _stmt.getText(_columnIndexOfMentionType);
          }
          _item.setMentionType(_tmpMentionType);
          final String _tmpMentionDisplayName;
          if (_stmt.isNull(_columnIndexOfMentionDisplayName)) {
            _tmpMentionDisplayName = null;
          } else {
            _tmpMentionDisplayName = _stmt.getText(_columnIndexOfMentionDisplayName);
          }
          _item.setMentionDisplayName(_tmpMentionDisplayName);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void clearMentionsForCommentId(final long commentID) {
    final String _sql = "delete from mention WHERE commentId = ?";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, commentID);
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
