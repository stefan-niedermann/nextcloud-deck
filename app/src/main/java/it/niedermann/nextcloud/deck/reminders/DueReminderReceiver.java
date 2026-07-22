package it.niedermann.nextcloud.deck.reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.util.ExecutorServiceProvider;

public class DueReminderReceiver extends BroadcastReceiver {

    static final String ACTION_SHOW_REMINDER = "it.niedermann.nextcloud.deck.reminders.SHOW_REMINDER";
    static final String ACTION_MARK_COMPLETE = "it.niedermann.nextcloud.deck.reminders.MARK_COMPLETE";
    static final String EXTRA_CARD_LOCAL_ID = "cardLocalId";

    @Override
    public void onReceive(Context context, Intent intent) {
        final PendingResult pendingResult = goAsync();
        if (intent == null) {
            pendingResult.finish();
            return;
        }

        final long cardLocalId = intent.getLongExtra(EXTRA_CARD_LOCAL_ID, -1L);
        if (cardLocalId <= 0L) {
            DeckLog.warn("Skipping due reminder without valid card local ID.");
            pendingResult.finish();
            return;
        }

        final var appContext = context.getApplicationContext();
        final String action = intent.getAction();
        ExecutorServiceProvider.getLinkedBlockingQueueExecutor().submit(() -> {
            try {
                if (ACTION_MARK_COMPLETE.equals(action)) {
                    DueReminderScheduler.markComplete(appContext, cardLocalId);
                } else {
                    DueReminderScheduler.showReminder(appContext, cardLocalId);
                }
            } finally {
                pendingResult.finish();
            }
        });
    }

    @NonNull
    static Intent createIntent(@NonNull Context context, long cardLocalId) {
        return createIntent(context, ACTION_SHOW_REMINDER, cardLocalId);
    }

    @NonNull
    static Intent createIntent(@NonNull Context context, @NonNull String action, long cardLocalId) {
        return new Intent(context, DueReminderReceiver.class)
                .setAction(action)
                .putExtra(EXTRA_CARD_LOCAL_ID, cardLocalId);
    }
}
