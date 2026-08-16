package it.niedermann.nextcloud.deck.javafx.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.javafx.di.fx.FxScope;
import it.niedermann.nextcloud.deck.javafx.services.application.ExceptionService;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.ExceptionDialog;
import jakarta.inject.Inject;
import javafx.application.Platform;

@FxScope
public class FxUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger logger = Logger.getLogger(FxUncaughtExceptionHandler.class.getName());

    private final ExceptionDialog.Factory exceptionDialogFactory;
    private final ExceptionService exceptionService;

    @Inject
    public FxUncaughtExceptionHandler(
            ExceptionDialog.Factory exceptionDialogFactory,
            ExceptionService exceptionService
    ) {
        this.exceptionDialogFactory = exceptionDialogFactory;
        this.exceptionService = exceptionService;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logger.log(Level.SEVERE, e.getMessage(), e);
        if (exceptionService.isDebugMode()) {
            final var dialog = exceptionDialogFactory.create(e);
            Platform.runLater(dialog::show);
        }
    }
}
