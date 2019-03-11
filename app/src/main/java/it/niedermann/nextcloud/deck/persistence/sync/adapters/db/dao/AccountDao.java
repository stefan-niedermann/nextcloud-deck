package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Account;

@Dao
public interface AccountDao extends GenericDao<Account> {

    @Query("SELECT * FROM account")
    LiveData<List<Account>> getAccounts();

    @Query("SELECT count(*) FROM account")
    int countAccountsDirectly();

    @Query("SELECT count(*) FROM account")
    LiveData<Integer> countAccounts();

    @Query("DELETE from account where id = :id")
    void deleteById(long id);

    @Query("SELECT * from account where id = :id")
    Account selectByIdDirectly(long id);

    @Query("SELECT * from account where id = :id")
    LiveData<Account> selectById(long id);

    @Query("SELECT * from account")
    LiveData<List<Account>> selectAll();
}