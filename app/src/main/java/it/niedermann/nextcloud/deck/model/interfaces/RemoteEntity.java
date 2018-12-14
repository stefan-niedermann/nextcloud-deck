package it.niedermann.nextcloud.deck.model.interfaces;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;

@Entity(
        inheritSuperIndices = true,
        indices = {
                @Index("accountId"),
                @Index("id"),
                @Index("lastModifiedLocal"),
                @Index(value = {"accountId", "id"}, unique = true)
        })
public abstract class RemoteEntity {
    @PrimaryKey(autoGenerate = true)
    protected Long localId;

    protected long accountId;

    protected Long id;

    @NonNull
    protected int status = DBStatus.UP_TO_DATE.getId();

    protected Date lastModified;
    protected Date lastModifiedLocal;

    public Long getLocalId() {
        return localId;
    }

    public void setLocalId(Long localId) {
        this.localId = localId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    public int getStatus() {
        return status;
    }

    public void setStatus(@NonNull int status) {
        this.status = status;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Date getLastModifiedLocal() {
        return lastModifiedLocal;
    }

    public void setLastModifiedLocal(Date lastModifiedLocal) {
        this.lastModifiedLocal = lastModifiedLocal;
    }

    public DBStatus getStatusEnum() {
        return DBStatus.findById(status);
    }

    public void setStatusEnum(DBStatus status) {
        this.status = status.getId();
    }
}
