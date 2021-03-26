package it.niedermann.nextcloud.deck.model.internal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.EDueType;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProject;

public class FilterInformation implements Serializable {

    public enum EArchiveStatus{
        ALL, ARCHIVED, NON_ARCHIVED
    }

    @NonNull
    private EDueType dueType = EDueType.NO_FILTER;
    private boolean noAssignedLabel = false;
    private boolean noAssignedUser = false;
    private boolean noAssignedProject = false;
    @NonNull
    private List<User> users = new ArrayList<>();
    @NonNull
    private List<Label> labels = new ArrayList<>();
    @NonNull
    private List<OcsProject> projects = new ArrayList<>();
    @NonNull
    private EArchiveStatus archiveStatus = EArchiveStatus.NON_ARCHIVED;
    @NonNull
    private String filterText = "";

    public FilterInformation() {
        // Default constructor
    }

    public FilterInformation(@Nullable FilterInformation filterInformation) {
        if (filterInformation != null) {
            this.dueType = filterInformation.getDueType();
            this.archiveStatus = filterInformation.getArchiveStatus();
            this.users.addAll(filterInformation.getUsers());
            this.labels.addAll(filterInformation.getLabels());
            this.noAssignedUser = filterInformation.isNoAssignedUser();
            this.noAssignedLabel = filterInformation.isNoAssignedLabel();
            this.archiveStatus = filterInformation.getArchiveStatus();
            this.noAssignedProject = filterInformation.isNoAssignedProject();
            this.projects = filterInformation.getProjects();
            this.filterText = filterInformation.getFilterText();
        }
    }

    public void setFilterText(@NonNull String filterText) {
        this.filterText = filterText;
    }

    @NonNull
    public String getFilterText() {
        return this.filterText;
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

    public boolean isNoAssignedLabel() {
        return noAssignedLabel;
    }

    public void setNoAssignedLabel(boolean noAssignedLabel) {
        this.noAssignedLabel = noAssignedLabel;
    }

    public void setArchiveStatus(@NonNull EArchiveStatus archiveStatus) {
        this.archiveStatus = archiveStatus;
    }

    public void setUsers(@NonNull List<User> users) {
        this.users = users;
    }

    public boolean isNoAssignedProject() {
        return noAssignedProject;
    }

    public void setNoAssignedProject(boolean noAssignedProject) {
        this.noAssignedProject = noAssignedProject;
    }

    public void setLabels(@NonNull List<Label> labels) {
        this.labels = labels;
    }

    @NonNull
    public List<OcsProject> getProjects() {
        return projects;
    }

    public void setProjects(@NonNull List<OcsProject> projects) {
        this.projects = projects;
    }

    @NonNull
    public EArchiveStatus getArchiveStatus() {
        return archiveStatus;
    }

    @NonNull
    @Override
    public String toString() {
        return "FilterInformation{" +
                "dueType=" + dueType +
                ", noAssignedLabel=" + noAssignedLabel +
                ", noAssignedUser=" + noAssignedUser +
                ", users=" + users +
                ", labels=" + labels +
                ", archiveStatus=" + archiveStatus +
                ", filterText=" + filterText +
                '}';
    }

    /**
     * @return whether or not the given {@param filterInformation} has any actual filters except {@link #filterText}
     */
    public static boolean hasActiveFilter(@Nullable FilterInformation filterInformation) {
        if (filterInformation == null) {
            return false;
        }
        return filterInformation.getDueType() != EDueType.NO_FILTER
                || filterInformation.getUsers().size() > 0
                || filterInformation.getProjects().size() > 0
                || filterInformation.getLabels().size() > 0
                || filterInformation.noAssignedUser
                || filterInformation.noAssignedProject
                || filterInformation.noAssignedLabel;
    }
}
