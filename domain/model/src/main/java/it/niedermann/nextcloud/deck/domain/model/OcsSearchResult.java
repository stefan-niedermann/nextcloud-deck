package it.niedermann.nextcloud.deck.domain.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public record OcsSearchResult(
        String name,
        boolean isPaginated,
        List<Entry> entries,
        Integer cursor
) implements Serializable {
    public record Entry(
            String id,
            String name,
            String subline,
            String icon,
            String link,
            String thumbnail,
            Map<String, String> attributes
    ) implements Serializable {
    }
}
