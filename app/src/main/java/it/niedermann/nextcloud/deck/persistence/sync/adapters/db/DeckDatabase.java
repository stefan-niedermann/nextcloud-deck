package it.niedermann.nextcloud.deck.persistence.sync.adapters.db;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

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
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.Mention;
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
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.SingleCardWidgetModelDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.StackDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.UserDao;

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
        },
        exportSchema = false,
        version = 15
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
}