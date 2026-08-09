package it.niedermann.nextcloud.deck.domain.model;

import java.io.Serializable;

public record OcsSearchProvider(
        String remoteId,
        String appId,
        String name,
        String icon,
        int order,
        boolean inAppSearch
) implements Serializable {
}
