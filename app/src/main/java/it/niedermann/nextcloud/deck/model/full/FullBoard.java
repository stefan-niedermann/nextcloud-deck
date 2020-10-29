package it.niedermann.nextcloud.deck.model.full;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.util.List;

import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;

public class FullBoard implements IRemoteEntity {
    @Embedded
    public Board board;

    @Relation(entity = Label.class, parentColumn = "localId", entityColumn = "boardId")
    public List<Label> labels;

    @Relation(parentColumn = "ownerId", entityColumn = "localId")
    public User owner;

    @Relation(entity = AccessControl.class, parentColumn = "localId", entityColumn = "boardId")
    public List<AccessControl> participants;

    @Relation(entity = Stack.class, parentColumn = "localId", entityColumn = "boardId")
    public List<Stack> stacks;

    @Ignore
    public List<User> users;

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Ignore
    @Override
    public Board getEntity() {
        return board;
    }

    public List<AccessControl> getParticipants() {
        return participants;
    }

    public void setParticipants(List<AccessControl> participants) {
        this.participants = participants;
    }

    public List<Stack> getStacks() {
        return stacks;
    }

    public void setStacks(List<Stack> stacks) {
        this.stacks = stacks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FullBoard fullBoard = (FullBoard) o;

        if (board != null ? !board.equals(fullBoard.board) : fullBoard.board != null) return false;
        if (labels != null ? !labels.equals(fullBoard.labels) : fullBoard.labels != null)
            return false;
        if (owner != null ? !owner.equals(fullBoard.owner) : fullBoard.owner != null) return false;
        if (participants != null ? !participants.equals(fullBoard.participants) : fullBoard.participants != null)
            return false;
        return stacks != null ? stacks.equals(fullBoard.stacks) : fullBoard.stacks == null;
    }

    @Override
    public int hashCode() {
        int result = board != null ? board.hashCode() : 0;
        result = 31 * result + (labels != null ? labels.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (participants != null ? participants.hashCode() : 0);
        result = 31 * result + (stacks != null ? stacks.hashCode() : 0);
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "FullBoard{" +
                "board=" + board +
                ", labels=" + labels +
                ", owner=" + owner +
                ", participants=" + participants +
                ", stacks=" + stacks +
                '}';
    }
}
