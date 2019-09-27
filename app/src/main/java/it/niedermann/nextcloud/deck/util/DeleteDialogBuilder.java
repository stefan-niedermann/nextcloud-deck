package it.niedermann.nextcloud.deck.util;

import android.app.AlertDialog;
import android.content.Context;

import it.niedermann.nextcloud.deck.R;

public class DeleteDialogBuilder extends AlertDialog.Builder {

    public DeleteDialogBuilder(Context context) {
        super(context, R.style.DeleteDialogTheme);
    }
}
