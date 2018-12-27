package it.niedermann.nextcloud.deck.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import java.util.Date;

import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;

@Entity(inheritSuperIndices = true, indices = {@Index("boardId")})
public class Stack extends AbstractRemoteEntity {
    private String title;

    @NonNull
    private long boardId;

    private Date deletedAt;

    @NonNull
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

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
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
}
