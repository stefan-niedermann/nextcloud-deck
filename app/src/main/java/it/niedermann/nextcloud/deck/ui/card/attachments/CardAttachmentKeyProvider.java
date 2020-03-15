package it.niedermann.nextcloud.deck.ui.card.attachments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Attachment;

public class CardAttachmentKeyProvider extends ItemKeyProvider<Long> {
    private final List<Attachment> itemList;

    CardAttachmentKeyProvider(int scope, List<Attachment> itemList) {
        super(scope);
        this.itemList = itemList;
    }

    @Nullable
    @Override
    public Long getKey(int position) {
        return itemList.get(position).getLocalId();
    }

    @Override
    public int getPosition(@NonNull Long key) {
        for (int i = 0; i < itemList.size(); i++) {
            if (key.equals(itemList.get(i).getLocalId())) {
                return i;
            }
        }
        throw new IllegalArgumentException("Could not find an attachment with key " + key);
    }
}