package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.content.Context;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import it.niedermann.nextcloud.deck.databinding.ItemAttachmentImageBinding;

public class GalleryItemViewHolder extends RecyclerView.ViewHolder {

    ItemAttachmentImageBinding binding;

    public GalleryItemViewHolder(@NonNull ItemAttachmentImageBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Long imageId) {
        Context context = itemView.getContext();
        Glide.with(context)
                .load(MediaStore.Images.Thumbnails.getThumbnail(
                        context.getContentResolver(), imageId,
                        MediaStore.Images.Thumbnails.MINI_KIND, null))
                .into(binding.preview);
    }
}
