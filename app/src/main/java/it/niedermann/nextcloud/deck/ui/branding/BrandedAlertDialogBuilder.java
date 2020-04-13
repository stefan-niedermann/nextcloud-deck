package it.niedermann.nextcloud.deck.ui.branding;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;

import androidx.annotation.CallSuper;
import androidx.appcompat.app.AlertDialog;

import org.jetbrains.annotations.NotNull;

import it.niedermann.nextcloud.deck.Application;

public class BrandedAlertDialogBuilder extends AlertDialog.Builder implements Branded {

    protected AlertDialog dialog;

    public BrandedAlertDialogBuilder(Context context) {
        super(context);
    }

    @NotNull
    @Override
    public AlertDialog create() {
        this.dialog = super.create();
        dialog.setOnShowListener(dialog -> Application.registerBrandedComponent(getContext(), this));
        return dialog;
    }

    @CallSuper
    @Override
    public void applyBrand(int mainColor, int textColor) {
        final Button[] buttons = new Button[3];
        buttons[0] = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttons[1] = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttons[2] = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        for (Button button : buttons) {
            if (button != null) {
                button.setTextColor(BrandedActivity.getColorDependingOnTheme(button.getContext(), mainColor));
            }
        }
        Application.deregisterBrandedComponent(this);
    }
}
