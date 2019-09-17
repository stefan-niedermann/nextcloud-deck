package it.niedermann.nextcloud.deck.model.enums;

public enum ActivityType {
    DECK (1, "deck-dark.svg"),
    CHANGE (2, "change.svg"),
    ADD (3, "add-color.svg"),
    DELETE (4, "delete-color.svg"),
    ARCHIVE (5, "archive.svg"),
    HISTORY (6, "actions/history.svg"),
    FILES (7, "places/files.svg"),
    COMMENT (8, "actions/comment.svg")
    ;

    int id;
    String name;
    ActivityType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ActivityType findById(int id) {
        for (ActivityType s : ActivityType.values()) {
            if (s.getId() == id) {
                return s;
            }
        }
        throw new IllegalArgumentException("unknown ActivityType key");
    }

    public static ActivityType findByPath(String path) {
        if (path == null) {
            throw new IllegalArgumentException("path is null");
        }
        for (ActivityType s : ActivityType.values()) {
            if (path.trim().endsWith(path)) {
                return s;
            }
        }
        throw new IllegalArgumentException("unknown ActivityType key");
    }
}
