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
    private EDueType dueType = EDueType.NO_FILTER;
    @NonNull
    private List<User> users = new ArrayList<>();
    @NonNull
    private List<Label> labels = new ArrayList<>();

    public EDueType getDueType() {
        return dueType;
    }

    public void setDueType(EDueType dueType) {
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

    public void addAllLabels(List<Label> labels) {
        this.labels.addAll(labels);
    }

    public void addAllUsers(List<User> users) {
        this.users.addAll(users);
    }

    public void clearLabels() {
        labels.clear();
    }

    public void clearUsers() {
        users.clear();
    }

    /**
     * @return whether or not any filter is set
     */
    public boolean hasActiveFilter() {
        return (dueType != null && dueType != EDueType.NO_FILTER) || users.size() > 0 || labels.size() > 0;
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
