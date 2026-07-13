package it.niedermann.nextcloud.deck.javafx;

import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.javafx.di.application.AppComponent;
import it.niedermann.nextcloud.deck.javafx.di.application.DaggerAppComponent;
import it.niedermann.nextcloud.deck.javafx.exception.FxUncaughtExceptionHandler;
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
//        appComponent.getPurgeService().purge();
    }

    @Override
    public void start(Stage stage) {

        Thread.setDefaultUncaughtExceptionHandler(new FxUncaughtExceptionHandler());

        final var fxComponent = appComponent.getFxComponentFactory().create(stage);
        final var applicationRouter = fxComponent.getApplicationRouter();

        applicationRouter.initialize();
    }
}
