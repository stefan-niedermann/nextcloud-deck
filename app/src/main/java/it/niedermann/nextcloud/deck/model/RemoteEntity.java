package it.niedermann.nextcloud.deck.model;

import java.io.Serializable;

public class RemoteEntity implements Serializable {
    protected long remoteId = 0;
    protected long id = 0;
    private DBStatus status = DBStatus.UP_TO_DATE;

    public RemoteEntity() {
    }

    public RemoteEntity(long remoteId) {
        super();
        this.remoteId = remoteId;
    }

    public RemoteEntity(long id, long remoteId) {
        this(remoteId);
        this.id = id;
    }

    public long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(long remoteId) {
        this.remoteId = remoteId;
    }

    public DBStatus getStatus() {
        return status;
    }

    public void setStatus(DBStatus status) {
        this.status = status;
    }

    public void setStatus(int statusId) {
        this.status = DBStatus.findById(statusId);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
