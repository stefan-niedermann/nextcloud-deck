package it.niedermann.nextcloud.deck.model;

import java.time.LocalDate;
import java.util.List;

public class Stack extends RemoteEntity {
    private String title;
    private long boardId;
    private LocalDate deletedAt;
    private int order;
    private List<Card> cards;

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

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}
