package it.niedermann.nextcloud.deck.ui.branding;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.EditText;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;

import static it.niedermann.nextcloud.deck.util.ColorUtil.contrastRatioIsSufficient;
import static it.niedermann.nextcloud.deck.util.ColorUtil.getForegroundColorForBackgroundColor;

public abstract class BrandedActivity extends AppCompatActivity implements Branded {

    @ColorInt
    protected int colorAccent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        @ColorInt final int mainColor = Application.readBrandMainColor(this);
        setTheme(R.style.AppTheme);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        colorAccent = typedValue.data;

        if (Application.isBrandingEnabled(this)) {
            @ColorInt final int mainColor = Application.readBrandMainColor(this);
            applyBrand(mainColor);
        }
    }

    protected void applyBrandToPrimaryTabLayout(@ColorInt int mainColor, @NonNull TabLayout tabLayout) {
        @ColorInt int finalMainColor = getSecondaryForegroundColorDependingOnTheme(this, mainColor);
        tabLayout.setTabTextColors(finalMainColor, finalMainColor);
        tabLayout.setTabIconTint(ColorStateList.valueOf(finalMainColor));
        tabLayout.setSelectedTabIndicatorColor(finalMainColor);
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
