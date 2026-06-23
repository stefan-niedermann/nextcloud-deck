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

    public void log(Object action, Object oldState, Object newState) {

        logger.info("\uD83D\uDCE2 " + action.toString());

        if (!Objects.equals(oldState, newState)) {

            if (newState == null) {

                logger.info("\uD83D\uDD04 - null -");

            } else {

                try {
                    logger.info("\uD83D\uDD04 " + gson.toJson(newState));
                } catch (Exception e) {
                    logger.info("\uD83D\uDD04 " + newState);
                    logger.warning(e::getMessage);
                }

            }

        } else {
            logger.info("\uD83D\uDD04 - no changes -");
        }
    }
}
