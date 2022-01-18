package it.niedermann.nextcloud.deck.util;

import android.app.PendingIntent;
import android.os.Build;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WidgetUtil {

    private WidgetUtil() {
        throw new UnsupportedOperationException("This class must not get instantiated");
    }

    /**
     * Android S requires either {@link PendingIntent#FLAG_MUTABLE} or
     * {@link PendingIntent#FLAG_IMMUTABLE} to be set on a {@link PendingIntent}.
     * This is enforced by Android and will lead to an app crash if neither of those flags is
     * present.
     * To keep the app working, this compatibility method can be used to add the
     * {@link PendingIntent#FLAG_MUTABLE} flag on Android S and higher to restore the behavior of
     * older SDK versions.
     *
     * @param flags wanted flags for {@link PendingIntent}
     * @return {@param flags} | {@link PendingIntent#FLAG_MUTABLE}
     */
    public static int pendingIntentFlagCompat(int flags) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return flags | PendingIntent.FLAG_MUTABLE;
        }
        return flags;
    }

    public static ExecutorService newWidgetExecutorService() {
        return new ThreadPoolExecutor(0, 100, 10L, TimeUnit.SECONDS, new SynchronousQueue<>());
    }
}
