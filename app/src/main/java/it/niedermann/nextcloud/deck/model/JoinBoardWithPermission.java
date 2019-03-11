package it.niedermann.nextcloud.deck.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;

@Entity(
        primaryKeys = {"permissionId", "boardId"},
        indices = {@Index("boardId"), @Index("permissionId")},
        foreignKeys = {
                @ForeignKey(entity = Board.class,
                        parentColumns = "localId",
                        childColumns = "boardId"),
                @ForeignKey(entity = Permission.class,
                        parentColumns = "id",
                        childColumns = "permissionId")
        })
public class JoinBoardWithPermission {
    @NonNull
    private Long permissionId;
    @NonNull
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JoinBoardWithPermission that = (JoinBoardWithPermission) o;

        if (!permissionId.equals(that.permissionId)) return false;
        return boardId.equals(that.boardId);
    }

    @Override
    public int hashCode() {
        int result = permissionId.hashCode();
        result = 31 * result + boardId.hashCode();
        return result;
    }
}
