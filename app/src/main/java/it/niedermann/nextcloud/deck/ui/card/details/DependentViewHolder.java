package it.niedermann.nextcloud.deck.ui.card.details;

import android.graphics.Paint;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.nextcloud.android.common.ui.theme.utils.ColorRole;

import java.time.Instant;
import java.util.stream.Stream;

import it.niedermann.nextcloud.deck.databinding.ItemDependentBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;

public class DependentViewHolder extends RecyclerView.ViewHolder {
    private final ItemDependentBinding binding;

    @SuppressWarnings("WeakerAccess")
    public DependentViewHolder(ItemDependentBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Account account,
                     @NonNull Card card,
                     @Nullable Consumer<Card> doneStatus,
                     @Nullable Consumer<Card> removeDependant,
                     @NonNull ThemeUtils utils) {

        binding.done.setChecked(card.getDone() != null);
        binding.done.setText(card.getTitle());

        Stream.of(
                binding.remove
        ).forEach(v -> utils.platform.colorImageView(v, ColorRole.SECONDARY));

        Stream.of(
                binding.done
        ).forEach(utils.material::colorMaterialCheckBox);

        if (doneStatus == null) {
            binding.remove.setVisibility(View.GONE);
            binding.done.setOnCheckedChangeListener(null);

        } else {
            binding.done.setOnCheckedChangeListener((v1, v2) -> {
                card.setDone(binding.done.isChecked() ? Instant.now() : null);
                doneStatus.accept(card);
                updateStrikeThrough(binding.done);
            });
            binding.remove.setVisibility(View.VISIBLE);
        }

        updateStrikeThrough(binding.done);

        if (removeDependant == null) {
            binding.remove.setVisibility(View.GONE);
            binding.remove.setOnClickListener(null);
        } else {
            binding.remove.setOnClickListener((v) -> removeDependant.accept(card));
            binding.remove.setVisibility(View.VISIBLE);
        }
    }

    private void updateStrikeThrough(CheckBox checkBox) {
        if (checkBox.isChecked()) {
            checkBox.setPaintFlags(checkBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            checkBox.setPaintFlags(checkBox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }
}