package it.niedermann.nextcloud.deck.model.internal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.EDueType;

public class FilterInformation implements Serializable {
    @NonNull
    private EDueType dueType = EDueType.NO_FILTER;
    private boolean noAssignedUser = false;
    @NonNull
    private List<User> users = new ArrayList<>();
    @NonNull
    private List<Label> labels = new ArrayList<>();

    public FilterInformation() {
        // Default constructor
    }

    public FilterInformation(@Nullable FilterInformation filterInformation) {
        if (filterInformation != null) {
            this.dueType = filterInformation.getDueType();
            this.users.addAll(filterInformation.getUsers());
            this.labels.addAll(filterInformation.getLabels());
            this.noAssignedUser = filterInformation.isNoAssignedUser();
        }
    }

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

    @NonNull
    public List<Label> getLabels() {
        return labels;
    }

    public void addLabel(@NonNull Label label) {
        this.labels.add(label);
    }

    public void addUser(@NonNull User user) {
        this.users.add(user);
    }

    public void removeLabel(@NonNull Label label) {
        labels.remove(label);
    }

    public void removeUser(@NonNull User user) {
        users.remove(user);
    }

    public boolean isNoAssignedUser() {
        return noAssignedUser;
    }

    public void setNoAssignedUser(boolean noAssignedUser) {
        this.noAssignedUser = noAssignedUser;
    }

    @NonNull
    @Override
    public String toString() {
        return "FilterInformation{" +
                "dueType=" + dueType +
                ", noAssignedUser=" + noAssignedUser +
                ", users=" + users +
                ", labels=" + labels +
                '}';
    }

    /**
     * @return whether or not the given {@param filterInformation} has any actual filters set
     */
    public static boolean hasActiveFilter(@Nullable FilterInformation filterInformation) {
        if (filterInformation == null) {
            return false;
        }
        return filterInformation.getDueType() != EDueType.NO_FILTER
                || filterInformation.getUsers().size() > 0
                || filterInformation.getLabels().size() > 0
                || filterInformation.noAssignedUser;
    }
}
