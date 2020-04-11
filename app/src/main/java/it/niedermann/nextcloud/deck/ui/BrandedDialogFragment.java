package it.niedermann.nextcloud.deck.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.Button;

import androidx.annotation.CallSuper;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import it.niedermann.nextcloud.deck.Application;

public class BrandedDialogFragment extends DialogFragment implements Application.Branded {

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

    @CallSuper
    @Override
    public void applyBrand(int mainColor, int textColor) {
        Dialog dialog = requireDialog();
        if (dialog instanceof AlertDialog) {
            AlertDialog alertDialog = (AlertDialog) dialog;
            Button[] buttons = new Button[3];
            buttons[0] = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            buttons[1] = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            buttons[2] = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
            for (Button button : buttons) {
                if (button != null) {
                    button.setTextColor(BrandedActivity.getColorDependingOnTheme(button.getContext(), mainColor));
                }
            }
        }
    }
}
