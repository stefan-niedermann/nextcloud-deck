package it.niedermann.nextcloud.deck.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;

@Entity(inheritSuperIndices = true)
public class User extends AbstractRemoteEntity {


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
}
