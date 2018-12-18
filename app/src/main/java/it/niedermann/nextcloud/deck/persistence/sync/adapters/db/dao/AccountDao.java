package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Set;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.User;

@Dao
public interface AccountDao extends GenericDao<Account> {

    @Query("SELECT * FROM account")
    Set<Account> getAccounts();

    @Query("SELECT count(*) FROM account")
    int countAccounts();

}