package it.niedermann.nextcloud.deck.ui.upcomingcards;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

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
}
