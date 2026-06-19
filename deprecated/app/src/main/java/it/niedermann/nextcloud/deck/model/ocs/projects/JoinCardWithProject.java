package it.niedermann.nextcloud.deck.model.ocs.projects;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.interfaces.AbstractJoinEntity;

@Entity(
        primaryKeys = {"projectId", "cardId"},
        indices = {@Index("cardId"), @Index("projectId")},
        foreignKeys = {
            @ForeignKey(entity = OcsProject.class,
                parentColumns = "localId",
                childColumns = "projectId",
                onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(entity = Card.class,
                parentColumns = "localId",
                childColumns = "cardId",
                onDelete = ForeignKey.CASCADE
            )
        })
public class JoinCardWithProject extends AbstractJoinEntity {
    @NonNull
    private Long projectId;
    @NonNull
    private Long cardId;

    @NonNull
    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(@NonNull Long projectId) {
        this.projectId = projectId;
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

        JoinCardWithProject that = (JoinCardWithProject) o;

        if (!projectId.equals(that.projectId)) return false;
        return cardId.equals(that.cardId);
    }

    @Override
    public int hashCode() {
        int result = projectId.hashCode();
        result = 31 * result + cardId.hashCode();
        return result;
    }
}
