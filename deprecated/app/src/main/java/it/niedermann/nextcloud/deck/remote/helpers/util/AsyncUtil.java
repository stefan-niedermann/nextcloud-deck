package it.niedermann.nextcloud.deck.remote.helpers.util;

import androidx.annotation.NonNull;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import it.niedermann.nextcloud.deck.DeckLog;

public class AsyncUtil {

    public static void awaitAsyncWork(int count, @NonNull Consumer<CountDownLatch> worker) {
        final var latch = new CountDownLatch(count);
        worker.accept(latch);
        try {
            latch.await();
        } catch (InterruptedException e) {
            DeckLog.logError(e);
        }
    }
}
