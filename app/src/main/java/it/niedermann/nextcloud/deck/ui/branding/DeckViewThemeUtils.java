package it.niedermann.nextcloud.deck.ui.branding;

import static com.nextcloud.android.common.ui.util.ColorStateListUtilsKt.buildColorStateList;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.MenuItem;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.nextcloud.android.common.ui.theme.MaterialSchemes;
import com.nextcloud.android.common.ui.theme.ViewThemeUtilsBase;
import com.nextcloud.android.common.ui.theme.utils.AndroidViewThemeUtils;
import com.nextcloud.android.common.ui.theme.utils.MaterialViewThemeUtils;

import kotlin.Pair;

/**
 * UI Elements which are not yet supported by the <a href="https://github.com/nextcloud/android-common"><code>android-commons</code></a> library.
 * Ideally there should at least be one Pull Request for Upstream for each method here.
 */
public class DeckViewThemeUtils extends ViewThemeUtilsBase {

    private final MaterialViewThemeUtils material;
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final AndroidViewThemeUtils platform;

    public DeckViewThemeUtils(
            @NonNull MaterialSchemes schemes,
            @NonNull MaterialViewThemeUtils material,
            @NonNull AndroidViewThemeUtils platform
    ) {
        super(schemes);
        this.material = material;
        this.platform = platform;
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
     * @param navigationView {@link NavigationView}
     * @see <a href="https://github.com/nextcloud/android-common/pull/69">Upstream Pull Request</a>
     */
    public void colorNavigationView(NavigationView navigationView) {
        withScheme(navigationView, scheme -> {
            if (navigationView.getItemBackground() != null) {
                navigationView.getItemBackground().setTintList(buildColorStateList(
                        new Pair<>(android.R.attr.state_checked, scheme.getSecondaryContainer()),
                        new Pair<>(-android.R.attr.state_checked, Color.TRANSPARENT)
                ));
            }
            // Fixes https://github.com/nextcloud/android-common/issues/66
            navigationView.getBackground().setTintList(ColorStateList.valueOf(scheme.getSurface()));

            final var colorStateList = buildColorStateList(
                    new Pair<>(android.R.attr.state_checked, scheme.getOnSecondaryContainer()),
                    new Pair<>(-android.R.attr.state_checked, scheme.getOnSurfaceVariant())
            );

            navigationView.setItemTextColor(colorStateList);
            // Fixes https://github.com/nextcloud/android-common/issues/64
            // navigationView.setItemIconTintList(colorStateList);
            return navigationView;
        });
    }

    /**
     * Applies the Primary color as background after applying {@link MaterialViewThemeUtils#themeTabLayout(TabLayout)}
     */
    public void themeTabLayout(@NonNull TabLayout tabLayout) {
        this.material.themeTabLayout(tabLayout);
        tabLayout.setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * {@link AndroidViewThemeUtils#colorMenuItemText(Context, MenuItem)} results in white icon on white background in light mode.
     */
    public void tintMenuIcon(@NonNull Context context, @NonNull MenuItem menuItem, @ColorInt int color) {
        // FIXME on light background does not work - maybe theme toolbar also?
        // this.platform.colorMenuItemText(context, menuItem);
        var drawable = menuItem.getIcon();
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, color);
            menuItem.setIcon(drawable);
        }
    }
}