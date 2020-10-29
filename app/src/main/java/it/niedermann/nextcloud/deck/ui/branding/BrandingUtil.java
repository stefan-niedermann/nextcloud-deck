package it.niedermann.nextcloud.deck.ui.branding;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import it.niedermann.android.util.ColorUtil;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;

import static it.niedermann.nextcloud.deck.DeckApplication.isDarkTheme;
import static it.niedermann.nextcloud.deck.util.DeckColorUtil.contrastRatioIsSufficient;
import static it.niedermann.nextcloud.deck.util.DeckColorUtil.contrastRatioIsSufficientBigAreas;

public abstract class BrandingUtil {

    private BrandingUtil() {
        // Util class
    }

    public static boolean isBrandingEnabled(@NonNull Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_key_branding), true);
    }

    @ColorInt
    public static int readBrandMainColor(@NonNull Context context) {
        if (isBrandingEnabled(context)) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            DeckLog.log("--- Read: shared_preference_theme_main");
            return sharedPreferences.getInt(context.getString(R.string.shared_preference_theme_main), context.getApplicationContext().getResources().getColor(R.color.defaultBrand));
        } else {
            return ContextCompat.getColor(context, R.color.defaultBrand);
        }
    }

    public static void saveBrandColors(@NonNull Context context, @ColorInt int mainColor) {
        if (isBrandingEnabled(context) && context instanceof BrandedActivity) {
            final BrandedActivity activity = (BrandedActivity) context;
            activity.applyBrand(mainColor);
        }
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Write: shared_preference_theme_main" + " | " + mainColor);
        editor.putInt(context.getString(R.string.shared_preference_theme_main), mainColor);
        editor.apply();
    }

    public static void clearBrandColors(@NonNull Context context) {
        if (isBrandingEnabled(context) && context instanceof BrandedActivity) {
            final BrandedActivity activity = (BrandedActivity) context;
            activity.applyBrand(ContextCompat.getColor(context, R.color.defaultBrand));
        }
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Write: Remove: shared_preference_theme_main" + " | ");
        editor.remove(context.getString(R.string.shared_preference_theme_main));
        editor.apply();
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
        return isDarkTheme(context) ? Color.WHITE : Color.BLACK;
    }

    public static void applyBrandToFAB(@ColorInt int mainColor, @NonNull FloatingActionButton fab) {
        final boolean contrastRatioIsSufficient = contrastRatioIsSufficientBigAreas(mainColor, ContextCompat.getColor(fab.getContext(), R.color.primary));
        fab.setSupportBackgroundTintList(ColorStateList.valueOf(contrastRatioIsSufficient
                ? mainColor
                : ContextCompat.getColor(fab.getContext(), R.color.accent)));
        fab.setColorFilter(contrastRatioIsSufficient ? ColorUtil.INSTANCE.getForegroundColorForBackgroundColor(mainColor) : mainColor);
    }

    public static void applyBrandToPrimaryTabLayout(@ColorInt int mainColor, @NonNull TabLayout tabLayout) {
        @ColorInt final int finalMainColor = getSecondaryForegroundColorDependingOnTheme(tabLayout.getContext(), mainColor);
        tabLayout.setBackgroundColor(ContextCompat.getColor(tabLayout.getContext(), R.color.primary));
        final boolean contrastRatioIsSufficient = ColorUtil.INSTANCE.getContrastRatio(mainColor, ContextCompat.getColor(tabLayout.getContext(), R.color.primary)) > 1.7d;
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
                        ContextCompat.getColor(editText.getContext(), R.color.fg_secondary)
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
