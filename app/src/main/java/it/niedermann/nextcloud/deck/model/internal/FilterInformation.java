package it.niedermann.nextcloud.deck.model.internal;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.EDueType;

public class FilterInformation implements Serializable {
    @NonNull
    private EDueType dueType = EDueType.NO_FILTER;
    @NonNull
    private List<User> users = new ArrayList<>();
    @NonNull
    private List<Label> labels = new ArrayList<>();

    @NonNull
    public EDueType getDueType() {
        return dueType;
    }

    public void setDueType(@NonNull EDueType dueType) {
        this.dueType = dueType;
    }

    @NonNull
    public List<User> getUsers() {
        return users;
    }

    @NotNull
    public List<Label> getLabels() {
        return labels;
    }

    public void addAllLabels(@NonNull List<Label> labels) {
        this.labels.addAll(labels);
    }

    public void addAllUsers(@NonNull List<User> users) {
        this.users.addAll(users);
    }

    public void clearLabels() {
        labels.clear();
    }

    public void clearUsers() {
        users.clear();
    }

    @NotNull
    @Override
    public String toString() {
        return "FilterInformation{" +
                "dueType=" + dueType +
                ", users=" + users +
                ", labels=" + labels +
                '}';
    }
}
