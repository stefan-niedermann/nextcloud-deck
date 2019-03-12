package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.room.Dao;

import it.niedermann.nextcloud.deck.model.Permission;

@Dao
public interface PermissionDao extends GenericDao<Permission> {
}

