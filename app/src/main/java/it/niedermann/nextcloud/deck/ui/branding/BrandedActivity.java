package it.niedermann.nextcloud.deck.ui.branding;

import android.util.TypedValue;
import android.view.Menu;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import it.niedermann.nextcloud.deck.R;

import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.isBrandingEnabled;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.readBrandMainColor;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.tintMenuIcon;

public abstract class BrandedActivity extends AppCompatActivity implements Branded {

    @ColorInt
    protected int colorAccent;

    @Override
    protected void onStart() {
        super.onStart();

        final TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        colorAccent = typedValue.data;

        if (isBrandingEnabled(this)) {
            @ColorInt final int mainColor = readBrandMainColor(this);
            applyBrand(mainColor);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            tintMenuIcon(menu.getItem(i), colorAccent);
        }
        return super.onCreateOptionsMenu(menu);
    }
}
