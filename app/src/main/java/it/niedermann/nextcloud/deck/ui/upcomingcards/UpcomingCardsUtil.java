package it.niedermann.nextcloud.deck.ui.upcomingcards;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.Card;

import static java.time.temporal.ChronoUnit.DAYS;

public class UpcomingCardsUtil {

    private UpcomingCardsUtil() {
        throw new UnsupportedOperationException("This class must not get instantiated");
    }

    @NonNull
    public static EUpcomingDueType getDueType(@Nullable Instant dueDate) {
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

    /**
     * @return a {@link Collection} of the given {@param items}, sorted and separated by {@link UpcomingCardsAdapterSectionItem}.
     */
    public static Collection<Object> addDueDateSeparators(@NonNull Context context, @NonNull List<UpcomingCardsAdapterItem> items) {
        final Collection<Object> ret = new ArrayList<>();

        final Comparator<UpcomingCardsAdapterItem> comparator = Comparator.comparing((card -> {
            if (card != null &&
                    card.getFullCard() != null &&
                    card.getFullCard().getCard() != null &&
                    card.getFullCard().getCard().getDueDate() != null) {
                return card.getFullCard().getCard().getDueDate();
            }
            return null;
        }), Comparator.nullsLast(Comparator.naturalOrder()));

        comparator.thenComparing(card -> {
            if (card != null &&
                    card.getFullCard() != null &&
                    card.getFullCard().getCard().getDueDate() != null) {

                final Card c = card.getFullCard().getCard();

                if (c.getLastModified() == null && c.getLastModifiedLocal() != null) {
                    return c.getLastModifiedLocal();
                } else if (c.getLastModified() != null && c.getLastModifiedLocal() == null) {
                    return c.getLastModified();
                } else {
                    return c.getLastModifiedLocal().toEpochMilli() > c.getLastModified().toEpochMilli() ?
                            c.getLastModifiedLocal() : c.getLastModified();
                }
            }
            return null;
        }, Comparator.nullsLast(Comparator.naturalOrder()));

        Collections.sort(
                items,
                comparator
        );

        EUpcomingDueType lastDueType = null;
        for (UpcomingCardsAdapterItem filterWidgetCard : items) {
            final EUpcomingDueType nextDueType = getDueType(filterWidgetCard.getFullCard().getCard().getDueDate());
            DeckLog.info(filterWidgetCard.getFullCard().getCard().getTitle() + ":", nextDueType.name());
            if (!nextDueType.equals(lastDueType)) {
                ret.add(new UpcomingCardsAdapterSectionItem(nextDueType.toString(context)));
                lastDueType = nextDueType;
            }
            ret.add(filterWidgetCard);
        }

        return ret;
    }
}
