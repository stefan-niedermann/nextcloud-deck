package it.niedermann.nextcloud.deck.database.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import it.niedermann.nextcloud.deck.database.model.interfaces.AbstractJoinEntity;

@Entity(
        primaryKeys = {"labelId", "boardId"},
        indices = {@Index("boardId"), @Index("labelId")},
        foreignKeys = {
                @ForeignKey(entity = Board.class,
                        parentColumns = "localId",
                        childColumns = "boardId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Label.class,
                        parentColumns = "localId",
                        childColumns = "labelId", onDelete = ForeignKey.CASCADE)
        })
public class JoinBoardWithLabel extends AbstractJoinEntity {
    @NonNull
    private Long boardId;
    @NonNull
    private Long labelId;


    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
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

        JoinBoardWithLabel that = (JoinBoardWithLabel) o;

        if (!boardId.equals(that.boardId)) return false;
        return labelId.equals(that.labelId);
    }

    @Override
    public int hashCode() {
        int result = boardId.hashCode();
        result = 31 * result + labelId.hashCode();
        return result;
    }
}
