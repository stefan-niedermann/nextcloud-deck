package it.niedermann.nextcloud.deck.data.repository;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.repository.ColumnRepository;

public class ColumnRepositoryImpl implements ColumnRepository {

    @Override
    public CompletableFuture<Void> createColumn(Column column) {
        System.out.println("Successfully added column: " + column);
        return CompletableFuture.completedFuture(null);
    }
}