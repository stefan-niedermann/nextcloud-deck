package it.niedermann.nextcloud.deck.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(
        primaryKeys = {"labelId", "cardId"},
        indices = {@Index("cardId"), @Index("labelId")},
        foreignKeys = {
                @ForeignKey(entity = Label.class,
                        parentColumns = "localId",
                        childColumns = "labelId"),
                @ForeignKey(entity = Card.class,
                        parentColumns = "localId",
                        childColumns = "cardId")
        })
public class JoinCardWithLabel {
    @NonNull
    private Long labelId;
    @NonNull
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
