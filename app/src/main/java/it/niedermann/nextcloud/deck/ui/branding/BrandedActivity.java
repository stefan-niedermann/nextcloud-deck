package it.niedermann.nextcloud.deck.ui.branding;

import android.content.res.ColorStateList;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.tabs.TabLayout;

import it.niedermann.nextcloud.deck.Application;
import it.niedermann.nextcloud.deck.R;

import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.getSecondaryForegroundColorDependingOnTheme;

public abstract class BrandedActivity extends AppCompatActivity implements Branded {

    @ColorInt
    protected int colorAccent;

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
        tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
//        tabLayout.setTabTextColors(finalMainColor, finalMainColor);
        tabLayout.setTabIconTint(ColorStateList.valueOf(finalMainColor));
        tabLayout.setSelectedTabIndicatorColor(finalMainColor);
    }
}
