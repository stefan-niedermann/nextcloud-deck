package it.niedermann.nextcloud.deck.model.interfaces;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.model.enums.DBStatus;

public interface IRemoteEntity {

    default IRemoteEntity getEntity() {
        return this;
    }

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

    default void setStatus(int status) {
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

    default <T> List<T> copyList(List<T> listToCopy) {
        if (listToCopy == null) {
            return null;
        }
        List<T> list = new ArrayList<>(listToCopy.size());
        list.addAll(listToCopy);
        return list;
    }
}
