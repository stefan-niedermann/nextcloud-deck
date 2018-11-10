package it.niedermann.nextcloud.deck.model;

import java.io.Serializable;

public class RemoteEntity implements Serializable {
    protected long localId = 0;
    protected long id = 0;
    protected DBStatus status = DBStatus.UP_TO_DATE;

    public RemoteEntity() {
    }

    public RemoteEntity(long localId) {
        super();
        this.localId = localId;
    }

    public RemoteEntity(long id, long localId) {
        this(localId);
        this.id = id;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
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
