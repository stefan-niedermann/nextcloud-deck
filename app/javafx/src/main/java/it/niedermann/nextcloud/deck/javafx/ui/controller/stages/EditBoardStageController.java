package it.niedermann.nextcloud.deck.javafx.ui.controller.stages;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.javafx.services.stage.StageRouter;
import jakarta.inject.Inject;

public class EditBoardStageController {

    private final StageRouter stageRouter;

    @Inject
    public EditBoardStageController(StageRouter stageRouter) {
        this.stageRouter = stageRouter;
    }

    public CompletableFuture<Void> initialize() {
        return CompletableFuture.failedFuture(new UnsupportedOperationException("Not yet implemented"));
    }
}
