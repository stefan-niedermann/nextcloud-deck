/**
 * Copyright 2017 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.niedermann.nextcloud.deck.javafx.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.reactivex.rxjava4.core.Scheduler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Executes work on the JavaFx UI thread.
 * This scheduler should only be used with actions that execute quickly.
 */
public final class JavaFxSchedulerTest {

    private static void waitForEmptyEventQueue() throws Exception {
        FXUtilities.runAndWait(() -> {
            // nothing to do, we're just waiting here for the event queue to be emptied
        });
    }

    public static class AsNonApp extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            // noop
        }
    }

    @BeforeAll
    public static void initJFX() {
        try {
            javafx.application.Platform.startup(() -> {
            });
        } catch (final IllegalStateException ignore) {
            // Already started
        } catch (final UnsupportedOperationException e) {
            assumeTrue(false, "JavaFX platform is not supported in this environment");
        }
    }

    @Test
    public void testPeriodicScheduling() throws Exception {
        final JavaFxScheduler scheduler = new JavaFxScheduler();
        final Scheduler.Worker inner = scheduler.createWorker();

        final CountDownLatch latch = new CountDownLatch(4);

        final Runnable innerAction = mock(Runnable.class);

        final Runnable action = () -> {
            try {
                innerAction.run();
                assertTrue(Platform.isFxApplicationThread());
            } finally {
                latch.countDown();
            }
        };

        inner.schedulePeriodically(action, 100, 400, TimeUnit.MILLISECONDS);

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("timed out waiting for tasks to execute");
        }

        inner.dispose();
        waitForEmptyEventQueue();
        verify(innerAction, times(4)).run();
    }

    @Test
    public void testNestedActions() throws Exception {
        final JavaFxScheduler scheduler = new JavaFxScheduler();
        final Scheduler.Worker inner = scheduler.createWorker();

        final CountDownLatch startAsync = new CountDownLatch(1);
        final CountDownLatch asyncStarted = new CountDownLatch(1);
        final AtomicBoolean directInvocation = new AtomicBoolean(false);

        TestAction a1n1 = TestAction.of("A1N1", () -> assertTrue(directInvocation.get()));
        TestAction a1n2 = TestAction.of("A1N2", () -> assertTrue(directInvocation.get()));
        TestAction a1n = TestAction.of("A1N", () -> {
            assertTrue(directInvocation.get());
            inner.schedule(a1n1);
            inner.schedule(a1n2);
        });
        TestAction a2n1 = TestAction.of("A2N1", () -> assertTrue(directInvocation.get()));
        TestAction a2n2 = TestAction.of("A2N2", () -> assertTrue(directInvocation.get()));
        TestAction a2n = TestAction.of("A2N", () -> {
            assertTrue(directInvocation.get());
            inner.schedule(a2n1);
            inner.schedule(a2n2);
        });
        TestAction a = TestAction.of("A", () -> {
            directInvocation.set(true);
            Platform.runLater(() -> directInvocation.set(false));

            inner.schedule(a1n);
            startAsync.countDown();
            try {
                asyncStarted.await(1, TimeUnit.SECONDS);
            } catch (Exception e) {
                fail();
            }
            inner.schedule(a2n);
        });
        TestAction b = TestAction.of("B", () -> {
        });
        TestAction async = TestAction.of("ASYNC", () -> {
        });

        inner.schedule(a);
        inner.schedule(b);

        startAsync.await();
        inner.schedule(async);
        asyncStarted.countDown();

        waitForEmptyEventQueue();

        TestAction.verifyOrder(a, b, a1n, async, a2n, a1n1, a1n2, a2n1, a2n2);
    }

    static class TestAction implements Runnable {
        private final Runnable userRunnable;
        private final Runnable start;
        private final Runnable end;

        public static TestAction of(String name, Runnable runnable) {
            return new TestAction(name, runnable);
        }

        private TestAction(String name, Runnable userRunnable) {
            this.userRunnable = userRunnable;
            this.start = mock(Runnable.class, name + "-Start");
            this.end = mock(Runnable.class, name + "-End");
        }

        @Override
        public void run() {
            assertTrue(Platform.isFxApplicationThread());
            start.run();
            userRunnable.run();
            end.run();
        }

        public static void verifyOrder(TestAction... actions) {
            Runnable[] runnables = new Runnable[actions.length * 2];
            for (int i = 0; i < actions.length; i++) {
                TestAction action = actions[i];
                runnables[2 * i] = action.start;
                runnables[2 * i + 1] = action.end;
            }
            InOrder inOrder = inOrder((Object[]) runnables);
            for (Runnable runnable : runnables) {
                inOrder.verify(runnable).run();
            }
        }
    }


    @Test
    public void bombardScheduler() {
        Scheduler.Worker w = JavaFxScheduler.platform().createWorker();

        CountDownLatch cdl = new CountDownLatch(2);
        int[] counter = {0, 0};

        new Thread(() -> {
            for (int i = 0; i < 1_000_000; i++) {
                w.schedule(() -> counter[0]++);
            }
            w.schedule(cdl::countDown);
        }).start();

        for (int i = 0; i < 1_000_000; i++) {
            w.schedule(() -> counter[1]++);
        }
        w.schedule(cdl::countDown);

        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(1_000_000, counter[0]);
        assertEquals(1_000_000, counter[1]);

        w.dispose();
    }

    /*
     * based on http://www.guigarage.com/2013/01/invokeandwait-for-javafx/
     * by hendrikebbers
     */
    static public class FXUtilities {

        /**
         * Simple helper class.
         *
         * @author hendrikebbers
         */
        private static class ThrowableWrapper {
            Throwable t;
        }

        /**
         * Invokes a Runnable in JFX Thread and waits while it's finished. Like
         * SwingUtilities.invokeAndWait does for EDT.
         *
         * @param run The Runnable that has to be called on JFX thread.
         * @throws InterruptedException f the execution is interrupted.
         * @throws ExecutionException   If a exception is occurred in the run method of the Runnable
         */
        public static void runAndWait(final Runnable run) throws InterruptedException, ExecutionException {
            if (Platform.isFxApplicationThread()) {
                try {
                    run.run();
                } catch (Exception e) {
                    throw new ExecutionException(e);
                }
            } else {
                final Lock lock = new ReentrantLock();
                final Condition condition = lock.newCondition();
                final ThrowableWrapper throwableWrapper = new ThrowableWrapper();
                lock.lock();
                try {
                    Platform.runLater(() -> {
                        lock.lock();
                        try {
                            run.run();
                        } catch (Throwable e) {
                            throwableWrapper.t = e;
                        } finally {
                            try {
                                condition.signal();
                            } finally {
                                lock.unlock();
                            }
                        }
                    });
                    condition.await();
                    if (throwableWrapper.t != null) {
                        throw new ExecutionException(throwableWrapper.t);
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}