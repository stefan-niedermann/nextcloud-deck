package it.niedermann.nextcloud.deck.app.shared.di.modules;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.deck.data.local.dao.AccessControlDao;
import it.niedermann.nextcloud.deck.data.local.dao.AccountDao;
import it.niedermann.nextcloud.deck.data.local.dao.ActivityDao;
import it.niedermann.nextcloud.deck.data.local.dao.AttachmentDao;
import it.niedermann.nextcloud.deck.data.local.dao.BoardDao;
import it.niedermann.nextcloud.deck.data.local.dao.CardDao;
import it.niedermann.nextcloud.deck.data.local.dao.ColumnDao;
import it.niedermann.nextcloud.deck.data.local.dao.CommentDao;
import it.niedermann.nextcloud.deck.data.local.dao.LabelDao;
import it.niedermann.nextcloud.deck.data.local.dao.UserDao;
import it.niedermann.nextcloud.deck.data.local.mapper.AccessControlMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.AccountMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.ActivityMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.AttachmentMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.BoardMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.CardMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.ColumnMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.CommentMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.LabelMapper;
import it.niedermann.nextcloud.deck.data.local.mapper.UserMapper;
import it.niedermann.nextcloud.deck.data.repository.AccountRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.ActivityRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.AttachmentRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.BoardRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.CardRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.ColumnRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.CommentRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.LabelRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.ShareRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.StateRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.UserRepositoryImpl;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.repository.ActivityRepository;
import it.niedermann.nextcloud.deck.domain.repository.AttachmentRepository;
import it.niedermann.nextcloud.deck.domain.repository.BoardRepository;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import it.niedermann.nextcloud.deck.domain.repository.ColumnRepository;
import it.niedermann.nextcloud.deck.domain.repository.CommentRepository;
import it.niedermann.nextcloud.deck.domain.repository.LabelRepository;
import it.niedermann.nextcloud.deck.domain.repository.ShareRepository;
import it.niedermann.nextcloud.deck.domain.repository.UserRepository;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;
import it.niedermann.nextcloud.deck.domain.state.StateRepository;
import it.niedermann.nextcloud.remote.ApiProvider;
import jakarta.inject.Singleton;

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    StateRepository provideStateRepository(KeyValueStore keyValueStore, AccountRepository accountRepository) {
        return new StateRepositoryImpl(keyValueStore, accountRepository);
    }

    @Provides
    @Singleton
    AccountRepository provideAccountRepository(AccountDao accountDao,
                                               AccountMapper accountMapper) {
        return new AccountRepositoryImpl(accountDao, accountMapper);
    }

    @Provides
    @Singleton
    BoardRepository provideBoardRepository(BoardDao boardDao,
                                           BoardMapper boardMapper) {
        return new BoardRepositoryImpl(boardDao, boardMapper);
    }

    @Provides
    @Singleton
    ColumnRepository provideColumnRepository(ColumnDao columnDao,
                                             ColumnMapper columnMapper) {
        return new ColumnRepositoryImpl(columnDao, columnMapper);
    }

    @Provides
    @Singleton
    CardRepository provideCardRepository(CardDao cardDao,
                                         CardMapper cardMapper,
                                         LabelDao labelDao,
                                         LabelMapper labelMapper,
                                         CommentDao commentDao,
                                         AttachmentDao attachmentDao) {
        return new CardRepositoryImpl(cardDao, cardMapper, labelDao, labelMapper, commentDao, attachmentDao);
    }

    @Provides
    @Singleton
    UserRepository provideUserRepository(ApiProvider.Factory apiProviderFactory,
                                         AccountRepository accountRepository,
                                         UserDao userDao,
                                         UserMapper userMapper) {
        return new UserRepositoryImpl(apiProviderFactory, accountRepository, userDao, userMapper);
    }

    @Provides
    @Singleton
    AttachmentRepository provideAttachmentRepository(AttachmentDao attachmentDao,
                                                     AttachmentMapper attachmentMapper) {
        return new AttachmentRepositoryImpl(attachmentDao, attachmentMapper);
    }

    @Provides
    @Singleton
    LabelRepository provideLabelRepository(LabelDao labelDao,
                                           LabelMapper labelMapper) {
        return new LabelRepositoryImpl(labelDao, labelMapper);
    }

    @Provides
    @Singleton
    CommentRepository provideCommentRepository(AccountRepository accountRepository,
                                               CommentDao commentDao,
                                               CommentMapper commentMapper) {
        return new CommentRepositoryImpl(accountRepository, commentDao, commentMapper);
    }

    @Provides
    @Singleton
    ActivityRepository provideActivityRepository(AccountRepository accountRepository,
                                                 ActivityDao activityDao,
                                                 ActivityMapper activityMapper) {
        return new ActivityRepositoryImpl(accountRepository, activityDao, activityMapper);
    }

    @Provides
    @Singleton
    ShareRepository provideShareRepository(AccessControlDao accessControlDao,
                                           BoardDao boardDao,
                                           UserDao userDao,
                                           AccessControlMapper accessControlMapper) {
        return new ShareRepositoryImpl(accessControlDao, boardDao, userDao, accessControlMapper);
    }
}
