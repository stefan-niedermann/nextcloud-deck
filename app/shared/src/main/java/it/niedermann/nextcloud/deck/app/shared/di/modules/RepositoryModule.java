package it.niedermann.nextcloud.deck.app.shared.di.modules;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.deck.data.local.DeckDatabase;
import it.niedermann.nextcloud.deck.data.local.KeyValueStore;
import it.niedermann.nextcloud.deck.data.local.mapper.AccountMapper;
import it.niedermann.nextcloud.deck.data.repository.AccountRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.ActivityRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.AttachmentRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.BoardRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.CardRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.ColumnRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.CommentRepositoryImpl;
import it.niedermann.nextcloud.deck.data.repository.StateRepositoryImpl;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.repository.ActivityRepository;
import it.niedermann.nextcloud.deck.domain.repository.AttachmentRepository;
import it.niedermann.nextcloud.deck.domain.repository.BoardRepository;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import it.niedermann.nextcloud.deck.domain.repository.ColumnRepository;
import it.niedermann.nextcloud.deck.domain.repository.CommentRepository;
import it.niedermann.nextcloud.deck.domain.repository.StateRepository;
import jakarta.inject.Singleton;

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    StateRepository provideStateRepository(KeyValueStore keyValueStore) {
        return new StateRepositoryImpl(keyValueStore);
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
    AttachmentRepository provideAttachmentsRepository() {
        return new AttachmentRepositoryImpl();
    }

    @Provides
    @Singleton
    CommentRepository provideCommentRepository() {
        return new CommentRepositoryImpl();
    }

    @Provides
    @Singleton
    ActivityRepository provideActivityRepository() {
        return new ActivityRepositoryImpl();
    }
}
