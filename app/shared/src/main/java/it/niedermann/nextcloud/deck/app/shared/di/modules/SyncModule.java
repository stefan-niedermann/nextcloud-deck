package it.niedermann.nextcloud.deck.app.shared.di.modules;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.deck.data.sync.QueueingSyncScheduler;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.sync.SyncScheduler;
import it.niedermann.nextcloud.remote.ApiProvider;
import jakarta.inject.Singleton;

@Module
public class SyncModule {

    @Provides
    @Singleton
    SyncScheduler provideSyncScheduler(ApiProvider.Factory apiProviderFactory,
                                       AccountRepository accountRepository) {
        return new QueueingSyncScheduler(apiProviderFactory, accountRepository);
    }
}
