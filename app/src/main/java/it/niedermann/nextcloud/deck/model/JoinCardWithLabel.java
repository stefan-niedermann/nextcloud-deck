package it.niedermann.nextcloud.deck.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

@Entity(
        primaryKeys = { "labelId", "cardId" },
        foreignKeys = {
                @ForeignKey(entity = Label.class,
                        parentColumns = "localId",
                        childColumns = "labelId"),
                @ForeignKey(entity = Card.class,
                        parentColumns = "localId",
                        childColumns = "cardId")
        })
public class JoinCardWithLabel {
    private Long labelId;
    private Long cardId;

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }
}
