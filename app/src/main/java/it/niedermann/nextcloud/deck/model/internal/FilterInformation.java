package it.niedermann.nextcloud.deck.model.internal;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.model.enums.EDueType;

public class FilterInformation implements Serializable {
    private EDueType dueType = EDueType.NO_FILTER;
    @NonNull
    private List<Long> userIDs = new ArrayList<>();
    @NonNull
    private List<Long> labelIDs = new ArrayList<>();

    public EDueType getDueType() {
        return dueType;
    }

    public void setDueType(EDueType dueType) {
        this.dueType = dueType;
    }

    @NotNull
    public List<Long> getUserIDs() {
        return userIDs;
    }

    @NotNull
    public List<Long> getLabelIDs() {
        return labelIDs;
    }

    public void addUserId(long id) {
        userIDs.add(id);
    }

    public void removeUserId(Long id) {
        userIDs.remove(id);
    }

    public void addAllLabelIds(List<Long> ids) {
        labelIDs.addAll(ids);
    }

    public void clearLabelIds() {
        labelIDs.clear();
    }

    public void removeLabelId(Long id) {
        labelIDs.remove(id);
    }

    /**
     * @return whether or not any filter is set
     */
    public boolean hasActiveFilter() {
        return (dueType != null && dueType != EDueType.NO_FILTER) || userIDs.size() > 0 || labelIDs.size() > 0;
    }

    @Override
    public String toString() {
        return "FilterInformation{" +
                "dueType=" + dueType +
                ", userIDs=" + userIDs +
                ", labelIDs=" + labelIDs +
                '}';
    }
}
