package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import androidx.annotation.RequiresApi;

import static android.os.Build.VERSION_CODES.LOLLIPOP;

public interface CardAttachmentPickerListener {

    @RequiresApi(api = LOLLIPOP)
    void pickCamera();
    void pickContact();
    void pickFile();
}
