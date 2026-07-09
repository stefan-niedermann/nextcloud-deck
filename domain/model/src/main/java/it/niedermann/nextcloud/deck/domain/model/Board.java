package it.niedermann.nextcloud.deck.domain.model;

import java.awt.Color;
import java.util.List;
import java.util.Set;

public record Board(Board.ID id,
                    String title,
                    Color color,
                    List<Column> columns,
                    Set<Label> labels) {

    public record ID(long value) {
    }
}
