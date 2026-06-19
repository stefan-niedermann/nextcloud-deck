package it.niedermann.nextcloud.deck.javafx;

import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import it.niedermann.nextcloud.deck.app.shared.Util;
import it.niedermann.nextcloud.deck.app.shared.data.PreferencesKeyValueStore;
import it.niedermann.nextcloud.deck.data.local.DeckDatabase;
import it.niedermann.nextcloud.deck.data.local.KeyValueStore;
import it.niedermann.nextcloud.deck.javafx.di.AppComponent;
import it.niedermann.nextcloud.deck.javafx.di.DaggerAppComponent;
import it.niedermann.nextcloud.deck.javafx.exception.FxUncaughtExceptionHandler;
import javafx.application.Application;
import javafx.stage.Stage;

public class JavaFxApplication extends Application {

    private static final Logger logger = Logger.getLogger(JavaFxApplication.class.getName());

    private static final int preferencesVersion = 0;

    static void main(String[] args) {
        if (args.length >= 1 && "--purge".equals(args[0])) {
            purge(args);
        } else {
            launch(args);
        }
    }

    @Override
    public void start(Stage stage) {

        Thread.setDefaultUncaughtExceptionHandler(new FxUncaughtExceptionHandler());

//        purge();
        final var appComponent = createAppComponent(stage);
        final var router = appComponent.getRouter();
        final var routeProvider = appComponent.getRouteProvider();

        router.navigateTo(routeProvider.getSplashScreenRoute())
                .whenCompleteAsync((_, exception) -> {
                    if (exception != null) {
                        logger.log(Level.SEVERE, exception.getMessage(), exception);
                    }
                });
    }

    private static AppComponent createAppComponent(Stage stage) {

        final var pathDatabase = Util.getDatabasePath();
        final var database = DeckDatabase.Companion.getDatabaseBuilder(pathDatabase).build();

        return DaggerAppComponent.factory().create(stage, database, createKeyValueStore());
    }

    private static void purge(String... args) {
        final var prefs = createKeyValueStore();
        final var dbPath = Util.getDatabasePath();

        try {
            prefs.clear();
            logger.info("✓ Cleared " + prefs.getClass().getSimpleName());
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "× " + prefs.getClass().getSimpleName() + " could not be cleared.", e);
        }

        try {
            Files.delete(dbPath);
            logger.info("✓ Deleted " + dbPath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "× Database file " + dbPath + " could not be deleted.", e);
        }
    }

    private static KeyValueStore createKeyValueStore() {
        final var prefs = Preferences.userRoot().node(String.valueOf(preferencesVersion));
        return new PreferencesKeyValueStore(prefs);
    }
}
