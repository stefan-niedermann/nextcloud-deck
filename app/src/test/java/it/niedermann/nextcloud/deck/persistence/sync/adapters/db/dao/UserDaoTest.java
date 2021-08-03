package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabaseTestUtil;

@RunWith(RobolectricTestRunner.class)
public class UserDaoTest extends AbstractDaoTest {

    @Test
    public void writeAndReadUser() {
        final var account = DeckDatabaseTestUtil.createAccount(db.getAccountDao());

        final var userToCreate = new User();
        userToCreate.setDisplayname("Test Tester");
        userToCreate.setUid("test");
        userToCreate.setAccountId(account.getId());

        db.getUserDao().insert(userToCreate);
        final var user = db.getUserDao().getUserByUidDirectly(account.getId(), "test");

        assertEquals("Test Tester", user.getDisplayname());
    }

}
