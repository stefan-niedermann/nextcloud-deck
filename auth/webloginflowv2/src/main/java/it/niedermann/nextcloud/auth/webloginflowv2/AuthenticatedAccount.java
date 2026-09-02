package it.niedermann.nextcloud.auth.webloginflowv2;

import java.net.URL;

public record AuthenticatedAccount(URL url, String username, String token) {
}
