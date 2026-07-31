package it.niedermann.nextcloud.deck.javafx.services.application;

public enum Theme {
    AUTO,
    LIGHT,
    DARK;

    public static Theme fromName(String name) {
        if (name == null) {
            return AUTO;
        }
        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {
            return AUTO;
        }
    }
}
