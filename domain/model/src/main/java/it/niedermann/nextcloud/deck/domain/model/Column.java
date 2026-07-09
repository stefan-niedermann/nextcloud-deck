package it.niedermann.nextcloud.deck.domain.model;

public record Column(Column.ID id,
                     String title) {

    public record ID(long value) {
    }
}
