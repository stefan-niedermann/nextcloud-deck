package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.room.Dao;

import it.niedermann.nextcloud.deck.model.ocs.comment.Mention;

@Dao
public interface MentionDao extends GenericDao<Mention> {
    // nothing more special yet
}