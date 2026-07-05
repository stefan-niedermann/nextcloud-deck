package it.niedermann.nextcloud.deck.javafx.ui.controller;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.javafx.services.stage.StageContext;
import it.niedermann.nextcloud.deck.javafx.services.stage.StageRouter;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.MainScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;

public abstract class StageController<TArgs> {

    private static final Logger logger = Logger.getLogger(StageController.class.getName());

    protected final StageContext context;
    protected final StageRouter stageRouter;

    public StageController(StageContext context, StageRouter stageRouter) {
        this.context = context;
        this.stageRouter = stageRouter;
    }

    public CompletableFuture<Void> initialize(TArgs args) {
        return this.stageRouter.navigateTo(SplashScreenScene.class)
                .thenComposeAsync(_ -> deriveInitialState(args))
                .thenAcceptAsync(initialState -> context.dispatch(new StageContext.Action.Initialize(initialState)))
                .thenComposeAsync(_ -> this.stageRouter.navigateTo(MainScene.class))
                .exceptionallyCompose(exception -> this.stageRouter.navigateTo(ExceptionScene.class))
                .thenRunAsync(() -> logger.info("Initialized stage with " + args));
    }

    abstract protected CompletableFuture<StageContext.State> deriveInitialState(TArgs args);
}
