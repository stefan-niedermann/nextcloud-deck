package it.niedermann.nextcloud.deck.ui.widget.stack;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainActivity;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;

public class StackWidget extends AppWidgetProvider {
    private static final int PENDING_INTENT_OPEN_APP_RQ = 0;
    private static final int PENDING_INTENT_EDIT_CARD_RQ = 1;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        updateAppWidget(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        final AppWidgetManager awm = AppWidgetManager.getInstance(context);

        if (ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                final int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
                DeckLog.verbose(ACTION_APPWIDGET_UPDATE + " for " + StackWidget.class.getSimpleName() + " with id " + appWidgetId + ", perform update.");
                updateAppWidget(context, awm, new int[]{appWidgetId});
            } else {
                DeckLog.verbose(ACTION_APPWIDGET_UPDATE + " for " + StackWidget.class.getSimpleName() + ": Triggering update for all widgets of this type.");
                updateAppWidget(context, awm, awm.getAppWidgetIds(new ComponentName(context, StackWidget.class)));
            }
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        final SyncManager syncManager = new SyncManager(context);

        for (int appWidgetId : appWidgetIds) {
            DeckLog.info("Delete " + StackWidget.class.getSimpleName() + " with id " + appWidgetId);
            syncManager.deleteFilterWidget(appWidgetId, new IResponseCallback<Boolean>(null) {
                @Override
                public void onResponse(Boolean response) {
                    DeckLog.verbose("Successfully deleted " + StackWidget.class.getSimpleName() + " with id " + appWidgetId);
                }
            });
        }
    }

    private static void updateAppWidget(Context context, AppWidgetManager awm, int[] appWidgetIds) {
        final SyncManager syncManager = new SyncManager(context);
        for (int appWidgetId : appWidgetIds) {
            new Thread(() -> {
                if (syncManager.filterWidgetExists(appWidgetId)) {
                    final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_stack);

                    final Intent serviceIntent = new Intent(context, StackWidgetService.class);
                    serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

                    final Intent intent = new Intent(Intent.ACTION_MAIN).setComponent(new ComponentName(context.getPackageName(), MainActivity.class.getName()));
                    final PendingIntent pendingIntent = PendingIntent.getActivity(context, PENDING_INTENT_OPEN_APP_RQ,
                            intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    final PendingIntent templatePI = PendingIntent.getActivity(context, PENDING_INTENT_EDIT_CARD_RQ,
                            new Intent(context, EditActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

                    views.setOnClickPendingIntent(R.id.widget_stack_header_rl, pendingIntent);


                    views.setPendingIntentTemplate(R.id.stack_widget_lv, templatePI);
                    views.setRemoteAdapter(R.id.stack_widget_lv, serviceIntent);
                    views.setEmptyView(R.id.stack_widget_lv, R.id.widget_stack_placeholder_iv);

                    awm.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stack_widget_lv);
                    awm.updateAppWidget(appWidgetId, views);
                } else {
                    DeckLog.warn("Does not yet exist");
                }
            }).start();
        }
    }
}
