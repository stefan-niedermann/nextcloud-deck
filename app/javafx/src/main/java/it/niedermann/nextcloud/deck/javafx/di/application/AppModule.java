package it.niedermann.nextcloud.deck.javafx.di.application;

import java.nio.file.Path;
import java.util.prefs.Preferences;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.deck.app.shared.data.PreferencesKeyValueStore;
import it.niedermann.nextcloud.deck.data.local.DeckDatabase;
import it.niedermann.nextcloud.deck.data.local.KeyValueStore;
import it.niedermann.nextcloud.deck.javafx.di.fx.FxComponent;
import it.niedermann.nextcloud.deck.javafx.di.named.NamedDbPath;
import it.niedermann.nextcloud.deck.javafx.di.named.NamedPreferencesVersion;
import jakarta.inject.Singleton;

@Module(subcomponents = FxComponent.class)
public class AppModule {

    @Provides
    @Singleton
    KeyValueStore provideKeyValueStore(@NamedPreferencesVersion int preferencesVersion) {
        final var prefs = Preferences.userRoot().node(String.valueOf(preferencesVersion));
        return new PreferencesKeyValueStore(prefs);
    }

    @Provides
    @Singleton
    DeckDatabase provideDeckDatabase(@NamedDbPath Path dbPath) {
        return DeckDatabase.Companion.getDatabaseBuilder(dbPath).build();
    }
}
