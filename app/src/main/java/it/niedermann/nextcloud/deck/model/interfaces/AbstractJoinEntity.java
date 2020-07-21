package it.niedermann.nextcloud.deck.model.interfaces;

import androidx.room.Entity;
import androidx.room.Ignore;

import it.niedermann.nextcloud.deck.model.enums.DBStatus;

@Entity()
public abstract class AbstractJoinEntity {

    protected int status = DBStatus.UP_TO_DATE.getId();

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Ignore
    public DBStatus getStatusEnum() {
        return DBStatus.findById(status);
    }

    @Ignore
    public void setStatusEnum(DBStatus status) {
        this.status = status.getId();
    }
}
