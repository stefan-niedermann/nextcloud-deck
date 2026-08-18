package it.niedermann.nextcloud.deck.javafx.ui.shared;

import java.util.concurrent.CompletableFuture;

public interface SavePromptGuarded {
    CompletableFuture<Boolean> canDeactivate();
    CompletableFuture<Boolean> save();
    CompletableFuture<Void> dismiss();
}
