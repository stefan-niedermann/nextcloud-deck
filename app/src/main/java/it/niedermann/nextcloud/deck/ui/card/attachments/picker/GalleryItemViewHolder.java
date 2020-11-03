package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAttachmentImageBinding;

public class GalleryItemViewHolder extends RecyclerView.ViewHolder {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ItemAttachmentImageBinding binding;

    public GalleryItemViewHolder(@NonNull ItemAttachmentImageBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Uri uri, @Nullable Bitmap image, @NonNull Consumer<Uri> onSelect) {
        itemView.setOnClickListener((v) -> onSelect.accept(uri));
        Glide.with(itemView.getContext())
                .load(image)
                .placeholder(R.drawable.ic_image_grey600_24dp)
                .into(binding.preview);
    }

    public void bindError() {
        itemView.setOnClickListener(null);
        Glide.with(itemView.getContext())
                .load(R.drawable.ic_image_grey600_24dp)
                .into(binding.preview);
    }
}
