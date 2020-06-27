package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Account;

@Dao
public interface AccountDao extends GenericDao<Account> {
    @Query("SELECT count(*) FROM account")
    int countAccountsDirectly();

    @Query("SELECT count(*) FROM account")
    LiveData<Integer> countAccounts();

    @Query("DELETE from account where id = :id")
    void deleteById(long id);

    @Query("SELECT * from account where id = :id")
    Account getAccountByIdDirectly(long id);

    @Query("SELECT * from account where id = :id")
    LiveData<Account> getAccountById(long id);

    @Query("SELECT * from account where name = :name")
    LiveData<Account> getAccountByName(String name);

    @Query("SELECT * from account where name = :name")
    Account getAccountByNameDirectly(String name);

    @Query("SELECT * from account")
    LiveData<List<Account>> getAllAccounts();

    @Query("SELECT * from account")
    List<Account> getAllAccountsDirectly();

    @Query("SELECT * from account a where a.url like :hostLike and exists (select 1 from board b where b.id = :boardRemoteId and a.id = b.accountId)")
    LiveData<List<Account>> readAccountsForHostWithReadAccessToBoard(String hostLike, long boardRemoteId);

    @Query("SELECT * from account a where a.url like :hostLike and exists (select 1 from board b where b.id = :boardRemoteId and a.id = b.accountId)")
    List<Account> readAccountsForHostWithReadAccessToBoardDirectly(String hostLike, long boardRemoteId);
}