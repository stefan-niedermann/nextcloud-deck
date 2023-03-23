package it.niedermann.nextcloud.deck.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * If we really want <strong>this</strong>, we should default to {@link Executors#newWorkStealingPool()}.
 * Though I recommend to distinguish between blocking threads and non-blocking threads (like network operations), where it does not make sense to limit it to available CPU cores.
 */
//@Deprecated(forRemoval = true)
public class ExecutorServiceProvider {

    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private static final ExecutorService EXECUTOR =
//            Executors.newWorkStealingPool();
            new ThreadPoolExecutor(NUMBER_OF_CORES>>1, NUMBER_OF_CORES,
                60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    private ExecutorServiceProvider() {
        // hide Constructor
    }

    public static ExecutorService getExecutorService() {
        return EXECUTOR;
    }
}
