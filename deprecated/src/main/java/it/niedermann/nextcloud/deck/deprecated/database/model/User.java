package it.niedermann.nextcloud.deck.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.io.Serializable;

import it.niedermann.nextcloud.deck.database.entity.interfaces.AbstractRemoteEntity;

@Entity(inheritSuperIndices = true,
        indices = {
                @Index(value = "uid", name = "user_uid")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = Account.class,
                        parentColumns = "id",
                        childColumns = "accountId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class User extends AbstractRemoteEntity implements Serializable {

    public static final long TYPE_USER = 0L;
    public static final long TYPE_GROUP = 1L;

    private String primaryKey;
    private String uid;
    private String displayname;
    private long type;

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
        this.type = user.getType();
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

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;
        return type == user.type && primaryKey.equals(user.primaryKey) &&
                uid.equals(user.uid) && displayname.equals(user.displayname);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + primaryKey.hashCode();
        result = 31 * result + uid.hashCode();
        result = 31 * result + displayname.hashCode();
        result = 31 * result + Long.hashCode(type);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "primaryKey='" + primaryKey + '\'' +
                ", uid='" + uid + '\'' +
                ", displayname='" + displayname + '\'' +
                ", type=" + type +
                ", localId=" + localId +
                ", accountId=" + accountId +
                ", id=" + id +
                ", status=" + status +
                ", lastModified=" + lastModified +
                ", lastModifiedLocal=" + lastModifiedLocal +
                ", etag='" + etag + '\'' +
                "} " + super.toString();
    }
}
