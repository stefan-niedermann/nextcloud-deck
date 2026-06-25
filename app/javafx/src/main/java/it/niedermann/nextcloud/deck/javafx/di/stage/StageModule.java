package it.niedermann.nextcloud.deck.javafx.di.stage;

import java.util.Map;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.deck.javafx.services.stage.StageRouter;
import it.niedermann.nextcloud.deck.javafx.ui.controller.ControllerFactory;
import it.niedermann.nextcloud.deck.javafx.ui.stages.MainStage;
import jakarta.inject.Provider;

@Module(includes = {
        ControllerModule.class,
})
public class StageModule {

    @Provides
    @StageScope
    ControllerFactory provideControllerFactory(Map<Class<?>, Provider<Object>> controllerProviderMap) {
        return new ControllerFactory(controllerProviderMap);
    }

    // region Stages

    @Provides
    @StageScope
    MainStage provideMainStage(StageRouter stageRouter) {
        return new MainStage(stageRouter);
    }

    // endregion
}
