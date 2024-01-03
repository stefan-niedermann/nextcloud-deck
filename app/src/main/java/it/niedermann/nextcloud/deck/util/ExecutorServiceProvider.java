package it.niedermann.nextcloud.deck.util;

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
            DeckLog.log("##submitting");
            return super.submit(new RetryableRunnable(task));
        }
    };

    private ExecutorServiceProvider() {
        // hide Constructor
    }

    public static ExecutorService getLinkedBlockingQueueExecutor() {
        return EXECUTOR;
    }

    private static class RetryableRunnable implements Runnable {
        private static final int MAX_RETRIES = 5;
        private final Runnable runnable;
        private int retriesLeft = MAX_RETRIES;

        public RetryableRunnable(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            try {
                runnable.run();
            } catch (Exception e) {
                if (retriesLeft < 1) {
                    DeckLog.error("Error executing task, already retried", MAX_RETRIES, " times, giving up. Error causing this:", DeckLog.getStacktraceAsString(e));
                    throw e;
                }
                DeckLog.error("Error executing task, retrying for", retriesLeft, " more times. Error causing this:", DeckLog.getStacktraceAsString(e));
                retriesLeft--;
                EXECUTOR.submit(this);
            }
        }
    }
}
