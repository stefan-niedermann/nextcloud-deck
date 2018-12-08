package it.niedermann.nextcloud.deck.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class JoinBoardWithUser {
    @Id
    private Long id;
    private Long userId;
    private Long boardId;

    @Generated(hash = 2044154780)
    public JoinBoardWithUser(Long id, Long userId, Long boardId) {
        this.id = id;
        this.userId = userId;
        this.boardId = boardId;
    }

    @Generated(hash = 1673962166)
    public JoinBoardWithUser() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }
}
