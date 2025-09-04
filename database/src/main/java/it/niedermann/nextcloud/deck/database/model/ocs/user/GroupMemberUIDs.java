package it.niedermann.nextcloud.deck.database.model.ocs.user;

import java.util.ArrayList;
import java.util.List;

public class GroupMemberUIDs {
    private List<String> uids = new ArrayList<>();

    public List<String> getUids() {
        return uids;
    }

    public void setUids(List<String> uids) {
        this.uids = uids;
    }

    @Override
    public String toString() {
        return "GroupMemberUIDs{" +
                "uids=" + uids +
                '}';
    }

    public void add(String uid) {
        uids.add(uid);
    }
}
