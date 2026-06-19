package it.niedermann.nextcloud.remote.ocs.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/// @see <a href="https://docs.nextcloud.com/server/latest/developer_manual/digging_deeper/search.html">Source</a>
public record OcsSearchProvider(
        @SerializedName("id")
        String remoteId,
        String appId,
        String name,
        String icon,
        int order,
        boolean inAppSearch
) implements Serializable {
}
