package it.niedermann.nextcloud.deck.javafx.ui.controller.stages;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.javafx.services.stage.StageRouter;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;
import jakarta.inject.Inject;

public class EditCardStageController {

    private final StageRouter stageRouter;

    @Inject
    public EditCardStageController(StageRouter stageRouter) {
        this.stageRouter = stageRouter;
    }

    public CompletableFuture<Void> initialize() {
        return this.stageRouter.navigateTo(SplashScreenScene.class);
    }
}
