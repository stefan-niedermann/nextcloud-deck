package it.niedermann.nextcloud.deck.javafx.di.fx;


import dagger.BindsInstance;
import dagger.Subcomponent;
import it.niedermann.nextcloud.deck.javafx.di.named.NamedPrimaryStage;
import it.niedermann.nextcloud.deck.javafx.exception.FxUncaughtExceptionHandler;
import it.niedermann.nextcloud.deck.javafx.services.application.ApplicationRouter;
import javafx.stage.Stage;

@FxScope
@Subcomponent(modules = {
        FxModule.class,
})
public interface FxComponent {

    @Subcomponent.Factory
    interface Factory {
        FxComponent create(@BindsInstance @NamedPrimaryStage Stage primaryStage);
    }

    ApplicationRouter getApplicationRouter();

    FxUncaughtExceptionHandler getFxUncaughtExceptionHandler();
}
