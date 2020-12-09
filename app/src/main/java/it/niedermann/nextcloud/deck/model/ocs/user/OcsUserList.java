package it.niedermann.nextcloud.deck.model.ocs.user;

import java.util.ArrayList;
import java.util.List;

public class OcsUserList {
    private List<OcsUser> users = new ArrayList<>();

    public List<OcsUser> getUsers() {
        return users;
    }

    public void addUser(OcsUser user) {
        this.users.add(user);
    }
}
