package it.niedermann.nextcloud.deck.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.time.Instant;
import java.util.Objects;

import it.niedermann.nextcloud.deck.database.entity.interfaces.AbstractRemoteEntity;

@Entity(
        inheritSuperIndices = true,
        indices = {@Index("boardId")},
        foreignKeys = {
                @ForeignKey(
                        entity = Board.class,
                        parentColumns = "localId",
                        childColumns = "boardId", onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Account.class,
                        parentColumns = "id",
                        childColumns = "accountId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class Stack extends AbstractRemoteEntity {

    public Stack() {

    }

    @Ignore
    public Stack(String title, long boardId) {
        this.title = title;
        this.boardId = boardId;
    }

    private String title;

    private long boardId;

    private Instant deletedAt;

    private int order;
//
//    @ToMany
//    @JoinEntity(entity = JoinStackWithCard.class, sourceProperty = "stackId", targetProperty = "cardId")
//    private List<Card> cards;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getBoardId() {
        return boardId;
    }

    public void setBoardId(long boardId) {
        this.boardId = boardId;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public long getAccountId() {
        return this.accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stack stack = (Stack) o;

        if (boardId != stack.boardId) return false;
        if (order != stack.order) return false;
        if (!Objects.equals(title, stack.title)) return false;
        return Objects.equals(deletedAt, stack.deletedAt);
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (int) (boardId ^ (boardId >>> 32));
        result = 31 * result + (deletedAt != null ? deletedAt.hashCode() : 0);
        result = 31 * result + order;
        return result;
    }
}
