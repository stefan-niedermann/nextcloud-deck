package it.niedermann.nextcloud.deck.model.enums;

/**
 * Helps to distinguish between different local change types for Server Synchronization.
 * Created by stefan on 19.09.15.
 */
public enum DBStatus {

    /**
     * UP_TO_DATE means, that the Entity was not modified locally
     */
    UP_TO_DATE(1),

    /**
     * LOCAL_EDITED means that a Entity was changed since the last successful synchronization.
     * If it was newly created, then REMOTE_ID is 0
     */
    LOCAL_EDITED(2),

    /**
     * LOCAL_DELETED means that the Entity was deleted locally, but this information was not yet synchronized.
     * Therefore, the Entity has to be kept locally until the synchronization has succeeded.
     * However, Entitys with this status should not be displayed in the UI.
     */
    LOCAL_DELETED(3),

    /**
     * LOCAL_MOVED means that the Entity was moved locally, but this information was not yet synchronized.
     * Therefore, the Entity has to be kept locally until the synchronization has succeeded.
     */
    LOCAL_MOVED(4),
    
    /**
     * LOCAL_EDITED_SILENT means the same as LOCAL_EDITED, but doesn't need to be shown as changed in the UI
     */
    LOCAL_EDITED_SILENT(5);

    private final int id;

    public int getId() {
        return id;
    }

    DBStatus(int id) {
        this.id = id;
    }

    public static DBStatus findById(int id) {
        for (DBStatus s : DBStatus.values()) {
            if (s.getId() == id) {
                return s;
            }
        }
        throw new IllegalArgumentException("unknown DBStatus key");
    }
}
