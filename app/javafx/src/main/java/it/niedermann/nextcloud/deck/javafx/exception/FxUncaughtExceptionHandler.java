package it.niedermann.nextcloud.deck.javafx.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.javafx.ui.controller.features.ExceptionDialog;
import javafx.application.Platform;

public class FxUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger logger = Logger.getLogger(FxUncaughtExceptionHandler.class.getName());

    private final ExceptionUnwrapper exceptionUnwrapper;

    public FxUncaughtExceptionHandler() {
        this.exceptionUnwrapper = new ExceptionUnwrapper();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        logger.log(Level.SEVERE, e.getMessage(), e);
        final var unwrappedException = exceptionUnwrapper.unwrap(e);
        Platform.runLater(() -> new ExceptionDialog().show(unwrappedException));

    }
}
