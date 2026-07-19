package it.niedermann.nextcloud.deck.domain.model;

import java.awt.Color;
import java.util.Objects;

public record Board(Board.ID id,
                    String title,
                    Color color,
                    Permissions permissions) {

    public Board {
        for (final var o : new Object[]{
                id,
                title,
                color,
                permissions,
        }) {
            Objects.requireNonNull(o);
        }
    }

    public record ID(long value) {
    }

    public record Permissions(boolean permissionRead,
                              boolean permissionEdit,
                              boolean permissionManage,
                              boolean permissionShare) {
    }
}
