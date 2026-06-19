package it.niedermann.nextcloud.deck.javafx.router;

import java.util.Optional;

public interface RouteContext {

    Optional<Long> accountId();

    Optional<Long> boardId();

    Optional<Long> cardId();

    RouteContext EMPTY = new RouteContext() {
        @Override
        public Optional<Long> accountId() {
            return Optional.empty();
        }

        @Override
        public Optional<Long> boardId() {
            return Optional.empty();
        }

        @Override
        public Optional<Long> cardId() {
            return Optional.empty();
        }
    };
}
