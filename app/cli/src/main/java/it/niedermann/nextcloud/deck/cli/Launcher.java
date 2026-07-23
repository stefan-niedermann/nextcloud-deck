package it.niedermann.nextcloud.deck.cli;

import java.util.prefs.Preferences;

import it.niedermann.nextcloud.deck.app.shared.Util;
import it.niedermann.nextcloud.deck.app.shared.data.PreferencesKeyValueStore;
import it.niedermann.nextcloud.deck.cli.di.AppComponent;
import it.niedermann.nextcloud.deck.cli.di.DaggerAppComponent;
import it.niedermann.nextcloud.deck.data.local.DeckDatabase;

public class Launcher {

    private static final int preferencesVersion = 0;

    static void main(String[] args) {

        final var appComponent = createAppComponent();
        new CliApplication(appComponent, args);

    }

    private static AppComponent createAppComponent() {

        final var pathDatabase = Util.getDatabasePath();
        final var database = DeckDatabase.Companion.getDatabaseBuilder(pathDatabase).build();

        final var prefs = Preferences.userRoot().node(String.valueOf(preferencesVersion));
        final var keyValueStore = new PreferencesKeyValueStore(prefs);

        return DaggerAppComponent.factory().create(database, keyValueStore);
    }
}