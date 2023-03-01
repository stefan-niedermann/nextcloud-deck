package it.niedermann.android.reactivelivedata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static it.niedermann.android.reactivelivedata.TestUtil.getOrAwaitValue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.core.util.Pair;
import androidx.lifecycle.MutableLiveData;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class ReactiveLiveDataTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void filter() throws InterruptedException {
        final var s1$ = new MutableLiveData<Integer>();

        final var reactive1$ = new ReactiveLiveData<>(s1$)
                .filter(val -> val < 2);

        s1$.postValue(1);
        assertEquals(Integer.valueOf(1), getOrAwaitValue(reactive1$));

        s1$.postValue(2);
        assertEquals(Integer.valueOf(1), getOrAwaitValue(reactive1$));
    }

    @Test
    public void map() throws InterruptedException {
        final var s1$ = new MutableLiveData<Integer>();

        final var reactive1$ = new ReactiveLiveData<>(s1$)
                .map(val -> val * 2);

        s1$.postValue(1);
        assertEquals(Integer.valueOf(2), getOrAwaitValue(reactive1$));

        s1$.postValue(2);
        assertEquals(Integer.valueOf(4), getOrAwaitValue(reactive1$));

        s1$.postValue(3);
        assertEquals(Integer.valueOf(6), getOrAwaitValue(reactive1$));
    }

    @Test
    public void flatMap() throws InterruptedException {
        final var s0$ = new MutableLiveData<Void>(null);
        final var s1$ = new MutableLiveData<>("Foo");
        final var s2$ = new MutableLiveData<>("Bar");

        final var reactive1$ = new ReactiveLiveData<>(s0$)
                .flatMap(() -> s1$);

        final var reactive2$ = new ReactiveLiveData<>(s0$)
                .flatMap(() -> s2$);

        assertEquals("Foo", getOrAwaitValue(reactive1$));
        assertEquals("Bar", getOrAwaitValue(reactive2$));
    }

    @Test
    public void flatMap_chained() throws InterruptedException {
        final var s0$ = new MutableLiveData<Void>(null);
        final var s1$ = new MutableLiveData<>("Foo");
        final var s2$ = new MutableLiveData<>("Bar");
        final var s3$ = new MutableLiveData<>("Baz");

        final var reactive1$ = new ReactiveLiveData<>(s0$)
                .flatMap(() -> s1$)
                .flatMap(val -> "Foo".equals(val) ? s2$ : s3$);

        assertEquals("Bar", getOrAwaitValue(reactive1$));

        s1$.postValue("Qux");

        assertEquals("Baz", getOrAwaitValue(reactive1$));
    }

    @Test
    public void combineWith() throws InterruptedException {
        final var s1$ = new MutableLiveData<>(5);
        final var s2$ = new MutableLiveData<>("Foo");

        final var reactive1$ = new ReactiveLiveData<>(s1$)
                .combineWith(val -> s2$);

        assertEquals(new Pair<>(5, "Foo"), getOrAwaitValue(reactive1$));
    }

    @Test
    public void merge() throws InterruptedException {
        final var s1$ = new MutableLiveData<>(5);
        final var s2$ = new MutableLiveData<>(9);

        final var reactive1$ = new ReactiveLiveData<>(s1$)
                .merge(() -> s2$);

        assertEquals(Integer.valueOf(5), getOrAwaitValue(reactive1$));
        assertEquals(Integer.valueOf(9), getOrAwaitValue(reactive1$));
    }

    @Test
    public void take() throws InterruptedException {
        assertThrows(RuntimeException.class, () -> new ReactiveLiveData<>().take(Integer.MAX_VALUE));
        assertThrows(RuntimeException.class, () -> new ReactiveLiveData<>().take(0));

        final var s1$ = new MutableLiveData<>(0);

        final var reactive1$ = new ReactiveLiveData<>(s1$)
                .take(3);

        assertEquals(Integer.valueOf(0), getOrAwaitValue(reactive1$));

        s1$.setValue(1);
        assertEquals(Integer.valueOf(1), getOrAwaitValue(reactive1$));

        s1$.setValue(2);
        assertEquals(Integer.valueOf(2), getOrAwaitValue(reactive1$));

        s1$.setValue(3);
        assertEquals(Integer.valueOf(2), getOrAwaitValue(reactive1$));

        s1$.setValue(4);
        assertEquals(Integer.valueOf(2), getOrAwaitValue(reactive1$));
    }

    @Test
    public void debounce() throws InterruptedException {
        final var s1$ = new MutableLiveData<>(0);

        final var reactive1$ = new ReactiveLiveData<>(s1$)
                .debounce(120);

        assertEquals(Integer.valueOf(0), getOrAwaitValue(reactive1$, 10, TimeUnit.MILLISECONDS));

        for (int i = 1; i <= 6; i++) {
            Thread.sleep(50);
            s1$.setValue(i);
            switch (i) {
                case 1:
                case 2:
                    assertEquals(Integer.valueOf(0), getOrAwaitValue(reactive1$, 10, TimeUnit.MILLISECONDS));
                    break;
                case 3:
                case 4:
                case 5:
                    assertEquals(Integer.valueOf(3), getOrAwaitValue(reactive1$, 10, TimeUnit.MILLISECONDS));
                    break;
                case 6:
                    assertEquals(Integer.valueOf(6), getOrAwaitValue(reactive1$, 10, TimeUnit.MILLISECONDS));
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    @Test
    public void debounce_shouldPickUpChangesAfterTheTimeoutDirectly() throws InterruptedException {
        final var s1$ = new MutableLiveData<>(0);

        final var reactive1$ = new ReactiveLiveData<>(s1$)
                .debounce(120);

        assertEquals(Integer.valueOf(0), getOrAwaitValue(reactive1$, 0, TimeUnit.MILLISECONDS));

        Thread.sleep(50);
        s1$.setValue(1);
        assertEquals(Integer.valueOf(0), getOrAwaitValue(reactive1$, 0, TimeUnit.MILLISECONDS));

        Thread.sleep(50);
        s1$.setValue(2);
        assertEquals(Integer.valueOf(0), getOrAwaitValue(reactive1$, 0, TimeUnit.MILLISECONDS));

        Thread.sleep(50);
        assertEquals(Integer.valueOf(2), getOrAwaitValue(reactive1$, 0, TimeUnit.MILLISECONDS));
    }
}
