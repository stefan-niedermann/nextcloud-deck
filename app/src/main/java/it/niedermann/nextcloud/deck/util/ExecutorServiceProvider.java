package it.niedermann.nextcloud.deck.util;

import androidx.annotation.NonNull;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import it.niedermann.nextcloud.deck.DeckLog;

public class ExecutorServiceProvider {

    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(NUMBER_OF_CORES >> 1, NUMBER_OF_CORES,
            60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>()) {
        @Override
        public Future<?> submit(Runnable task) {
            return super.submit(new RetryableRunnable(task));
        }
    };

    private ExecutorServiceProvider() {
        // hide Constructor
    }

    public static ExecutorService getLinkedBlockingQueueExecutor() {
        return EXECUTOR;
    }

    public static void awaitExecution(@NonNull Runnable runnable) {
        final var latch = new CountDownLatch(1);
        EXECUTOR.submit(() -> {
            runnable.run();
            latch.countDown();
        });
        try {
            latch.await();
        } catch (Throwable e) {
            DeckLog.error(e);
        }
    }

    private static class RetryableRunnable implements Runnable {
        private final int maxRetries;
        @NonNull
        private final Runnable runnable;
        private int retriesLeft;

        public RetryableRunnable(@NonNull Runnable runnable) {
            this(runnable, 5);
        }

        public RetryableRunnable(@NonNull Runnable runnable, int maxRetries) {
            this.runnable = runnable;
            this.maxRetries = maxRetries;
            this.retriesLeft = maxRetries;
        }

        @Override
        public void run() {
            try {
                runnable.run();
            } catch (Exception e) {
                if (retriesLeft < 1) {
                    DeckLog.error("Error executing task, already retried", maxRetries, " times, giving up. Error causing this:", DeckLog.getStacktraceAsString(e));
                    throw e;
                }
                DeckLog.error("Error executing task, retrying for", retriesLeft, " more times. Error causing this:", DeckLog.getStacktraceAsString(e));
                retriesLeft--;
                EXECUTOR.submit(this);
            }
        }
    }
}
