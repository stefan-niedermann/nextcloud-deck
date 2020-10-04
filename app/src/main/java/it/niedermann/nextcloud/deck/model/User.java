package it.niedermann.nextcloud.deck.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import java.io.Serializable;

import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;

@Entity(inheritSuperIndices = true, indices = {@Index(value = "uid", name = "user_uid")})
public class User extends AbstractRemoteEntity implements Serializable {


    private String primaryKey;
    private String uid;
    private String displayname;

    public User() {
        super();
    }

    @Ignore
    public User(String primaryKey, String uid, String displayname) {
        this.primaryKey = primaryKey;
        this.uid = uid;
        this.displayname = displayname;
    }

    public User(User user) {
        super(user);
        this.primaryKey = user.getPrimaryKey();
        this.uid = user.getUid();
        this.displayname = user.getDisplayname();
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (primaryKey != null ? !primaryKey.equals(user.primaryKey) : user.primaryKey != null)
            return false;
        if (uid != null ? !uid.equals(user.uid) : user.uid != null) return false;
        return displayname != null ? displayname.equals(user.displayname) : user.displayname == null;
    }

    @Override
    public int hashCode() {
        int result = primaryKey != null ? primaryKey.hashCode() : 0;
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        result = 31 * result + (displayname != null ? displayname.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "primaryKey='" + primaryKey + '\'' +
                ", uid='" + uid + '\'' +
                ", displayname='" + displayname + '\'' +
                ", localId=" + localId +
                ", accountId=" + accountId +
                ", id=" + id +
                ", status=" + status +
                ", lastModified=" + lastModified +
                ", lastModifiedLocal=" + lastModifiedLocal +
                "} " + super.toString();
    }
}
