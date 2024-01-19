package it.niedermann.nextcloud.deck.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.database.converter.DateTypeConverter;
import it.niedermann.nextcloud.deck.database.converter.EnumConverter;
import it.niedermann.nextcloud.deck.database.dao.AccessControlDao;
import it.niedermann.nextcloud.deck.database.dao.AccountDao;
import it.niedermann.nextcloud.deck.database.dao.ActivityDao;
import it.niedermann.nextcloud.deck.database.dao.AttachmentDao;
import it.niedermann.nextcloud.deck.database.dao.BoardDao;
import it.niedermann.nextcloud.deck.database.dao.CardDao;
import it.niedermann.nextcloud.deck.database.dao.CommentDao;
import it.niedermann.nextcloud.deck.database.dao.JoinBoardWithLabelDao;
import it.niedermann.nextcloud.deck.database.dao.JoinBoardWithPermissionDao;
import it.niedermann.nextcloud.deck.database.dao.JoinBoardWithUserDao;
import it.niedermann.nextcloud.deck.database.dao.JoinCardWithLabelDao;
import it.niedermann.nextcloud.deck.database.dao.JoinCardWithUserDao;
import it.niedermann.nextcloud.deck.database.dao.LabelDao;
import it.niedermann.nextcloud.deck.database.dao.MentionDao;
import it.niedermann.nextcloud.deck.database.dao.PermissionDao;
import it.niedermann.nextcloud.deck.database.dao.StackDao;
import it.niedermann.nextcloud.deck.database.dao.UserDao;
import it.niedermann.nextcloud.deck.database.dao.UserInBoardDao;
import it.niedermann.nextcloud.deck.database.dao.UserInGroupDao;
import it.niedermann.nextcloud.deck.database.dao.projects.JoinCardWithOcsProjectDao;
import it.niedermann.nextcloud.deck.database.dao.projects.OcsProjectDao;
import it.niedermann.nextcloud.deck.database.dao.projects.OcsProjectResourceDao;
import it.niedermann.nextcloud.deck.database.dao.widgets.SingleCardWidgetModelDao;
import it.niedermann.nextcloud.deck.database.dao.widgets.filter.FilterWidgetAccountDao;
import it.niedermann.nextcloud.deck.database.dao.widgets.filter.FilterWidgetBoardDao;
import it.niedermann.nextcloud.deck.database.dao.widgets.filter.FilterWidgetDao;
import it.niedermann.nextcloud.deck.database.dao.widgets.filter.FilterWidgetLabelDao;
import it.niedermann.nextcloud.deck.database.dao.widgets.filter.FilterWidgetProjectDao;
import it.niedermann.nextcloud.deck.database.dao.widgets.filter.FilterWidgetSortDao;
import it.niedermann.nextcloud.deck.database.dao.widgets.filter.FilterWidgetStackDao;
import it.niedermann.nextcloud.deck.database.dao.widgets.filter.FilterWidgetUserDao;
import it.niedermann.nextcloud.deck.database.migration.Migration_10_11;
import it.niedermann.nextcloud.deck.database.migration.Migration_11_12;
import it.niedermann.nextcloud.deck.database.migration.Migration_12_13;
import it.niedermann.nextcloud.deck.database.migration.Migration_13_14;
import it.niedermann.nextcloud.deck.database.migration.Migration_14_15;
import it.niedermann.nextcloud.deck.database.migration.Migration_15_16;
import it.niedermann.nextcloud.deck.database.migration.Migration_16_17;
import it.niedermann.nextcloud.deck.database.migration.Migration_17_18;
import it.niedermann.nextcloud.deck.database.migration.Migration_18_19;
import it.niedermann.nextcloud.deck.database.migration.Migration_19_20;
import it.niedermann.nextcloud.deck.database.migration.Migration_20_21;
import it.niedermann.nextcloud.deck.database.migration.Migration_21_22;
import it.niedermann.nextcloud.deck.database.migration.Migration_22_23;
import it.niedermann.nextcloud.deck.database.migration.Migration_23_24;
import it.niedermann.nextcloud.deck.database.migration.Migration_24_25;
import it.niedermann.nextcloud.deck.database.migration.Migration_25_26;
import it.niedermann.nextcloud.deck.database.migration.Migration_26_27;
import it.niedermann.nextcloud.deck.database.migration.Migration_27_28;
import it.niedermann.nextcloud.deck.database.migration.Migration_28_29;
import it.niedermann.nextcloud.deck.database.migration.Migration_29_30;
import it.niedermann.nextcloud.deck.database.migration.Migration_30_31;
import it.niedermann.nextcloud.deck.database.migration.Migration_31_32;
import it.niedermann.nextcloud.deck.database.migration.Migration_32_33;
import it.niedermann.nextcloud.deck.database.migration.Migration_8_9;
import it.niedermann.nextcloud.deck.database.migration.Migration_9_10;
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
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.Mention;
import it.niedermann.nextcloud.deck.model.ocs.projects.JoinCardWithProject;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProject;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectResource;
import it.niedermann.nextcloud.deck.model.relations.UserInBoard;
import it.niedermann.nextcloud.deck.model.relations.UserInGroup;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetAccount;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetBoard;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetLabel;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetProject;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetSort;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetStack;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetUser;
import it.niedermann.nextcloud.deck.model.widget.singlecard.SingleCardWidgetModel;
import it.niedermann.nextcloud.deck.remote.api.LastSyncUtil;

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
                OcsProject.class,
                OcsProjectResource.class,
                JoinCardWithProject.class,
                UserInGroup.class,
                UserInBoard.class,
                FilterWidget.class,
                FilterWidgetAccount.class,
                FilterWidgetBoard.class,
                FilterWidgetStack.class,
                FilterWidgetLabel.class,
                FilterWidgetUser.class,
                FilterWidgetProject.class,
                FilterWidgetSort.class,
        },
        exportSchema = false,
        version = 33
)
@TypeConverters({DateTypeConverter.class, EnumConverter.class})
public abstract class DeckDatabase extends RoomDatabase {

    private static final String DECK_DB_NAME = "NC_DECK_DB.db";
    private static volatile DeckDatabase instance;

    public static final RoomDatabase.Callback ON_CREATE_CALLBACK = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            DeckLog.info("Database", DECK_DB_NAME, "created.");
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
                .addMigrations(new Migration_8_9())
                .addMigrations(new Migration_9_10())
                .addMigrations(new Migration_10_11())
                .addMigrations(new Migration_11_12())
                .addMigrations(new Migration_12_13())
                .addMigrations(new Migration_13_14())
                .addMigrations(new Migration_14_15(context))
                .addMigrations(new Migration_15_16())
                .addMigrations(new Migration_16_17())
                .addMigrations(new Migration_17_18())
                .addMigrations(new Migration_18_19())
                .addMigrations(new Migration_19_20())
                .addMigrations(new Migration_20_21())
                .addMigrations(new Migration_21_22(context))
                .addMigrations(new Migration_22_23())
                .addMigrations(new Migration_23_24(context))
                .addMigrations(new Migration_24_25())
                .addMigrations(new Migration_25_26())
                .addMigrations(new Migration_26_27())
                .addMigrations(new Migration_27_28())
                .addMigrations(new Migration_28_29())
                .addMigrations(new Migration_29_30(context))
                .addMigrations(new Migration_30_31())
                .addMigrations(new Migration_31_32(context))
                .addMigrations(new Migration_32_33())
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

    public abstract OcsProjectDao getOcsProjectDao();

    public abstract OcsProjectResourceDao getOcsProjectResourceDao();

    public abstract JoinCardWithOcsProjectDao getJoinCardWithOcsProjectDao();

    public abstract UserInGroupDao getUserInGroupDao();

    public abstract UserInBoardDao getUserInBoardDao();

    public abstract FilterWidgetDao getFilterWidgetDao();

    public abstract FilterWidgetAccountDao getFilterWidgetAccountDao();

    public abstract FilterWidgetBoardDao getFilterWidgetBoardDao();

    public abstract FilterWidgetStackDao getFilterWidgetStackDao();

    public abstract FilterWidgetLabelDao getFilterWidgetLabelDao();

    public abstract FilterWidgetUserDao getFilterWidgetUserDao();

    public abstract FilterWidgetProjectDao getFilterWidgetProjectDao();

    public abstract FilterWidgetSortDao getFilterWidgetSortDao();

}