package it.niedermann.nextcloud.deck.javafx.services.application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.data.local.DeckDatabase;
import it.niedermann.nextcloud.deck.data.local.KeyValueStore;
import it.niedermann.nextcloud.deck.javafx.di.named.NamedDbPath;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javafx.application.Platform;

@Singleton
public class PurgeService {

    private static final Logger logger = Logger.getLogger(PurgeService.class.getName());

    private final Path dbPath;
    private final DeckDatabase database;
    private final KeyValueStore keyValueStore;

    @Inject
    public PurgeService(@NamedDbPath Path dbPath,
                        DeckDatabase database,
                        KeyValueStore keyValueStore) {
        this.dbPath = dbPath;
        this.database = database;
        this.keyValueStore = keyValueStore;
    }

    public void purge() {
        try {
            keyValueStore.clear();
            logger.info("✓ Cleared " + keyValueStore.getClass().getSimpleName());
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "× " + keyValueStore.getClass().getSimpleName() + " could not be cleared.", e);
        }

        try {
            Platform.exit();
            database.close();
            Files.delete(dbPath);
            logger.info("✓ Deleted " + dbPath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "× Database file " + dbPath + " could not be deleted.", e);
        }
    }
}
