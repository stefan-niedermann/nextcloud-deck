package it.niedermann.nextcloud.deck.ui.widget.stack;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import java.util.NoSuchElementException;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.appwidgets.StackWidgetModel;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainActivity;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;

public class StackWidget extends AppWidgetProvider {
    public static final String ACCOUNT_ID_KEY = "stack_widget_account_id";
    public static final String ACCOUNT_KEY = "stack_widget_account";
    public static final String STACK_ID_KEY = "stack_widget_stack_id";
    public static final String BUNDLE_KEY = "stack_widget_bundle";
    private static final int PENDING_INTENT_OPEN_APP_RQ = 0;
    private static final int PENDING_INTENT_EDIT_CARD_RQ = 1;

    static void updateAppWidget(Context context, AppWidgetManager awm, int[] appWidgetIds, Account account) {
        final SyncManager syncManager = new SyncManager(context);

        for (int appWidgetId : appWidgetIds) {
            new Thread(() -> {
                try {
                    final StackWidgetModel model = syncManager.getStackWidgetModelDirectly(appWidgetId);
                    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_stack);
                    Intent serviceIntent = new Intent(context, StackWidgetService.class);

                    serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    serviceIntent.putExtra(ACCOUNT_ID_KEY + appWidgetId, model.getAccountId());
                    serviceIntent.putExtra(STACK_ID_KEY + appWidgetId, model.getStackId());
                    if (account != null) {
                        Bundle extras = new Bundle();
                        extras.putSerializable(StackWidget.ACCOUNT_KEY + appWidgetId, account);
                        serviceIntent.putExtra(BUNDLE_KEY + appWidgetId, extras);
                    }
                    serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setComponent(new ComponentName(context.getPackageName(), MainActivity.class.getName()));
                    PendingIntent pendingIntent = PendingIntent.getActivity(context,  PENDING_INTENT_OPEN_APP_RQ,
                            intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    views.setOnClickPendingIntent(R.id.widget_stack_header_rl, pendingIntent);

                    PendingIntent templatePI = PendingIntent.getActivity(context, PENDING_INTENT_EDIT_CARD_RQ,
                            new Intent(context, EditActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

                    views.setPendingIntentTemplate(R.id.stack_widget_lv, templatePI);
                    views.setRemoteAdapter(R.id.stack_widget_lv, serviceIntent);
                    views.setEmptyView(R.id.stack_widget_lv, R.id.widget_stack_placeholder_iv);
                    awm.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stack_widget_lv);
                    awm.updateAppWidget(appWidgetId, views);
                } catch (NoSuchElementException e) {
                    // onUpdate has been triggered before the user finished configuring the widget
                }
            }).start();
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        updateAppWidget(context, appWidgetManager, appWidgetIds, null);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final Account account;

        super.onReceive(context, intent);

        AppWidgetManager awm = AppWidgetManager.getInstance(context);

        if (intent.getAction() != null) {
            if (intent.getAction().equals(ACTION_APPWIDGET_UPDATE)) {
                if (intent.hasExtra(BUNDLE_KEY)) {
                    Bundle extras = intent.getBundleExtra(StackWidget.BUNDLE_KEY);
                    account = (Account) extras.getSerializable(ACCOUNT_KEY);

                    if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                        if (intent.getExtras() != null) {
                            updateAppWidget(context, awm, new int[]{intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)}, account);
                        }
                    } else {
                        updateAppWidget(context, awm, awm.getAppWidgetIds(new ComponentName(context, StackWidget.class)), account);
                    }
                }
            }
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        final SyncManager syncManager = new SyncManager(context);

        for (int appWidgetId : appWidgetIds) {
            syncManager.deleteStackWidgetModel(appWidgetId);
        }
    }

    /**
     * Updates UI data of all {@link StackWidget} instances
     */
    public static void notifyDatasetChanged(Context context) {
        context.sendBroadcast(new Intent(context, StackWidget.class).setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE));
    }
}
