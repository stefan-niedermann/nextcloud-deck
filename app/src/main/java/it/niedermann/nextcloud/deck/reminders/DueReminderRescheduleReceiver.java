package it.niedermann.nextcloud.deck.reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.util.ExecutorServiceProvider;

public class DueReminderRescheduleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final var appContext = context.getApplicationContext();
        ExecutorServiceProvider.getLinkedBlockingQueueExecutor().submit(() -> {
            DeckLog.info("Rescheduling local due reminders after", intent == null ? "unknown broadcast" : intent.getAction());
            DueReminderScheduler.rescheduleAll(appContext);
        });
    }
}
