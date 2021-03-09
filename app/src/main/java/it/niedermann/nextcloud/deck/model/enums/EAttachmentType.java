package it.niedermann.nextcloud.deck.model.enums;

public enum EAttachmentType {
    // Do not change values. They match the Deck server apps values.
    DECK_FILE(1, "deck_file"),
    FILE(2, "file");

    private final int id;
    private final String value;

    EAttachmentType(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public static EAttachmentType findByValue(String value) {
        for (EAttachmentType s : EAttachmentType.values()) {
            if (s.value.equals(value)) {
                return s;
            }
        }
        throw new IllegalArgumentException("unknown " + EAttachmentType.class.getSimpleName() + " value: " + value);
    }

    public String getValue() {
        return value;
    }
}
