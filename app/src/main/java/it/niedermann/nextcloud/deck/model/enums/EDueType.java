package it.niedermann.nextcloud.deck.model.enums;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import it.niedermann.nextcloud.deck.R;

public enum EDueType {
    NO_FILTER(R.string.filter_no_filter),
    OVERDUE(R.string.filter_overdue),
    TODAY(R.string.filter_today),
    WEEK(R.string.filter_week),
    MONTH(R.string.filter_month),
    NO_DUE(R.string.filter_no_due);

    private int value;

    EDueType(@StringRes int value) {
        this.value = value;
    }

    @NonNull
    public String toString(Context context) {
        return context.getString(this.value);
    }
}
