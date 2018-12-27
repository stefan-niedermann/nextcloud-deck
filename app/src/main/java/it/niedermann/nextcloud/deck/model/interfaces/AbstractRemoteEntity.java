package it.niedermann.nextcloud.deck.model.interfaces;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import it.niedermann.nextcloud.deck.model.enums.DBStatus;

@Entity(
        indices = {
                @Index("accountId"),
                @Index("id"),
                @Index("lastModifiedLocal"),
                @Index(value = {"accountId", "id"}, unique = true)
        })
public abstract class AbstractRemoteEntity implements IRemoteEntity {
    @PrimaryKey(autoGenerate = true)
    protected Long localId;

    protected long accountId;

    protected Long id;

    @NonNull
    protected int status = DBStatus.UP_TO_DATE.getId();

    protected Date lastModified;
    protected Date lastModifiedLocal;

    @Ignore
    @Override
    public IRemoteEntity getEntity() {
        return this;
    }
}
