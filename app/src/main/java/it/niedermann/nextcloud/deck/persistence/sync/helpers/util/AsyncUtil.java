package it.niedermann.nextcloud.deck.persistence.sync.helpers.util;

import java.util.concurrent.CountDownLatch;

import it.niedermann.nextcloud.deck.DeckLog;

public class AsyncUtil {
    public interface LatchCallback {
        void doWork(CountDownLatch latch);
    }

    public static void awaitAsyncWork(int count, LatchCallback worker){
        CountDownLatch countDownLatch = new CountDownLatch(count);
        worker.doWork(countDownLatch);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            DeckLog.logError(e);
        }
    }
}
