package it.niedermann.nextcloud.deck.domain.repository;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateColumn;

public interface ColumnRepository {

    CompletableFuture<Void> createColumn(CreateColumn column);

    CompletableFuture<Void> updateColumn(Column column);
}
