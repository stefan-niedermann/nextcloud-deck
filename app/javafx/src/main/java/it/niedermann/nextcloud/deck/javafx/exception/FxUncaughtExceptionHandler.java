package it.niedermann.nextcloud.deck.javafx.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.javafx.di.fx.FxScope;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.ExceptionDialog;
import jakarta.inject.Inject;
import javafx.application.Platform;

@FxScope
public class FxUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger logger = Logger.getLogger(FxUncaughtExceptionHandler.class.getName());

    private final ExceptionDialog.Factory exceptionDialogFactory;

    @Inject
    public FxUncaughtExceptionHandler(
            ExceptionDialog.Factory exceptionDialogFactory
    ) {
        this.exceptionDialogFactory = exceptionDialogFactory;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logger.log(Level.SEVERE, e.getMessage(), e);
        final var dialog = exceptionDialogFactory.create(e);
        Platform.runLater(dialog::show);
    }
}
