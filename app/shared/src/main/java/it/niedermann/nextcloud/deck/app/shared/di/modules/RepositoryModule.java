package it.niedermann.nextcloud.deck.app.shared.di.modules;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.deck.data.local.DeckDatabase;
import it.niedermann.nextcloud.deck.data.local.mapper.AccountMapper;
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
    AccountRepository provideAccountRepository(DeckDatabase deckDatabase,
                                               AccountMapper accountMapper) {
        return new AccountRepositoryImpl(deckDatabase.getAccountDao(), accountMapper);
    }

    @Provides
    @Singleton
    BoardRepository provideBoardRepository() {
        return new BoardRepositoryImpl();
    }

    @Provides
    @Singleton
    ColumnRepository provideColumnRepository() {
        return new ColumnRepositoryImpl();
    }

    @Provides
    @Singleton
    CardRepository provideCardRepository() {
        return new CardRepositoryImpl();
    }

    @Provides
    @Singleton
    UserRepository provideUserRepository(ApiProvider.Factory apiProviderFactory, AccountRepository accountRepository) {
        return new UserRepositoryImpl(apiProviderFactory, accountRepository);
    }

    @Provides
    @Singleton
    AttachmentRepository provideAttachmentRepository() {
        return new AttachmentRepositoryImpl();
    }

    @Provides
    @Singleton
    LabelRepository provideLabelRepository() {
        return new LabelRepositoryImpl();
    }

    @Provides
    @Singleton
    CommentRepository provideCommentRepository(AccountRepository accountRepository) {
        return new CommentRepositoryImpl(accountRepository);
    }

    @Provides
    @Singleton
    ActivityRepository provideActivityRepository(AccountRepository accountRepository) {
        return new ActivityRepositoryImpl(accountRepository);
    }

    @Provides
    @Singleton
    ShareRepository provideShareRepository() {
        return new ShareRepositoryImpl();
    }
}
