package it.niedermann.nextcloud.deck.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import it.niedermann.nextcloud.deck.model.enums.PermissionType;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Permission {
    @Id
    long id;

    @Generated(hash = 1746588406)
    public Permission(long id) {
        this.id = id;
    }

    @Generated(hash = 600656733)
    public Permission() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PermissionType getType(){
        return PermissionType.findById(id);
    }
    public void setType(PermissionType type){
        this.id = type.getId();
    }
}
