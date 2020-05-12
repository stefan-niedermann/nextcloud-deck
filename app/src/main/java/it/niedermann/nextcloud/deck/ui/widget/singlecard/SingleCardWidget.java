package it.niedermann.nextcloud.deck.ui.widget.singlecard;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import java.util.NoSuchElementException;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullSingleCardWidgetModel;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;
import it.niedermann.nextcloud.deck.util.DateUtil;

public class SingleCardWidget extends AppWidgetProvider {

    void updateAppWidget(Context context, AppWidgetManager awm, int[] appWidgetIds) {
        final SyncManager syncManager = new SyncManager(context);

        for (int appWidgetId : appWidgetIds) {
            new Thread(() -> {
                try {
                    final FullSingleCardWidgetModel fullModel = syncManager.getSingleCardWidgetModelDirectly(appWidgetId);

                    final Intent intent = EditActivity.createEditCardIntent(context, fullModel.getAccount(), fullModel.getModel().getBoardId(), fullModel.getFullCard().getLocalId());
                    final PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_single_card);

                    views.setOnClickPendingIntent(R.id.widget_card, pendingIntent);

                    views.setTextViewText(R.id.title, fullModel.getFullCard().getCard().getTitle());

                    if (fullModel.getFullCard().getCard().getDueDate() != null) {
                        views.setTextViewText(R.id.card_due_date, DateUtil.getRelativeDateTimeString(context, fullModel.getFullCard().getCard().getDueDate().getTime()));
                        // TODO Use multiple views for background colors and only set the necessary to View.VISIBLE
                        // https://stackoverflow.com/a/3376537
                        // Because otherwise using Reflection is the only way
                        views.setViewVisibility(R.id.card_due_date, View.VISIBLE);
                    } else {
                        views.setViewVisibility(R.id.card_due_date, View.GONE);
                    }


                    final String counterMaxValue = context.getString(R.string.counter_max_value);

                    final int attachmentsCount = fullModel.getFullCard().getAttachments().size();
                    if (attachmentsCount == 0) {
                        views.setViewVisibility(R.id.card_count_attachments, View.GONE);
                    } else {
                        views.setViewVisibility(R.id.card_count_attachments, View.VISIBLE);
                        setupCounter(views, R.id.card_count_attachments, attachmentsCount, counterMaxValue);
                    }

                    final int commentsCount = fullModel.getFullCard().getCommentCount();
                    if (commentsCount == 0) {
                        views.setViewVisibility(R.id.card_count_comments, View.GONE);
                    } else {
                        setupCounter(views, R.id.card_count_comments, commentsCount, counterMaxValue);
                        views.setViewVisibility(R.id.card_count_comments, View.VISIBLE);
                    }

                    final Card.TaskStatus taskStatus = fullModel.getFullCard().getCard().getTaskStatus();
                    if (taskStatus.taskCount > 0) {
                        views.setTextViewText(R.id.card_count_tasks, context.getResources().getString(R.string.task_count, String.valueOf(taskStatus.doneCount), String.valueOf(taskStatus.taskCount)));
                        views.setViewVisibility(R.id.card_count_tasks, View.VISIBLE);
                    } else {
                        views.setViewVisibility(R.id.card_count_tasks, View.GONE);
                    }

                    awm.updateAppWidget(appWidgetId, views);
                } catch (NoSuchElementException e) {
                    // onUpdate has been triggered before the user finished configuring the widget
                }
            }).start();
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
