package it.niedermann.nextcloud.deck.app.shared.di;

import com.google.gson.Gson;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.deck.app.shared.di.modules.MapperModule;
import it.niedermann.nextcloud.deck.app.shared.di.modules.RemoteModule;
import it.niedermann.nextcloud.deck.app.shared.di.modules.RepositoryModule;
import it.niedermann.nextcloud.deck.app.shared.di.modules.SyncModule;
import it.niedermann.nextcloud.remote.GsonProvider;
import jakarta.inject.Singleton;

@Module(includes = {
        RepositoryModule.class,
        MapperModule.class,
        SyncModule.class,
        RemoteModule.class,
})
public class SharedModule {

    @Provides
    @Singleton
    GsonProvider provideGsonProvider() {
        return new GsonProvider();
    }

    @Provides
    @Singleton
    Gson provideGson(GsonProvider gsonProvider) {
        return gsonProvider.getGson();
    }
}
