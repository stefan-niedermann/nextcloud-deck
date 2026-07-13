package it.niedermann.nextcloud.deck.javafx.di.stage;

import java.util.Map;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.deck.javafx.ui.controller.ControllerFactory;
import jakarta.inject.Provider;
import javafx.stage.Stage;

@Module(includes = {
        ControllerModule.class,
})
public class StageModule {

    @Provides
    @StageScope
    Stage providerStage() {
        return new Stage();
    }

    @Provides
    ControllerFactory provideControllerFactory(Map<Class<?>, Provider<Object>> controllerProviderMap) {
        return new ControllerFactory(controllerProviderMap);
    }
}
