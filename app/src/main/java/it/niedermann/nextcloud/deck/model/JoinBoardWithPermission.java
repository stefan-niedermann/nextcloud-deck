package it.niedermann.nextcloud.deck.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

@Entity(
        primaryKeys = { "permissionId", "boardId" },
        foreignKeys = {
                @ForeignKey(entity = Board.class,
                        parentColumns = "id",
                        childColumns = "boardId"),
                @ForeignKey(entity = Permission.class,
                        parentColumns = "id",
                        childColumns = "permissionId")
        })
public class JoinBoardWithPermission {
    private Long permissionId;
    private Long boardId;

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }
}
