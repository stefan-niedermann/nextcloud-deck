package it.niedermann.nextcloud.deck.deprecated.ui.preparecreate;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.function.Consumer;

import it.niedermann.nextcloud.deck.databinding.ItemPrepareCreateStackBinding;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.Themed;

class PickStackViewHolder extends RecyclerView.ViewHolder implements Themed {

    private final ItemPrepareCreateStackBinding binding;

    public PickStackViewHolder(@NonNull ItemPrepareCreateStackBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Stack stack, @NonNull Consumer<Stack> onStackSelected, @Nullable Stack selectedStack, @Nullable @ColorInt Integer color) {
        binding.stackTitle.setText(stack.getTitle());
        itemView.setSelected(stack.getLocalId().equals(selectedStack == null ? -1 : selectedStack.getLocalId()));
        itemView.setOnClickListener(view -> {
            if (!itemView.isSelected()) {
                onStackSelected.accept(stack);
            }
        });
        if (color != null) {
            applyTheme(color);
        }
    }

    @Override
    public void applyTheme(@ColorInt int color) {
        final var utils = ThemeUtils.of(color, itemView.getContext());
        utils.deck.themeSelectedCheck(binding.selectedCheck.getContext(), binding.selectedCheck.getDrawable());
    }
}