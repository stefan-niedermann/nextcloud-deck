package it.niedermann.nextcloud.deck.ui.widget.singlecard;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.NoSuchElementException;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.SingleCardWidgetModel;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;

public class SingleCardWidget extends AppWidgetProvider {

    void updateAppWidget(Context context, AppWidgetManager awm, int[] appWidgetIds) {
        final SyncManager syncManager = new SyncManager(context);

        for (int appWidgetId : appWidgetIds) {
            try {
                final SingleCardWidgetModel model = syncManager.getSingleCardWidgetModelDirectly(appWidgetId);

                Intent intent = EditActivity.createEditCardIntent(context, model.getAccount(), model.getBoardLocalId(), model.getFullCard().getLocalId());
                PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_single_card);
                views.setOnClickPendingIntent(R.id.title, pendingIntent);

                DateFormat format = SimpleDateFormat.getTimeInstance(
                        SimpleDateFormat.MEDIUM, Locale.getDefault());
                CharSequence text = format.format(new Date());
                views.setTextViewText(R.id.title, "My Widget Homie " + text);
                awm.updateAppWidget(appWidgetId, views);
            } catch (NoSuchElementException e) {
                // onUpdate has been triggered before the user finished configuring the widget
            }
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


    /**
     * Updates UI data of all {@link SingleCardWidget} instances
     */
    public static void notifyDatasetChanged(Context context) {
        Intent intent = new Intent(context, SingleCardWidget.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        context.sendBroadcast(intent);
    }
}
