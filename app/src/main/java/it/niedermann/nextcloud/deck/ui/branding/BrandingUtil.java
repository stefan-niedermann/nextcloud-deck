package it.niedermann.nextcloud.deck.ui.branding;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;

import static it.niedermann.nextcloud.deck.util.ColorUtil.contrastRatioIsSufficient;
import static it.niedermann.nextcloud.deck.util.ColorUtil.contrastRatioIsSufficientBigAreas;
import static it.niedermann.nextcloud.deck.util.ColorUtil.getContrastRatio;
import static it.niedermann.nextcloud.deck.util.ColorUtil.getForegroundColorForBackgroundColor;

public abstract class BrandingUtil {

    private BrandingUtil() {
        // Util class
    }

    /**
     * Since we may collide with dark theme in this area, we have to make sure that the color is visible depending on the background
     */
    @ColorInt
    public static int getSecondaryForegroundColorDependingOnTheme(@NonNull Context context, @ColorInt int mainColor) {
        if (contrastRatioIsSufficient(mainColor, ContextCompat.getColor(context, R.color.primary))) {
            return mainColor;
        }
        DeckLog.verbose("Contrast ratio between brand color " + String.format("#%06X", (0xFFFFFF & mainColor)) + " and primary theme background is too low. Falling back to WHITE/BLACK as brand color.");
        return Application.isDarkTheme(context) ? Color.WHITE : Color.BLACK;
    }

    public static void applyBrandToFAB(@ColorInt int mainColor, @NonNull FloatingActionButton fab) {
        final boolean contrastRatioIsSufficient = contrastRatioIsSufficientBigAreas(mainColor, ContextCompat.getColor(fab.getContext(), R.color.primary));
        fab.setSupportBackgroundTintList(ColorStateList.valueOf(contrastRatioIsSufficient
                ? mainColor
                : ContextCompat.getColor(fab.getContext(), R.color.accent)));
        fab.setColorFilter(contrastRatioIsSufficient ? getForegroundColorForBackgroundColor(mainColor) : mainColor);
    }

    public static void applyBrandToPrimaryTabLayout(@ColorInt int mainColor, @NonNull TabLayout tabLayout) {
        @ColorInt int finalMainColor = getSecondaryForegroundColorDependingOnTheme(tabLayout.getContext(), mainColor);
        tabLayout.setBackgroundColor(ContextCompat.getColor(tabLayout.getContext(), R.color.primary));
        final boolean contrastRatioIsSufficient = getContrastRatio(mainColor, ContextCompat.getColor(tabLayout.getContext(), R.color.primary)) > 1.7d;
        tabLayout.setSelectedTabIndicatorColor(contrastRatioIsSufficient ? mainColor : finalMainColor);
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

    public static void tintMenuIcon(@NonNull MenuItem menuItem, @ColorInt int color) {
        Drawable drawable = menuItem.getIcon();
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, color);
            menuItem.setIcon(drawable);
        }
    }
}
