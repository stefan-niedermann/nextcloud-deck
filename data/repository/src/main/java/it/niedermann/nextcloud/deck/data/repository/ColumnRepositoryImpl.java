package it.niedermann.nextcloud.deck.data.repository;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateColumn;
import it.niedermann.nextcloud.deck.domain.repository.ColumnRepository;

public class ColumnRepositoryImpl implements ColumnRepository {

    @Override
    public CompletableFuture<Void> createColumn(CreateColumn column) {
        System.out.println("Successfully added column: " + column);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> updateColumn(Column column) {
        System.out.println("Successfully updated column: " + column);
        return CompletableFuture.completedFuture(null);
    }
}