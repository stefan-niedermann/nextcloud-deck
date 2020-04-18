package it.niedermann.nextcloud.deck.util;

import android.content.Context;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;

public final class DimensionUtil {
    private DimensionUtil() {
    }

    public static int dpToPx(@NonNull Context context, @DimenRes int resource) {
        return context.getResources().getDimensionPixelSize(resource);
    }
}
