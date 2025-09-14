package it.niedermann.nextcloud.deck.deprecated.ui.main.search;

import androidx.annotation.NonNull;

import com.nextcloud.android.common.ui.theme.utils.ColorRole;

import it.niedermann.nextcloud.deck.databinding.ItemSearchStackBinding;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.deprecated.ui.theme.ThemeUtils;

public class SearchStackViewHolder extends SearchViewHolder {

    private final ItemSearchStackBinding binding;

    public SearchStackViewHolder(@NonNull ItemSearchStackBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull Stack stack) {
        binding.title.setText(stack.getTitle());
    }

    public void applyTheme(int color) {
        final var utils = ThemeUtils.of(color, binding.getRoot().getContext());
        utils.platform.colorTextView(binding.title, ColorRole.ON_SURFACE_VARIANT);
    }
}
