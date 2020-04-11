package it.niedermann.nextcloud.deck.ui;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.view.Window;

import androidx.annotation.CallSuper;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.tabs.TabLayout;

import it.niedermann.nextcloud.deck.Application;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.M;
import static it.niedermann.nextcloud.deck.util.ColorUtil.isColorDark;

public abstract class AbstractThemableActivity extends AppCompatActivity implements Application.NextcloudTheme {

    /**
     * Member variable needed for onCreateOptionsMenu()-callback
     */
    @Nullable
    @ColorInt
    private Integer textColor = null;

    @Override
    protected void onResume() {
        super.onResume();
        Application.registerThemableComponent(this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Application.deregisterThemableComponent(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Application.deregisterThemableComponent(this);
    }

    @CallSuper
    @Override
    public void applyNextcloudTheme(@ColorInt int mainColor, @ColorInt int textColor) {
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

    protected void applyNextcloudThemeToToolbar(@ColorInt int mainColor, @ColorInt int textColor, @NonNull Toolbar toolbar) {
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

    protected void applyNextcloudThemeToTablayout(@ColorInt int mainColor, @ColorInt int textColor, @NonNull TabLayout tabLayout) {
        tabLayout.setBackgroundColor(mainColor);
        tabLayout.setTabTextColors(textColor, textColor);
        tabLayout.setTabIconTint(new ColorStateList(new int[][]{new int[]{}}, new int[]{textColor}));
        tabLayout.setSelectedTabIndicatorColor(textColor);
    }
}
