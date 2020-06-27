package it.niedermann.nextcloud.deck.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.io.Serializable;
import java.util.Date;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;
import it.niedermann.nextcloud.deck.util.ColorUtil;

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
public class Board extends AbstractRemoteEntity implements Serializable {

    public Board() {

    }

    @Ignore
    public Board(String title, String color) {
        this.title = title;
        setColor(color);
    }

    private String title;
    private long ownerId;
    /**
     * Deck App sends color strings without leading # character
     */
    private String color;
    private boolean archived;
    private int shared;
    private Date deletedAt;
    private boolean permissionRead = false;
    private boolean permissionEdit = false;
    private boolean permissionManage = false;
    private boolean permissionShare = false;


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
        try {
            // Nextcloud might return color format #000 which cannot be parsed by Color.parseColor()
            // https://github.com/stefan-niedermann/nextcloud-deck/issues/466
            this.color = ColorUtil.formatColorToParsableHexString(color).substring(1);
        } catch (Exception e) {
            DeckLog.logError(e);
            this.color = "757575";
        }
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

    public boolean isPermissionRead() {
        return permissionRead;
    }

    public void setPermissionRead(boolean permissionRead) {
        this.permissionRead = permissionRead;
    }

    public boolean isPermissionEdit() {
        return permissionEdit;
    }

    public void setPermissionEdit(boolean permissionEdit) {
        this.permissionEdit = permissionEdit;
    }

    public boolean isPermissionManage() {
        return permissionManage;
    }

    public void setPermissionManage(boolean permissionManage) {
        this.permissionManage = permissionManage;
    }

    public boolean isPermissionShare() {
        return permissionShare;
    }

    public void setPermissionShare(boolean permissionShare) {
        this.permissionShare = permissionShare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Board board = (Board) o;

        if (ownerId != board.ownerId) return false;
        if (archived != board.archived) return false;
        if (shared != board.shared) return false;
        if (permissionRead != board.permissionRead) return false;
        if (permissionEdit != board.permissionEdit) return false;
        if (permissionManage != board.permissionManage) return false;
        if (permissionShare != board.permissionShare) return false;
        if (title != null ? !title.equals(board.title) : board.title != null) return false;
        if (color != null ? !color.equals(board.color) : board.color != null) return false;
        return deletedAt != null ? deletedAt.equals(board.deletedAt) : board.deletedAt == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (int) (ownerId ^ (ownerId >>> 32));
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (archived ? 1 : 0);
        result = 31 * result + shared;
        result = 31 * result + (deletedAt != null ? deletedAt.hashCode() : 0);
        result = 31 * result + (permissionRead ? 1 : 0);
        result = 31 * result + (permissionEdit ? 1 : 0);
        result = 31 * result + (permissionManage ? 1 : 0);
        result = 31 * result + (permissionShare ? 1 : 0);
        return result;
    }
}
