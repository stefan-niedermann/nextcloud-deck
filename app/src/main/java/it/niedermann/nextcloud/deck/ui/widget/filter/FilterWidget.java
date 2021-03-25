package it.niedermann.nextcloud.deck.ui.widget.filter;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.NoSuchElementException;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;

public class FilterWidget extends AppWidgetProvider {
    public static final String ACCOUNT_KEY = "filter_widget_account";
    public static final String BUNDLE_KEY = "filter_widget_bundle";

    static void updateAppWidget(Context context, AppWidgetManager awm, int[] appWidgetIds, Account account) {
        final SyncManager syncManager = new SyncManager(context);

        for (int appWidgetId : appWidgetIds) {
            new Thread(() -> {
                try {
                    // TODO implement
                    throw new UnsupportedOperationException("Not yet implemented");
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
                    Bundle extras = intent.getBundleExtra(FilterWidget.BUNDLE_KEY);
                    account = (Account) extras.getSerializable(ACCOUNT_KEY);

                    if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                        if (intent.getExtras() != null) {
                            updateAppWidget(context, awm, new int[]{intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)}, account);
                        }
                    } else {
                        updateAppWidget(context, awm, awm.getAppWidgetIds(new ComponentName(context, FilterWidget.class)), account);
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
            syncManager.deleteFilterWidget(appWidgetId, response -> DeckLog.verbose("Successfully deleted " + FilterWidget.class.getSimpleName() + " with id " + appWidgetId));
        }
    }
}
