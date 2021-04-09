package it.niedermann.nextcloud.deck.ui.upcomingcards;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import it.niedermann.nextcloud.deck.R;

public enum EUpcomingDueType {
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