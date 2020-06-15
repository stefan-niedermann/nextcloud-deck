package it.niedermann.nextcloud.deck.ui.branding;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.util.ColorUtil;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.M;
import static it.niedermann.nextcloud.deck.util.ColorUtil.contrastRatioIsSufficient;
import static it.niedermann.nextcloud.deck.util.ColorUtil.isColorDark;

public abstract class BrandedActivity extends AppCompatActivity implements Branded {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Application.isBrandingEnabled(this)) {
            @ColorInt final int mainColor = Application.readBrandMainColor(this);
            @ColorInt final int textColor = Application.readBrandTextColor(this);
            setTheme(ColorUtil.isColorDark(textColor) ? R.style.AppThemeLightBrand : R.style.AppTheme);
            applyBrandToStatusbar(getWindow(), mainColor, textColor);
        } else {
            setTheme(R.style.AppTheme);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Application.isBrandingEnabled(this)) {
            @ColorInt final int mainColor = Application.readBrandMainColor(this);
            @ColorInt final int textColor = Application.readBrandTextColor(this);
            applyBrand(mainColor, textColor);
        }
    }

    // TODO maybe this can be handled in R.style.AppThemLightBrand
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        @ColorInt final int textColor = Application.readBrandTextColor(this);
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, textColor);
                menu.getItem(i).setIcon(drawable);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    public static void applyBrandToStatusbar(@NonNull Window window, @ColorInt int mainColor, @ColorInt int textColor) {
        if (SDK_INT >= LOLLIPOP) { // Set status bar color
            window.setStatusBarColor(mainColor);
            if (SDK_INT >= M) { // Set icon and text color of status bar
                final View decorView = window.getDecorView();
                if (isColorDark(mainColor)) {
                    int flags = decorView.getSystemUiVisibility();
                    flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    decorView.setSystemUiVisibility(flags);
                } else {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }
        }
    }

    public static void applyBrandToPrimaryToolbar(@ColorInt int mainColor, @ColorInt int textColor, @NonNull Toolbar toolbar) {
        toolbar.setBackgroundColor(mainColor);
        toolbar.setTitleTextColor(textColor);
        final Drawable overflowDrawable = toolbar.getOverflowIcon();
        if (overflowDrawable != null) {
            overflowDrawable.setColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
            toolbar.setOverflowIcon(overflowDrawable);
        }

        final Drawable navigationDrawable = toolbar.getNavigationIcon();
        if (navigationDrawable != null) {
            navigationDrawable.setColorFilter(textColor, PorterDuff.Mode.SRC_ATOP);
            toolbar.setNavigationIcon(navigationDrawable);
        }
    }

    protected void applyBrandToPrimaryTabLayout(@ColorInt int mainColor, @ColorInt int textColor, @NonNull TabLayout tabLayout) {
        tabLayout.setBackgroundColor(mainColor);
        tabLayout.setTabTextColors(textColor, textColor);
        tabLayout.setTabIconTint(ColorStateList.valueOf(textColor));
        tabLayout.setSelectedTabIndicatorColor(textColor);
    }

    public static void applyBrandToFAB(@ColorInt int mainColor, @ColorInt int textColor, @NonNull FloatingActionButton fab) {
        fab.setSupportBackgroundTintList(ColorStateList.valueOf(mainColor));
        fab.setColorFilter(textColor);
    }

    public static void applyBrandToEditText(@ColorInt int mainColor, @ColorInt int textColor, @NonNull EditText editText) {
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

    /**
     * Since we may collide with dark theme in this area, we have to make sure that the color is visible depending on the background
     */
    @ColorInt
    public static int
    getSecondaryForegroundColorDependingOnTheme(@NonNull Context context, @ColorInt int mainColor) {
        final boolean isDarkTheme = Application.getAppTheme(context);
        if (isDarkTheme && !contrastRatioIsSufficient(mainColor, Color.BLACK)) {
            DeckLog.verbose("Contrast ratio between brand color " + String.format("#%06X", (0xFFFFFF & mainColor)) + " and dark theme is too low. Falling back to WHITE as brand color.");
            return Color.WHITE;
        } else if (!isDarkTheme && !contrastRatioIsSufficient(mainColor, Color.WHITE)) {
            DeckLog.verbose("Contrast ratio between brand color " + String.format("#%06X", (0xFFFFFF & mainColor)) + " and light theme is too low. Falling back to BLACK as brand color.");
            return Color.BLACK;
        } else {
            return mainColor;
        }
    }
}
