package it.niedermann.nextcloud.deck.ui.widget.upcoming;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.enums.EDueType;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.widget.filter.dto.FilterWidgetCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;

import static java.time.temporal.ChronoUnit.DAYS;

public class UpcomingWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context context;
    private final int appWidgetId;

    @NonNull
    private final List<Object> data = new ArrayList<>();

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
                Collections.sort(response, (card1, card2) -> {
                    if (card1 == null || card1.getCard() == null || card1.getCard().getCard().getDueDate() == null) {
                        return -1;
                    }
                    if (card2 == null || card2.getCard() == null || card2.getCard().getCard().getDueDate() == null) {
                        return 1;
                    }
                    return card1.getCard().getCard().getDueDate().compareTo(card2.getCard().getCard().getDueDate()) * -1;
                });
                EDueType lastDueType = null;
                for (FilterWidgetCard filterWidgetCard : response) {
                    if (filterWidgetCard.getCard().getCard().getDueDate() != null) {
                        final EDueType nextDueType = getDueType(context, filterWidgetCard.getCard().getCard().getDueDate().atZone(ZoneId.systemDefault()).toLocalDate());
                        if (!nextDueType.equals(lastDueType)) {
                            data.add(new Separator(EDueType.OVERDUE.toString(context)));
                            lastDueType = nextDueType;
                        }
                    }
                    data.add(filterWidgetCard);
                }
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
        if (i > (data.size() - 1) || data.get(i) == null) {
            DeckLog.error("No card or separator not found at position " + i);
            return null;
        }
        final RemoteViews widget_entry;
        if (data.get(i).getClass() == Separator.class) {
            final Separator separator = (Separator) data.get(i);
            widget_entry = new RemoteViews(context.getPackageName(), R.layout.widget_separator);
            widget_entry.setTextViewText(R.id.widget_entry_content_tv, separator.title);
        } else {
            final FullCard card = ((FilterWidgetCard) data.get(i)).getCard();
            widget_entry = new RemoteViews(context.getPackageName(), R.layout.widget_stack_entry);
            widget_entry.setTextViewText(R.id.widget_entry_content_tv, card.getCard().getTitle());
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

    private void notifyAppWidgetUpdate(RemoteViews views) {
        AppWidgetManager awm = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = awm.getAppWidgetIds(new ComponentName(context, UpcomingWidget.class));
        awm.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.upcoming_widget_lv);
        awm.updateAppWidget(appWidgetId, views);
    }

    private static EDueType getDueType(@NonNull Context context, @Nullable LocalDate dueDate) {
        if (dueDate == null) {
            return EDueType.NO_DUE;
        }

        long diff = DAYS.between(LocalDate.now(), dueDate);

        if (diff > 7 && diff <= 30) {
            return EDueType.MONTH;
        } else if (diff > 0 && diff <= 7) {
            return EDueType.WEEK;
        } else if (diff == 0) {
            return EDueType.TODAY;
        } else if (diff < 0) {
            return EDueType.OVERDUE;
        }
        return EDueType.NO_FILTER;
    }

    private static class Separator {
        public String title;

        private Separator(String title) {
            this.title = title;
        }
    }
}
