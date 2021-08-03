package it.niedermann.nextcloud.deck.ui.branding;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import it.niedermann.nextcloud.deck.R;

public class DeleteAlertDialogBuilder extends AlertDialog.Builder {

    protected AlertDialog dialog;

    public DeleteAlertDialogBuilder(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public AlertDialog create() {
        this.dialog = super.create();
        applyBrand();
        dialog.setOnShowListener(dialog -> applyBrand());
        return dialog;
    }

    public void applyBrand() {
        final var positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (positiveButton != null) {
            positiveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.danger));
        }
    }
}
