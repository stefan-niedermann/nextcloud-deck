package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

public interface GenericDao<T> {

    @Insert
    long insert(T entity);

    @Insert
    long[] insert(T... entity);

    @Update
    void update(T... entity);

    @Delete
    void delete(T... entity);
}
