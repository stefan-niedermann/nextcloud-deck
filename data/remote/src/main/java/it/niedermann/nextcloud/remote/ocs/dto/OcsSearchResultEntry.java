package it.niedermann.nextcloud.remote.ocs.dto;

import java.io.Serializable;
import java.util.Map;

/// @see <a href="https://github.com/nextcloud/server/blob/c54038cae610a73c9c2165e6d24d84d7c2bb076b/lib/public/Search/SearchResultEntry.php">Source</a>
public record OcsSearchResultEntry(
        /// @since 20.0.0
        String thumbnailUrl,
        /// @since 20.0.0
        String title,
        /// @since 20.0.0
        String subline,
        /// @since 20.0.0
        String resourceUrl,
        /// @since 20.0.0
        String icon,
        /// @since 20.0.0
        boolean rounded,
        ///@since 20.0.0
        Map<String, String> attributes
) implements Serializable {
}
