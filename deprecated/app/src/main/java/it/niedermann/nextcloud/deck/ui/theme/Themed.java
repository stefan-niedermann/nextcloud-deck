package it.niedermann.nextcloud.deck.ui.theme;

import androidx.annotation.ColorInt;
import androidx.annotation.UiThread;

public interface Themed {
    @UiThread
    void applyTheme(@ColorInt int color);
}