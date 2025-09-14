package it.niedermann.nextcloud.deck.database.entity.enums;

public enum PermissionType {

    READ(1, "PERMISSION_READ"),
    EDIT(2, "PERMISSION_EDIT"),
    MANAGE(3, "PERMISSION_MANAGE"),
    SHARE(4, "PERMISSION_SHARE");

    private final long id;
    private final String key;

    PermissionType(long id, String key) {
        this.id = id;
        this.key = key;
    }

    public static PermissionType findByKey(String key) {
        for (PermissionType s : PermissionType.values()) {
            if (s.getKey().equals(key)) {
                return s;
            }
        }
        throw new IllegalArgumentException("unknown Permission key");
    }

    public static PermissionType findById(long key) {
        for (PermissionType s : PermissionType.values()) {
            if (s.getId() == key) {
                return s;
            }
        }
        throw new IllegalArgumentException("unknown Permission ID");
    }

    public String getKey() {
        return key;
    }

    public long getId() {
        return id;
    }
}
