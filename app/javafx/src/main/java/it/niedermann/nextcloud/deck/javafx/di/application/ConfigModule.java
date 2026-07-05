package it.niedermann.nextcloud.deck.javafx.di.application;

import static it.niedermann.nextcloud.deck.app.shared.Constants.DECK_DB_NAME;

import java.nio.file.Path;
import java.nio.file.Paths;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.deck.javafx.di.named.NamedDbPath;
import it.niedermann.nextcloud.deck.javafx.di.named.NamedPreferencesVersion;
import it.niedermann.nextcloud.deck.javafx.di.named.NamedUserHomePath;
import jakarta.inject.Singleton;

@Module()
public class ConfigModule {

    @Provides
    @Singleton
    @NamedPreferencesVersion
    int providePreferencesVersion() {
        return 0;
    }

    @Provides
    @Singleton
    @NamedUserHomePath
    Path provideUserHomePath() {
        final var envVarUserHome = System.getProperty("user.home");
        return Paths.get(envVarUserHome);
    }

    @Provides
    @Singleton
    @NamedDbPath
    Path provideDbPath(@NamedUserHomePath Path userHome) {
        return userHome.resolve(DECK_DB_NAME);
    }

}
