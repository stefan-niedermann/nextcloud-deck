package it.niedermann.nextcloud.deck.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;

@Entity(
        primaryKeys = {"userId", "cardId"},
        indices = {@Index("cardId"), @Index("userId")},
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "localId",
                        childColumns = "userId"),
                @ForeignKey(entity = Card.class,
                        parentColumns = "localId",
                        childColumns = "cardId")
        })
public class JoinCardWithUser {
    @NonNull
    private Long userId;
    @NonNull
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JoinCardWithUser that = (JoinCardWithUser) o;

        if (!userId.equals(that.userId)) return false;
        return cardId.equals(that.cardId);
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + cardId.hashCode();
        return result;
    }
}
