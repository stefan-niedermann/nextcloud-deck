package it.niedermann.nextcloud.deck.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;
import it.niedermann.nextcloud.deck.model.interfaces.AbstractJoinEntity;

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
public class JoinCardWithLabel extends AbstractJoinEntity {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JoinCardWithLabel that = (JoinCardWithLabel) o;

        if (!labelId.equals(that.labelId)) return false;
        return cardId.equals(that.cardId);
    }

    @Override
    public int hashCode() {
        int result = labelId.hashCode();
        result = 31 * result + cardId.hashCode();
        return result;
    }
}
