package it.niedermann.nextcloud.deck;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import it.niedermann.nextcloud.deck.ui.card.CardAdapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Local unit tests for due date calculations (overdue, today, tommorrow, far in the future).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DueDateUnitTest {
    @Test
    public void dueDate_isOverdue() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(2018, 12, 10);
        Date due = calendar.getTime();

        calendar.set(2018, 12, 11);
        Date actual = calendar.getTime();

        long diff = CardAdapter.getDayDifference(actual, due);

        assertEquals(-1, diff);
    }

    @Test
    public void dueDate_isToday() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(2018, 12, 11);
        Date due = calendar.getTime();

        calendar.set(2018, 12, 11);
        Date actual = calendar.getTime();

        long diff = CardAdapter.getDayDifference(actual, due);

        assertEquals(0, diff);
    }

    @Test
    public void dueDate_isTomorrow() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(2018, 12, 12);
        Date due = calendar.getTime();

        calendar.set(2018, 12, 11);
        Date actual = calendar.getTime();

        long diff = CardAdapter.getDayDifference(actual, due);

        assertEquals(1, diff);
    }

    @Test
    public void dueDate_isInTheFarFuture() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(2018, 12, 20);
        Date due = calendar.getTime();

        calendar.set(2018, 12, 11);
        Date actual = calendar.getTime();

        long diff = CardAdapter.getDayDifference(actual, due);

        assertTrue(diff>1);
    }
}