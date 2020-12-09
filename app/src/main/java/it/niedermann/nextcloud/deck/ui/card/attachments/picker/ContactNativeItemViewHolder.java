package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemPickerNativeBinding;

public class ContactNativeItemViewHolder extends RecyclerView.ViewHolder {

    private final ItemPickerNativeBinding binding;

    public ContactNativeItemViewHolder(@NonNull ItemPickerNativeBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Runnable onOpenMajorPicker) {
        binding.title.setText(R.string.show_all_contacts);
        binding.subtitle.setText(R.string.contacts);
        itemView.setOnClickListener((v) -> onOpenMajorPicker.run());
    }
}
