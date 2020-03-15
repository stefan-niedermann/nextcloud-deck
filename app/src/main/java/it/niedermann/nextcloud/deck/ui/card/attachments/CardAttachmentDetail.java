package it.niedermann.nextcloud.deck.ui.card.attachments;

import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;

public class CardAttachmentDetail extends ItemDetailsLookup.ItemDetails<Long> {
    private final int adapterPosition;
    private final long selectionKey;

    CardAttachmentDetail(int adapterPosition, long selectionKey) {
        this.adapterPosition = adapterPosition;
        this.selectionKey = selectionKey;
    }

    @Override
    public int getPosition() {
        return adapterPosition;
    }

    @Nullable
    @Override
    public Long getSelectionKey() {
        return selectionKey;
    }
}