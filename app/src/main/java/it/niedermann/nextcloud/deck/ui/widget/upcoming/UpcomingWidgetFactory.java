package it.niedermann.nextcloud.deck.ui.widget.upcoming;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.widget.filter.FilterWidget;
import it.niedermann.nextcloud.deck.repository.BaseRepository;
import it.niedermann.nextcloud.deck.ui.upcomingcards.UpcomingCardsAdapterItem;
import it.niedermann.nextcloud.deck.ui.upcomingcards.UpcomingCardsAdapterSectionItem;
import it.niedermann.nextcloud.deck.ui.upcomingcards.UpcomingCardsUtil;
import it.niedermann.nextcloud.deck.util.WidgetUtil;

public class UpcomingWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context context;
    private final int appWidgetId;
    private final BaseRepository baseRepository;
    private final int headerHorizontalPadding;
    private final int headerVerticalPaddingNth;

    @Nullable
    private FilterWidget config;

    @NonNull
    private final List<Object> data = new ArrayList<>();

    UpcomingWidgetFactory(@NonNull Context context, Intent intent) {
        this.context = context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        this.baseRepository = new BaseRepository(context);

        final var resources = context.getResources();
        this.headerHorizontalPadding = resources.getDimensionPixelSize(R.dimen.spacer_1hx);
        this.headerVerticalPaddingNth = resources.getDimensionPixelSize(R.dimen.spacer_2x);
    }

    @Override
    public void onCreate() {
        // Nothing to do here...
    }

    @Override
    public void onDataSetChanged() {
        try {
            config = baseRepository.getFilterWidgetByIdDirectly(appWidgetId);
            final List<UpcomingCardsAdapterItem> response = baseRepository.getCardsForUpcomingCardsForWidget();
            DeckLog.verbose(UpcomingWidgetFactory.class.getSimpleName(), "with id", appWidgetId, "fetched", response.size(), "cards from the database.");
            data.clear();
            data.addAll(UpcomingCardsUtil.addDueDateSeparators(context, response));
        } catch (NoSuchElementException e) {
            DeckLog.error("No", UpcomingWidget.class.getSimpleName(), "for appWidgetId", appWidgetId, "found.");
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
        if (data.get(i).getClass() == UpcomingCardsAdapterSectionItem.class || data.get(i) instanceof UpcomingCardsAdapterSectionItem) {
            final UpcomingCardsAdapterSectionItem separator = (UpcomingCardsAdapterSectionItem) data.get(i);
            widget_entry = new RemoteViews(context.getPackageName(), R.layout.widget_separator);
            widget_entry.setTextViewText(R.id.widget_entry_content_tv, separator.getTitle());
            if (i == 0) {
                widget_entry.setViewPadding(R.id.widget_entry_content_tv, headerHorizontalPadding, 0, headerHorizontalPadding, 0);
            } else {
                widget_entry.setViewPadding(R.id.widget_entry_content_tv, headerHorizontalPadding, headerVerticalPaddingNth, headerHorizontalPadding, 0);
            }
            if (config != null && config.getSectionTextColor() != null) {
                widget_entry.setTextColor(R.id.widget_entry_content_tv, config.getSectionTextColor());
            }
            widget_entry.setOnClickFillInIntent(R.id.widget_stack_entry, UpcomingWidget.fillOpenPendingIntent());
        } else if (data.get(i).getClass() == UpcomingCardsAdapterItem.class || data.get(i) instanceof UpcomingCardsAdapterItem) {
            final FullCard card = ((UpcomingCardsAdapterItem) data.get(i)).getFullCard();
            widget_entry = new RemoteViews(context.getPackageName(), R.layout.widget_stack_entry);
            widget_entry.setTextViewText(R.id.widget_entry_content_tv, card.getCard().getTitle());
            if (config != null) {
                if (config.getEntryTextColor() != null) {
                    widget_entry.setTextColor(R.id.widget_entry_content_tv, config.getEntryTextColor());
                }
                if (config.getListBackgroundColor() != null) {
                    WidgetUtil.applyBackgroundColor(widget_entry, R.id.widget_stack_entry, config.getListBackgroundColor());
                }
            }
            widget_entry.setOnClickFillInIntent(R.id.widget_stack_entry, UpcomingWidget.fillEditPendingIntent(card.getAccountId(), card.getLocalId()));
        } else {
            DeckLog.logError(new IllegalStateException("Expected items to be instance of " + UpcomingCardsAdapterSectionItem.class.getSimpleName() + " or " + UpcomingCardsAdapterItem.class.getSimpleName()));
            return null;
        }
        return widget_entry;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
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
