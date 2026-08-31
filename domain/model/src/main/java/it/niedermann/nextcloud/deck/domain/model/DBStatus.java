package it.niedermann.nextcloud.deck.domain.model;

public enum DBStatus {
    UP_TO_DATE(1),
    LOCAL_EDITED(2),
    LOCAL_DELETED(3),
    LOCAL_MOVED(4),
    LOCAL_EDITED_SILENT(5),
    CONFLICT(6),
    RESOLVED(7);

    private final int id;

    DBStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static DBStatus findById(int id) {
        for (DBStatus s : DBStatus.values()) {
            if (s.getId() == id) {
                return s;
            }
        }
        throw new IllegalArgumentException("unknown DBStatus key: " + id);
    }
}
