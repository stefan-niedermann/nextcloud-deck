package it.niedermann.nextcloud.deck.persistence.sync.adapters.db;

import android.content.Context;
import android.os.Build;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.google.common.util.concurrent.MoreExecutors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabaseTestUtil.createAccount;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabaseTestUtil.createBoard;
import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DeckDatabaseTestUtil.createUser;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.P})
public class DataBaseAdapterTest {

    private DeckDatabase db;
    private DataBaseAdapter adapter;

    @Before
    public void createAdapter() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final Constructor<DataBaseAdapter> constructor = DataBaseAdapter.class.getDeclaredConstructor(Context.class, DeckDatabase.class, ExecutorService.class);
        if (Modifier.isPrivate(constructor.getModifiers())) {
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
        final Account account = createAccount(db.getAccountDao());
        final User user = createUser(db.getUserDao(), account);
        final Board board = createBoard(db.getBoardDao(), account, user);
        final FullBoard fetchedBoard = adapter.getFullBoardByLocalIdDirectly(account.getId(), board.getLocalId());

        assertEquals(board.getTitle(), fetchedBoard.getBoard().getTitle());
    }

    @Test
    public void testFillSqlWithEntityListValues() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final User user = createUser(db.getUserDao(), createAccount(db.getAccountDao()));
        final StringBuilder builder = new StringBuilder();
        final List<Object> args = new ArrayList<>(1);
        final List<? extends AbstractRemoteEntity> entities = new ArrayList<AbstractRemoteEntity>(1) {{
            add(user);
        }};

        final Method fillSqlWithListValues = DataBaseAdapter.class.getDeclaredMethod("fillSqlWithListValues", StringBuilder.class, List.class, List.class);
        fillSqlWithListValues.setAccessible(true);
        fillSqlWithListValues.invoke(adapter, builder, args, entities);
        assertEquals("?", builder.toString());
        assertEquals(user.getLocalId(), ((IRemoteEntity)args.get(0)).getLocalId());
    }

    @Test
    public void testFillSqlWithListValues() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final User user = createUser(db.getUserDao(), createAccount(db.getAccountDao()));
        final StringBuilder builder = new StringBuilder();
        final List<Object> args = new ArrayList<>(1);
        final Long leet = 1337L;
        final List<?> entities = new ArrayList<Long>(1) {{
            add(leet);
        }};

        final Method fillSqlWithListValues = DataBaseAdapter.class.getDeclaredMethod("fillSqlWithListValues", StringBuilder.class, List.class, List.class);
        fillSqlWithListValues.setAccessible(true);
        fillSqlWithListValues.invoke(adapter, builder, args, entities);
        assertEquals("?", builder.toString());
        assertEquals(leet, args.get(0));
    }
    @Test
    public void testFillSqlWithMultipleListValues() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final User user = createUser(db.getUserDao(), createAccount(db.getAccountDao()));
        final StringBuilder builder = new StringBuilder();
        final List<Object> args = new ArrayList<>(2);
        final Long leet = 1337L;
        final List<?> entities = new ArrayList<Long>(2) {{
            add(leet);
            add(leet+1);
        }};

        final Method fillSqlWithListValues = DataBaseAdapter.class.getDeclaredMethod("fillSqlWithListValues", StringBuilder.class, List.class, List.class);
        fillSqlWithListValues.setAccessible(true);
        fillSqlWithListValues.invoke(adapter, builder, args, entities);
        assertEquals("?, ?", builder.toString());
        assertEquals(leet, args.get(0));
        assertEquals(leet+1, args.get(1));
    }

}
