package it.niedermann.nextcloud.deck.javafx;

import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.javafx.di.application.AppComponent;
import it.niedermann.nextcloud.deck.javafx.di.application.DaggerAppComponent;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.AvatarView;
import javafx.application.Application;
import javafx.stage.Stage;

public class JavaFxApplication extends Application {

    private static final Logger logger = Logger.getLogger(JavaFxApplication.class.getName());

    private final AppComponent appComponent;

    static void main(String[] args) {
        launch(args);
    }

    public JavaFxApplication() {
        super();
        appComponent = DaggerAppComponent.factory().create();
        // TODO Provide Purge-Button in ExceptionDialog
        // appComponent.getPurgeService().purge();
    }

    @Override
    public void start(Stage stage) {
        final var fxComponent = appComponent.getFxComponentFactory().create(stage);
        final var exceptionHandler = fxComponent.getFxUncaughtExceptionHandler();

        try {

            Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
            AvatarView.initialize(fxComponent.getGetAvatarUseCase());

            final var applicationRouter = fxComponent.getApplicationRouter();
            applicationRouter.initialize();

        } catch (Exception e) {
            exceptionHandler.uncaughtException(Thread.currentThread(), e);
        }
    }
}
