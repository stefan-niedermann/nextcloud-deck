package it.niedermann.nextcloud.deck.app.shared.di;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.deck.app.shared.di.model.BuildConfig;
import it.niedermann.nextcloud.deck.app.shared.di.modules.LocalModule;
import it.niedermann.nextcloud.deck.app.shared.di.modules.MapperModule;
import it.niedermann.nextcloud.deck.app.shared.di.modules.RemoteModule;
import it.niedermann.nextcloud.deck.app.shared.di.modules.RepositoryModule;
import it.niedermann.nextcloud.deck.app.shared.di.modules.SyncModule;
import it.niedermann.nextcloud.remote.GsonProvider;
import jakarta.inject.Singleton;

@Module(includes = {
        LocalModule.class,
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

    @Provides
    @Singleton
    BuildConfig provideBuildConfig() {
        final var properties = new Properties();

        try (final var inputStream = SharedModule.class.getClassLoader().getResourceAsStream("buildconfig.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not load buildconfig.properties", e);
        }

        final var helpUri = properties.getProperty("help_uri");

        if (helpUri == null) {
            throw new IllegalStateException("Property 'help_uri' must be set in buildconfig.properties");
        }

        final var uri = URI.create(helpUri);

        return new BuildConfig(uri);

    }
}
