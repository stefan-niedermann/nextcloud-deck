package it.niedermann.nextcloud.deck.model.full;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Relation;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;

public class FullBoard implements IRemoteEntity {
    @Embedded
    public Board board;

    @Relation(entity = Label.class, parentColumn = "localId", entityColumn = "localId")
    public List<Label> labels;

    @Relation(parentColumn = "ownerId", entityColumn = "localId")
    public List<User> owner;

    @Relation(entity = User.class, parentColumn = "localId", entityColumn = "localId")
    public List<User> participants;


    public List<User> getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        List<User> user = new ArrayList<>();
        user.add(owner);
        this.owner = user;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    @Ignore
    @Override
    public IRemoteEntity getEntity() {
        return board;
    }

    @Override
    public String toString() {
        return "FullBoard{" +
                "board=" + board +
                ", labels=" + labels +
                ", owner=" + owner +
                ", participants=" + participants +
                '}';
    }
}
