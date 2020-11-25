package it.niedermann.nextcloud.deck.persistence.sync.adapters.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import it.niedermann.android.util.ColorUtil;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.LastSyncUtil;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.JoinBoardWithLabel;
import it.niedermann.nextcloud.deck.model.JoinBoardWithPermission;
import it.niedermann.nextcloud.deck.model.JoinBoardWithUser;
import it.niedermann.nextcloud.deck.model.JoinCardWithLabel;
import it.niedermann.nextcloud.deck.model.JoinCardWithUser;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Permission;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.appwidgets.StackWidgetModel;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.Mention;
import it.niedermann.nextcloud.deck.model.ocs.projects.JoinCardWithProject;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProject;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectResource;
import it.niedermann.nextcloud.deck.model.relations.UserInBoard;
import it.niedermann.nextcloud.deck.model.relations.UserInGroup;
import it.niedermann.nextcloud.deck.model.widget.singlecard.SingleCardWidgetModel;
import it.niedermann.nextcloud.deck.persistence.sync.SyncWorker;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.AccessControlDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.AccountDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.ActivityDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.AttachmentDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.BoardDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.CardDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.CommentDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.JoinBoardWithLabelDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.JoinBoardWithPermissionDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.JoinBoardWithUserDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.JoinCardWithLabelDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.JoinCardWithUserDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.LabelDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.MentionDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.PermissionDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.StackDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.UserDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.UserInBoardDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.UserInGroupDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.projects.JoinCardWithOcsProjectDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.projects.OcsProjectDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.projects.OcsProjectResourceDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.widgets.SingleCardWidgetModelDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.widgets.StackWidgetModelDao;

@Database(
        entities = {
                Account.class,
                Attachment.class,
                AccessControl.class,
                Board.class,
                Card.class,
                JoinBoardWithLabel.class,
                JoinBoardWithPermission.class,
                JoinBoardWithUser.class,
                JoinCardWithLabel.class,
                JoinCardWithUser.class,
                Label.class,
                Permission.class,
                Stack.class,
                User.class,
                Activity.class,
                DeckComment.class,
                Mention.class,
                SingleCardWidgetModel.class,
                StackWidgetModel.class,
                OcsProject.class,
                OcsProjectResource.class,
                JoinCardWithProject.class,
                UserInGroup.class,
                UserInBoard.class,
        },
        exportSchema = false,
        version = 23
)
@TypeConverters({DateTypeConverter.class})
public abstract class DeckDatabase extends RoomDatabase {


    private static final String DECK_DB_NAME = "NC_DECK_DB.db";
    private static volatile DeckDatabase instance;

    private static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `DeckComment` (`localId` INTEGER PRIMARY KEY AUTOINCREMENT, `accountId` INTEGER NOT NULL, `id` INTEGER, `status` INTEGER NOT NULL, `lastModified` INTEGER, `lastModifiedLocal` INTEGER, `objectId` INTEGER, `actorType` TEXT, `creationDateTime` INTEGER, `actorId` TEXT, `actorDisplayName` TEXT, `message` TEXT, FOREIGN KEY(`objectId`) REFERENCES `Card`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
            database.execSQL("CREATE TABLE `Mention` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `commentId` INTEGER, `mentionId` TEXT, `mentionType` TEXT, `mentionDisplayName` TEXT, FOREIGN KEY(`commentId`) REFERENCES `DeckComment`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
            database.execSQL("CREATE INDEX `index_DeckComment_accountId` ON `DeckComment` (`accountId`)");
            database.execSQL("CREATE INDEX `comment_accID` ON `DeckComment` (`accountId`)");
            database.execSQL("CREATE UNIQUE INDEX `index_DeckComment_accountId_id` ON `DeckComment` (`accountId`, `id`)");
            database.execSQL("CREATE INDEX `index_DeckComment_id` ON `DeckComment` (`id`)");
            database.execSQL("CREATE INDEX `index_DeckComment_lastModifiedLocal` ON `DeckComment` (`lastModifiedLocal`)");
            database.execSQL("CREATE INDEX `index_DeckComment_objectId` ON `DeckComment` (`objectId`)");
            database.execSQL("CREATE INDEX `index_Mention_commentId` ON `Mention` (`commentId`)");
        }
    };

    private static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `Account` ADD `color` TEXT NOT NULL DEFAULT '#0082c9'");
            database.execSQL("ALTER TABLE `Account` ADD `textColor` TEXT NOT NULL DEFAULT '#ffffff'");
            database.execSQL("ALTER TABLE `Account` ADD `serverDeckVersion` TEXT NOT NULL DEFAULT '0.6.4'");
            database.execSQL("ALTER TABLE `Account` ADD `maintenanceEnabled` INTEGER NOT NULL DEFAULT 0");
        }
    };

    private static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // replace duplicates with the server-known ones
            Cursor duplucatesCursor = database.query("SELECT boardId, title, count(*) FROM Label group by boardid, title having count(*) > 1");
            if (duplucatesCursor != null && duplucatesCursor.moveToFirst()) {
                do {
                    long boardId = duplucatesCursor.getLong(0);
                    String title = duplucatesCursor.getString(1);
                    Cursor singleDuplicateCursor = database.query("select localId from Label where boardId = ? and title = ? order by id desc", new Object[]{boardId, title});
                    if (singleDuplicateCursor != null && singleDuplicateCursor.moveToFirst()) {
                        long idToUse = -1;
                        do {
                            if (idToUse < 0) {
                                // desc order -> first one is the one with remote ID or a random one. keep this one.
                                idToUse = singleDuplicateCursor.getLong(0);
                                continue;
                            }
                            long idToReplace = singleDuplicateCursor.getLong(0);
                            Cursor cardsAssignedToDuplicateCursor = database.query("select cardId, exists(select 1 from JoinCardWithLabel ij where ij.labelId = ? and ij.cardId = cardId) " +
                                    "from JoinCardWithLabel where labelId = ?", new Object[]{idToUse, idToReplace});
                            if (cardsAssignedToDuplicateCursor != null && cardsAssignedToDuplicateCursor.moveToFirst()) {
                                do {
                                    long cardId = cardsAssignedToDuplicateCursor.getLong(0);
                                    boolean hasDestinationLabelAssigned = cardsAssignedToDuplicateCursor.getInt(1) > 0;
                                    database.execSQL("DELETE FROM JoinCardWithLabel where labelId = ? and cardId = ?", new Object[]{idToReplace, cardId});

                                    if (!hasDestinationLabelAssigned) {
                                        database.execSQL("INSERT INTO JoinCardWithLabel (status,labelId,cardId) VALUES (?, ?, ?)", new Object[]{DBStatus.LOCAL_EDITED.getId(), idToUse, cardId});
                                    }
                                } while (cardsAssignedToDuplicateCursor.moveToNext());
                            }
                            database.execSQL("DELETE FROM Label where localId = ?", new Object[]{idToReplace});
                        } while (singleDuplicateCursor.moveToNext());
                    }
                } while (duplucatesCursor.moveToNext());
            }
//            database.execSQL("DELETE FROM Label WHERE id IS NULL AND EXISTS(SELECT 1 FROM Label il WHERE il.boardId = boardId AND il.title = title AND id IS NOT NULL)");
            database.execSQL("CREATE UNIQUE INDEX idx_label_title_unique ON Label(boardId, title)");
        }
    };

    private static final Migration MIGRATION_11_12 = new Migration(11, 12) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `SingleCardWidgetModel` (`widgetId` INTEGER PRIMARY KEY, `accountId` INTEGER, `boardId` INTEGER, `cardId` INTEGER, FOREIGN KEY(`accountId`) REFERENCES `Account`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, FOREIGN KEY(`boardId`) REFERENCES `Board`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE, FOREIGN KEY(`cardId`) REFERENCES `Card`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
            database.execSQL("CREATE INDEX `index_SingleCardWidgetModel_cardId` ON `SingleCardWidgetModel` (`cardId`)");
        }
    };

    private static final Migration MIGRATION_12_13 = new Migration(12, 13) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE INDEX `idx_cardWidgetModel_accountId` ON `SingleCardWidgetModel` (`accountId`)");
            database.execSQL("CREATE INDEX `idx_cardWidgetModel_boardId` ON `SingleCardWidgetModel` (`boardId`)");
        }
    };

    private static final Migration MIGRATION_13_14 = new Migration(13, 14) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `DeckComment` ADD `parentId` INTEGER REFERENCES DeckComment(localId) ON DELETE CASCADE");
            database.execSQL("CREATE INDEX `idx_comment_parentID` ON DeckComment(parentId)");
        }
    };

    private static final Migration MIGRATION_15_16 = new Migration(15, 16) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `StackWidgetModel` (`appWidgetId` INTEGER PRIMARY KEY, `accountId` INTEGER, `stackId` INTEGER, `darkTheme` INTEGER CHECK (`darkTheme` IN (0,1)) NOT NULL, " +
                    "FOREIGN KEY(`accountId`) REFERENCES `Account`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                    "FOREIGN KEY(`stackId`) REFERENCES `Stack`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
            database.execSQL("CREATE INDEX `index_StackWidgetModel_stackId` ON `StackWidgetModel` (`stackId`)");
            database.execSQL("CREATE INDEX `index_StackWidgetModel_accountId` ON `StackWidgetModel` (`accountId`)");
        }
    };

    private static final Migration MIGRATION_16_17 = new Migration(16, 17) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `OcsProject` (`localId` INTEGER PRIMARY KEY AUTOINCREMENT, `accountId` INTEGER NOT NULL, `id` INTEGER, `name` TEXT NOT NULL, `status` INTEGER NOT NULL, `lastModified` INTEGER, `lastModifiedLocal` INTEGER)");
            database.execSQL("CREATE UNIQUE INDEX `index_OcsProject_accountId_id` ON `OcsProject` (`accountId`, `id`)");
            database.execSQL("CREATE INDEX `index_project_accID` ON `OcsProject` (`accountId`)");
            database.execSQL("CREATE INDEX `index_OcsProject_id` ON `OcsProject` (`id`)");
            database.execSQL("CREATE INDEX `index_OcsProject_lastModifiedLocal` ON `OcsProject` (`lastModifiedLocal`)");

            database.execSQL("CREATE TABLE `OcsProjectResource` (`localId` INTEGER PRIMARY KEY AUTOINCREMENT, `accountId` INTEGER NOT NULL, `id` INTEGER, `name` TEXT, `status` INTEGER NOT NULL, `lastModified` INTEGER, `lastModifiedLocal` INTEGER, `projectId` INTEGER NOT NULL, `type` TEXT , `link` TEXT , `path` TEXT, `iconUrl` TEXT , `previewAvailable` INTEGER, `mimetype` TEXT, FOREIGN KEY(`projectId`) REFERENCES `OcsProject`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE)");
            database.execSQL("CREATE INDEX `index_projectResource_accID` ON `OcsProjectResource` (`accountId`)");
            database.execSQL("CREATE INDEX `index_projectResource_projectId` ON `OcsProjectResource` (`projectId`)");
            database.execSQL("CREATE UNIQUE INDEX `index_OcsProjectResource_accountId_id` ON `OcsProjectResource` (`accountId`, `id`, `projectId`)");
            database.execSQL("CREATE INDEX `index_OcsProjectResource_id` ON `OcsProjectResource` (`id`)");
            database.execSQL("CREATE INDEX `index_OcsProjectResource_lastModifiedLocal` ON `OcsProjectResource` (`lastModifiedLocal`)");

            database.execSQL("CREATE TABLE `JoinCardWithProject` (`status` INTEGER NOT NULL, `projectId` INTEGER NOT NULL, `cardId` INTEGER NOT NULL, PRIMARY KEY (`projectId`, `cardId`), FOREIGN KEY(`cardId`) REFERENCES `Card`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE, FOREIGN KEY(`projectId`) REFERENCES `OcsProject`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE)");
            database.execSQL("CREATE INDEX `index_JoinCardWithProject_projectId` ON `JoinCardWithProject` (`projectId`)");
            database.execSQL("CREATE INDEX `index_JoinCardWithProject_cardId` ON `JoinCardWithProject` (`cardId`)");
        }
    };

    private static final Migration MIGRATION_17_18 = new Migration(17, 18) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // https://github.com/stefan-niedermann/nextcloud-deck/issues/435
            database.execSQL("ALTER TABLE `Account` ADD `etag` TEXT");
        }
    };
    private static final Migration MIGRATION_18_19 = new Migration(18, 19) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // https://github.com/stefan-niedermann/nextcloud-deck/issues/619
            database.execSQL("DROP INDEX `index_OcsProjectResource_accountId_id`");
            database.execSQL("ALTER TABLE `OcsProjectResource` ADD `idString` TEXT");
            database.execSQL("CREATE UNIQUE INDEX `index_OcsProjectResource_accountId_id` ON `OcsProjectResource` (`accountId`, `id`, `idString`, `projectId`)");
        }
    };
    private static final Migration MIGRATION_19_20 = new Migration(19, 20) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // https://github.com/stefan-niedermann/nextcloud-deck/issues/492
            // https://github.com/stefan-niedermann/nextcloud-deck/issues/631
            database.execSQL("CREATE TABLE `UserInGroup` (`groupId` INTEGER NOT NULL, `memberId` INTEGER NOT NULL, " +
                    "primary KEY(`groupId`, `memberId`), " +
                    "FOREIGN KEY(`groupId`) REFERENCES `User`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                    "FOREIGN KEY(`memberId`) REFERENCES `User`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE)");
            database.execSQL("CREATE UNIQUE INDEX `unique_idx_group_member` ON `UserInGroup` (`groupId`, `memberId`)");
            database.execSQL("CREATE INDEX `index_UserInGroup_groupId` ON `UserInGroup` (`groupId`)");
            database.execSQL("CREATE INDEX `index_UserInGroup_memberId` ON `UserInGroup` (`memberId`)");

            database.execSQL("CREATE TABLE `UserInBoard` (`userId` INTEGER NOT NULL, `boardId` INTEGER NOT NULL, " +
                    "primary KEY(`userId`, `boardId`), " +
                    "FOREIGN KEY(`userId`) REFERENCES `User`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                    "FOREIGN KEY(`boardId`) REFERENCES `Board`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE)");
            database.execSQL("CREATE UNIQUE INDEX `unique_idx_user_board` ON `UserInBoard` (`userId`, `boardId`)");
            database.execSQL("CREATE INDEX `index_UserInBoard_userId` ON `UserInBoard` (`userId`)");
            database.execSQL("CREATE INDEX `index_UserInBoard_boardId` ON `UserInBoard` (`boardId`)");
        }
    };

    private static final Migration MIGRATION_20_21 = new Migration(20, 21) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // https://github.com/stefan-niedermann/nextcloud-deck/issues/556
            String suffix = "_new";
            {
                String tableName = "Account";
                database.execSQL("CREATE TABLE `" + tableName + suffix + "` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT NOT NULL, `userName` TEXT NOT NULL, `url` TEXT NOT NULL, " +
                        "`color` INTEGER NOT NULL DEFAULT 0, `textColor` INTEGER NOT NULL DEFAULT 0, `serverDeckVersion` TEXT NOT NULL DEFAULT '0.6.4', `maintenanceEnabled` INTEGER NOT NULL DEFAULT 0, `etag` TEXT)");
                Cursor cursor = database.query("select * from `" + tableName + "`");
                while (cursor.moveToNext()) {
                    String colorAsString1 = cursor.getString(4); // color
                    String colorAsString2 = cursor.getString(5); // textColor

                    @ColorInt Integer color1 = null;
                    @ColorInt Integer color2 = null;
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

                    @ColorInt Integer color1 = null;
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

                    @ColorInt Integer color1 = null;
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
    };

    private static final Migration MIGRATION_22_23 = new Migration(22, 23) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // https://github.com/stefan-niedermann/nextcloud-deck/issues/359
            database.execSQL("ALTER TABLE `Account` ADD `boardsEtag` TEXT");
            database.execSQL("ALTER TABLE `Board` ADD `etag` TEXT");
            database.execSQL("ALTER TABLE `Stack` ADD `etag` TEXT");
            database.execSQL("ALTER TABLE `Card` ADD `etag` TEXT");
            database.execSQL("ALTER TABLE `Label` ADD `etag` TEXT");
            database.execSQL("ALTER TABLE `AccessControl` ADD `etag` TEXT");
            database.execSQL("ALTER TABLE `Attachment` ADD `etag` TEXT");
            database.execSQL("ALTER TABLE `User` ADD `etag` TEXT");
            database.execSQL("ALTER TABLE `DeckComment` ADD `etag` TEXT");
            database.execSQL("ALTER TABLE `Activity` ADD `etag` TEXT");
            database.execSQL("ALTER TABLE `OcsProject` ADD `etag` TEXT");
            database.execSQL("ALTER TABLE `OcsProjectResource` ADD `etag` TEXT");
        }
    };

    public static final RoomDatabase.Callback ON_CREATE_CALLBACK = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            DeckLog.log("onCreate triggered!!!");
            LastSyncUtil.resetAll();
        }
    };

    public static synchronized DeckDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static DeckDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                DeckDatabase.class,
                DECK_DB_NAME)
                .addMigrations(MIGRATION_8_9)
                .addMigrations(MIGRATION_9_10)
                .addMigrations(MIGRATION_10_11)
                .addMigrations(MIGRATION_11_12)
                .addMigrations(MIGRATION_12_13)
                .addMigrations(MIGRATION_13_14)
                .addMigrations(new Migration(14, 15) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        // https://github.com/stefan-niedermann/nextcloud-deck/issues/570
                        SyncWorker.update(context);
                        // https://github.com/stefan-niedermann/nextcloud-deck/issues/525
                        PreferenceManager
                                .getDefaultSharedPreferences(context)
                                .edit()
                                .remove("it.niedermann.nextcloud.deck.theme_text")
                                .apply();
                    }
                })
                .addMigrations(MIGRATION_15_16)
                .addMigrations(MIGRATION_16_17)
                .addMigrations(MIGRATION_17_18)
                .addMigrations(MIGRATION_18_19)
                .addMigrations(MIGRATION_19_20)
                .addMigrations(MIGRATION_20_21)
                .addMigrations(new Migration(21, 22) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        // https://github.com/stefan-niedermann/nextcloud-deck/issues/715
                        final SharedPreferences.Editor lastSyncPref = context.getApplicationContext().getSharedPreferences("it.niedermann.nextcloud.deck.last_sync", Context.MODE_PRIVATE).edit();
                        Cursor cursor = database.query("select id from `Account`");
                        while (cursor.moveToNext()) {
                            lastSyncPref.remove("lS_" + cursor.getLong(0));
                        }
                        cursor.close();
                        lastSyncPref.apply();
                    }
                })
                .addMigrations(MIGRATION_22_23)
                .fallbackToDestructiveMigration()
                .addCallback(ON_CREATE_CALLBACK)
                .build();
    }

    public abstract AccountDao getAccountDao();

    public abstract AccessControlDao getAccessControlDao();

    public abstract BoardDao getBoardDao();

    public abstract CardDao getCardDao();

    public abstract JoinBoardWithLabelDao getJoinBoardWithLabelDao();

    public abstract JoinBoardWithPermissionDao getJoinBoardWithPermissionDao();

    public abstract JoinBoardWithUserDao getJoinBoardWithUserDao();

    public abstract JoinCardWithLabelDao getJoinCardWithLabelDao();

    public abstract JoinCardWithUserDao getJoinCardWithUserDao();

    public abstract LabelDao getLabelDao();

    public abstract ActivityDao getActivityDao();

    public abstract PermissionDao getPermissionDao();

    public abstract StackDao getStackDao();

    public abstract UserDao getUserDao();

    public abstract AttachmentDao getAttachmentDao();

    public abstract CommentDao getCommentDao();

    public abstract MentionDao getMentionDao();

    public abstract SingleCardWidgetModelDao getSingleCardWidgetModelDao();

    public abstract StackWidgetModelDao getStackWidgetModelDao();

    public abstract OcsProjectDao getOcsProjectDao();

    public abstract OcsProjectResourceDao getOcsProjectResourceDao();

    public abstract JoinCardWithOcsProjectDao getJoinCardWithOcsProjectDao();

    public abstract UserInGroupDao getUserInGroupDao();

    public abstract UserInBoardDao getUserInBoardDao();
}