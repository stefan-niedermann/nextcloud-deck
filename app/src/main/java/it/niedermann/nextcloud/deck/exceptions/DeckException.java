package it.niedermann.nextcloud.deck.exceptions;

public class DeckException extends IllegalArgumentException {

    public enum Hint {
        CAPABILITIES_NOT_PARSABLE,
        CAPABILITIES_VERSION_NOT_PARSABLE,
        UNKNOWN_ACCOUNT_USER_ID,
        DEPENDENCY_NOT_SYNCED_YET
    }

    private Hint hint;

    public DeckException(Hint hint, String message) {
        super(message);
        this.hint = hint;
    }

    public Hint getHint() {
        return hint;
    }
}
