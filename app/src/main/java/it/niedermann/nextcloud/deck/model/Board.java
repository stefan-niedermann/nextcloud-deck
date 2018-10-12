package it.niedermann.nextcloud.deck.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Board extends RemoteEntity {
    private long accountId;
    private String title;
    private User owner;
    private String color;
    private boolean archived;
    private List<Label> labels = new ArrayList<>();
    private String acl;
    private List<Permissions> permissions = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private int shared;
    private LocalDate deletedAt;


    public Board(long accountId, long remoteId, String title) {
        super(remoteId);
        this.accountId = accountId;
        this.title = title;
    }

    public Board(long accountId, long id, String title, DBStatus status) {
        super();
        this.accountId = accountId;
        this.id = id;
        this.title = title;
        this.status = status;
    }

    public long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(long remoteId) {
        this.remoteId = remoteId;
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
