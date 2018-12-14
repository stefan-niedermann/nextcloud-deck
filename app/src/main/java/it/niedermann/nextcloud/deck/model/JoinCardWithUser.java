package it.niedermann.nextcloud.deck.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

@Entity(
        primaryKeys = { "userId", "cardId" },
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "localId",
                        childColumns = "userId"),
                @ForeignKey(entity = Card.class,
                        parentColumns = "localId",
                        childColumns = "cardId")
        })
public class JoinCardWithUser {
    private Long userId;
    private Long cardId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }
}
