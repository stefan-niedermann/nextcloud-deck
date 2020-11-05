package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.net.Uri;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestBuilder;

import java.util.function.BiConsumer;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAttachmentDefaultBinding;

import static android.text.format.Formatter.formatFileSize;
import static it.niedermann.nextcloud.deck.util.AttachmentUtil.getIconForMimeType;
import static it.niedermann.nextcloud.deck.util.DateUtil.getRelativeDateTimeString;

public class FileItemViewHolder extends RecyclerView.ViewHolder {

    private final ItemAttachmentDefaultBinding binding;

    public FileItemViewHolder(@NonNull ItemAttachmentDefaultBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Uri uri, @NonNull String name, String mimeType, long size, long modified, @Nullable BiConsumer<Uri, Pair<String, RequestBuilder<?>>> onSelect) {
        itemView.setOnClickListener(onSelect == null ? null : (v) -> onSelect.accept(uri, new Pair<>(name, null)));
        binding.filename.setText(name);
        binding.filesize.setText(formatFileSize(binding.filesize.getContext(), size));
        binding.modified.setText(getRelativeDateTimeString(binding.modified.getContext(), modified));
        binding.preview.setImageResource(getIconForMimeType(mimeType));
    }

    public void bindError() {
        binding.filename.setText(R.string.simple_exception);
        binding.filesize.setText(null);
        binding.modified.setText(null);
        itemView.setOnClickListener(null);
        binding.preview.setImageResource(R.drawable.ic_attach_file_grey600_24dp);
    }
}
