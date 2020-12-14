package it.niedermann.nextcloud.deck.ui.widget.upcoming;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.widget.filter.EWidgetType;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetAccount;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetUser;
import it.niedermann.nextcloud.deck.model.widget.filter.dto.FilterWidgetCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainActivity;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;

public class UpcomingWidget extends AppWidgetProvider {
    public static final String BUNDLE_KEY = "upcoming_widget_bundle";
    private static final int PENDING_INTENT_OPEN_APP_RQ = 0;
    private static final int PENDING_INTENT_EDIT_CARD_RQ = 1;

    static void updateAppWidget(Context context, AppWidgetManager awm, int[] appWidgetIds) {
        final SyncManager syncManager = new SyncManager(context);

        for (int appWidgetId : appWidgetIds) {
            new Thread(() -> {
                try {
                    syncManager.getCardsForFilterWidget(appWidgetId, new IResponseCallback<List<FilterWidgetCard>>(null) {
                        @Override
                        public void onResponse(List<FilterWidgetCard> response) {

                            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_upcoming);

                            final Intent serviceIntent = new Intent(context, UpcomingWidgetService.class);
                            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

                            final Intent intent = new Intent(Intent.ACTION_MAIN).setComponent(new ComponentName(context.getPackageName(), MainActivity.class.getName()));
                            final PendingIntent pendingIntent = PendingIntent.getActivity(context, PENDING_INTENT_OPEN_APP_RQ, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            final PendingIntent templatePI = PendingIntent.getActivity(context, PENDING_INTENT_EDIT_CARD_RQ,
                                    new Intent(context, EditActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

                            views.setOnClickPendingIntent(R.id.widget_upcoming_header_rl, pendingIntent);
                            views.setPendingIntentTemplate(R.id.upcoming_widget_lv, templatePI);
                            views.setRemoteAdapter(R.id.upcoming_widget_lv, serviceIntent);
                            views.setEmptyView(R.id.upcoming_widget_lv, R.id.widget_upcoming_placeholder_iv);

                            awm.notifyAppWidgetViewDataChanged(appWidgetId, R.id.upcoming_widget_lv);
                            awm.updateAppWidget(appWidgetId, views);
                        }
                    });
                } catch (NoSuchElementException e) {
                    // onUpdate has been triggered before the user finished configuring the widget
                    DeckLog.warn("Caught " + NoSuchElementException.class.getSimpleName() + " for " + UpcomingWidget.class.getSimpleName() + " with ID " + appWidgetId);
                }
            }).start();
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        final SyncManager syncManager = new SyncManager(context);

        for (int appWidgetId : appWidgetIds) {
            new Thread(() -> {
                if (syncManager.filterWidgetExists(appWidgetId)) {
                    DeckLog.verbose(UpcomingWidget.class.getSimpleName() + "with id " + appWidgetId + " already exists, update instead.");
                    updateAppWidget(context, appWidgetManager, appWidgetIds);
                } else {
                    final List<Account> accountsList = syncManager.readAccountsDirectly();
                    final FilterWidget config = new FilterWidget();
                    config.setWidgetType(EWidgetType.UPCOMING_WIDGET);
                    config.setId(appWidgetId);
                    config.setAccounts(accountsList.stream().map(account -> {
                        final FilterWidgetAccount fwa = new FilterWidgetAccount();
                        fwa.setAccountId(account.getId());
                        final FilterWidgetUser fwu = new FilterWidgetUser();
                        fwu.setUserId(syncManager.getUserByUidDirectly(account.getId(), account.getUserName()).getId());
                        fwa.setUsers(Collections.singletonList(fwu));
                        return fwa;
                    }).collect(Collectors.toList()));
                    syncManager.createFilterWidget(config, new IResponseCallback<Integer>(null) {
                        @Override
                        public void onResponse(Integer response) {
                            DeckLog.verbose("Created " + UpcomingWidget.class.getSimpleName() + "with id " + appWidgetId);
                            updateAppWidget(context, appWidgetManager, appWidgetIds);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            super.onError(throwable);
                            onDeleted(context, appWidgetIds);
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        final AppWidgetManager awm = AppWidgetManager.getInstance(context);

        if (intent.getAction() != null) {
            if (intent.getAction().equals(ACTION_APPWIDGET_UPDATE)) {
                if (intent.hasExtra(BUNDLE_KEY)) {
                    if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                        if (intent.getExtras() != null) {
                            updateAppWidget(context, awm, new int[]{intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)});
                        }
                    } else {
                        updateAppWidget(context, awm, awm.getAppWidgetIds(new ComponentName(context, UpcomingWidget.class)));
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
            syncManager.deleteFilterWidget(appWidgetId, new IResponseCallback<Boolean>(null) {
                @Override
                public void onResponse(Boolean response) {

                }
            });
        }
    }

    /**
     * Updates UI data of all {@link UpcomingWidget} instances
     */
    public static void notifyDatasetChanged(Context context) {
        context.sendBroadcast(new Intent(context, UpcomingWidget.class).setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE));
    }
}
