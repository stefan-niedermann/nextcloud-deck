package it.niedermann.nextcloud.deck.ui.branding;

import static com.nextcloud.android.common.ui.util.ColorStateListUtilsKt.buildColorStateList;
import static it.niedermann.nextcloud.deck.DeckApplication.isDarkTheme;
import static it.niedermann.nextcloud.deck.util.DeckColorUtil.contrastRatioIsSufficient;

import android.content.Context;
import android.graphics.Color;
import android.view.MenuItem;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.nextcloud.android.common.ui.color.ColorUtil;
import com.nextcloud.android.common.ui.theme.MaterialSchemes;
import com.nextcloud.android.common.ui.theme.ViewThemeUtilsBase;
import com.nextcloud.android.common.ui.theme.utils.AndroidViewThemeUtils;
import com.nextcloud.android.common.ui.theme.utils.AndroidXViewThemeUtils;
import com.nextcloud.android.common.ui.theme.utils.DialogViewThemeUtils;
import com.nextcloud.android.common.ui.theme.utils.MaterialViewThemeUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import kotlin.Pair;

public class ViewThemeUtils extends ViewThemeUtilsBase {

    private static final ConcurrentMap<Integer, ViewThemeUtils> CACHE = new ConcurrentHashMap<>();

    public final AndroidViewThemeUtils platform;
    public final MaterialViewThemeUtils material;
    public final AndroidXViewThemeUtils androidx;
    public final DialogViewThemeUtils dialog;
    public final DeckViewThemeUtils deck;

    private ViewThemeUtils(
            final MaterialSchemes schemes,
            final ColorUtil colorUtil
    ) {
        super(schemes);

        this.platform = new AndroidViewThemeUtils(schemes, colorUtil);
        this.material = new MaterialViewThemeUtils(schemes, colorUtil);
        this.androidx = new AndroidXViewThemeUtils(schemes, this.platform);
        this.dialog = new DialogViewThemeUtils(schemes);
        this.deck = new DeckViewThemeUtils(schemes);
    }

    public static ViewThemeUtils of(@ColorInt int color, @NonNull Context context) {
        return CACHE.computeIfAbsent(color, c -> new ViewThemeUtils(
                MaterialSchemes.Companion.fromColor(c),
                new ColorUtil(context)
        ));
    }

    @ColorInt
    public static int readBrandMainColor(@NonNull Context context) {
        final var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        DeckLog.log("--- Read:", context.getString(R.string.shared_preference_theme_main));
        return sharedPreferences.getInt(context.getString(R.string.shared_preference_theme_main), context.getApplicationContext().getResources().getColor(R.color.defaultBrand));
    }

    public static void saveBrandColors(@NonNull Context context, @ColorInt int mainColor) {
        final var editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Write:", context.getString(R.string.shared_preference_theme_main), "|", mainColor);
        editor.putInt(context.getString(R.string.shared_preference_theme_main), mainColor);
        editor.apply();
    }

    public static void clearBrandColors(@NonNull Context context) {
        final var editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Remove:", context.getString(R.string.shared_preference_theme_main));
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
        DeckLog.verbose("Contrast ratio between brand color", String.format("#%06X", (0xFFFFFF & mainColor)), "and primary theme background is too low. Falling back to WHITE/BLACK as brand color.");
        return isDarkTheme(context) ? Color.WHITE : Color.BLACK;
    }

    /**
     * UI Elements which are not yet supported by the <a href="https://github.com/nextcloud/android-common"><code>android-commons</code></a> library.
     * Ideally there should at least be one Pull Request for Upstream for each method here.
     */
    public static class DeckViewThemeUtils extends ViewThemeUtilsBase {

        public DeckViewThemeUtils(@NonNull MaterialSchemes schemes) {
            super(schemes);
        }

        /**
         * @param fab {@link ExtendedFloatingActionButton}
         * @see <a href="https://github.com/nextcloud/android-common/pull/68">Upstream Pull Request</a>
         */
        public void themeExtendedFAB(ExtendedFloatingActionButton fab) {
            withScheme(fab, scheme -> {
                fab.setBackgroundTintList(buildColorStateList(
                        new Pair<>(android.R.attr.state_enabled, scheme.getPrimaryContainer()),
                        new Pair<>(-android.R.attr.state_enabled, Color.GRAY)
                ));
                fab.setIconTint(buildColorStateList(
                        new Pair<>(android.R.attr.state_enabled, scheme.getOnPrimaryContainer()),
                        new Pair<>(-android.R.attr.state_enabled, Color.WHITE)
                ));
                return fab;
            });
        }

        /**
         * Applies the Primary color as background after applying {@link MaterialViewThemeUtils#themeTabLayout(TabLayout)}
         */
        public void themeTabLayout(@ColorInt int color, @NonNull TabLayout tabLayout) {
            of(color, tabLayout.getContext()).material.themeTabLayout(tabLayout);
            tabLayout.setBackgroundColor(ContextCompat.getColor(tabLayout.getContext(), R.color.primary));
        }

        /**
         * {@link AndroidViewThemeUtils#colorMenuItemIcon(int, MenuItem)} results in white icon on white background in light mode.
         */
        public void tintMenuIcon(@NonNull Context context, @NonNull MenuItem menuItem, @ColorInt int color) {
            // FIXME on light background does not work - maybe theme toolbar also?
            // of(color, context).platform.colorMenuItemText(context, menuItem);
            var drawable = menuItem.getIcon();
            if (drawable != null) {
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, color);
                menuItem.setIcon(drawable);
            }
        }
    }
}
