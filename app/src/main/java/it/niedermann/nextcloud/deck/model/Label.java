package it.niedermann.nextcloud.deck.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.io.Serializable;

import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;

@Entity(inheritSuperIndices = true,
        indices = {@Index("boardId"), @Index(value = {"boardId", "title"}, unique = true, name = "idx_label_title_unique")},
        foreignKeys = {
        @ForeignKey(
            entity = Board.class,
            parentColumns = "localId",
            childColumns = "boardId",
            onDelete = ForeignKey.CASCADE
        )
    }
)
public class Label extends AbstractRemoteEntity implements Serializable {
    private String title;
    private String color;
    private long boardId;

    public Label() {
    }

    public Label(Label labelToCopy) {
        super(labelToCopy);
        this.title = labelToCopy.getTitle();
        this.color = labelToCopy.getColor();
        this.boardId = labelToCopy.getBoardId();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public long getBoardId() {
        return boardId;
    }

    public void setBoardId(long boardId) {
        this.boardId = boardId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Label label = (Label) o;

        if (boardId != label.boardId) return false;
        if (title != null ? !title.equals(label.title) : label.title != null) return false;
        return color != null ? color.equals(label.color) : label.color == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (int) (boardId ^ (boardId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Label{" +
                "title='" + title + '\'' +
                ", color='" + color + '\'' +
                ", boardId=" + boardId +
                ", localId=" + localId +
                ", accountId=" + accountId +
                ", id=" + id +
                ", status=" + status +
                ", lastModified=" + lastModified +
                ", lastModifiedLocal=" + lastModifiedLocal +
                '}';
    }
}
