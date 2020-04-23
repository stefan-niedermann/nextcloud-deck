package it.niedermann.nextcloud.deck.model.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.model.enums.EDueType;

public class FilterInformation implements Serializable {
    private EDueType dueType = null;
    private List<Integer> userIDs = new ArrayList<>();
    private List<Integer> labelIDs = new ArrayList<>();

    public EDueType getDueType() {
        return dueType;
    }

    public void setDueType(EDueType dueType) {
        this.dueType = dueType;
    }

    public List<Integer> getUserIDs() {
        return userIDs;
    }

    public List<Integer> getLabelIDs() {
        return labelIDs;
    }

    public void addUserId(int id) {
        userIDs.add(id);
    }

    public void removeUserId(Integer id) {
        userIDs.remove(id);
    }

    public void addLabelId(int id) {
        labelIDs.add(id);
    }

    public void removeLabelId(Integer id) {
        labelIDs.remove(Integer.valueOf(id));
    }
}
