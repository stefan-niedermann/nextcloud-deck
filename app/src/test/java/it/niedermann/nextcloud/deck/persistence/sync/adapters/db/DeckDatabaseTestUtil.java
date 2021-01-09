package it.niedermann.nextcloud.deck.persistence.sync.adapters.db;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.AccountDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.BoardDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.CardDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.StackDao;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao.UserDao;

public class DeckDatabaseTestUtil {

    private static long currentLong = 1;

    private DeckDatabaseTestUtil() {
        // Util class
    }

    /**
     * @see <a href="https://gist.github.com/JoseAlcerreca/1e9ee05dcdd6a6a6fa1cbfc125559bba">Source</a>
     */
    public static <T> T getOrAwaitValue(final LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(@Nullable T o) {
                data[0] = o;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);
        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new RuntimeException("LiveData value was never set.");
        }
        //noinspection unchecked
        return (T) data[0];
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

    public static Board createBoard(@NonNull BoardDao dao, @NonNull Account account, @NonNull User owner) {
        final Board boardToCreate = new Board();
        boardToCreate.setAccountId(account.getId());
        boardToCreate.setTitle(randomString(10));
        boardToCreate.setOwnerId(owner.getLocalId());
        boardToCreate.setId(currentLong++);
        long id = dao.insert(boardToCreate);
        return dao.getBoardByLocalIdDirectly(id);
    }

    public static Stack createStack(@NonNull StackDao dao, @NonNull Account account, @NonNull Board board) {
        final Stack stackToCreate = new Stack();
        stackToCreate.setTitle(randomString(5));
        stackToCreate.setAccountId(account.getId());
        stackToCreate.setBoardId(board.getLocalId());
        stackToCreate.setId(currentLong++);
        long id = dao.insert(stackToCreate);
        return dao.getStackByLocalIdDirectly(id);
    }

    public static Card createCard(@NonNull CardDao dao, @NonNull Account account, @NonNull Stack stack) {
        final Card cardToCreate = new Card();
        cardToCreate.setAccountId(account.getId());
        cardToCreate.setTitle(randomString(15));
        cardToCreate.setDescription(randomString(50));
        cardToCreate.setStackId(stack.getLocalId());
        cardToCreate.setId(currentLong++);

        long id = dao.insert(cardToCreate);
        return dao.getCardByLocalIdDirectly(account.getId(), id);
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
