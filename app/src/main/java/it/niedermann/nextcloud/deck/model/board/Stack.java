package it.niedermann.nextcloud.deck.model.board;

import java.io.Serializable;
import java.time.LocalDate;

import it.niedermann.nextcloud.deck.model.DBStatus;
import it.niedermann.nextcloud.deck.model.RemoteEntity;

public class Stack extends RemoteEntity {
    private String title;
    private long boardId;
    private LocalDate deletedAt;
    private int order;
    private DBStatus status = DBStatus.UP_TO_DATE;

    public Stack() {
        super();
    }

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

    public LocalDate getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDate deletedAt) {
        this.deletedAt = deletedAt;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public DBStatus getStatus() {
        return status;
    }

    public void setStatus(DBStatus status) {
        this.status = status;
    }
}
