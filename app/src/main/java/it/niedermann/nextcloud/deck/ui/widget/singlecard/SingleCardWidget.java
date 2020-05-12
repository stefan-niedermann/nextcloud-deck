package it.niedermann.nextcloud.deck.ui.widget.singlecard;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;
import it.niedermann.owncloud.notes.R;

public class SingleCardWidget extends AppWidgetProvider {

    void updateAppWidget(Context context, AppWidgetManager awm, int[] appWidgetIds) {
        final SyncManager syncManager = new SyncManager(context);

        for (int appWidgetId : appWidgetIds) {
            syncManager.getSingleCardWidgetModel(appWidgetId).observe(this, (model) -> {

                Intent templateIntent = EditActivity.createEditCardIntent(context, model.getAccount(), model.getBoardLocalId(), model.getCardLocalId());

                PendingIntent templatePendingIntent = PendingIntent.getActivity(context, appWidgetId, templateIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                Intent serviceIntent = new Intent(context, SingleCardWidgetService.class);
                serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

                RemoteViews views;

                views = new RemoteViews(context.getPackageName(), R.layout.widget_single_note);
                views.setPendingIntentTemplate(R.id.single_note_widget_lv, templatePendingIntent);
                views.setRemoteAdapter(R.id.single_note_widget_lv, serviceIntent);
                views.setEmptyView(R.id.single_note_widget_lv, R.id.widget_single_note_placeholder_tv);
                awm.notifyAppWidgetViewDataChanged(appWidgetId, R.id.single_note_widget_lv);
                awm.updateAppWidget(appWidgetId, views);
            });
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        updateAppWidget(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager awm = AppWidgetManager.getInstance(context);

        updateAppWidget(context, AppWidgetManager.getInstance(context),
                (awm.getAppWidgetIds(new ComponentName(context, SingleCardWidget.class))));
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        final SyncManager syncManager = new SyncManager(context);

        for (int appWidgetId : appWidgetIds) {
            syncManager.deleteSingleCardWidgetModel(appWidgetId);
        }

        super.onDeleted(context, appWidgetIds);
    }
}
