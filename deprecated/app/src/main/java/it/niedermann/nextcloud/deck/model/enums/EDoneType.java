package it.niedermann.nextcloud.deck.model.enums;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import it.niedermann.nextcloud.deck.R;

public enum EDoneType {
    NO_FILTER(1, R.string.filter_done_no_filter),
    DONE(2, R.string.filter_done_done),
    UNDONE(3, R.string.filter_done_undone);

    private final int value;
    private final int id;

    EDoneType(int id, @StringRes int value) {
        this.value = value;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static EDoneType findById(int id) {
        for (EDoneType s : EDoneType.values()) {
            if (s.getId() == id) {
                return s;
            }
        }
        throw new IllegalArgumentException("unknown " + EDoneType.class.getSimpleName() + " key: " + id);
    }

    @NonNull
    public String toString(Context context) {
        return context.getString(this.value);
    }
}
