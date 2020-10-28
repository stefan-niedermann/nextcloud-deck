package it.niedermann.nextcloud.deck.ui.branding;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import it.niedermann.android.util.ColorUtil;
import it.niedermann.nextcloud.deck.R;

import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.isBrandingEnabled;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.readBrandMainColor;

public class BrandedSnackbar {

    @NonNull
    public static Snackbar make(
            @NonNull View view, @NonNull CharSequence text, @BaseTransientBottomBar.Duration int duration) {
        final Snackbar snackbar = Snackbar.make(view, text, duration);
        if (isBrandingEnabled(view.getContext())) {
            @ColorInt final int color = readBrandMainColor(view.getContext());
            snackbar.setActionTextColor(ColorUtil.INSTANCE.isColorDark(color) ? Color.WHITE : color);
        } else {
            snackbar.setActionTextColor(ContextCompat.getColor(view.getContext(), R.color.defaultBrand));
        }
        return snackbar;
    }

    @NonNull
    public static Snackbar make(@NonNull View view, @StringRes int resId, @BaseTransientBottomBar.Duration int duration) {
        return make(view, view.getResources().getText(resId), duration);
    }

}