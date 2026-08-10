package it.niedermann.nextcloud.deck.util;

import android.app.PendingIntent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.ColorInt;

public class WidgetUtil {

    private WidgetUtil() {
        throw new UnsupportedOperationException("This class must not get instantiated");
    }

    /**
     * Android S requires either {@link PendingIntent#FLAG_MUTABLE} or
     * {@link PendingIntent#FLAG_IMMUTABLE} to be set on a {@link PendingIntent}.
     * This is enforced by Android and will lead to an app crash if neither of those flags is
     * present.
     * To keep the app working, this compatibility method can be used to add the
     * {@link PendingIntent#FLAG_MUTABLE} flag on Android S and higher to restore the behavior of
     * older SDK versions.
     *
     * @param flags wanted flags for {@link PendingIntent}
     * @return {@param flags} | {@link PendingIntent#FLAG_MUTABLE}
     */
    public static int pendingIntentFlagCompat(int flags) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return flags | PendingIntent.FLAG_MUTABLE;
        }
        return flags;
    }

    /**
     * Applies a custom background {@param color} to the view identified by {@param viewId}.
     * <p>
     * On Android S and higher, the color is applied as a background tint, which preserves the
     * shape (e.g. rounded corners) of the view's existing background drawable. On older versions,
     * {@link android.view.View#setBackgroundColor(int)} is used instead, which replaces the
     * background entirely and therefore loses any rounded corners.
     */
    public static void applyBackgroundColor(RemoteViews views, int viewId, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            views.setColorStateList(viewId, "setBackgroundTintList", ColorStateList.valueOf(color));
        } else {
            views.setInt(viewId, "setBackgroundColor", color);
        }
    }
}
