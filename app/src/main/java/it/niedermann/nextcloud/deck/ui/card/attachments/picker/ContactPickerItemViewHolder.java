package it.niedermann.nextcloud.deck.ui.card.attachments.picker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemFilterUserBinding;

public class ContactPickerItemViewHolder extends RecyclerView.ViewHolder {

    private final ItemFilterUserBinding binding;

    public ContactPickerItemViewHolder(@NonNull ItemFilterUserBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Runnable onOpenMajorPicker) {
        Glide.with(itemView.getContext())
                .load(R.drawable.ic_baseline_account_circle_24)
                .into(binding.avatar);
        binding.displayName.setText("Show all contacts");
        itemView.setOnClickListener((v) -> onOpenMajorPicker.run());
    }
}
