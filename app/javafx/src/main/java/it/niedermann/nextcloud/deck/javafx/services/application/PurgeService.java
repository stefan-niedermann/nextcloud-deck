package it.niedermann.nextcloud.deck.javafx.services.application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.data.local.DeckDatabase;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;
import it.niedermann.nextcloud.deck.javafx.di.named.NamedDbPath;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

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
            keyValueStore.clear().join();
            logger.info("✓ Cleared " + keyValueStore.getClass().getSimpleName());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "× " + keyValueStore.getClass().getSimpleName() + " could not be cleared.", e);
        }

        try {
            database.close();
            deleteFile(dbPath);
            deleteFile(dbPath.resolveSibling(dbPath.getFileName() + "-wal"));
            deleteFile(dbPath.resolveSibling(dbPath.getFileName() + "-shm"));
            deleteFile(dbPath.resolveSibling(dbPath.getFileName() + ".lck"));
            logger.info("✓ Purge completed for " + dbPath);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "× Database files could not be fully deleted.", e);
        }
    }

    private void deleteFile(Path path) {
        for (int i = 0; i < 5; i++) {
            try {
                if (Files.deleteIfExists(path)) {
                    logger.info("✓ Deleted " + path);
                }
                return;
            } catch (IOException e) {
                logger.log(Level.WARNING, "× Could not delete " + path + " (attempt " + (i + 1) + "): " + e.getMessage());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
