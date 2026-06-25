package it.niedermann.nextcloud.deck.javafx.ui.stages;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.javafx.services.stage.StageRouter;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;
import jakarta.inject.Inject;

public class EditCardStage {

    private final StageRouter stageRouter;

    @Inject
    public EditCardStage(StageRouter stageRouter) {
        this.stageRouter = stageRouter;
    }

    public CompletableFuture<Void> initialize() {
        return this.stageRouter.navigateTo(SplashScreenScene.class);
    }
}
