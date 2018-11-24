package it.niedermann.nextcloud.deck.model.join.board;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class JoinBoardWithPermission {
    @Id
    private Long id;
    private Long permissionId;
    private Long boardId;

    @Generated(hash = 370286870)
    public JoinBoardWithPermission(Long id, Long permissionId, Long boardId) {
        this.id = id;
        this.permissionId = permissionId;
        this.boardId = boardId;
    }

    @Generated(hash = 103844199)
    public JoinBoardWithPermission() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
