package it.niedermann.nextcloud.deck.domain.model;

import java.io.Serializable;

public record Capabilities(
        Version serverVersion,
        Color themingColor,
        boolean commentsEnabled,
        boolean activityEnabled
) implements Serializable {
}
