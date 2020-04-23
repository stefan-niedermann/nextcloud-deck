package it.niedermann.nextcloud.deck.model.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.model.enums.EDueType;

public class FilterInformation implements Serializable {
    private EDueType dueType = EDueType.NO_FILTER;
    private List<Long> userIDs = new ArrayList<>();
    private List<Long> labelIDs = new ArrayList<>();

    public EDueType getDueType() {
        return dueType;
    }

    public void setDueType(EDueType dueType) {
        this.dueType = dueType;
    }

    public List<Long> getUserIDs() {
        return userIDs;
    }

    public List<Long> getLabelIDs() {
        return labelIDs;
    }

    public void addUserId(long id) {
        userIDs.add(id);
    }

    public void removeUserId(Long id) {
        userIDs.remove(id);
    }

    public void addLabelId(long id) {
        labelIDs.add(id);
    }

    public void removeLabelId(Long id) {
        labelIDs.remove(id);
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
