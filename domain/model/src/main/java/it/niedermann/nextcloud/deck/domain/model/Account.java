package it.niedermann.nextcloud.deck.domain.model;

import java.net.URL;

public record Account(long id,
                      URL url,
                      String username,
                      String token,
                      String accountName) {

    public long id() {
        return id;
    }

    public URL url() {
        return url;
    }

    public String username() {
        return username;
    }

    public String token() {
        return token;
    }

    public String accountName() {
        return accountName;
    }

}