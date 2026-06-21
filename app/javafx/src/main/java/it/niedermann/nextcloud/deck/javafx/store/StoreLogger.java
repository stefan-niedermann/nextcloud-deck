package it.niedermann.nextcloud.deck.javafx.store;

import com.google.gson.Gson;

import java.util.Objects;
import java.util.logging.Logger;

import jakarta.inject.Inject;

public class StoreLogger {

    private static final Logger logger = Logger.getLogger(StoreLogger.class.getName());

    private final Gson gson;

    @Inject
    public StoreLogger(Gson gson) {
        this.gson = gson;
    }

    public void log(Action action, Object oldState, Object newState) {

        logger.info("\uD83D\uDCE2 " + action.toString());

        if (!Objects.equals(oldState, newState)) {
            logger.info("\uD83D\uDD04 " + gson.toJson(newState));

        } else {
            logger.info("\uD83D\uDD04 - no changes -");
        }
    }
}
