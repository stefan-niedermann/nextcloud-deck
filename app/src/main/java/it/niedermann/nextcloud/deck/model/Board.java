package it.niedermann.nextcloud.deck.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Board extends RemoteEntity {
    private String title;
    private User owner;
    private String color;
    private boolean archived;
    private List<Label> labels = new ArrayList<>();
    private String acl;
    private List<Permissions> permissions = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private int shared;
    //private LocalDate deletedAt;


    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DBStatus getStatus() {
        return status;
    }

    public void setStatus(DBStatus status) {
        this.status = status;
    }
}
