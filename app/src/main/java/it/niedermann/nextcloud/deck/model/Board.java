package it.niedermann.nextcloud.deck.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Date;

import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;

@Entity(
        inheritSuperIndices = true,
        indices = {@Index("ownerId")},
        foreignKeys = {
            @ForeignKey(
                entity = User.class,
                parentColumns = "localId",
                childColumns = "ownerId", onDelete = ForeignKey.SET_NULL
            )
        }
)
public class Board extends AbstractRemoteEntity {

    private String title;
    private long ownerId;
    private String color;
    private boolean archived;
    // TODO: seems to be something like shares to other users. how to handle this stuff??
//    private String acl;
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
//
//    public String getAcl() {
//        return acl;
//    }
//
//    public void setAcl(String acl) {
//        this.acl = acl;
//    }

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

    @Override
    public String toString() {
        return "Board{" +
                "title='" + title + '\'' +
                ", ownerId=" + ownerId +
                ", color='" + color + '\'' +
                ", archived=" + archived +
                ", shared=" + shared +
                ", deletedAt=" + deletedAt +
                ", localId=" + localId +
                ", accountId=" + accountId +
                ", id=" + id +
                ", status=" + status +
                ", lastModified=" + lastModified +
                ", lastModifiedLocal=" + lastModifiedLocal +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;

        if (ownerId != board.ownerId) return false;
        if (archived != board.archived) return false;
        if (shared != board.shared) return false;
        if (title != null ? !title.equals(board.title) : board.title != null) return false;
        if (color != null ? !color.equals(board.color) : board.color != null) return false;
        return deletedAt != null ? deletedAt.equals(board.deletedAt) : board.deletedAt == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (int) (ownerId ^ (ownerId >>> 32));
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (archived ? 1 : 0);
        result = 31 * result + shared;
        result = 31 * result + (deletedAt != null ? deletedAt.hashCode() : 0);
        return result;
    }
}
