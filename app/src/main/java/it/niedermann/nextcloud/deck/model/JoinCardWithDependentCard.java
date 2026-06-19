package it.niedermann.nextcloud.deck.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Objects;

import it.niedermann.nextcloud.deck.model.interfaces.AbstractJoinEntity;

@Entity(

        primaryKeys = {"localCardId", "dependentRemoteCardId"},
        indices = {@Index("localCardId"), @Index("dependentRemoteCardId")},
        foreignKeys = {
                @ForeignKey(entity = Card.class,
                        parentColumns = "localId",
                        childColumns = "localCardId", onDelete = ForeignKey.CASCADE),
        })
public class JoinCardWithDependentCard extends AbstractJoinEntity {
    @NonNull
    private Long localCardId;
    @NonNull
    private Long dependentRemoteCardId;

    @NonNull
    public Long getLocalCardId() {
        return localCardId;
    }

    public void setLocalCardId(@NonNull Long localCardId) {
        this.localCardId = localCardId;
    }

    @NonNull
    public Long getDependentRemoteCardId() {
        return dependentRemoteCardId;
    }

    public void setDependentRemoteCardId(@NonNull Long dependentRemoteCardId) {
        this.dependentRemoteCardId = dependentRemoteCardId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        JoinCardWithDependentCard that = (JoinCardWithDependentCard) o;
        return Objects.equals(localCardId, that.localCardId) && Objects.equals(dependentRemoteCardId, that.dependentRemoteCardId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localCardId, dependentRemoteCardId);
    }
}
