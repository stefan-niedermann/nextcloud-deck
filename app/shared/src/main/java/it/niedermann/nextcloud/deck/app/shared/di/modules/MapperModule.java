package it.niedermann.nextcloud.deck.app.shared.di.modules;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.deck.data.local.mapper.AccountMapper;
import jakarta.inject.Singleton;

@Module
public class MapperModule {

    @Provides
    @Singleton
    AccountMapper provideAccountMapper() {
        return AccountMapper.INSTANCE;
    }
    
}
