package it.niedermann.nextcloud.deck.ui.theme;

import static it.niedermann.nextcloud.deck.ui.theme.ThemeUtils.readBrandMainColor;

import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class ThemedSnackbar {

    @NonNull
    public static Snackbar make(@NonNull View view, @NonNull CharSequence text, @BaseTransientBottomBar.Duration int duration) {
        @ColorInt final int color = readBrandMainColor(view.getContext());
        final var snackbar = Snackbar.make(view, text, duration);
        final var utils = ThemeUtils.of(color, view.getContext());

        utils.material.themeSnackbar(snackbar);

        return snackbar;
    }

    @NonNull
    public static Snackbar make(@NonNull View view, @StringRes int resId, @BaseTransientBottomBar.Duration int duration) {
        return make(view, view.getResources().getText(resId), duration);
    }
}