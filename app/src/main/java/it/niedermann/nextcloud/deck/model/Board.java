package it.niedermann.nextcloud.deck.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

import java.util.Date;

import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;

@Entity(
        inheritSuperIndices = true,
        indices = {@Index("ownerId")},
        foreignKeys = {@ForeignKey(entity = User.class, parentColumns = "localId", childColumns = "ownerId")}
)
public class Board extends AbstractRemoteEntity {

    private String title;
    long ownerId;
    //    @ToOne(joinProperty = "ownerId")
//    private User owner;
    private String color;
    private boolean archived;
    //    @ToMany
//    @JoinEntity(entity = JoinBoardWithLabel.class, sourceProperty = "boardId", targetProperty = "labelId")
//    private List<Label> labels = new ArrayList<>();
    private String acl;
    //    @ToMany
//    @JoinEntity(entity = JoinBoardWithPermission.class, sourceProperty = "boardId", targetProperty = "permissionId")
//    private List<Permission> permissions = new ArrayList<>();
//    @ToMany
//    @JoinEntity(entity = JoinBoardWithUser.class, sourceProperty = "boardId", targetProperty = "userId")
//    private List<User> users = new ArrayList<>();
    private int shared;
    private Date deletedAt;


    @Override
    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public Date getLastModifiedLocal() {
        return lastModifiedLocal;
    }

    @Override
    public void setLastModifiedLocal(Date lastModifiedLocal) {
        this.lastModifiedLocal = lastModifiedLocal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isArchived() {
        return archived;
    }

    public String getAcl() {
        return acl;
    }

    public void setAcl(String acl) {
        this.acl = acl;
    }

    public int getShared() {
        return shared;
    }

    public void setShared(int shared) {
        this.shared = shared;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DBStatus getStatusEnum() {
        return DBStatus.findById(status);
    }

    public void setStatusEnum(DBStatus status) {
        this.status = status.getId();
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public long getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
