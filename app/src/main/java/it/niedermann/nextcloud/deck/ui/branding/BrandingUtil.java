package it.niedermann.nextcloud.deck.ui.branding;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.widget.EditText;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;

import static it.niedermann.nextcloud.deck.util.ColorUtil.contrastRatioIsSufficient;
import static it.niedermann.nextcloud.deck.util.ColorUtil.getForegroundColorForBackgroundColor;

public abstract class BrandingUtil {

    private BrandingUtil() {
        // Util class
    }

    /**
     * Since we may collide with dark theme in this area, we have to make sure that the color is visible depending on the background
     */
    @ColorInt
    public static int
    getSecondaryForegroundColorDependingOnTheme(@NonNull Context context, @ColorInt int mainColor) {
        final boolean isDarkTheme = Application.isDarkTheme(context);
        if (isDarkTheme && !contrastRatioIsSufficient(mainColor, Color.BLACK)) {
            DeckLog.verbose("Contrast ratio between brand color " + String.format("#%06X", (0xFFFFFF & mainColor)) + " and dark theme is too low. Falling back to WHITE as brand color.");
            return Color.WHITE;
        } else if (!contrastRatioIsSufficient(mainColor, Color.WHITE)) {
            DeckLog.verbose("Contrast ratio between brand color " + String.format("#%06X", (0xFFFFFF & mainColor)) + " and light theme is too low. Falling back to BLACK as brand color.");
            return Color.BLACK;
        } else {
            return mainColor;
        }
    }

    public static void applyBrandToFAB(@ColorInt int mainColor, @NonNull FloatingActionButton fab) {
        fab.setSupportBackgroundTintList(ColorStateList.valueOf(mainColor));
        fab.setColorFilter(getForegroundColorForBackgroundColor(mainColor));
    }

    public static void applyBrandToEditText(@ColorInt int mainColor, @NonNull EditText editText) {
        @ColorInt final int finalMainColor = getSecondaryForegroundColorDependingOnTheme(editText.getContext(), mainColor);
        DrawableCompat.setTintList(editText.getBackground(), new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_active},
                        new int[]{android.R.attr.state_activated},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{}
                },
                new int[]{
                        finalMainColor,
                        finalMainColor,
                        finalMainColor,
                        finalMainColor,
                        editText.getContext().getResources().getColor(R.color.fg_secondary)
                }
        ));
    }
}
