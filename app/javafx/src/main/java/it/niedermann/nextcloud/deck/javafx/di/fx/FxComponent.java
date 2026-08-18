package it.niedermann.nextcloud.deck.javafx.di.fx;


import dagger.BindsInstance;
import dagger.Subcomponent;
import it.niedermann.nextcloud.deck.domain.usecases.users.GetAvatarUseCase;
import it.niedermann.nextcloud.deck.javafx.di.named.NamedPrimaryStage;
import it.niedermann.nextcloud.deck.javafx.services.ApplicationRouter;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.FxUncaughtExceptionHandler;
import javafx.application.HostServices;
import javafx.stage.Stage;

@FxScope
@Subcomponent(modules = {
        FxModule.class,
})
public interface FxComponent {

    @Subcomponent.Factory
    interface Factory {
        FxComponent create(@BindsInstance HostServices hostServices,
                           @BindsInstance @NamedPrimaryStage Stage primaryStage);
    }

    ApplicationRouter getApplicationRouter();

    GetAvatarUseCase getGetAvatarUseCase();

    FxUncaughtExceptionHandler getFxUncaughtExceptionHandler();
}
