package it.niedermann.nextcloud.deck.persistence.sync.adapters.db;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.AccountDao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class DeckDatabaseTest {
    private AccountDao accountDao;
    private DeckDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, DeckDatabase.class).build();
        accountDao = db.getAccountDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void writeAndReadAccount() {
        final Account account = new Account();
        account.setName("test@example.com");
        account.setUserName("test");
        account.setUrl("https://example.com");
        accountDao.insert(account);
        final Account byName = accountDao.getAccountByNameDirectly("test@example.com");
        assertEquals("test", byName.getUserName());
        assertEquals("https://example.com", byName.getUrl());
        assertEquals(Integer.valueOf(0), byName.getColor());
        assertEquals(Integer.valueOf(0), byName.getTextColor());
        assertEquals("0.6.4", byName.getServerDeckVersion());
        assertNull(byName.getEtag());
        assertFalse(byName.isMaintenanceEnabled());
        assertEquals("https://example.com/index.php/avatar/test/1337", byName.getAvatarUrl(1337));
    }
}
