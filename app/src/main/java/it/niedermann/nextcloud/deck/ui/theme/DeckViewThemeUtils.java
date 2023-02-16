package it.niedermann.nextcloud.deck.ui.theme;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.tabs.TabLayout;
import com.nextcloud.android.common.ui.theme.MaterialSchemes;
import com.nextcloud.android.common.ui.theme.ViewThemeUtilsBase;
import com.nextcloud.android.common.ui.theme.utils.MaterialViewThemeUtils;

import it.niedermann.nextcloud.deck.R;

/**
 * UI Elements which are not yet supported by the <a href="https://github.com/nextcloud/android-common"><code>android-commons</code></a> library.
 * Ideally there should at least be one Pull Request for Upstream for each method here.
 */
public class DeckViewThemeUtils extends ViewThemeUtilsBase {

    private final MaterialViewThemeUtils material;

    public DeckViewThemeUtils(
            @NonNull MaterialSchemes schemes,
            @NonNull MaterialViewThemeUtils material
    ) {
        super(schemes);
        this.material = material;
    }

    /**
     * Convenience method for calling {@link #themeTabLayout(TabLayout, int)} with the primary color
     */
    public void themeTabLayout(@NonNull TabLayout tabLayout) {
        themeTabLayout(tabLayout, ContextCompat.getColor(tabLayout.getContext(), R.color.primary));
    }

    /**
     * Themes the <code>tabLayout</code> using {@link MaterialViewThemeUtils#themeTabLayout(TabLayout)}
     * and then applies <code>backgroundColor</code>.
     */
    public void themeTabLayout(@NonNull TabLayout tabLayout, @ColorInt int backgroundColor) {
        this.material.themeTabLayout(tabLayout);
        tabLayout.setBackgroundColor(backgroundColor);
    }
}