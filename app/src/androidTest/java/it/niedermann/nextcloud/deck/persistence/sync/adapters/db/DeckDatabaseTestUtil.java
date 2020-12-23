package it.niedermann.nextcloud.deck.persistence.sync.adapters.db;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.AccountDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.UserDao;

public class DeckDatabaseTestUtil {

    private DeckDatabaseTestUtil() {
        // Util class
    }

    public static Account createAccount(@NonNull AccountDao dao) {
        final Account accountToCreate = new Account();
        accountToCreate.setName("test@example.com");
        accountToCreate.setUserName("test");
        accountToCreate.setUrl("https://example.com");
        dao.insert(accountToCreate);
        return dao.getAccountByNameDirectly("test@example.com");
    }

    public static User createUser(@NonNull UserDao dao, @NonNull Account account) {
        final User userToCreate = new User();
        userToCreate.setDisplayname("Test Tester");
        userToCreate.setUid("test");
        userToCreate.setAccountId(account.getId());
        dao.insert(userToCreate);
        return dao.getUserByUidDirectly(account.getId(), "test");
    }
}
