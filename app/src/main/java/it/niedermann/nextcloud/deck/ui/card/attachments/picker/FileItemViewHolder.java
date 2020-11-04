package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import android.net.Uri;
import android.text.format.Formatter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.function.Consumer;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemPickerUserBinding;

public class FileItemViewHolder extends RecyclerView.ViewHolder {

    private final ItemPickerUserBinding binding;

    public FileItemViewHolder(@NonNull ItemPickerUserBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Uri uri, @NonNull String displayName, long size, @NonNull Consumer<Uri> onSelect) {
        itemView.setOnClickListener((v) -> onSelect.accept(uri));
        binding.displayName.setText(displayName);
        binding.contactInformation.setText(Formatter.formatFileSize(itemView.getContext(), size));
        Glide.with(itemView.getContext())
                .load(R.drawable.ic_attach_file_grey600_24dp)
                .placeholder(R.drawable.ic_person_grey600_24dp)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.avatar);
    }

    public void bindError() {
        itemView.setOnClickListener(null);
        Glide.with(itemView.getContext())
                .load(R.drawable.ic_person_grey600_24dp)
                .into(binding.avatar);
    }
}
