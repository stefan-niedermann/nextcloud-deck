package it.niedermann.nextcloud.deck.javafx;

import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.javafx.di.fx.FxComponent;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.AvatarView;
import javafx.application.Application;
import javafx.stage.Stage;

public class JavaFxApplication extends Application {

    private static final Logger logger = Logger.getLogger(JavaFxApplication.class.getName());

    protected static FxComponent.Factory fxComponentFactory = null;

    public static void inject(FxComponent.Factory fxComponentFactory) {
        JavaFxApplication.fxComponentFactory = fxComponentFactory;
    }

    public JavaFxApplication() {
        super();

        if (JavaFxApplication.fxComponentFactory == null) {
            throw new IllegalStateException("inject() not called");
        }
    }

    @Override
    public void start(Stage stage) {
        final var fxComponent = fxComponentFactory.create(stage);
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
