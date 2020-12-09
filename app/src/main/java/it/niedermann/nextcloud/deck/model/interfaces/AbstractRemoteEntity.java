package it.niedermann.nextcloud.deck.model.interfaces;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.Instant;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;

@Entity(
        indices = {
                @Index("accountId"),
                @Index("id"),
                @Index("lastModifiedLocal"),
                @Index(value = {"accountId", "id"}, unique = true)
        },
        foreignKeys = {
                @ForeignKey(
                        entity = Account.class,
                        parentColumns = "id",
                        childColumns = "accountId", onDelete = ForeignKey.CASCADE
                )
        }
)
public abstract class AbstractRemoteEntity implements IRemoteEntity {
    @PrimaryKey(autoGenerate = true)
    protected Long localId;

    protected long accountId;

    protected Long id;

    protected int status = DBStatus.UP_TO_DATE.getId();

    protected Instant lastModified;
    protected Instant lastModifiedLocal;

    protected String etag;

    public AbstractRemoteEntity() {
    }

    public AbstractRemoteEntity(AbstractRemoteEntity abstractRemoteEntity) {
        this.localId = abstractRemoteEntity.getLocalId();
        this.accountId = abstractRemoteEntity.getAccountId();
        this.id = abstractRemoteEntity.getId();
        this.status = abstractRemoteEntity.getStatus();
        this.lastModified = abstractRemoteEntity.getLastModified();
        this.lastModifiedLocal = abstractRemoteEntity.getLastModifiedLocal();
    }

    @Ignore
    @Override
    public IRemoteEntity getEntity() {
        return this;
    }

    @Override
    public Long getLocalId() {
        return localId;
    }


    @Override
    public void setLocalId(Long localId) {
        this.localId = localId;
    }


    @Override
    public long getAccountId() {
        return accountId;
    }


    @Override
    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }


    @Override
    public Long getId() {
        return id;
    }


    @Override
    public void setId(Long id) {
        this.id = id;
    }


    @Override
    public int getStatus() {
        return status;
    }


    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public Instant getLastModified() {
        return this.lastModified;
    }

    @Override
    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public Instant getLastModifiedLocal() {
        return this.lastModifiedLocal;
    }

    @Override
    public void setLastModifiedLocal(Instant lastModifiedLocal) {
        this.lastModifiedLocal = lastModifiedLocal;
    }

    @Ignore
    @Override
    public DBStatus getStatusEnum() {
        return DBStatus.findById(status);
    }


    @Ignore
    @Override
    public void setStatusEnum(DBStatus status) {
        this.status = status.getId();
    }

    @Override
    public String getEtag() {
        return etag;
    }

    @Override
    public void setEtag(String etag) {
        this.etag = etag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractRemoteEntity that = (AbstractRemoteEntity) o;

        if (accountId != that.accountId) return false;
        if (status != that.status) return false;
        if (localId != null ? !localId.equals(that.localId) : that.localId != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (lastModified != null ? !lastModified.equals(that.lastModified) : that.lastModified != null)
            return false;
        return lastModifiedLocal != null ? lastModifiedLocal.equals(that.lastModifiedLocal) : that.lastModifiedLocal == null;
    }

    @Override
    public int hashCode() {
        int result = localId != null ? localId.hashCode() : 0;
        result = 31 * result + (int) (accountId ^ (accountId >>> 32));
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + status;
        result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
        result = 31 * result + (lastModifiedLocal != null ? lastModifiedLocal.hashCode() : 0);
        return result;
    }
}
