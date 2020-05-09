package it.niedermann.nextcloud.deck.ui.branding;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.util.ColorUtil;

public class BrandedSnackbar {

    @NonNull
    public static Snackbar make(
            @NonNull View view, @NonNull CharSequence text, @BaseTransientBottomBar.Duration int duration) {
        final Snackbar snackbar = Snackbar.make(view, text, duration);
        if (Application.isBrandingEnabled(view.getContext())) {
            int color = Application.readBrandMainColor(view.getContext());
            snackbar.setActionTextColor(ColorUtil.isColorDark(color) ? Color.WHITE : color);
        }
        return snackbar;
    }

    @NonNull
    public static Snackbar make(@NonNull View view, @StringRes int resId, @BaseTransientBottomBar.Duration int duration) {
        return make(view, view.getResources().getText(resId), duration);
    }

}