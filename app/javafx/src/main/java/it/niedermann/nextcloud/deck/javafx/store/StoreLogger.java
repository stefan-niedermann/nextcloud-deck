package it.niedermann.nextcloud.deck.javafx.store;

import com.google.gson.Gson;

import java.util.Objects;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.javafx.di.fx.FxScope;
import jakarta.inject.Inject;

@FxScope
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
                    // TODO This does not play well with Optional<?> types and causes lots of warnings (see catch block)
                    logger.info("\uD83D\uDD04 " + gson.toJson(newState));
                } catch (Exception e) {
                    logger.info("\uD83D\uDD04 " + newState);
                    // logger.warning(e::getMessage);
                }

            }

        } else {
            logger.info("\uD83D\uDD04 - no changes -");
        }
    }
}
