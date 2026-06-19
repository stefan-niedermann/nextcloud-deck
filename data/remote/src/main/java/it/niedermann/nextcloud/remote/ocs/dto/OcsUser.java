package it.niedermann.nextcloud.remote.ocs.dto;

import com.google.gson.annotations.SerializedName;

/**
 * <p>This is a basic implementation for the <code>users</code> endpoint which maps often required properties.<br>
 * You can use it directly in combination with {@link OcsResponse} or extend it.</p>
 * <p>Example usage with Retrofit:</p>
 * <pre>
 * {@code
 * @GET("/ocs/v2.php/cloud/users/{search}?format=json")
 * Call<OcsResponse < OcsUser>> getUser(@Path("search") String userId);
 * }
 * </pre>
 * @see <a href="https://docs.nextcloud.com/server/latest/developer_manual/client_apis/OCS/ocs-api-overview.html#user-metadata">User API</a>
 */
@SuppressWarnings("SpellCheckingInspection")
public record OcsUser(
    boolean enabled,
    @SerializedName("id")
    String userId,
    long lastLogin,
    OcsQuota quota,
    String email,
    @SerializedName("displayname")
    String displayName,
    String phone,
    String address,
    String website,
    String twitter,
    String[] groups,
    String language,
    String locale
) {
    public record OcsQuota(
        long free,
        long used,
        long total
    ) {
    }
}
