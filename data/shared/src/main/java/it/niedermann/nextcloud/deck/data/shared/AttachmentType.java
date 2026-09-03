package it.niedermann.nextcloud.deck.data.shared;

import lombok.Getter;

@Getter
public enum AttachmentType {
    DECK_FILE(1, "deck_file"),
    FILE(2, "file"),
    UNKNOWN(1337, "unknown");

    private final int id;
    private final String value;

    AttachmentType(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public static AttachmentType findByValue(String value) {
        for (AttachmentType type : AttachmentType.values()) {
            if (type.value.equals(value)) return type;
        }
        return UNKNOWN;
    }
}
