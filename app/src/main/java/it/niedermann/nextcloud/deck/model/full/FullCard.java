package it.niedermann.nextcloud.deck.model.full;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Relation;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.JoinCardWithLabel;
import it.niedermann.nextcloud.deck.model.JoinCardWithUser;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;

public class FullCard implements IRemoteEntity {
    @Embedded
    public Card card;

    @Relation(entity = JoinCardWithLabel.class, parentColumn = "localId", entityColumn = "cardId", projection = "labelId")
    public List<Long> labelIDs;

    @Ignore
    public List<Label> labels;


    @Relation(entity = JoinCardWithUser.class, parentColumn = "localId", entityColumn = "cardId", projection = "userId")
    public List<Long> assignedUserIDs;

    @Ignore
    public List<User> assignedUsers;

    @Relation(parentColumn = "userId", entityColumn = "localId")
    public List<User> owner;

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public List<User> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(List<User> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public List<User> getOwner() {
        return owner;
    }

    public List<Long> getAssignedUserIDs() {
        return assignedUserIDs;
    }

    public void setAssignedUserIDs(List<Long> assignedUserIDs) {
        this.assignedUserIDs = assignedUserIDs;
    }

    public List<Long> getLabelIDs() {
        return labelIDs;
    }

    public void setLabelIDs(List<Long> labelIDs) {
        this.labelIDs = labelIDs;
    }

    public void setOwner(User owner) {
        List<User> user = new ArrayList<>();
        user.add(owner);
        this.owner = user;
    }

    @Ignore
    @Override
    public IRemoteEntity getEntity() {
        return card;
    }
}
