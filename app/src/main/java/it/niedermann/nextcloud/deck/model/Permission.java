package it.niedermann.nextcloud.deck.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import it.niedermann.nextcloud.deck.model.enums.PermissionType;

@Entity(inheritSuperIndices = true)
public class Permission {
    @PrimaryKey(autoGenerate = true)
    long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PermissionType getType() {
        return PermissionType.findById(id);
    }

    public void setType(PermissionType type) {
        this.id = type.getId();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Permission that = (Permission) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
