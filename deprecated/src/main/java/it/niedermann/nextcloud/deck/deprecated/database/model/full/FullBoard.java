package it.niedermann.nextcloud.deck.database.entity.full;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.deck.database.entity.AccessControl;
import it.niedermann.nextcloud.deck.database.entity.Board;
import it.niedermann.nextcloud.deck.database.entity.Label;
import it.niedermann.nextcloud.deck.database.entity.Stack;
import it.niedermann.nextcloud.deck.database.entity.User;
import it.niedermann.nextcloud.deck.database.entity.interfaces.IRemoteEntity;

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

        if (!Objects.equals(board, fullBoard.board)) return false;
        if (!Objects.equals(labels, fullBoard.labels))
            return false;
        if (!Objects.equals(owner, fullBoard.owner)) return false;
        if (!Objects.equals(participants, fullBoard.participants))
            return false;
        return Objects.equals(stacks, fullBoard.stacks);
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
