package it.niedermann.nextcloud.deck.domain.model;

import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public record Board(Board.ID id,
                    String title,
                    Color color,
                    List<Column> columns,
                    Set<Label> labels) {

    public Board {
        for (final var o : new Object[]{
                id,
                title,
                color,
                columns,
                labels,
        }) {
            Objects.requireNonNull(o);
        }
    }

    public record ID(long value) {
    }
}
