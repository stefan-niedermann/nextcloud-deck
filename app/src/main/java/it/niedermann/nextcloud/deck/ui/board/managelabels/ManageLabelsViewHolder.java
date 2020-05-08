package it.niedermann.nextcloud.deck.ui.board.managelabels;

import android.content.res.ColorStateList;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.databinding.ItemManageLabelBinding;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.util.ColorUtil;

public class ManageLabelsViewHolder extends RecyclerView.ViewHolder {
    private ItemManageLabelBinding binding;

    @SuppressWarnings("WeakerAccess")
    public ManageLabelsViewHolder(ItemManageLabelBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Label label, @NonNull ManageLabelListener listener) {
        binding.label.setText(label.getTitle());
        final int labelColor = Color.parseColor("#" + label.getColor());
        binding.label.setChipBackgroundColor(ColorStateList.valueOf(labelColor));
        final int color = ColorUtil.getForegroundColorForBackgroundColor(labelColor);
        binding.label.setTextColor(color);
        binding.delete.setOnClickListener((v) -> listener.requestDelete(label));
        binding.editText.setOnClickListener((v) -> listener.requestEdit(label));
    }
}