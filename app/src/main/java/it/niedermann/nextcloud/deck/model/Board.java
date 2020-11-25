package it.niedermann.nextcloud.deck.model;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import com.google.gson.annotations.JsonAdapter;

import java.io.Serializable;
import java.time.Instant;

import it.niedermann.android.util.ColorUtil;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.json.JsonColorSerializer;
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
public class Board extends AbstractRemoteEntity implements Serializable {

    public Board() {

    }

    @Ignore
    public Board(String title, @ColorInt int color) {
        setTitle(title);
        setColor(color);
    }

    private String title;
    private long ownerId;
    @JsonAdapter(JsonColorSerializer.class)
    private Integer color;
    private boolean archived;
    private int shared;
    private Instant deletedAt;
    private boolean permissionRead = false;
    private boolean permissionEdit = false;
    private boolean permissionManage = false;
    private boolean permissionShare = false;


    @Override
    public Instant getLastModified() {
        return lastModified;
    }

    @Override
    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public Instant getLastModifiedLocal() {
        return lastModifiedLocal;
    }

    @Override
    public void setLastModifiedLocal(Instant lastModifiedLocal) {
        this.lastModifiedLocal = lastModifiedLocal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ColorInt
    public Integer getColor() {
        return color;
    }


    public void setColor(String color) {
        try {
            setColor(Color.parseColor(ColorUtil.INSTANCE.formatColorToParsableHexString(color)));
        } catch (Exception e) {
            DeckLog.logError(e);
            setColor(Color.GRAY);
        }
    }

    public void setColor(@ColorInt Integer color) {
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

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
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
