package it.niedermann.nextcloud.deck.database.entity.ocs.user;

import java.util.Objects;

public class OcsUser {
    String id;
    String displayName;

    public OcsUser() {

    }

    public OcsUser(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OcsUser ocsUser = (OcsUser) o;

        if (!Objects.equals(id, ocsUser.id)) return false;
        return Objects.equals(displayName, ocsUser.displayName);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        return result;
    }
}
