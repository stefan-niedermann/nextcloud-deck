package it.niedermann.nextcloud.deck.ui.widget.filter;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.widget.filter.dto.FilterWidgetCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

public class FilterWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context context;
    private final int appWidgetId;
    private final SyncManager syncManager;

    @NonNull
    private final List<FilterWidgetCard> data = new ArrayList<>();

    FilterWidgetFactory(Context context, Intent intent) {
        this.context = context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        this.syncManager = new SyncManager(context);
    }

    @Override
    public void onCreate() {
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
            DeckLog.error("Card not found at position", i);
            return null;
        }

        FullCard card = data.get(i).getCard();

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
}
