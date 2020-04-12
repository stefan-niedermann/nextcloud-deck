package it.niedermann.nextcloud.deck.ui.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.Button;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.ui.branding.BrandedDialogFragment;

public class DeleteDialogFragment extends BrandedDialogFragment {

    @Override
    public void applyBrand(@ColorInt int mainColor, @ColorInt int textColor) {
        super.applyBrand(mainColor, textColor);
        final Dialog dialog = requireDialog();
        if (dialog instanceof AlertDialog) {
            final Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
            if (positiveButton != null) {
                positiveButton.setTextColor(getResources().getColor(R.color.danger));
            }
        }
    }

}
