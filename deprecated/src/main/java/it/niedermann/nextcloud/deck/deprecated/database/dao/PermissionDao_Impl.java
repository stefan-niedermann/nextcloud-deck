package it.niedermann.nextcloud.deck.database.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.sqlite.SQLiteStatement;
import it.niedermann.nextcloud.deck.model.Permission;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class PermissionDao_Impl implements PermissionDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Permission> __insertAdapterOfPermission;

  private final EntityDeleteOrUpdateAdapter<Permission> __deleteAdapterOfPermission;

  private final EntityDeleteOrUpdateAdapter<Permission> __updateAdapterOfPermission;

  public PermissionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfPermission = new EntityInsertAdapter<Permission>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `Permission` (`id`) VALUES (nullif(?, 0))";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Permission entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deleteAdapterOfPermission = new EntityDeleteOrUpdateAdapter<Permission>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `Permission` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Permission entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfPermission = new EntityDeleteOrUpdateAdapter<Permission>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `Permission` SET `id` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final Permission entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getId());
      }
    };
  }

  @Override
  public long insert(final Permission entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfPermission.insertAndReturnId(_connection, entity);
    });
  }

  @Override
  public long[] insert(final Permission... entity) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfPermission.insertAndReturnIdsArray(_connection, entity);
    });
  }

  @Override
  public void delete(final Permission... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfPermission.handleMultiple(_connection, entity);
      return null;
    });
  }

  @Override
  public void update(final Permission... entity) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfPermission.handleMultiple(_connection, entity);
      return null;
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
