package it.niedermann.nextcloud.deck.ui.board.managelabels;

import android.content.res.ColorStateList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.android.util.ColorUtil;
import it.niedermann.nextcloud.deck.databinding.ItemManageLabelBinding;
import it.niedermann.nextcloud.deck.model.Label;

public class ManageLabelsViewHolder extends RecyclerView.ViewHolder {
    private ItemManageLabelBinding binding;

    @SuppressWarnings("WeakerAccess")
    public ManageLabelsViewHolder(ItemManageLabelBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
        this.binding.label.setClickable(false);
    }

    public void bind(@NonNull Label label, @NonNull ManageLabelListener listener) {
        binding.label.setText(label.getTitle());
        final int labelColor = label.getColor();
        binding.label.setChipBackgroundColor(ColorStateList.valueOf(labelColor));
        final int color = ColorUtil.INSTANCE.getForegroundColorForBackgroundColor(labelColor);
        binding.label.setTextColor(color);
        binding.delete.setOnClickListener((v) -> listener.requestDelete(label));
        binding.editText.setOnClickListener((v) -> listener.requestEdit(label));
    }
}