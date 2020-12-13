package it.niedermann.nextcloud.deck.exceptions;

public class OfflineException extends IllegalStateException {
    public OfflineException() {
        super("Device is currently offline.");
    }
    public OfflineException(String message) {
        super(message);
    }
}
