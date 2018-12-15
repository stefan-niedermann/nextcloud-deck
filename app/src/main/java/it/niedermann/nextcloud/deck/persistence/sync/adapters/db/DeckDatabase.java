package it.niedermann.nextcloud.deck.persistence.sync.adapters.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.JoinBoardWithLabel;
import it.niedermann.nextcloud.deck.model.JoinBoardWithPermission;
import it.niedermann.nextcloud.deck.model.JoinBoardWithUser;
import it.niedermann.nextcloud.deck.model.JoinCardWithLabel;
import it.niedermann.nextcloud.deck.model.JoinCardWithUser;
import it.niedermann.nextcloud.deck.model.JoinStackWithCard;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Permission;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.UserDao;

@Database(
        entities = {
                Account.class,
                Board.class,
                Card.class,
                JoinBoardWithLabel.class,
                JoinBoardWithPermission.class,
                JoinBoardWithUser.class,
                JoinCardWithLabel.class,
                JoinCardWithUser.class,
                JoinStackWithCard.class,
                Label.class,
                Permission.class,
                Stack.class,
                User.class,

        },
        version = 1
)
public abstract class DeckDatabase extends RoomDatabase {

//    public abstract AccountDao getAccountDao();
//
//    public abstract BoardDao getBoardDao();
//
//    public abstract CardDao getCardDao();
//
//    public abstract JoinBoardWithLabelDao getJoinBoardWithLabelDao();
//
//    public abstract JoinBoardWithPermissionDao getJoinBoardWithPermissionDao();
//
//    public abstract JoinBoardWithUserDao getJoinBoardWithUserDao();
//
//    public abstract JoinCardWithLabelDao getJoinCardWithLabelDao();
//
//    public abstract JoinCardWithUserDao getJoinCardWithUserDao();
//
//    public abstract JoinStackWithCardDao getJoinStackWithCardDao();
//
//    public abstract LabelDao getLabelDao();
//
//    public abstract PermissionDao getPermissionDao();
//
//    public abstract StackDao getStackDao();

    public abstract UserDao getUserDao();
}