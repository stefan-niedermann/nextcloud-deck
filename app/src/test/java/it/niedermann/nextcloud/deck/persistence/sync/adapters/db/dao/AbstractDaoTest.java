package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabase;

@RunWith(RobolectricTestRunner.class)
public abstract class AbstractDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    protected DeckDatabase db;

    @Before
    public void createDb() {
        db = Room
                .inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), DeckDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() {
        db.close();
    }
}
