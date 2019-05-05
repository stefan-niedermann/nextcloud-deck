package it.niedermann.nextcloud.deck.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;

@Entity(inheritSuperIndices = true,
    indices = {@Index(value = "accountId", name = "acl_acc")},
    foreignKeys = {
        @ForeignKey(
            entity = Board.class,
            parentColumns = "localId",
            childColumns = "boardId", onDelete = ForeignKey.CASCADE
        )
    }
)
public class AccessControl extends AbstractRemoteEntity {

    private Long type;
    private Long boardId;
    private boolean owner;
    private boolean permissionEdit;
    private boolean permissionShare;
    private boolean permissionManage;

    private Long userId;
    @Ignore
    private User user;

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public boolean isPermissionEdit() {
        return permissionEdit;
    }

    public void setPermissionEdit(boolean permissionEdit) {
        this.permissionEdit = permissionEdit;
    }

    public boolean isPermissionShare() {
        return permissionShare;
    }

    public void setPermissionShare(boolean permissionShare) {
        this.permissionShare = permissionShare;
    }

    public boolean isPermissionManage() {
        return permissionManage;
    }

    public void setPermissionManage(boolean permissionManage) {
        this.permissionManage = permissionManage;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
