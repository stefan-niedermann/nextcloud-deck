package it.niedermann.nextcloud.deck.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(
        primaryKeys = {"stackId", "cardId"},
        indices = {@Index("cardId"), @Index("stackId")},
        foreignKeys = {
                @ForeignKey(entity = Stack.class,
                        parentColumns = "localId",
                        childColumns = "stackId"),
                @ForeignKey(entity = Card.class,
                        parentColumns = "localId",
                        childColumns = "cardId")
        })
public class JoinStackWithCard {
    @NonNull
    private Long stackId;
    @NonNull
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
