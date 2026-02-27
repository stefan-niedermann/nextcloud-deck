package it.niedermann.nextcloud.deck.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.adapter.database.AccountDao;

@Dao
public interface RoomAccountDao extends AccountDao {

    @Query("SELECT count(a.id) > 0 FROM Account a")
    Flowable<Boolean> hasAccounts();
}
