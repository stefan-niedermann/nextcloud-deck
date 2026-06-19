/*
 * Nextcloud Android SingleSignOn Library
 *
 * SPDX-FileCopyrightText: 2021-2024 Nextcloud GmbH and Nextcloud contributors
 * SPDX-FileCopyrightText: 2021 Stefan Niedermann <info@niedermann.it>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.nextcloud.remote.ocs.dto;

import com.google.gson.annotations.SerializedName;

/**
 * <p>This is a basic implementation for the <code>capabilities</code> endpoint which maps version and theming properties.<br>
 * You can use it directly in combination with {@link OcsResponse} or extend it.</p>
 * <p>Example usage with Retrofit:</p>
 * <pre>
 * {@code
 * @GET("/ocs/v2.php/cloud/capabilities?format=json")
 * Call<OcsResponse < OcsCapabilities>> getCapabilities();
 * }
 * </pre>
 *
 * @see <a href="https://docs.nextcloud.com/server/latest/developer_manual/client_apis/OCS/ocs-api-overview.html#capabilities-api">Capabilities API</a>
 */
@SuppressWarnings("unused, SpellCheckingInspection")
public record OcsCapabilitiesResponse(
    OcsVersion version,
    OcsCapabilities capabilities
) {
    public record OcsVersion(
        int major,
        int minor,
        int macro,
        String string,
        String edition,
        boolean extendedSupport
    ) {
    }

    public record OcsCapabilities(
        OcsTheming theming
    ) {
        public record OcsTheming(
            String name,
            String url,
            String slogan,
            String color,
            @SerializedName("color-text")
            String colorText,
            @SerializedName("color-element")
            String colorElement,
            @SerializedName("color-element-bright")
            String colorElementBright,
            @SerializedName("color-element-dark")
            String colorElementDark,
            String logo,
            String background,
            @SerializedName("background-plain")
            boolean backgroundPlain,
            @SerializedName("background-default")
            boolean backgroundDefault,
            @SerializedName("logoheader")
            String logoHeader,
            String favicon
        ) {
        }
    }
}
