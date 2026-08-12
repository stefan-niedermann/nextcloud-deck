package it.niedermann.nextcloud.deck.javafx.ui.controller;

import java.util.concurrent.CompletableFuture;

public interface SavePromptGuarded {
    CompletableFuture<Void> canDeactivate();
    CompletableFuture<Boolean> save();
    CompletableFuture<Void> dismiss();
}
