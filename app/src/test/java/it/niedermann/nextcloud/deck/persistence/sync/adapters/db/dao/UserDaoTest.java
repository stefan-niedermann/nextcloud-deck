package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabaseTestUtil;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class UserDaoTest extends AbstractDaoTest {

    @Test
    public void writeAndReadUser() {
        final Account account = DeckDatabaseTestUtil.createAccount(db.getAccountDao());

        final User userToCreate = new User();
        userToCreate.setDisplayname("Test Tester");
        userToCreate.setUid("test");
        userToCreate.setAccountId(account.getId());

        db.getUserDao().insert(userToCreate);
        final User user = db.getUserDao().getUserByUidDirectly(account.getId(), "test");

        assertEquals("Test Tester", user.getDisplayname());
    }

}
