package it.niedermann.nextcloud.deck.model.ocs.user;

public class UserForAssignment {
    // 0 for user, 1 for group
    private int type;
    // UUID from user-Table
    private String userId;

    public UserForAssignment(int type, String userId) {
        this.type = type;
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
