package it.niedermann.nextcloud.deck.model.enums;

/**
 * Helps to distinguish between different local change types for Server Synchronization.
 * Created by stefan on 19.09.15.
 */
public enum DBStatus {

    /**
     * VOID means, that the Note was not modified locally
     */
    UP_TO_DATE(1),

    /**
     * LOCAL_EDITED means that a Note was created and/or changed since the last successful synchronization.
     * If it was newly created, then REMOTE_ID is 0
     */
    LOCAL_EDITED(2),

    /**
     * LOCAL_DELETED means that the Note was deleted locally, but this information was not yet synchronized.
     * Therefore, the Note have to be kept locally until the synchronization has succeeded.
     * However, Notes with this status should not be displayed in the UI.
     */
    LOCAL_DELETED(3);

    private final int id;

    public int getId() {
        return id;
    }

    DBStatus(int id) {
        this.id = id;
    }

    /**
     * Parse a String an get the appropriate DBStatus enum element.
     *
     * @param id The String containing the DBStatus identifier. Must not null.
     * @return The DBStatus fitting to the String.
     */
    public static DBStatus findById(int id) {
        for (DBStatus s : DBStatus.values()) {
            if (s.getId() == id) {
                return s;
            }
        }
        throw new IllegalArgumentException("unknown DBStatus key");
    }
}
