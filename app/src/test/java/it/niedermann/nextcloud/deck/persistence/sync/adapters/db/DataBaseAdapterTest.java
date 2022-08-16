package it.niedermann.nextcloud.deck.persistence.sync.adapters.db;

import static org.junit.Assert.assertEquals;
import static java.lang.reflect.Modifier.isPrivate;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabaseTestUtil.createAccount;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabaseTestUtil.createBoard;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabaseTestUtil.createUser;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.google.common.util.concurrent.MoreExecutors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;

@RunWith(RobolectricTestRunner.class)
public class DataBaseAdapterTest {

    private DeckDatabase db;
    private DataBaseAdapter adapter;

    @Before
    public void createAdapter() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final var constructor = DataBaseAdapter.class.getDeclaredConstructor(Context.class, DeckDatabase.class, ExecutorService.class);
        if (isPrivate(constructor.getModifiers())) {
            constructor.setAccessible(true);
            db = Room
                    .inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), DeckDatabase.class)
                    .allowMainThreadQueries()
                    .build();
            adapter = constructor.newInstance(ApplicationProvider.getApplicationContext(), db, MoreExecutors.newDirectExecutorService());
        }
    }

    @After
    public void closeDb() {
        if (db != null) {
            db.close();
        }
    }

    @Test
    public void testCreate() {
        final var account = createAccount(db.getAccountDao());
        final var user = createUser(db.getUserDao(), account);
        final var board = createBoard(db.getBoardDao(), account, user);
        final var fetchedBoard = adapter.getFullBoardByLocalIdDirectly(account.getId(), board.getLocalId());

        assertEquals(board.getTitle(), fetchedBoard.getBoard().getTitle());
    }

    @Test
    public void testFillSqlWithEntityListValues() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final var user = createUser(db.getUserDao(), createAccount(db.getAccountDao()));
        final var builder = new StringBuilder();
        final var args = new ArrayList<>(1);
        final var entities = Collections.singletonList(user);

        final var fillSqlWithListValues = DataBaseAdapter.class.getDeclaredMethod("fillSqlWithListValues", StringBuilder.class, Collection.class, List.class);
        fillSqlWithListValues.setAccessible(true);
        fillSqlWithListValues.invoke(adapter, builder, args, entities);
        assertEquals("?", builder.toString());
        assertEquals(user.getLocalId(), ((IRemoteEntity) args.get(0)).getLocalId());
    }

    @Test
    public void testFillSqlWithListValues() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final var user = createUser(db.getUserDao(), createAccount(db.getAccountDao()));
        final var builder = new StringBuilder();
        final var args = new ArrayList<>(1);
        final long leet = 1337L;
        final var entities = Collections.singletonList(leet);

        final Method fillSqlWithListValues = DataBaseAdapter.class.getDeclaredMethod("fillSqlWithListValues", StringBuilder.class, Collection.class, List.class);
        fillSqlWithListValues.setAccessible(true);
        fillSqlWithListValues.invoke(adapter, builder, args, entities);
        assertEquals("?", builder.toString());
        assertEquals(leet, args.get(0));
    }

    @Test
    public void testFillSqlWithMultipleListValues() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final var builder = new StringBuilder();
        final var args = new ArrayList<>(2);
        final long leet = 1337L;
        final var entities = List.of(leet, leet + 1);

        final var fillSqlWithListValues = DataBaseAdapter.class.getDeclaredMethod("fillSqlWithListValues", StringBuilder.class, Collection.class, List.class);
        fillSqlWithListValues.setAccessible(true);
        fillSqlWithListValues.invoke(adapter, builder, args, entities);
        assertEquals("?, ?", builder.toString());
        assertEquals(leet, args.get(0));
        assertEquals(leet + 1, args.get(1));
    }

}
