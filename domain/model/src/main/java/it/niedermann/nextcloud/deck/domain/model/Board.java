package it.niedermann.nextcloud.deck.domain.model;

import java.awt.Color;
import java.util.List;

public record Board(long id,
                    String title,
                    Color color,
                    List<Column> columns) {
}
