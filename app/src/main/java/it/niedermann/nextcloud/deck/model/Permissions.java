package it.niedermann.nextcloud.deck.model;

public enum Permissions {

    READ("PERMISSION_READ"),
    EDIT("PERMISSION_EDIT"),
    MANAGE("PERMISSION_MANAGE"),
    SHARE("PERMISSION_SHARE");

    private String key;

    Permissions(String key){
        this.key = key;
    }

    public static Permissions findByKey(String key) {
        for (Permissions s : Permissions.values()){
            if (s.getKey().equals(key)) {
                return s;
            }
        }
        throw new IllegalArgumentException("unknown Permission key");
    }

    public String getKey() {
        return key;
    }
}
