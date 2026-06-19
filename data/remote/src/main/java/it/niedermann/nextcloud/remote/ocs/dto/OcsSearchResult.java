package it.niedermann.nextcloud.remote.ocs.dto;

import java.io.Serializable;
import java.util.List;

public record OcsSearchResult(
        String name,
        boolean isPaginated,
        List<OcsSearchResultEntry> entries,
        Integer cursor
) implements Serializable {
}
