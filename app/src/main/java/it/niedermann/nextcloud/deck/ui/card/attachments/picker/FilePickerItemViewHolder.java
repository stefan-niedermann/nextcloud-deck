package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemFilterUserBinding;

public class FilePickerItemViewHolder extends RecyclerView.ViewHolder {

    private final ItemFilterUserBinding binding;

    public FilePickerItemViewHolder(@NonNull ItemFilterUserBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Runnable onOpenMajorPicker) {
        Glide.with(itemView.getContext())
                .load(R.drawable.ic_attach_file_grey600_24dp)
                .into(binding.avatar);
        binding.displayName.setText("Show all files");
        itemView.setOnClickListener((v) -> onOpenMajorPicker.run());
    }
}
