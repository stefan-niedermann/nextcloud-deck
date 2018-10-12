package it.niedermann.nextcloud.deck.model.board;

import java.util.ArrayList;

import it.niedermann.nextcloud.deck.model.DBStatus;

public class Board {
    private long id;
    private long remoteId;
    private long accountId;
    private String title;
    private DBStatus status = DBStatus.UP_TO_DATE;
    private ArrayList<Task> tasks;

    public Board(long accountId, long remoteId, String title) {
        this.accountId = accountId;
        this.remoteId = remoteId;
        this.title = title;
    }

    public Board(long accountId, long id, String title, DBStatus status) {
        this.accountId = accountId;
        this.id = id;
        this.title = title;
        this.status = status;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }
}
