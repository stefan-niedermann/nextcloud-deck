package it.niedermann.nextcloud.deck.domain.model;

public enum PreviewMode {
    CROP("crop"),
    FILL("fill");

    private final String value;

    PreviewMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
