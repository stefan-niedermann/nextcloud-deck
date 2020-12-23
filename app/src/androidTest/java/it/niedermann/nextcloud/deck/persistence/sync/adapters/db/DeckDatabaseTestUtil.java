package it.niedermann.nextcloud.deck.persistence.sync.adapters.db;

import androidx.annotation.NonNull;

import java.util.Random;

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
        accountToCreate.setName(randomString(15) + " " + randomString(15));
        accountToCreate.setUserName(randomString(10));
        accountToCreate.setUrl("https://" + randomString(10) + ".example.com");
        final long id = dao.insert(accountToCreate);
        return dao.getAccountByIdDirectly(id);
    }

    public static User createUser(@NonNull UserDao dao, @NonNull Account account) {
        final User userToCreate = new User();
        userToCreate.setDisplayname(randomString(15) + " " + randomString(15));
        userToCreate.setUid(randomString(10));
        userToCreate.setAccountId(account.getId());
        final long id = dao.insert(userToCreate);
        return dao.getUserByLocalIdDirectly(id);
    }

    private static String randomString(int length) {
        final int leftLimit = 48; // numeral '0'
        final int rightLimit = 122; // letter 'z'

        return new Random().ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
