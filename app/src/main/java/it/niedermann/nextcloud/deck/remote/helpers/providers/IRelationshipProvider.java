package it.niedermann.nextcloud.deck.remote.helpers.providers;

import it.niedermann.nextcloud.deck.database.DataBaseAdapter;

public interface IRelationshipProvider {
    void insertAllNecessary(DataBaseAdapter dataBaseAdapter, long accountId);

    void deleteAllExisting(DataBaseAdapter dataBaseAdapter, long accountId);
}
