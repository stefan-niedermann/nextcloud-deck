package it.niedermann.nextcloud.deck.ui.widget.upcoming;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.widget.filter.dto.FilterWidgetCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

public class UpcomingWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context context;
    private final int appWidgetId;

    @NonNull
    private final List<FilterWidgetCard> data = new ArrayList<>();

    UpcomingWidgetFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        final SyncManager syncManager = new SyncManager(context);

        syncManager.getCardsForFilterWidget(appWidgetId, new IResponseCallback<List<FilterWidgetCard>>(null) {
            @Override
            public void onResponse(List<FilterWidgetCard> response) {
                data.clear();
                data.addAll(response);
                final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_upcoming);
                notifyAppWidgetUpdate(views);
            }
        });
    }

    @Override
    public void onDataSetChanged() {

    }


    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews widget_entry;

        if (i > (data.size() - 1) || data.get(i) == null) {
            DeckLog.error("Card not found at position " + i);
            return null;
        }

        FullCard card = data.get(i).getCard();

        widget_entry = new RemoteViews(context.getPackageName(), R.layout.widget_stack_entry);
        widget_entry.setTextViewText(R.id.widget_entry_content_tv, card.getCard().getTitle());

        return widget_entry;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void notifyAppWidgetUpdate(RemoteViews views) {
        AppWidgetManager awm = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = awm.getAppWidgetIds(new ComponentName(context, UpcomingWidget.class));
        awm.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.upcoming_widget_lv);
        awm.updateAppWidget(appWidgetId, views);
    }
}
