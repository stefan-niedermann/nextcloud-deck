package it.niedermann.nextcloud.deck.exceptions;

public class ServerAppVersionNotParsableException extends IllegalArgumentException {

    public ServerAppVersionNotParsableException(String message) {
        super(message);
    }
}
