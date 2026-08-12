package it.niedermann.nextcloud.deck.app.shared.di.modules;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.deck.data.local.DeckDatabase;
import it.niedermann.nextcloud.deck.data.local.dao.AccessControlDao;
import it.niedermann.nextcloud.deck.data.local.dao.AccountDao;
import it.niedermann.nextcloud.deck.data.local.dao.ActivityDao;
import it.niedermann.nextcloud.deck.data.local.dao.AttachmentDao;
import it.niedermann.nextcloud.deck.data.local.dao.BoardDao;
import it.niedermann.nextcloud.deck.data.local.dao.CardDao;
import it.niedermann.nextcloud.deck.data.local.dao.ColumnDao;
import it.niedermann.nextcloud.deck.data.local.dao.CommentDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinBoardWithLabelDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinBoardWithPermissionDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinBoardWithUserDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinCardWithLabelDao;
import it.niedermann.nextcloud.deck.data.local.dao.JoinCardWithUserDao;
import it.niedermann.nextcloud.deck.data.local.dao.LabelDao;
import it.niedermann.nextcloud.deck.data.local.dao.UserDao;
import jakarta.inject.Singleton;

@Module
public class LocalModule {

    @Provides
    @Singleton
    public AccountDao provideAccountDao(DeckDatabase deckDatabase) {
        return deckDatabase.getAccountDao();
    }

    @Provides
    @Singleton
    public BoardDao provideBoardDao(DeckDatabase deckDatabase) {
        return deckDatabase.getBoardDao();
    }

    @Provides
    @Singleton
    public ColumnDao provideColumnDao(DeckDatabase deckDatabase) {
        return deckDatabase.getColumnDao();
    }

    @Provides
    @Singleton
    public CardDao provideCardDao(DeckDatabase deckDatabase) {
        return deckDatabase.getCardDao();
    }

    @Provides
    @Singleton
    public LabelDao provideLabelDao(DeckDatabase deckDatabase) {
        return deckDatabase.getLabelDao();
    }

    @Provides
    @Singleton
    public AttachmentDao provideAttachmentDao(DeckDatabase deckDatabase) {
        return deckDatabase.getAttachmentDao();
    }

    @Provides
    @Singleton
    public CommentDao provideCommentDao(DeckDatabase deckDatabase) {
        return deckDatabase.getCommentDao();
    }

    @Provides
    @Singleton
    public AccessControlDao provideAccessControlDao(DeckDatabase deckDatabase) {
        return deckDatabase.getAccessControlDao();
    }

    @Provides
    @Singleton
    public JoinBoardWithLabelDao provideJoinBoardWithLabelDao(DeckDatabase deckDatabase) {
        return deckDatabase.getJoinBoardWithLabelDao();
    }

    @Provides
    @Singleton
    public JoinBoardWithPermissionDao provideJoinBoardWithPermissionDao(DeckDatabase deckDatabase) {
        return deckDatabase.getJoinBoardWithPermissionDao();
    }

    @Provides
    @Singleton
    public JoinBoardWithUserDao provideJoinBoardWithUserDao(DeckDatabase deckDatabase) {
        return deckDatabase.getJoinBoardWithUserDao();
    }

    @Provides
    @Singleton
    public JoinCardWithLabelDao provideJoinCardWithLabelDao(DeckDatabase deckDatabase) {
        return deckDatabase.getJoinCardWithLabelDao();
    }

    @Provides
    @Singleton
    public JoinCardWithUserDao provideJoinCardWithUserDao(DeckDatabase deckDatabase) {
        return deckDatabase.getJoinCardWithUserDao();
    }

    @Provides
    @Singleton
    public UserDao provideUserDao(DeckDatabase deckDatabase) {
        return deckDatabase.getUserDao();
    }

    @Provides
    @Singleton
    public ActivityDao provideActivityDao(DeckDatabase deckDatabase) {
        return deckDatabase.getActivityDao();
    }
}
