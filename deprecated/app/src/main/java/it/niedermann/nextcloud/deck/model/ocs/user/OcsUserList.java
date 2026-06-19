package it.niedermann.nextcloud.deck.model.ocs.user;

import java.util.ArrayList;
import java.util.List;

public class OcsUserList {
    private List<OcsUser> users = new ArrayList<>();
    private List<OcsUser> groups = new ArrayList<>();

    public List<OcsUser> getUsers() {
        return users;
    }

    public void addUser(OcsUser user) {
        this.users.add(user);
    }

    public List<OcsUser> getGroups() {
        return groups;
    }

    public void addGroup(OcsUser user) {
        this.groups.add(user);
    }
}
