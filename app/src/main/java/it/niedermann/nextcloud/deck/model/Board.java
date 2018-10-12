package it.niedermann.nextcloud.deck.model;

import it.niedermann.nextcloud.deck.model.DBStatus;
import it.niedermann.nextcloud.deck.model.RemoteEntity;

public class Board extends RemoteEntity {
    private long accountId;
    private String title;
    private DBStatus status = DBStatus.UP_TO_DATE;

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
