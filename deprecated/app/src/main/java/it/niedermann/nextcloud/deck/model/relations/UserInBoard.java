package it.niedermann.nextcloud.deck.model.relations;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.User;

@Entity(
        primaryKeys = {"userId", "boardId"},
        indices = {@Index("userId"), @Index("boardId"), @Index(name = "unique_idx_user_board", value = {"userId","boardId"}, unique = true)},
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "localId",
                        childColumns = "userId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Board.class,
                        parentColumns = "localId",
                        childColumns = "boardId", onDelete = ForeignKey.CASCADE)
        })
public class UserInBoard {
    @NonNull
    private Long userId;
    @NonNull
    private Long boardId;

    @NonNull
    public Long getUserId() {
        return userId;
    }

    public void setUserId(@NonNull Long userId) {
        this.userId = userId;
    }

    @NonNull
    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(@NonNull Long boardId) {
        this.boardId = boardId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserInBoard that = (UserInBoard) o;

        if (!userId.equals(that.userId)) return false;
        return boardId.equals(that.boardId);
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + boardId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UserInGroup{" +
                "userId=" + userId +
                ", boardId=" + boardId +
                '}';
    }
}
