package it.niedermann.nextcloud.deck.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static java.lang.reflect.Modifier.isProtected;
import static it.niedermann.nextcloud.deck.database.DeckDatabaseTestUtil.createAccount;
import static it.niedermann.nextcloud.deck.database.DeckDatabaseTestUtil.createBoard;
import static it.niedermann.nextcloud.deck.database.DeckDatabaseTestUtil.createCard;
import static it.niedermann.nextcloud.deck.database.DeckDatabaseTestUtil.createStack;
import static it.niedermann.nextcloud.deck.database.DeckDatabaseTestUtil.createUser;

import android.content.Context;

import androidx.annotation.NonNull;
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
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import it.niedermann.nextcloud.deck.TestUtil;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;

@RunWith(RobolectricTestRunner.class)
public class DataBaseAdapterTest {

    private DeckDatabase db;
    private DataBaseAdapter adapter;

    @Before
    public void createAdapter() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final var constructor = DataBaseAdapter.class.getDeclaredConstructor(Context.class, DeckDatabase.class, ExecutorService.class, ExecutorService.class);
        if (isProtected(constructor.getModifiers())) {
            constructor.setAccessible(true);
            db = Room
                    .inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), DeckDatabase.class)
                    .allowMainThreadQueries()
                    .build();
            adapter = constructor.newInstance(ApplicationProvider.getApplicationContext(), db, MoreExecutors.newDirectExecutorService(), MoreExecutors.newDirectExecutorService());
        } else {
            throw new RuntimeException("Expected constructor to be protected.");
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

    @Test
    public void testSearchCards() throws InterruptedException {
        final var account = createAccount(db.getAccountDao());
        final var user = createUser(db.getUserDao(), account);
        final var board = createBoard(db.getBoardDao(), account, user);

        final var stack1 = createStack(db.getStackDao(), account, board);
        final var stack2 = createStack(db.getStackDao(), account, board);
        final var card1_1 = createCard(db.getCardDao(), account, stack1, "Foo", "Hello world");
        final var card1_2 = createCard(db.getCardDao(), account, stack1, "Bar", "Hello Bar");
        final var card1_3 = createCard(db.getCardDao(), account, stack2, "Baz", "");
        final var card2_1 = createCard(db.getCardDao(), account, stack2, "Qux", "Hello Foo");
        final var card2_2 = createCard(db.getCardDao(), account, stack2, "Lorem", "Ipsum");

        var result = TestUtil.getOrAwaitValue(adapter.searchCards(account.getId(), board.getLocalId(), "Hello", 3));
        assertEquals(2, result.size());
        assertEquals(2, countCardsOf(result, stack1));
        assertEquals(1, countCardsOf(result, stack2));
        assertTrue(containsCard(result, stack1, card1_1));
        assertTrue(containsCard(result, stack1, card1_2));
        assertTrue(containsCard(result, stack2, card2_1));
    }

    private int countCardsOf(@NonNull Map<Stack, List<FullCard>> map, @NonNull Stack stackToFind) {
        for (final var stack : map.keySet()) {
            if (Objects.equals(stack.getLocalId(), stackToFind.getLocalId())) {
                //noinspection ConstantConditions
                return map.get(stack).size();
            }
        }
        throw new NoSuchElementException();
    }

    private boolean containsCard(@NonNull Map<Stack, List<FullCard>> map, @NonNull Stack stackToFind, @NonNull Card cardToFind) {
        for (final var stack : map.keySet()) {
            if (Objects.equals(stack.getLocalId(), stackToFind.getLocalId())) {
                //noinspection ConstantConditions
                for (final var fullCard : map.get(stack)) {
                    if (Objects.equals(fullCard.getLocalId(), cardToFind.getLocalId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
