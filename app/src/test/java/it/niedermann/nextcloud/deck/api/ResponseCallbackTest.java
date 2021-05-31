package it.niedermann.nextcloud.deck.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.List;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class ResponseCallbackTest {

    @Test
    public void testFillAccountIDs() {
        final Account account = new Account(1337L);
        ResponseCallback<Object> callback = new ResponseCallback<Object>(account) {
            @Override
            public void onResponse(Object response) {
                fail("I didn't ask you!");
            }
        };

        // Must do nothing
        callback.fillAccountIDs(null);

        final Card card = new Card();
        assertNotEquals(1337, card.getAccountId());
        callback.fillAccountIDs(card);
        assertEquals(1337, card.getAccountId());

        final List<Board> boards = Arrays.asList(new Board(), new Board(), new Board());
        assertFalse(boards.stream().anyMatch(b -> b.getAccountId() == 1337));
        callback.fillAccountIDs(boards);
        assertTrue(boards.stream().allMatch(b -> b.getAccountId() == 1337));
    }

    @Test
    public void testFrom() {
        // No lambda, since Mockito requires a non final class for a spy
        //noinspection Convert2Lambda
        final IResponseCallback<Void> originalCallback = new IResponseCallback<Void>() {
            @Override
            public void onResponse(Void response) {
                // Do nothing...
            }
        };
        final IResponseCallback<Void> originalCallbackSpy = spy(originalCallback);
        final ResponseCallback<Void> callback = ResponseCallback.from(mock(Account.class), originalCallbackSpy);

        callback.onResponse(null);
        verify(originalCallbackSpy, times(1)).onResponse(null);

        callback.onError(null);
        verify(originalCallbackSpy, times(1)).onError(null);
    }

}
