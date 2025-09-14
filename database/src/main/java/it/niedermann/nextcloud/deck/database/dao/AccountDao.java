package it.niedermann.nextcloud.deck.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface AccountDao {

    @Query("SELECT count(a.id) > 0 FROM Account a")
    Flowable<Boolean> hasAccounts();
}
