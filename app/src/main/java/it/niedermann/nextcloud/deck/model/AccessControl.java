package it.niedermann.nextcloud.deck.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import com.google.gson.annotations.SerializedName;

import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;
import it.niedermann.nextcloud.deck.model.ocs.user.GroupMemberUIDs;

@Entity(inheritSuperIndices = true,
    indices = {
        @Index(value = "accountId", name = "acl_accId"),
        @Index("boardId")
    },
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
    @SerializedName("participant")
    private User user;
    @Ignore
    private GroupMemberUIDs groupMemberUIDs;

    public AccessControl() {
        super();
    }

    public AccessControl(AccessControl accessControl) {
        this.type = accessControl.getType();
        this.boardId = accessControl.getBoardId();
        this.owner = accessControl.isOwner();
        this.permissionEdit = accessControl.isPermissionEdit();
        this.permissionShare = accessControl.isPermissionShare();
        this.permissionManage = accessControl.isPermissionManage();
        this.userId = accessControl.getUserId();
        this.user = accessControl.getUser();
    }

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

    public GroupMemberUIDs getGroupMemberUIDs() {
        return groupMemberUIDs;
    }

    public void setGroupMemberUIDs(GroupMemberUIDs groupMemberUIDs) {
        this.groupMemberUIDs = groupMemberUIDs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AccessControl that = (AccessControl) o;

        if (owner != that.owner) return false;
        if (permissionEdit != that.permissionEdit) return false;
        if (permissionShare != that.permissionShare) return false;
        if (permissionManage != that.permissionManage) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (boardId != null ? !boardId.equals(that.boardId) : that.boardId != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        return user != null ? user.equals(that.user) : that.user == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (boardId != null ? boardId.hashCode() : 0);
        result = 31 * result + (owner ? 1 : 0);
        result = 31 * result + (permissionEdit ? 1 : 0);
        result = 31 * result + (permissionShare ? 1 : 0);
        result = 31 * result + (permissionManage ? 1 : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AccessControl{" +
                "type=" + type +
                ", boardId=" + boardId +
                ", owner=" + owner +
                ", permissionEdit=" + permissionEdit +
                ", permissionShare=" + permissionShare +
                ", permissionManage=" + permissionManage +
                ", userId=" + userId +
                ", user=" + user +
                ", localId=" + localId +
                ", accountId=" + accountId +
                ", id=" + id +
                ", status=" + status +
                ", lastModified=" + lastModified +
                ", lastModifiedLocal=" + lastModifiedLocal +
                "} " + super.toString();
    }
}
