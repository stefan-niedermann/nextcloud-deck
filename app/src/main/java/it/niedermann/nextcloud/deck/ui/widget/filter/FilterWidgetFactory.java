package it.niedermann.nextcloud.deck.ui.widget.filter;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.lifecycle.LiveData;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidgetData;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.widget.stack.StackWidget;

public class FilterWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context context;
    private final int appWidgetId;

    private FilterWidgetData data;

    FilterWidgetFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        final SyncManager syncManager = new SyncManager(context);

        LiveData<FilterWidgetData> filterWidgetData$ = syncManager.getFilterWidgetData(appWidgetId);
        filterWidgetData$.observeForever((data) -> {
            if (data != null) {
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_stack);
                this.data = data;
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
        return data == null ? 0 : data.getCards().size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews widget_entry;

        if (data.getCards() == null || i > (data.getCards().size() - 1) || data.getCards().get(i) == null) {
            DeckLog.error("Card not found at position " + i);
            return null;
        }

        FullCard card = data.getCards().get(i);

        widget_entry = new RemoteViews(context.getPackageName(), R.layout.widget_stack_entry);
        widget_entry.setTextViewText(R.id.widget_entry_content_tv, card.card.getTitle());

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
        int[] appWidgetIds = awm.getAppWidgetIds(new ComponentName(context, StackWidget.class));
        awm.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stack_widget_lv);
        awm.updateAppWidget(appWidgetId, views);
    }
}
