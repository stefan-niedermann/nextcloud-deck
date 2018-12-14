package it.niedermann.nextcloud.deck.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

@Entity(
        primaryKeys = { "userId", "boardId" },
        foreignKeys = {
                @ForeignKey(entity = Board.class,
                        parentColumns = "localId",
                        childColumns = "boardId"),
                @ForeignKey(entity = User.class,
                        parentColumns = "localId",
                        childColumns = "userId")
        })
public class JoinBoardWithUser {
    private Long userId;
    private Long boardId;

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
