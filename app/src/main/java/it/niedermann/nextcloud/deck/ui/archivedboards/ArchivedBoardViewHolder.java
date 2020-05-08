package it.niedermann.nextcloud.deck.ui.archivedboards;

import androidx.recyclerview.widget.RecyclerView;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemArchivedBoardBinding;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.util.ViewUtil;

@SuppressWarnings("WeakerAccess")
public class ArchivedBoardViewHolder extends RecyclerView.ViewHolder {

    private final ItemArchivedBoardBinding binding;

    public ArchivedBoardViewHolder(ItemArchivedBoardBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    void bind(Board board) {
        binding.boardIcon.setImageDrawable(ViewUtil.getTintedImageView(binding.boardIcon.getContext(), R.drawable.circle_grey600_36dp, "#" + board.getColor()));
        binding.boardTitle.setText(board.getTitle());
    }
}
