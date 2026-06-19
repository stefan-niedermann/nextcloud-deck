package it.niedermann.nextcloud.deck.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import it.niedermann.nextcloud.deck.model.interfaces.AbstractJoinEntity;

@Entity(
        primaryKeys = {"userId", "boardId"},
        indices = {@Index("boardId"), @Index("userId")},
        foreignKeys = {
                @ForeignKey(entity = Board.class,
                        parentColumns = "localId",
                        childColumns = "boardId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                        parentColumns = "localId",
                        childColumns = "userId", onDelete = ForeignKey.CASCADE)
        })
public class JoinBoardWithUser extends AbstractJoinEntity {
    @NonNull
    private Long userId;
    @NonNull
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JoinBoardWithUser that = (JoinBoardWithUser) o;

        if (!userId.equals(that.userId)) return false;
        return boardId.equals(that.boardId);
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + boardId.hashCode();
        return result;
    }
}
