package it.niedermann.nextcloud.deck.javafx.ui.stages;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.javafx.services.stage.StageRouter;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.MainScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;
import jakarta.inject.Inject;

public class MainStage {

    private final StageRouter stageRouter;

    @Inject
    public MainStage(StageRouter stageRouter) {
        this.stageRouter = stageRouter;
    }

    public CompletableFuture<Void> initialize() {
        return this.stageRouter.navigateTo(SplashScreenScene.class);
    }

    public CompletableFuture<Void> showLogin() {
        return this.stageRouter.navigateTo(LoginScene.class);
    }

    public CompletableFuture<Void> showMain() {
        return this.stageRouter.navigateTo(MainScene.class);
    }
}
