package it.niedermann.nextcloud.deck.deprecated.ui.stack;

import android.content.DialogInterface;

public interface EditStackListener extends DialogInterface.OnDismissListener {
    void onCreateStack(long accountId, long boardId, String title);

    void onUpdateStack(long stackId, String title);
}