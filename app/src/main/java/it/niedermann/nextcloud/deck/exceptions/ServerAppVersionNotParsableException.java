package it.niedermann.nextcloud.deck.exceptions;

public class ServerAppVersionNotParsableException extends IllegalArgumentException {

    public enum Hint {
        CAPABILITIES_NOT_PARSABLE,
        CAPABILITIES_VERSION_NOT_PARSABLE,
    }

    private Hint hint;

    public ServerAppVersionNotParsableException(Hint hint, String message) {
        super(message);
        this.hint = hint;
    }

    public Hint getHint() {
        return hint;
    }
}
