package it.niedermann.nextcloud.deck.ui.branding;

import static it.niedermann.nextcloud.deck.DeckApplication.isDarkTheme;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.getAttribute;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.readBrandMainColor;
import static it.niedermann.nextcloud.deck.util.DeckColorUtil.contrastRatioIsSufficient;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import it.niedermann.nextcloud.deck.R;

public class BrandedSnackbar {

    @NonNull
    public static Snackbar make(@NonNull View view, @NonNull CharSequence text, @BaseTransientBottomBar.Duration int duration) {
        final var snackbar = Snackbar.make(view, text, duration);

        @ColorInt final int backgroundColor = getAttribute(view.getContext(), R.attr.colorSurfaceInverse);
        @ColorInt final int color = readBrandMainColor(view.getContext());

        if (contrastRatioIsSufficient(backgroundColor, color)) {
            snackbar.setActionTextColor(color);
        } else {
            if (isDarkTheme(view.getContext())) {
                snackbar.setActionTextColor(Color.BLACK);
            } else {
                snackbar.setActionTextColor(Color.WHITE);
            }
        }

        return snackbar;
    }

    @NonNull
    public static Snackbar make(@NonNull View view, @StringRes int resId, @BaseTransientBottomBar.Duration int duration) {
        return make(view, view.getResources().getText(resId), duration);
    }
}