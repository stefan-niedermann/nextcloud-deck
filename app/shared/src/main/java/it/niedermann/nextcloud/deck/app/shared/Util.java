package it.niedermann.nextcloud.deck.app.shared;

import static it.niedermann.nextcloud.deck.app.shared.Constants.DECK_DB_NAME;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Util {

    private static final Path DB_PATH;

    static {
        final var envVarUserHome = System.getProperty("user.home");
        final var pathUserHome = Paths.get(envVarUserHome);
        DB_PATH = pathUserHome.resolve(DECK_DB_NAME);
    }

    private Util() {
        // Util class
    }

    public static Path getDatabasePath() {
        return DB_PATH;
    }
}
