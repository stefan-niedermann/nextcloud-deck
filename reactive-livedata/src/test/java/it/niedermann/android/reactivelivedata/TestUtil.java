package it.niedermann.android.reactivelivedata;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestUtil {

    private TestUtil() {
        // Util class
    }
    
    /**
     * @see #getOrAwaitValue(LiveData, long, TimeUnit)
     */
    public static <T> T getOrAwaitValue(final LiveData<T> liveData) throws InterruptedException {
        return getOrAwaitValue(liveData, 2, TimeUnit.SECONDS);
    }

    /**
     * @see <a href="https://gist.github.com/JoseAlcerreca/1e9ee05dcdd6a6a6fa1cbfc125559bba">Source</a>
     */
    public static <T> T getOrAwaitValue(final LiveData<T> liveData, long timeout, TimeUnit unit) throws InterruptedException {
        final var data = new Object[1];
        final var latch = new CountDownLatch(1);
        final var observer = new Observer<T>() {
            @Override
            public void onChanged(@Nullable T o) {
                data[0] = o;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);
        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(timeout, unit)) {
            throw new RuntimeException("LiveData value was never set.");
        }
        //noinspection unchecked
        return (T) data[0];
    }
}
