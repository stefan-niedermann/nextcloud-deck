package it.niedermann.nextcloud.deck.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

@Entity(
        primaryKeys = {"labelId", "boardId"},
        foreignKeys = {
                @ForeignKey(entity = Board.class,
                        parentColumns = "localId",
                        childColumns = "boardId"),
                @ForeignKey(entity = Label.class,
                        parentColumns = "localId",
                        childColumns = "labelId")
        })
public class JoinBoardWithLabel {
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
}
