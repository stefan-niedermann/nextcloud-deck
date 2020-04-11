package it.niedermann.nextcloud.deck.ui.branding;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import androidx.annotation.CallSuper;
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

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.M;
import static it.niedermann.nextcloud.deck.util.ColorUtil.contrastRatioIsSufficient;
import static it.niedermann.nextcloud.deck.util.ColorUtil.isColorDark;

public abstract class BrandedActivity extends AppCompatActivity implements Branded {

    /**
     * Member variable needed for onCreateOptionsMenu()-callback
     */
    @Nullable
    @ColorInt
    private Integer textColor = null;

    @Override
    protected void onResume() {
        super.onResume();
        Application.registerBrandedComponent(this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Application.deregisterBrandedComponent(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Application.deregisterBrandedComponent(this);
    }

    @CallSuper
    @Override
    public void applyBrand(@ColorInt int mainColor, @ColorInt int textColor) {
        this.textColor = textColor;
        if (SDK_INT >= LOLLIPOP) { // Set status bar color
            final Window window = getWindow();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (this.textColor != null) {
            for (int i = 0; i < menu.size(); i++) {
                Drawable drawable = menu.getItem(i).getIcon();
                if (drawable != null) {
                    drawable = DrawableCompat.wrap(drawable);
                    DrawableCompat.setTint(drawable, this.textColor);
                    menu.getItem(i).setIcon(drawable);
                }
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    protected static void applyBrandToPrimaryToolbar(@ColorInt int mainColor, @ColorInt int textColor, @NonNull Toolbar toolbar) {
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

    protected static void applyBrandToPrimaryTabLayout(@ColorInt int mainColor, @ColorInt int textColor, @NonNull TabLayout tabLayout) {
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
        final Drawable background = editText.getBackground();
        final ColorFilter oldColorFilter = DrawableCompat.getColorFilter(background);
        final View.OnFocusChangeListener oldOnFocusChangeListener = editText.getOnFocusChangeListener();

        @ColorInt final int finalMainColor = getColorDependingOnTheme(editText.getContext(), mainColor);

        final boolean isFocused = editText.isFocused();
        if (isFocused) {
            editText.clearFocus();
        }
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                background.setColorFilter(finalMainColor, PorterDuff.Mode.SRC_ATOP);
            } else {
                background.setColorFilter(oldColorFilter);
            }
            if (oldOnFocusChangeListener != null) {
                oldOnFocusChangeListener.onFocusChange(v, hasFocus);
            }
        });
        if (isFocused) {
            editText.requestFocus();
        }
    }

    /**
     * Since we may collide with dark theme in this area, we have to make sure that the color is visible depending on the background
     */
    @ColorInt
    public static int getColorDependingOnTheme(@NonNull Context context, @ColorInt int mainColor) {
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
