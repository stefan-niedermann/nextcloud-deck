package it.niedermann.nextcloud.deck.ui.widget.upcoming;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.deck.BuildConfig;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.enums.ESortCriteria;
import it.niedermann.nextcloud.deck.model.widget.filter.EWidgetType;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetAccount;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetSort;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetUser;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;

public class UpcomingWidget extends AppWidgetProvider {
    private static final String PENDING_INTENT_ACTION_EDIT = "edit";
    private static final String PENDING_INTENT_ACTION_OPEN = "open";
    private static final String PENDING_INTENT_PARAM_LOCAL_CARD_ID = "localCardId";
    private static final String PENDING_INTENT_PARAM_ACCOUNT_ID = "accountId";
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        final SyncManager syncManager = new SyncManager(context);

        for (int appWidgetId : appWidgetIds) {
            executor.submit(() -> {
                if (syncManager.filterWidgetExists(appWidgetId)) {
                    DeckLog.warn(UpcomingWidget.class.getSimpleName(), "with id", appWidgetId, "already exists, perform update instead.");
                    updateAppWidget(executor, context, appWidgetManager, appWidgetIds);
                } else {
                    final List<Account> accountsList = syncManager.readAccountsDirectly();
                    final FilterWidget config = new FilterWidget(appWidgetId, EWidgetType.UPCOMING_WIDGET);
                    config.setSorts(new FilterWidgetSort(ESortCriteria.DUE_DATE, true));
                    config.setAccounts(accountsList.stream().map(account -> {
                        final FilterWidgetAccount fwa = new FilterWidgetAccount(account.getId(), false);
                        fwa.setUsers(new FilterWidgetUser(syncManager.getUserByUidDirectly(account.getId(), account.getUserName()).getLocalId()));
                        return fwa;
                    }).collect(Collectors.toList()));
                    syncManager.createFilterWidget(config, new IResponseCallback<>() {
                        @Override
                        public void onResponse(Integer response) {
                            DeckLog.verbose("Successfully created", UpcomingWidget.class.getSimpleName(), "with id", appWidgetId);
                            updateAppWidget(executor, context, appWidgetManager, appWidgetIds);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            DeckLog.error("Error while creating", UpcomingWidget.class.getSimpleName(), "with id", appWidgetId);
                            IResponseCallback.super.onError(throwable);
                            onDeleted(context, appWidgetIds);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        final AppWidgetManager awm = AppWidgetManager.getInstance(context);

        if (ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                final int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
                DeckLog.verbose(ACTION_APPWIDGET_UPDATE, "for", UpcomingWidget.class.getSimpleName(), "with id", appWidgetId, "→ perform update.");
                updateAppWidget(executor, context, awm, new int[]{appWidgetId});
            } else {
                DeckLog.verbose(ACTION_APPWIDGET_UPDATE, "→ Triggering update for all widgets of type", UpcomingWidget.class.getSimpleName());
                updateAppWidget(executor, context, awm, awm.getAppWidgetIds(new ComponentName(context, UpcomingWidget.class)));
            }
        } else if (PENDING_INTENT_ACTION_EDIT.equals(intent.getAction())) {
            if (intent.hasExtra(PENDING_INTENT_PARAM_ACCOUNT_ID) && intent.hasExtra(PENDING_INTENT_PARAM_LOCAL_CARD_ID)) {
                executor.submit(() -> {
                    final SyncManager syncManager = new SyncManager(context);
                    context.startActivity(EditActivity.createEditCardIntent(context, syncManager.readAccountDirectly(intent.getLongExtra(PENDING_INTENT_PARAM_ACCOUNT_ID, -1)), syncManager.getBoardLocalIdByLocalCardIdDirectly(intent.getLongExtra(PENDING_INTENT_PARAM_LOCAL_CARD_ID, -1)), intent.getLongExtra(PENDING_INTENT_PARAM_LOCAL_CARD_ID, -1)));
                });
            } else {
                DeckLog.error(PENDING_INTENT_PARAM_ACCOUNT_ID, "and", PENDING_INTENT_PARAM_LOCAL_CARD_ID, "must be provided for action", PENDING_INTENT_ACTION_EDIT);
            }
        } else if (PENDING_INTENT_ACTION_OPEN.equals(intent.getAction())) {
            context.startActivity(context.getPackageManager().getLaunchIntentForPackage(BuildConfig.APPLICATION_ID));
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        final SyncManager syncManager = new SyncManager(context);

        for (int appWidgetId : appWidgetIds) {
            DeckLog.info("Delete", UpcomingWidget.class.getSimpleName(), "with id", appWidgetId);
            syncManager.deleteFilterWidget(appWidgetId, response -> DeckLog.verbose("Successfully deleted " + UpcomingWidget.class.getSimpleName() + " with id " + appWidgetId));
        }
    }

    private static void updateAppWidget(@NonNull ExecutorService executor, @NonNull Context context, @NonNull AppWidgetManager awm, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            executor.submit(() -> {
                final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_upcoming);

                final Intent serviceIntent = new Intent(context, UpcomingWidgetService.class);
                serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

                final PendingIntent templatePI = PendingIntent.getBroadcast(context, appWidgetId, new Intent(context, UpcomingWidget.class), PendingIntent.FLAG_UPDATE_CURRENT);

                views.setPendingIntentTemplate(R.id.upcoming_widget_lv, templatePI);
                views.setRemoteAdapter(R.id.upcoming_widget_lv, serviceIntent);
                views.setEmptyView(R.id.upcoming_widget_lv, R.id.widget_upcoming_placeholder_iv);

                awm.notifyAppWidgetViewDataChanged(appWidgetId, R.id.upcoming_widget_lv);
                awm.updateAppWidget(appWidgetId, views);
            });
        }
    }

    static Intent fillEditPendingIntent(long accountId, long localCardId) {
        return new Intent()
                .setAction(PENDING_INTENT_ACTION_EDIT)
                .putExtra(PENDING_INTENT_PARAM_ACCOUNT_ID, accountId)
                .putExtra(PENDING_INTENT_PARAM_LOCAL_CARD_ID, localCardId);
    }

    static Intent fillOpenPendingIntent() {
        return new Intent()
                .setAction(PENDING_INTENT_ACTION_OPEN);
    }
}
