package it.niedermann.nextcloud.deck.ui.widget.upcoming;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.widget.filter.dto.FilterWidgetCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;

import static java.time.temporal.ChronoUnit.DAYS;

public class UpcomingWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context context;
    private final int appWidgetId;
    private final SyncManager syncManager;

    @NonNull
    private final List<Object> data = new ArrayList<>();

    UpcomingWidgetFactory(@NonNull Context context, Intent intent) {
        this.context = context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
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
            DeckLog.verbose(UpcomingWidgetFactory.class.getSimpleName() + " with id " + appWidgetId + " fetched " + response.size() + " cards from the database.");
            data.clear();
            Collections.sort(response, (card1, card2) -> {
                if (card1 == null || card1.getCard() == null || card1.getCard().getCard().getDueDate() == null) {
                    return 1;
                }
                if (card2 == null || card2.getCard() == null || card2.getCard().getCard().getDueDate() == null) {
                    return -1;
                }
                return card1.getCard().getCard().getDueDate().compareTo(card2.getCard().getCard().getDueDate());
            });
            EUpcomingDueType lastDueType = null;
            for (FilterWidgetCard filterWidgetCard : response) {
                final EUpcomingDueType nextDueType = getDueType(filterWidgetCard.getCard().getCard().getDueDate());
                DeckLog.info(filterWidgetCard.getCard().getCard().getTitle() + ": " + nextDueType.name());
                if (!nextDueType.equals(lastDueType)) {
                    data.add(new Separator(nextDueType.toString(context)));
                    lastDueType = nextDueType;
                }
                data.add(filterWidgetCard);
            }
        } catch (NoSuchElementException e) {
            DeckLog.error("No " + UpcomingWidget.class.getSimpleName() + " for appWidgetId " + appWidgetId + " found.");
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

            final Long localCardId = card.getCard().getLocalId();
            final Intent intent = EditActivity.createEditCardIntent(context, syncManager.readAccountDirectly(card.getAccountId()), syncManager.getBoardLocalIdByLocalCardIdDirectly(localCardId), localCardId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            widget_entry.setOnClickFillInIntent(R.id.widget_stack_entry, intent);
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

    @NonNull
    private static EUpcomingDueType getDueType(@Nullable Instant dueDate) {
        if (dueDate == null) {
            return EUpcomingDueType.NO_DUE;
        }

        long diff = DAYS.between(LocalDate.now(), dueDate.atZone(ZoneId.systemDefault()).toLocalDate());

        if (diff > 7) {
            return EUpcomingDueType.LATER;
        } else if (diff > 1) {
            return EUpcomingDueType.WEEK;
        } else if (diff > 0) {
            return EUpcomingDueType.TOMORROW;
        } else if (diff == 0) {
            return EUpcomingDueType.TODAY;
        } else {
            return EUpcomingDueType.OVERDUE;
        }
    }

    private enum EUpcomingDueType {
        OVERDUE(1, R.string.filter_overdue),
        TODAY(2, R.string.filter_today),
        TOMORROW(3, R.string.filter_tomorrow),
        WEEK(4, R.string.filter_week),
        LATER(5, R.string.filter_later),
        NO_DUE(6, R.string.filter_no_due);

        private final int value;
        private final int id;

        EUpcomingDueType(int id, @StringRes int value) {
            this.value = value;
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @NonNull
        public String toString(Context context) {
            return context.getString(this.value);
        }
    }

    private static class Separator {
        public String title;

        private Separator(String title) {
            this.title = title;
        }
    }
}
