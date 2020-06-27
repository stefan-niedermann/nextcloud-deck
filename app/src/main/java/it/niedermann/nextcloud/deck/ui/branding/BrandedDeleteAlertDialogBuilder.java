package it.niedermann.nextcloud.deck.ui.branding;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;

import androidx.annotation.CallSuper;

import it.niedermann.nextcloud.deck.R;

public class BrandedDeleteAlertDialogBuilder extends BrandedAlertDialogBuilder {

    public BrandedDeleteAlertDialogBuilder(Context context) {
        super(context);
    }

    @CallSuper
    @Override
    public void applyBrand(int mainColor) {
        super.applyBrand(mainColor);
        final Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (positiveButton != null) {
            positiveButton.setTextColor(getContext().getResources().getColor(R.color.danger));
        }
    }
}
