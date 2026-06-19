package it.niedermann.nextcloud.deck.cli;

import java.util.prefs.Preferences;

import it.niedermann.nextcloud.deck.app.shared.Util;
import it.niedermann.nextcloud.deck.app.shared.data.PreferencesKeyValueStore;
import it.niedermann.nextcloud.deck.cli.commands.RootCmd;
import it.niedermann.nextcloud.deck.cli.di.AppComponent;
import it.niedermann.nextcloud.deck.cli.di.CommandFactory;
import it.niedermann.nextcloud.deck.cli.di.DaggerAppComponent;
import it.niedermann.nextcloud.deck.data.local.DeckDatabase;
import picocli.CommandLine;

public class CliApplication {

    private static final int preferencesVersion = 0;

    public static void main(String[] args) {

        final var appComponent = createAppComponent();
        final var commandFactory = new CommandFactory(appComponent);
        final var rootCmd = new CommandLine(RootCmd.class, commandFactory);

        final int exitCode = rootCmd.execute(args);
        System.exit(exitCode);
    }

    private static AppComponent createAppComponent() {

        final var pathDatabase = Util.getDatabasePath();
        final var database = DeckDatabase.Companion.getDatabaseBuilder(pathDatabase).build();

        final var prefs = Preferences.userRoot().node(String.valueOf(preferencesVersion));
        final var keyValueStore = new PreferencesKeyValueStore(prefs);

        return DaggerAppComponent.factory().create(database, keyValueStore);
    }
}