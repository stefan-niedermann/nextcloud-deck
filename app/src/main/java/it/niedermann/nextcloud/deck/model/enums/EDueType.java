package it.niedermann.nextcloud.deck.model.enums;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import it.niedermann.nextcloud.deck.R;

public enum EDueType {
    NO_FILTER(1, R.string.filter_no_filter),
    OVERDUE(2, R.string.filter_overdue),
    TODAY(3, R.string.filter_today),
    WEEK(4, R.string.filter_week),
    MONTH(5, R.string.filter_month),
    NO_DUE(6, R.string.filter_no_due);

    private final int value;
    private final int id;

    EDueType(int id, @StringRes int value) {
        this.value = value;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static EDueType findById(int id) {
        for (EDueType s : EDueType.values()) {
            if (s.getId() == id) {
                return s;
            }
        }
        throw new IllegalArgumentException("unknown " + EDueType.class.getSimpleName() + " key: " + id);
    }

    @NonNull
    public String toString(Context context) {
        return context.getString(this.value);
    }
}
