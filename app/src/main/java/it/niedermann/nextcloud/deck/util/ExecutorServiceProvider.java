package it.niedermann.nextcloud.deck.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceProvider {

    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(NUMBER_OF_CORES >> 1, NUMBER_OF_CORES,
            60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    private ExecutorServiceProvider() {
        // hide Constructor
    }

    public static ExecutorService getLinkedBlockingQueueExecutor() {
        return EXECUTOR;
    }
}
