package it.niedermann.nextcloud.deck.ui.theme;

import static com.nextcloud.android.common.ui.util.ColorStateListUtilsKt.buildColorStateList;
import static com.nextcloud.android.common.ui.util.PlatformThemeUtil.isDarkMode;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.TextViewCompat;

import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.android.material.tabs.TabLayout;
import com.nextcloud.android.common.ui.theme.MaterialSchemes;
import com.nextcloud.android.common.ui.theme.ViewThemeUtilsBase;
import com.nextcloud.android.common.ui.theme.utils.AndroidViewThemeUtils;
import com.nextcloud.android.common.ui.theme.utils.ColorRole;
import com.nextcloud.android.common.ui.theme.utils.MaterialViewThemeUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.ui.view.EmptyContentView;
import kotlin.Pair;

/**
 * UI Elements which are not yet supported by the <a href="https://github.com/nextcloud/android-common"><code>android-commons</code></a> library.
 * Ideally there should at least be one Pull Request for Upstream for each method here.
 */
public class DeckViewThemeUtils extends ViewThemeUtilsBase {

    private final AndroidViewThemeUtils platform;
    private final MaterialViewThemeUtils material;

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
     * Themes the <code>tabLayout</code> using {@link MaterialViewThemeUtils#themeTabLayout(TabLayout)}
     * and then applies <code>null</code> as {@link TabLayout#setBackground(Drawable)}.
     */
    public void themeTabLayoutOnTransparent(@NonNull TabLayout tabLayout) {
        this.material.themeTabLayout(tabLayout);
        tabLayout.setBackground(null);
    }

    public void themeSearchBar(@NonNull SearchBar searchBar) {
        withScheme(searchBar.getContext(), scheme -> {
            final var colorStateList = ColorStateList.valueOf(
                    isDarkMode(searchBar.getContext())
                            ? scheme.getSurface()
                            : scheme.getSurfaceVariant());

            searchBar.setBackgroundTintList(colorStateList);

            final var menu = searchBar.getMenu();
            for (int i = 0; i < menu.size(); i++) {
                if (menu.getItem(i).getItemId() != R.id.avatar) {
                    platform.colorToolbarMenuIcon(searchBar.getContext(), menu.getItem(i));
                }
            }

            return searchBar;
        });
    }

    public void themeEmptyContentView(@NonNull EmptyContentView emptyContentView) {
        withScheme(emptyContentView.getContext(), scheme -> {
            platform.colorImageView(emptyContentView.getImage(), ColorRole.SURFACE_VARIANT);
            platform.colorTextView(emptyContentView.getTitle(), ColorRole.ON_BACKGROUND);
            platform.colorTextView(emptyContentView.getDescription(), ColorRole.ON_BACKGROUND);
            return emptyContentView;
        });
    }

    public void themeSearchView(@NonNull SearchView searchView) {
        withScheme(searchView.getContext(), scheme -> {
            searchView.setBackgroundTintList(ColorStateList.valueOf(scheme.getSurface()));
            return searchView;
        });
    }

    public void colorTextViewCompoundDrawables(@NonNull TextView textView) {
        withScheme(textView.getContext(), scheme -> {
            TextViewCompat.setCompoundDrawableTintList(textView, ColorStateList.valueOf(scheme.getOnSurfaceVariant()));
            return textView;
        });
    }

    public Drawable themeNavigationViewIcon(@NonNull Context context, @DrawableRes int icon) {
        return withScheme(context, scheme -> {
            final var colorStateList = buildColorStateList(
                    new Pair<>(android.R.attr.state_checked, scheme.getOnSecondaryContainer()),
                    new Pair<>(-android.R.attr.state_checked, scheme.getOnSurfaceVariant())
            );

            final var drawable = ContextCompat.getDrawable(context, icon);
            assert drawable != null;
            final var wrapped = DrawableCompat.wrap(drawable).mutate();
            DrawableCompat.setTintList(wrapped, colorStateList);
            wrapped.invalidateSelf();

            return wrapped;
        });
    }

    /**
     * There is currently no way to retrieve the actual color used for generating the current scheme.
     * Therefore we let pass it as argument.
     */
    @Nullable
    public Drawable getColoredBoardDrawable(@NonNull Context context, @ColorInt int boardColor) {
        final var drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.circle_36dp, null);
        return drawable == null ? null : platform.colorDrawable(drawable, boardColor);
    }

    /**
     * Use <strong>only</strong> for <code>@drawable/selected_check</code>
     */
    public void themeSelectedCheck(@NonNull Context context, @NonNull Drawable selectedCheck) {
        getStateDrawable(selectedCheck, android.R.attr.state_selected, R.id.foreground)
                .ifPresent(drawable -> platform.tintDrawable(context, drawable, ColorRole.ON_PRIMARY));
        getStateDrawable(selectedCheck, android.R.attr.state_selected, R.id.background)
                .ifPresent(drawable -> platform.tintDrawable(context, drawable, ColorRole.PRIMARY));
    }

    private Optional<Drawable> getStateDrawable(@NonNull Drawable drawable, @AttrRes int state, @IdRes int layerId) {
        return getStateDrawable(drawable, new int[]{state}, layerId);
    }

    private Optional<Drawable> getStateDrawable(@NonNull Drawable drawable, @AttrRes int[] states, @IdRes int layerId) {
        try {
            final var stateListDrawable = ((StateListDrawable) drawable);
            return findStateDrawableIndex(stateListDrawable, states)
                    .flatMap(stateIndex -> getStateDrawable(stateListDrawable, stateIndex))
                    .map(layerDrawable -> layerDrawable.findDrawableByLayerId(layerId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<Integer> findStateDrawableIndex(@NonNull StateListDrawable drawable, int[] states) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return Optional.of(drawable.findStateDrawableIndex(states));
        } else {
            try {
                // getStateDrawableIndex has been renamed and made public since API 29 / Android 10 / Android Q

                //noinspection JavaReflectionMemberAccess
                final var getStateDrawableIndex = StateListDrawable.class.getMethod("getStateDrawableIndex", int[].class);
                //noinspection PrimitiveArrayArgumentToVarargsMethod
                return Optional.ofNullable((Integer) getStateDrawableIndex.invoke(drawable, states));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                return Optional.empty();
            }
        }
    }

    private Optional<LayerDrawable> getStateDrawable(@NonNull StateListDrawable drawable, int index) {
        Drawable result;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            result = drawable.getStateDrawable(index);
        } else {
            try {
                result = (Drawable) StateListDrawable.class.getMethod("getStateDrawable", int.class).invoke(drawable, index);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                return Optional.empty();
            }
        }

        if (result instanceof LayerDrawable) {
            return Optional.of((LayerDrawable) result);
        }

        return Optional.empty();
    }

    @Deprecated(forRemoval = true)
    public static Drawable getTintedImageView(@NonNull Context context, @DrawableRes int imageId, @ColorInt int color) {
        final var drawable = ContextCompat.getDrawable(context, imageId);
        assert drawable != null;
        final var wrapped = DrawableCompat.wrap(drawable).mutate();
        DrawableCompat.setTint(wrapped, color);
        return drawable;
    }

    @Deprecated(forRemoval = true)
    public static void setImageColor(@NonNull Context context, @NonNull ImageView imageView, @ColorRes int colorRes) {
        imageView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)));
    }
}