package it.niedermann.nextcloud.deck.ui.branding;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.isBrandingEnabled;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.readBrandMainColor;

public abstract class BrandedFragment extends Fragment implements Branded {

    @Override
    public void onStart() {
        super.onStart();

        @Nullable Context context = getContext();
        if (context != null && isBrandingEnabled(context)) {
            applyBrand(readBrandMainColor(context));
        }
    }
}
