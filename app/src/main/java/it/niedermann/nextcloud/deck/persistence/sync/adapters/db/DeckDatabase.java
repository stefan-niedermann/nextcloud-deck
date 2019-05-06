package it.niedermann.nextcloud.deck.persistence.sync.adapters.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

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
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.AccessControlDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.AccountDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.AttachmentDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.BoardDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.CardDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.JoinBoardWithLabelDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.JoinBoardWithPermissionDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.JoinBoardWithUserDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.JoinCardWithLabelDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.JoinCardWithUserDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.LabelDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.PermissionDao;
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
        },
        exportSchema = false,
        version = 3
)
@TypeConverters({DateTypeConverter.class})
public abstract class DeckDatabase extends RoomDatabase {


    private static final String DECK_DB_NAME = "NC_DECK_DB.db";
    private static volatile DeckDatabase instance;

    public static final RoomDatabase.Callback ON_CREATE_CALLBACK = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
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
                //FIXME: remove destructive Migration as soon as schema is stable!
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

    public abstract PermissionDao getPermissionDao();

    public abstract StackDao getStackDao();

    public abstract UserDao getUserDao();

    public abstract AttachmentDao getAttachmentDao();
}