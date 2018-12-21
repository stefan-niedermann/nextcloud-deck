package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

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
