package it.niedermann.nextcloud.auth.webloginflowv2;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public record AuthenticatedAccount(URL url, String username, String token) {
    public AuthenticatedAccount(OcsV2CoreApi.PollResponse response) throws MalformedURLException {
        this(URI.create(response.server()).toURL(), response.loginName(), response.appPassword());
    }
}
