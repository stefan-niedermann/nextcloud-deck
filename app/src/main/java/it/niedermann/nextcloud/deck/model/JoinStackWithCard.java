package it.niedermann.nextcloud.deck.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

@Entity(
        primaryKeys = { "stackId", "cardId" },
        foreignKeys = {
                @ForeignKey(entity = Stack.class,
                        parentColumns = "localId",
                        childColumns = "stackId"),
                @ForeignKey(entity = Card.class,
                        parentColumns = "localId",
                        childColumns = "cardId")
        })
public class JoinStackWithCard {
    private Long stackId;
    private Long cardId;

    public Long getStackId() {
        return stackId;
    }

    public void setStackId(Long stackId) {
        this.stackId = stackId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }
}
