package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.nextcloud.deck.databinding.ItemAttachmentImageBinding;

public class GalleryItemViewHolder extends RecyclerView.ViewHolder {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ItemAttachmentImageBinding binding;

    public GalleryItemViewHolder(@NonNull ItemAttachmentImageBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@Nullable Bitmap image) {
        Glide.with(itemView.getContext())
                .load(image)
                .into(binding.preview);
    }
}
