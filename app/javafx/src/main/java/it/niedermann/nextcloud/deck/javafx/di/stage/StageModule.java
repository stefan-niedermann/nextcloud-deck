package it.niedermann.nextcloud.deck.javafx.di.stage;

import dagger.Module;
import dagger.Provides;
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

//    @Provides
//    ControllerFactory provideControllerFactory(Map<Class<?>, Provider<Object>> controllerProviderMap) {
//        return new ControllerFactory(controllerProviderMap);
//    }
}
