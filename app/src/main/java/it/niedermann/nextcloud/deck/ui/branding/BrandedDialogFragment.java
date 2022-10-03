package it.niedermann.nextcloud.deck.ui.branding;

import static it.niedermann.nextcloud.deck.ui.branding.BrandingUtil.readBrandMainColor;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public abstract class BrandedDialogFragment extends DialogFragment implements Branded {

    @Override
    public void onStart() {
        super.onStart();

        @Nullable final var context = getContext();
        if (context != null) {
            applyBrand(readBrandMainColor(context));
        }
    }
}
