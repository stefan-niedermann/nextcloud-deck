package it.niedermann.nextcloud.deck.util;

import android.content.Context;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Px;

public final class DimensionUtil {
    private DimensionUtil() {
    }

    @Px
    public static int dpToPx(@NonNull Context context, @DimenRes int resource) {
        return context.getResources().getDimensionPixelSize(resource);
    }
}
