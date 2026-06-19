package it.niedermann.nextcloud.deck.model.enums;

public enum ESortCriteria {
    /**
     * Account → Board → Stack
     */
    LOCATION(1),
    /**
     * Modification date of the card including comments, attachments etc.
     */
    MODIFIED(2),
    LAST_COMMENTED(3),
    DUE_DATE(4),
    ASSIGNEE(5),
    LABEL(6);

    private final int id;

    ESortCriteria(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ESortCriteria findById(int id) {
        for (ESortCriteria s : ESortCriteria.values()) {
            if (s.getId() == id) {
                return s;
            }
        }
        throw new IllegalArgumentException("unknown " + ESortCriteria.class.getSimpleName() + " key: " + id);
    }
}
