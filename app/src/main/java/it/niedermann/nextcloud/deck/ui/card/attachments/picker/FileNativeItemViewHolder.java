package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemPickerNativeBinding;

public class FileNativeItemViewHolder extends RecyclerView.ViewHolder {

    private final ItemPickerNativeBinding binding;

    public FileNativeItemViewHolder(@NonNull ItemPickerNativeBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Runnable onOpenMajorPicker) {
        binding.title.setText(R.string.show_all_files);
        binding.subtitle.setText(R.string.downloads);
        itemView.setOnClickListener((v) -> onOpenMajorPicker.run());
    }
}
