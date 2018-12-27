package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public interface IRelationshipProvider {
    void insertAllNecessary(DataBaseAdapter dataBaseAdapter, long accountId);

    void deleteAllExisting(DataBaseAdapter dataBaseAdapter, long accountId);
}
