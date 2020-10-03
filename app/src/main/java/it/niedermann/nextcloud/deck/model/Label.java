package it.niedermann.nextcloud.deck.model;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
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

    @NonNull
    @ColumnInfo(defaultValue = "0")
    private Integer color;
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

    @ColorInt
    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
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
        if (!super.equals(o)) return false;

        Label label = (Label) o;

        if (boardId != label.boardId) return false;
        if (title != null ? !title.equals(label.title) : label.title != null) return false;
        return color.equals(label.color);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + color.hashCode();
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
