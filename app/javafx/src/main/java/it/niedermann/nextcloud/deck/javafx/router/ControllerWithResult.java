package it.niedermann.nextcloud.deck.javafx.router;

import java.util.concurrent.CompletableFuture;

public interface ControllerWithResult<ResultType> {

    default CompletableFuture<ResultType> getResult() {
        throw new UnsupportedOperationException("Override getResult() in your controller to retrieve a value from it.");
    }
}
