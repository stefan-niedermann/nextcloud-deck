package it.niedermann.nextcloud.deck.ui.branding;

import androidx.annotation.ColorInt;
import androidx.annotation.UiThread;

public interface Branded {
    @UiThread
    void applyBrand(@ColorInt int mainColor);
}