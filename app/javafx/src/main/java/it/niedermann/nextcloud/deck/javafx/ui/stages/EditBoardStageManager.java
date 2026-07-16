package it.niedermann.nextcloud.deck.javafx.ui.stages;

import java.util.concurrent.CompletableFuture;

import jakarta.inject.Inject;

public class EditBoardStageManager {

    @Inject
    public EditBoardStageManager() {

    }

    public CompletableFuture<Void> initialize() {
        return CompletableFuture.failedFuture(new UnsupportedOperationException("Not yet implemented"));
    }
}
