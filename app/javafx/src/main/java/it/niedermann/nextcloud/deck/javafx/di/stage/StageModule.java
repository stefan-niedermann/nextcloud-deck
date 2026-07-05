package it.niedermann.nextcloud.deck.javafx.di.stage;

import java.util.Map;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.deck.javafx.services.stage.StageContext;
import it.niedermann.nextcloud.deck.javafx.services.stage.StageRouter;
import it.niedermann.nextcloud.deck.javafx.ui.controller.ControllerFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.stages.MainStageController;
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

    // region Stages

    @Provides
    @StageScope
    MainStageController provideMainStage(StageContext context, StageRouter stageRouter) {
        return new MainStageController(context, stageRouter);
    }

    // endregion
}
