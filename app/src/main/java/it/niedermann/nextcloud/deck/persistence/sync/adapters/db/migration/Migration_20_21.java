package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import android.database.Cursor;
import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import it.niedermann.android.util.ColorUtil;

/**
 * @see <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/556">Store colors as integer in database</a>
 */
public class Migration_20_21 extends Migration {

    public Migration_20_21() {
        super(20, 21);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        String suffix = "_new";
        {
            String tableName = "Account";
            database.execSQL("CREATE TABLE `" + tableName + suffix + "` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT NOT NULL, `userName` TEXT NOT NULL, `url` TEXT NOT NULL, " +
                    "`color` INTEGER NOT NULL DEFAULT 0, `textColor` INTEGER NOT NULL DEFAULT 0, `serverDeckVersion` TEXT NOT NULL DEFAULT '0.6.4', `maintenanceEnabled` INTEGER NOT NULL DEFAULT 0, `etag` TEXT)");
            Cursor cursor = database.query("select * from `" + tableName + "`");
            while (cursor.moveToNext()) {
                String colorAsString1 = cursor.getString(4); // color
                String colorAsString2 = cursor.getString(5); // textColor

                @ColorInt int color1;
                @ColorInt int color2;
                try {
                    color1 = Color.parseColor(ColorUtil.INSTANCE.formatColorToParsableHexString(colorAsString1));
                    color2 = Color.parseColor(ColorUtil.INSTANCE.formatColorToParsableHexString(colorAsString2));
                } catch (Exception e) {
                    color1 = Color.GRAY;
                    color2 = Color.GRAY;
                }
                database.execSQL("Insert into `" + tableName + suffix + "` VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{
                        cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        color1, color2, cursor.getString(6), cursor.getInt(7), cursor.getString(8)});

            }


            database.execSQL("DROP TABLE `" + tableName + "`");
            database.execSQL("ALTER TABLE `" + tableName + suffix + "` RENAME TO `" + tableName + "`");
            database.execSQL("CREATE UNIQUE INDEX `index_Account_name` ON `" + tableName + "` (`name`)");
            database.execSQL("UPDATE SQLITE_SEQUENCE SET seq = (select max(id) from " + tableName + ") WHERE name = ?", new Object[]{tableName});
        }
        {
            String tableName = "Board";
            database.execSQL("CREATE TABLE `" + tableName + suffix + "` (`localId` INTEGER PRIMARY KEY AUTOINCREMENT, `accountId` INTEGER NOT NULL, `id` INTEGER, `status` INTEGER NOT NULL, " +
                    "`lastModified` INTEGER, `lastModifiedLocal` INTEGER, `title` TEXT, `ownerId` INTEGER NOT NULL, `color` INTEGER, " +
                    "`archived` INTEGER NOT NULL, `shared` INTEGER NOT NULL, `deletedAt` INTEGER, `permissionRead` INTEGER NOT NULL, " +
                    "`permissionEdit` INTEGER NOT NULL, `permissionManage` INTEGER NOT NULL, `permissionShare` INTEGER NOT NULL, " +
                    "FOREIGN KEY(`ownerId`) REFERENCES `User`(`localId`) ON UPDATE NO ACTION ON DELETE SET NULL )");
            Cursor cursor = database.query("select * from `" + tableName + "`");
            while (cursor.moveToNext()) {
                String colorAsString1 = cursor.getString(8); // color

                @ColorInt int color1;
                try {
                    color1 = Color.parseColor(ColorUtil.INSTANCE.formatColorToParsableHexString(colorAsString1));
                } catch (Exception e) {
                    color1 = Color.GRAY;
                }
                database.execSQL("Insert into `" + tableName + suffix + "` VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{
                        cursor.getLong(0), cursor.getLong(1), cursor.getLong(2), cursor.getInt(3),
                        cursor.getLong(4), cursor.getLong(5), cursor.getString(6), cursor.getLong(7), color1,
                        cursor.getInt(9), cursor.getInt(10), cursor.getInt(11), cursor.getInt(12),
                        cursor.getInt(13), cursor.getInt(14), cursor.getInt(15)
                });

            }


            database.execSQL("DROP TABLE `" + tableName + "`");
            database.execSQL("ALTER TABLE `" + tableName + suffix + "` RENAME TO `" + tableName + "`");
            database.execSQL("CREATE INDEX `index_Board_accountId` ON `" + tableName + "` (`accountId`)");
            database.execSQL("CREATE UNIQUE INDEX `index_Board_accountId_id` ON `" + tableName + "` (`accountId`, `id`)");
            database.execSQL("CREATE INDEX `index_Board_id` ON `" + tableName + "` (`id`)");
            database.execSQL("CREATE INDEX `index_Board_ownerId` ON `" + tableName + "` (`ownerId`)");
            database.execSQL("CREATE INDEX `index_Board_lastModifiedLocal` ON `" + tableName + "` (`lastModifiedLocal`)");
            database.execSQL("UPDATE SQLITE_SEQUENCE SET seq = (select max(id) from " + tableName + ") WHERE name = ?", new Object[]{tableName});
        }
        {
            String tableName = "Label";
            database.execSQL("CREATE TABLE `" + tableName + suffix + "` (`localId` INTEGER PRIMARY KEY AUTOINCREMENT, `accountId` INTEGER NOT NULL, `id` INTEGER, `status` INTEGER NOT NULL, " +
                    "`lastModified` INTEGER, `lastModifiedLocal` INTEGER, `title` TEXT, `color` INTEGER NOT NULL DEFAULT 0, `boardId` INTEGER NOT NULL, " +
                    "FOREIGN KEY(`boardId`) REFERENCES `Board`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
            Cursor cursor = database.query("select * from `" + tableName + "`");
            while (cursor.moveToNext()) {
                String colorAsString1 = cursor.getString(7); // color

                @ColorInt int color1;
                try {
                    color1 = Color.parseColor(ColorUtil.INSTANCE.formatColorToParsableHexString(colorAsString1));
                } catch (Exception e) {
                    color1 = Color.GRAY;
                }
                database.execSQL("Insert into `" + tableName + suffix + "` VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{
                        cursor.getLong(0), cursor.getLong(1), cursor.getLong(2), cursor.getInt(3),
                        cursor.getLong(4), cursor.getLong(5), cursor.getString(6), color1, cursor.getLong(8)});

            }


            database.execSQL("DROP TABLE `" + tableName + "`");
            database.execSQL("ALTER TABLE `" + tableName + suffix + "` RENAME TO `" + tableName + "`");
            database.execSQL("CREATE UNIQUE INDEX `index_Label_accountId_id` ON `" + tableName + "` (`accountId`, `id`)");
            database.execSQL("CREATE INDEX `index_Label_boardId` ON `" + tableName + "` (`boardId`)");
            database.execSQL("CREATE INDEX `index_Label_accountId` ON `" + tableName + "` (`accountId`)");
            database.execSQL("CREATE UNIQUE INDEX `idx_label_title_unique` ON `" + tableName + "` (`boardId`, `title`)");
            database.execSQL("CREATE INDEX `index_Label_id` ON `" + tableName + "` (`id`)");
            database.execSQL("CREATE INDEX `index_Label_lastModifiedLocal` ON `" + tableName + "` (`lastModifiedLocal`)");
            database.execSQL("UPDATE SQLITE_SEQUENCE SET seq = (select max(id) from " + tableName + ") WHERE name = ?", new Object[]{tableName});
        }
    }
}
