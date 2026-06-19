package it.niedermann.nextcloud.deck.domain.repository;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Column;

public interface ColumnRepository {

    CompletableFuture<Void> createColumn(Column column);
}
