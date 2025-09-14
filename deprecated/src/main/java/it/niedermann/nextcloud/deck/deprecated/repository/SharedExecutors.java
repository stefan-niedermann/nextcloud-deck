package it.niedermann.nextcloud.deck.deprecated.repository;

import androidx.annotation.NonNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public final class SharedExecutors {

    private static final Logger logger = Logger.getLogger(SharedExecutors.class.getSimpleName());

    private static final PriorityThreadFactory PRIORITY_THREAD_FACTORY = new PriorityThreadFactory();

    private static final ExecutorService IO_DB_READ_HIGH_PRIORITY = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> PRIORITY_THREAD_FACTORY.newThread(r, Thread.MAX_PRIORITY));
    private static final ExecutorService IO_DB_WRITE_HIGH_PRIORITY = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> PRIORITY_THREAD_FACTORY.newThread(r, Thread.MAX_PRIORITY));
    private static final ThreadPoolExecutor IO_DB_READ_LOW_PRIORITY = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), r -> PRIORITY_THREAD_FACTORY.newThread(r, Thread.MIN_PRIORITY));
    private static final ThreadPoolExecutor IO_DB_WRITE_LOW_PRIORITY = new ThreadPoolExecutor(5, 5, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), r -> PRIORITY_THREAD_FACTORY.newThread(r, Thread.MIN_PRIORITY));


    @Deprecated(forRemoval = true)
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    @Deprecated(forRemoval = true)
    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(NUMBER_OF_CORES >> 1, NUMBER_OF_CORES,
            10L, TimeUnit.MINUTES, new LinkedBlockingQueue<>()) {
        @Override
        public Future<?> submit(Runnable task) {
            return super.submit(new RetryableRunnable(task));
        }
    };

    static {
        IO_DB_READ_LOW_PRIORITY.allowCoreThreadTimeOut(true);
        IO_DB_WRITE_LOW_PRIORITY.allowCoreThreadTimeOut(true);
    }

    @NonNull
    public static ExecutorService getIoDbReadHighPriority() {
        return IO_DB_READ_HIGH_PRIORITY;
    }

    @NonNull
    public static ExecutorService getIoDbWriteHighPriority() {
        return IO_DB_WRITE_HIGH_PRIORITY;
    }

    public static ExecutorService getIoDbReadLowPriority() {
        return IO_DB_READ_LOW_PRIORITY;
    }

    public static ExecutorService getIoDbWriteLowPriority() {
        return IO_DB_WRITE_LOW_PRIORITY;
    }

    /// @deprecated Use one of the more semantic getters
    /// - [#getIoDbReadHighPriority()] for blocking db reading access with high priority like user interactions (e. g. read entities  to display them)
    /// - [#getIoDbWriteHighPriority()] for blocking db writing access with high priority like user interactions (e. g. create an entity)
    /// - [#getIoDbReadLowPriority()] for blocking db reading access with low priority like background interactions (e. g. synchronization)
    /// - [#getIoDbWriteLowPriority()] for blocking db writing access with low priority like background interactions (e. g. synchronization)
    public static ExecutorService getLinkedBlockingQueueExecutor() {
        return EXECUTOR;
    }

    private static class PriorityThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            return newThread(r, Thread.NORM_PRIORITY);
        }

        public Thread newThread(Runnable r, int priority) {
            final var thread = Executors.defaultThreadFactory().newThread(r);
            thread.setPriority(priority);
            return thread;
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
                    logger.severe("Error executing task, already retried" + maxRetries + " times, giving up. Error causing this: " + getStacktraceAsString(e));
                    throw e;
                }
                logger.severe("Error executing task, retrying for " + retriesLeft + " more times. Error causing this: " + getStacktraceAsString(e));
                retriesLeft--;
                EXECUTOR.submit(this);
            }
        }

        private static String getStacktraceAsString(Throwable e) {
            final var sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            return sw.toString();
        }
    }
}
