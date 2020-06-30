package it.niedermann.nextcloud.deck.ui.branding;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.isBrandingEnabled;
import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.readBrandMainColor;

public abstract class BrandedDialogFragment extends DialogFragment implements Branded {

    @Override
    public void onStart() {
        super.onStart();

        @Nullable Context context = getContext();
        if (context != null) {
            if (isBrandingEnabled(context)) {
                applyBrand(readBrandMainColor(context));
            }
        }
    }
}
