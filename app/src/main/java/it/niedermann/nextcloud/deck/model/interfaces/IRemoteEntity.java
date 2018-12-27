package it.niedermann.nextcloud.deck.model.interfaces;

import android.support.annotation.NonNull;

import java.util.Date;

import it.niedermann.nextcloud.deck.model.enums.DBStatus;

public interface IRemoteEntity {

    IRemoteEntity getEntity();

    default Long getLocalId() {
        return getEntity().getLocalId();
    }

    
    default void setLocalId(Long localId) {
        getEntity().setLocalId(localId);
    }

    
    default long getAccountId() {
        return getEntity().getAccountId();
    }

    
    default void setAccountId(long accountId) {
        getEntity().setAccountId(accountId);
    }

    
    default Long getId() {
        return getEntity().getId();
    }

    
    default void setId(Long id) {
        getEntity().setId(id);
    }

    
    default int getStatus() {
        return getEntity().getStatus();
    }

    
    default void setStatus(@NonNull int status) {
        getEntity().setStatus(status);
    }

    
    default Date getLastModified() {
        return getEntity().getLastModified();
    }

    
    default void setLastModified(Date lastModified) {
        getEntity().setLastModified(lastModified);
    }

    
    default Date getLastModifiedLocal() {
        return getEntity().getLastModifiedLocal();
    }

    
    default void setLastModifiedLocal(Date lastModifiedLocal) {
        getEntity().setLastModifiedLocal(lastModifiedLocal);
    }

    
    default DBStatus getStatusEnum() {
        return getEntity().getStatusEnum();
    }

    
    default void setStatusEnum(DBStatus status) {
        getEntity().setStatusEnum(status);
    }
}
