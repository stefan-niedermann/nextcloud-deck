package it.niedermann.nextcloud.deck.model;

import java.util.Objects;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;

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
    @NonNull
    private int status = DBStatus.UP_TO_DATE.getId();

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JoinCardWithUser that = (JoinCardWithUser) o;
        return status == that.status &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(cardId, that.cardId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, cardId, status);
    }
}
