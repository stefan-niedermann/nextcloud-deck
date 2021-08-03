package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import it.niedermann.nextcloud.deck.TestUtil;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabaseTestUtil;

@RunWith(RobolectricTestRunner.class)
public class AccountDaoTest extends AbstractDaoTest {

    @Test
    public void testCreate() {
        final var accountToCreate = new Account();
        accountToCreate.setName("test@example.com");
        accountToCreate.setUserName("test");
        accountToCreate.setUrl("https://example.com");

        final long id = db.getAccountDao().insert(accountToCreate);
        final var account = db.getAccountDao().getAccountByIdDirectly(id);

        assertEquals("test", account.getUserName());
        assertEquals("https://example.com", account.getUrl());
        assertEquals(Integer.valueOf(Capabilities.DEFAULT_COLOR), account.getColor());
        assertEquals(Integer.valueOf(Capabilities.DEFAULT_TEXT_COLOR), account.getTextColor());
        assertEquals("0.6.4", account.getServerDeckVersion());
        assertEquals("https://example.com/index.php/avatar/test/1337", account.getAvatarUrl(1337));
        assertEquals(1, db.getAccountDao().countAccountsDirectly());
        assertNull(account.getEtag());
        assertFalse(account.isMaintenanceEnabled());
    }

    @Test
    public void testGetAccountById() throws InterruptedException {
        final var account = DeckDatabaseTestUtil.createAccount(db.getAccountDao());
        assertEquals(account.getName(), TestUtil.getOrAwaitValue(db.getAccountDao().getAccountById(account.getId())).getName());
    }

    @Test
    public void testGetAccountByName() throws InterruptedException {
        final var account = DeckDatabaseTestUtil.createAccount(db.getAccountDao());
        assertEquals(account.getUserName(), TestUtil.getOrAwaitValue(db.getAccountDao().getAccountByName(account.getName())).getUserName());
    }

    @Test
    public void testGetAllAccounts() throws InterruptedException {
        final int expectedCount = 13;
        for (int i = 0; i < expectedCount; i++) {
            DeckDatabaseTestUtil.createAccount(db.getAccountDao());
        }
        assertEquals(expectedCount, TestUtil.getOrAwaitValue(db.getAccountDao().getAllAccounts()).size());
    }

    @Test
    public void testCountAccountsDirectly() {
        final int expectedCount = 12;
        for (int i = 0; i < expectedCount; i++) {
            DeckDatabaseTestUtil.createAccount(db.getAccountDao());
        }
        assertEquals(expectedCount, db.getAccountDao().countAccountsDirectly());
    }

    @Test
    public void testCountAccounts() throws InterruptedException {
        final int expectedCount = 13;
        for (int i = 0; i < expectedCount; i++) {
            DeckDatabaseTestUtil.createAccount(db.getAccountDao());
        }
        assertEquals(Integer.valueOf(expectedCount), TestUtil.getOrAwaitValue(db.getAccountDao().countAccounts()));
    }

    @Test
    public void testGetAllAccountsDirectly() {
        final int expectedCount = 12;
        for (int i = 0; i < expectedCount; i++) {
            DeckDatabaseTestUtil.createAccount(db.getAccountDao());
        }
        assertEquals(expectedCount, db.getAccountDao().getAllAccountsDirectly().size());
    }

    @Test
    public void testGetAccountByNameDirectly() {
        final var account = DeckDatabaseTestUtil.createAccount(db.getAccountDao());
        assertEquals(account.getName(), db.getAccountDao().getAccountByNameDirectly(account.getName()).getName());
    }
}
