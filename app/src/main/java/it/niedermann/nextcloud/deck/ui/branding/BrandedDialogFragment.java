package it.niedermann.nextcloud.deck.ui.branding;

import androidx.fragment.app.DialogFragment;

import it.niedermann.nextcloud.deck.Application;

public abstract class BrandedDialogFragment extends DialogFragment implements Branded {

    @Override
    public void onResume() {
        super.onResume();
        Application.registerBrandedComponent(requireContext(), this);
    }

    @Override
    public void onPause() {
        Application.deregisterBrandedComponent(this);
        super.onPause();
    }
}
