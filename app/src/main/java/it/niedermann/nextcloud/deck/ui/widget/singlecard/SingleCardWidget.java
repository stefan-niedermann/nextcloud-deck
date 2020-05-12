package it.niedermann.nextcloud.deck.ui.widget.singlecard;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import java.util.NoSuchElementException;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Card;
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

                views.setTextViewText(R.id.title, model.getFullCard().getCard().getTitle());
                views.setTextViewText(R.id.card_count_tasks, model.getFullCard().getCard().getTitle());

                final String counterMaxValue = context.getString(R.string.counter_max_value);

                final int attachmentsCount = model.getFullCard().getAttachments().size();
                if (attachmentsCount == 0) {
                    views.setViewVisibility(R.id.card_count_attachments, View.GONE);
                } else {
                    views.setViewVisibility(R.id.card_count_attachments, View.VISIBLE);
                    setupCounter(views, R.id.card_count_attachments, attachmentsCount, counterMaxValue);
                }

                final int commentsCount = model.getFullCard().getCommentCount();

                if (commentsCount == 0) {
                    views.setViewVisibility(R.id.card_count_comments, View.GONE);
                } else {
                    setupCounter(views, R.id.card_count_comments, commentsCount, counterMaxValue);
                    views.setViewVisibility(R.id.card_count_comments, View.VISIBLE);
                }

                Card.TaskStatus taskStatus = model.getFullCard().getCard().getTaskStatus();
                if (taskStatus.taskCount > 0) {
                    views.setTextViewText(R.id.card_count_tasks, context.getResources().getString(R.string.task_count, String.valueOf(taskStatus.doneCount), String.valueOf(taskStatus.taskCount)));
                    views.setViewVisibility(R.id.card_count_tasks, View.VISIBLE);
                } else {
                    views.setViewVisibility(R.id.card_count_tasks, View.GONE);
                }
                TypedValue a = new TypedValue();
                context.getTheme().resolveAttribute(android.R.attr.background, a, true);
                views.setInt(R.id.widget_card, "setBackgroundColor", a.data);

                awm.updateAppWidget(appWidgetId, views);
            } catch (NoSuchElementException e) {
                // onUpdate has been triggered before the user finished configuring the widget
            }
        }
    }

    private static void setupCounter(@NonNull RemoteViews views, @IdRes int textViewId, int count, String counterMaxValue) {
        if (count > 99) {
            views.setTextViewText(textViewId, counterMaxValue);
        } else if (count > 1) {
            views.setTextViewText(textViewId, String.valueOf(count));
        } else if (count == 1) {
            views.setTextViewText(textViewId, "");
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
