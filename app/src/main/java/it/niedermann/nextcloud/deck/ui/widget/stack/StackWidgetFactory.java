package it.niedermann.nextcloud.deck.ui.widget.stack;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.widget.filter.dto.FilterWidgetCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;

public class StackWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context context;
    private final int appWidgetId;
    private final SyncManager syncManager;

    @NonNull
    private final List<FilterWidgetCard> data = new ArrayList<>();

    StackWidgetFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        this.syncManager = new SyncManager(context);
    }

    @Override
    public void onCreate() {
        // Nothing to do here...
    }

    @Override
    public void onDataSetChanged() {
        try {
            final List<FilterWidgetCard> response = syncManager.getCardsForFilterWidget(appWidgetId);
            DeckLog.verbose(StackWidget.class.getSimpleName(), "with id", appWidgetId, "fetched", response.size(), "cards from the database.");
            data.clear();
            Collections.sort(response, Comparator.comparingLong(value -> value.getCard().getCard().getOrder()));
            data.addAll(response);
        } catch (NoSuchElementException e) {
            DeckLog.error("No", StackWidget.class.getSimpleName(), "for appWidgetId", appWidgetId, "found.");
            DeckLog.logError(e);
        }
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
        if (i > (data.size() - 1) || data.get(i) == null) {
            DeckLog.error("No card or separator not found at position", i);
            return null;
        }
        final RemoteViews widget_entry;
        final FilterWidgetCard filterWidgetCard = data.get(i);

        widget_entry = new RemoteViews(context.getPackageName(), R.layout.widget_stack_entry);
        widget_entry.setTextViewText(R.id.widget_entry_content_tv, filterWidgetCard.getCard().getCard().getTitle());

        final Intent intent = EditActivity.createEditCardIntent(context,  syncManager.readAccountDirectly(filterWidgetCard.getCard().getAccountId()), filterWidgetCard.getStack().getBoardId(), filterWidgetCard.getCard().getLocalId());
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        widget_entry.setOnClickFillInIntent(R.id.widget_stack_entry, intent);

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
