package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.os.Build;

import androidx.annotation.RequiresApi;

public interface CardAttachmentPickerListener {

    void pickCamera();
    void pickContact();
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void pickFile();
}
