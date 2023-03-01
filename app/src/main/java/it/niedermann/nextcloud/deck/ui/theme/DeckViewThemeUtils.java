package it.niedermann.nextcloud.deck.ui.theme;

import static com.nextcloud.android.common.ui.util.ColorStateListUtilsKt.buildColorStateList;
import static java.time.temporal.ChronoUnit.DAYS;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.TextViewCompat;

import com.google.android.material.tabs.TabLayout;
import com.nextcloud.android.common.ui.theme.MaterialSchemes;
import com.nextcloud.android.common.ui.theme.ViewThemeUtilsBase;
import com.nextcloud.android.common.ui.theme.utils.AndroidViewThemeUtils;
import com.nextcloud.android.common.ui.theme.utils.ColorRole;
import com.nextcloud.android.common.ui.theme.utils.MaterialViewThemeUtils;

import java.time.LocalDate;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
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

    public Drawable themeNavigationViewIcon(@NonNull Context context, @DrawableRes int icon) {
        return withScheme(context, scheme -> {
            final var colorStateListe = buildColorStateList(
                    new Pair<>(android.R.attr.state_checked, scheme.getOnSecondaryContainer()),
                    new Pair<>(-android.R.attr.state_checked, scheme.getOnSurfaceVariant())
            );

            final var drawable = ContextCompat.getDrawable(context, icon);
            assert drawable != null;
            final var wrapped = DrawableCompat.wrap(drawable).mutate();
            DrawableCompat.setTintList(wrapped, colorStateListe);
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
        final var drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.circle_grey600_36dp, null);
        return drawable == null ? null : platform.colorDrawable(drawable, boardColor);
    }

    /**
     * Use <strong>only</strong> for <code>@drawable/selected_check</code>
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void colorSelectedCheck(@NonNull Context context, @NonNull Drawable selectedCheck) {
        try {
            final var check = ((StateListDrawable) selectedCheck);
            final var checkSelectedIndex = check.findStateDrawableIndex(new int[]{android.R.attr.state_selected});
            final var checkSelectedDrawable = check.getStateDrawable(checkSelectedIndex);

            final var backgroundDrawable = ((LayerDrawable) checkSelectedDrawable).findDrawableByLayerId(R.id.background);
            final var foregroundDrawable = ((LayerDrawable) checkSelectedDrawable).findDrawableByLayerId(R.id.foreground);
            platform.tintDrawable(context, backgroundDrawable, ColorRole.PRIMARY);
            platform.tintDrawable(context, foregroundDrawable, ColorRole.ON_PRIMARY);
        } catch (Exception e) {
            DeckLog.error(e);
        }
    }

    @Deprecated(forRemoval = true)
    public static void themeDueDate(@NonNull TextView cardDueDate, @NonNull LocalDate dueDate) {
        final var context = cardDueDate.getContext();
        final long diff = DAYS.between(LocalDate.now(), dueDate);

        @ColorInt @Nullable Integer textColor = null;
        @DrawableRes int backgroundDrawable = 0;

        if (diff == 1) {
            // due date: tomorrow
            backgroundDrawable = R.drawable.due_tomorrow_background;
            textColor = ContextCompat.getColor(context, R.color.due_text_tomorrow);
        } else if (diff == 0) {
            // due date: today
            backgroundDrawable = R.drawable.due_today_background;
            textColor = ContextCompat.getColor(context, R.color.due_text_today);
        } else if (diff < 0) {
            // due date: overdue
            backgroundDrawable = R.drawable.due_overdue_background;
            textColor = ContextCompat.getColor(context, R.color.due_text_overdue);
        }

        cardDueDate.setBackgroundResource(backgroundDrawable);
        if (textColor != null) {
            cardDueDate.setTextColor(textColor);
            TextViewCompat.setCompoundDrawableTintList(cardDueDate, ColorStateList.valueOf(textColor));
        }
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