package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabase;

@RunWith(AndroidJUnit4.class)
public abstract class AbstractDaoTest {

    protected DeckDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, DeckDatabase.class).build();
    }

    @After
    public void closeDb() {
        db.close();
    }
}
