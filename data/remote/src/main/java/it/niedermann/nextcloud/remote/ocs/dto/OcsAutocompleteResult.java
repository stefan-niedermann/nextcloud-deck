package it.niedermann.nextcloud.remote.ocs.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/// [Documentation](https://docs.nextcloud.com/server/latest/developer_manual/client_apis/OCS/ocs-api-overview.html#auto-complete-and-user-search)
public record OcsAutocompleteResult(
        String id,
        String label,
        String icon,
        OcsAutocompleteSource source,
        String subline,
        String shareWithDisplayNameUnique
) implements Serializable {

    public enum OcsAutocompleteSource {
        @SerializedName(value = "0", alternate = "users")
        USERS(0),
        @SerializedName(value = "1", alternate = "groups")
        GROUPS(1),
        @SerializedName(value = "7", alternate = "teams")
        TEAMS(7),
        ;

        public final int shareType;

        OcsAutocompleteSource(int shareType) {
            this.shareType = shareType;
        }
    }
}
